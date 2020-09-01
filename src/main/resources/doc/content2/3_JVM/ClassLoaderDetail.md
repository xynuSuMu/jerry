> 在上一篇文章中，我们通过两道面试题简单的堆类加载的过程进行类描述，而这一篇文章我们首先来看类加载器，然后再具体来看类加载的每一个阶段

这里首先引用上一篇文章的图：
![image](https://p3.pstatp.com/origin/pgc-image/3f2ebf5f6846479d8922d20b61bf2d04)

其中初始化是类加载的最后一步，使用和卸载不属于类加载的过程、此外在这些动作中，
加载阶段是唯一一个用户可以通过**类加载器**参与的阶段(非数组类的加载)，
后续的其余阶段是完全由虚拟机主导，所以这一篇文章类加载器是重点要说明的，我们首先看类加载器

#### 类加载器

类加载器只用于实现类的加载动作，这里说的加载动作不是指加载阶段，而是整个类加载过程。从JVM层面上看类加载一共分为两类，
一类是启动类加载器(C++实现)，一类是其他类加载器(Java实现)，但是从开发人员角度看，有如下划分：

![image](https://p3.pstatp.com/origin/pgc-image/b26ba1d6613d426da80e6a4859168a6c)

他们彼此负责的功能如下：

* Bootstrap ClassLoader： 也叫根ClassLoader，用C++实现，专门用来加载Java的核心API：$JAVA_HOME中jre/lib/rt.jar中所有class文件，rt的意思是runtime
* Extension ClassLoader： 加载Java扩展API jre/lib/ext中的类
* App ClassLoader： 加载classpath目录下定义的class，也就是应用程序用到的ClassLoader。
* Custom ClassLoader： 可以自定义的ClassLoader，可以通过继承ClassLoader来实现自定义类加载器。

如果我们想知道一个类是被哪种类加载器加载的，或者说验证上面类加载器负责的功能，我们可以通过getClassLoader获取类加载器。

```java
public class MyClassLoader {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(String.class.getClassLoader());//null
        System.out.println(JarFileSystemProvider.class.getClassLoader());//sun.misc.Launcher$ExtClassLoader@7c53a9eb
        System.out.println(MyClassLoader.class.getClassLoader());//sun.misc.Launcher$AppClassLoader@18b4aac2
    }
}
```
结果
```text
null
sun.misc.Launcher$ExtClassLoader@7c53a9eb
sun.misc.Launcher$AppClassLoader@18b4aac2
```
因为根类加载器是使用C++编写的，JVM不能够也不允许程序员获取该类，所以返回的是null，而其它
的输出结果也符合我们上面说的类加载器负责加载的范围。此外在ClassLoader对象中，有**parent**字段，
该字段可以获取其父类构造器，其层级关系就如上面类加载器的划分图一致。

看完了Java虚拟机自带的三种类加载器之后，我们可以知道Java虚拟机自带的类加载器加载文件都是有固定的路径，那么如果我们需要要加载的class文件
不在上面三个类加载器的路径范围内，比如网络流，那么如何加载？这里就需要我们通过继承ClassLoader来实现一个自定义类加载器，核心就是重写findClass方法，
通过重写findClass方法获取其他来源的class文件的字节，然后再进行加载。

代码如下：
```java
public class MyClassLoader extends ClassLoader {
    private String path;
    private String pack;
    public MyClassLoader(String path, String pack) {
        this.path = path;
        this.pack = pack;
    }
    @Override
    protected Class<?> findClass(String name) {
        try {
            byte[] b = loadClassData(name);
            return defineClass(pack + name, b, 0, b.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private byte[] loadClassData(String name) throws IOException {
        name = path + name + ".class";
        InputStream is = null;
        ByteArrayOutputStream outputStream = null;
        try {
            is = new FileInputStream(new File(name));
            outputStream = new ByteArrayOutputStream();
            int i = 0;
            while ((i = is.read()) != -1) {
                outputStream.write(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (is != null) {
                is.close();
            }
        }
        return outputStream.toByteArray();
    }
}
```

我们首先来通过自定义类加载器加载在虚拟机提供的类加载器加载范围内的文件，我们看看是什么结果

```java
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        MyClassLoader o = new MyClassLoader("", "");
        System.out.println(o.getClass().getClassLoader());

        MyClassLoader myClassLoader = new MyClassLoader("", "");
        Class clazz = myClassLoader.loadClass("com.studyjava.email.test.jvm.MyClassLoader");
        System.out.println(clazz.getClassLoader());
     }
```
结果如下：。

```text
sun.misc.Launcher$AppClassLoader@18b4aac2
sun.misc.Launcher$AppClassLoader@18b4aac2
```
我们发现自定义类加载器好像并没有生效，根本原因是因为**双亲委派**机制，表面原因是我们没有使用findClass方法去
加载，而是用loadClass，这会使加载器加载前会查找该全限类名(包名+类名)是否存在，如果存在那就不会再加载该类了

如果我们改成下面这种方式
```java
MyClassLoader myClassLoader = new MyClassLoader("/Users/XX/target/classes/jvm/", 
"com.studyjava.email.test.jvm.");
Class clazz = myClassLoader.loadClass("MyClassLoader");
System.out.println(clazz.getClassLoader());
```

那么结果就会改变，这是因为全限定名发生了改变，判定该类属于未被加载的类，所以会通过我们自定义类加载进行加载，我们可以在findClass方法断点验证一下，第一种方案不会走到断点处，第二种会走到。

```text
com.studyjava.email.test.jvm.MyClassLoader@11028347
```

如果我们加载外部的文件，比如这里我把class文件移到桌面上，那么加载方式如下:
```java
MyClassLoader myClassLoader2 = new MyClassLoader("/Users/chenlong/Desktop/","com.studyjava.email.jvm.");
Class clazz2 = myClassLoader2.loadClass("MyClassLoader");
```

到这里对于类加载器就介绍完了，那么自定义类加载器有什么意义呢？

首先是对class文件进行加密，在加载阶段进行解密，防止反编译。

其次是扩展类加载的范围，因为虚拟机提供的三个类加载器的加载范围都是存在限制的，通过自定义类加载器
我们可以加载非标准来源的代码

最后就是动态创建：为了性能和内存，根据实际情况动态创建代码并执行。


下面具体来看类加载的每一个阶段，到底做了什么事情。


#### 加载

加载是类加载的一个阶段，在前面的文章中，我们提及过class文件是通过类加载器加载到JVM内存中的，而加载阶段需要完成的事情有三点：

* 通过一个类的全限定名(包名+类名)来获取定义此类的二进制字节流
* 将这个字节流所代表的静态存储结构转为方法区的运行时数据结构
* 在内存中(并非Java堆)生成一个代表该类的java.lang.Class对象，作为方法区中该类的各种数据访问入口

这三步中第一步对于开发人员来说可控性最强的，
因为虚拟机规范并没有规定一定要从Class文件中获取，所以可以通过定义自己的类加载器来完成(通过重写一个类加载器的findClass()方法)，
可以实现从jar、zip、war等压缩包中读取，也可以同网络中获取，
甚至可以在运行是计算生成(例如java的动态代理就是利用ProxyGenerator.generateProxyClass()来为特定接口生成代理类的二进制字节流)。


#### 验证

验证是连接阶段的第一步，这一步的目的是为了确保Class文件字节流中包含的信息符合当前虚拟机的要求，并且不会威胁虚拟机自身的安全。

可能有人会问class文件不是由javac编译器编译而来的吗，还用验证什么？首先class文件并不是一定由javac编译而来，比如Groovy等语言的Class文件,
其次即使是javac编译而来的class文件也不是一定安全的，因为class文件是可以被篡改的。

验证主要有如下几个部分

* 文件格式验证
* 元数据验证
* 字节码验证
* 符号引用验证

#### 准备

准备阶段是正式为类实例变量分配内存并且设置类变量初始值的阶段，这些变量所使用的内存都将在方法区中进行分配。
这里的类变量指的是被static修饰的变量，不包括实例变量，实例变量将会在对象实例化的时候随着对象一起分配在java堆中；
另外这里的初始值不是代码中指的初始值而是变量数据类型对应的零值(int为0,String为null等。但是final修饰的静态变量会赋予真实值)。


#### 解析

解析阶段简单的来说就是虚拟机将常量池内的符号引用替换为直接引用的过程

#### 初始化

初始化过程，才会真正开始执行类中定义的Java程序代码。这一步主要就是执行静态变量的初始化，包括静态变量的赋值和静态初始化块的执行


关于卸载相关文章:

https://www.cnblogs.com/mengdd/p/3594608.html

