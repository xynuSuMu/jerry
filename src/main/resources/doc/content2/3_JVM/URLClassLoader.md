> 在前面关于类加载子系统的文章中，我们提到启动类加载器是其他类加载器的parent，是一个本身无parent的类加载器、扩展类加载器的parent是启动类加载器、应用类加载器的parent是扩展类加载器，但是在那篇文章中并没有强调parent代表的含义是一个“委派”体系，而并不是继承关系。

在类加载器的体系中，真实的继承关系如下:

![image](http://p1.pstatp.com/large/pgc-image/23e2ef5f7a374290a2d48703243b77e0)

通过源码我们可以发现AppClassLoader和ExtClassLoader都是Launcher的静态内部类，都继承自URLClassLoader。

那么URLClassLoader和SecureClassLoader具体是做什么的呢？

* SecureClassLoader：扩展了ClassLoader，并为定义具有相关代码源和权限的类提供了额外支持，这些代码源和权限默认情况下由系统策略检索。

* URLClassLoader：继承自SecureClassLoader，支持从jar文件和文件夹中获取class，继承于classload，加载时首先去classload里判断是否由启动类加载器加载过。


今天这篇文章我们重点要说的就是URLClassLoader，在上面类加载器的真实继承关系图中，我们知道URLClassLoader扩展了ClassLoader，在ClassLoader的基础上扩展了一些功能，这些扩展的功能中，最主要的一点就是URLClassLoader却可以加载任意路径下的类(ClassLoader只能加载classpath下面的类)。

如果你未接触URLClassLoader，那么要实现动态加载类都是使用用Class.forName()这个方法，但是这个方法只能创建程序中已经引用的类，如果我们需要动态加载程序外的类，Class.forName()是不够的，这个时候就是需要使用URLClassLoader的时候。

在我的个人项目中，对于URLClassLoader是真实使用过的，这里以我的项目未案例，来讲一下URLClassLoader。

**背景:** 在我的个人网站和小程序接口开发的最开始阶段，是使用Spring Boot来开发的，这种方式搭建接口十分便捷，但是在后面不断学习的过程中，我萌发了自己实现一个类似Tomcat的小应用，然后在模拟Spring实现自己的一套开发规范，这套个人项目首先是一个Maven项目，以Netty为基础实现Http协议，然后参考Spring实现DI和AOP，以及一系列的注解实现接口的开发，下面大概展示一些目录结构：

![image](http://p1.pstatp.com/large/pgc-image/794cca4c37454743816dfc82957c9efe)

那么这个应用和URLClassLoader有什么关系呢？

首先一个普通Maven项目在打包成jar的时候，jar包内部本身是不包含依赖的，我们可以通过Maven-assembly-plugin插件将程序和它本身所依赖的jar包一起打到一个包里，但是这种方式每次打包后，jar都会比较大，所以最终将依赖和项目代码进行分离，依赖jar方式lib文件下。

```
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <!--target/lib是依赖jar包的输出目录，根据自己喜好配置-->
                            <outputDirectory>target/lib</outputDirectory>
                            <excludeTransitive>false</excludeTransitive>
                            <stripVersion>false</stripVersion>
                            <includeScope>runtime</includeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

当程序代码和依赖代码分离之后，如何部署到服务器成为了一个问题，最开始我想的是将其打包为一个可执行的jar，以类似SpringBoot的方式去部署，但是思考之后我更倾向于能有一个统一的部署方案，对于后续新的应用也可以部署，于是就想到了URLClassLoader：将项目jar和lib下的依赖jar放入URLClassLoader类的urls(存储加载的classes和resources)中，然后通过JarFile对项目jar进行遍历，找到程序的入口Main，然后使用URLClassLoader对类进行加载，最终启动它。这里的jar和lib路径可以使用参数进行传递，然后将程序编译为可执行的命令(参考模拟javac命令文章)，后续就可以进行统一部署发布了

```java
   public static void main(String[] args) throws Exception {
        String jar = "/jerry/target/jerry-1.0-SNAPSHOT.jar";
        String lib = "/jerry/target/lib";
        JarFile jarFile = new JarFile(new File(jar));
        Enumeration<JarEntry> entry = jarFile.entries();
        //Lib文件+项目Jar
        URL url = new URL("file:" + jar);
        File file_directory = new File(lib);
        URL[] urls = new URL[file_directory.listFiles().length + 1];
        int i = 0;
        urls[i++] = url;
        for (File file : file_directory.listFiles()) {
            urls[i++] = new URL("file:" + file.getAbsolutePath());
        }
        ClassLoader loader = new URLClassLoader(urls);
        Class<?> c = null;
        Object o = null;
        while (entry.hasMoreElements()) {
            JarEntry jarEntry = entry.nextElement();
            String name = jarEntry.getName();
            System.out.println(name);
            if (name != null && name.endsWith(".class")) {
                if (name.equals("Main.class")) {
                    c = loader.loadClass(name.substring(0, name.length() - 6));
                    o = c.newInstance();
                }
            }
        }
        if (o != null) {
            System.out.println(o);
            System.out.println(c);
            Method method = c.getDeclaredMethod("main", String[].class);
            method.invoke(o, (Object) new String[]{});
        }
    }
```