### 模拟Spring事务注解

**目录**
* 原子性
* Spring 事务
* 模拟@Transacational实现@MyTranscational
* Transcational失效场景(AOP由动态代理方式实现)，

#### 原子性

原子性指的是整个程序中的所有操作,要么全部完成,要么全部不完成,不可能停滞在中间某个环节，
**保证程序的原子性在程序设计中是不容忽视一环**

#### Spring事务

在Spring项目中，当我们为了保证数据库的原子性时，我们可以选择使用Spring声明式事务管理(编程式事务管理很少用)，
这种事务管理方式是建立在Spring AOP的基础上对于目标方法前后进行拦截，并在目标方法开始前创建或者加入一个事务，
在目标方法执行完之后根据执行情况提交或者回滚事务。

Spring 声明式事务可以采用 基于 **XML配置** 和 **基于注解** 两种方式实现，通常我们会选择方便的 **@Transacational**来实现。

#### 模拟@Transacational实现@MyTranscational

我们自己如何去模拟实现Spring事务的注解？

在Spring框架的体系中，核心是提供IOC和AOP服务，而事务注解的实现也离不开IOC和AOP。

举个简单例子，当Spring项目启动过程中，会扫描项目包，将需要装配的bean(类似Component注解修饰对象)进行属性注入(类似Autowired修饰的字段)，
而AOP可以通过JDK动态代理对对象进行增强
（Spring AOP也可以是用CGLIB实现，其原理是使用字节码增强框架ASM），
那么在对bean注入的过程中，我们使用将AOP增强的对象注入即可。

过程如下：

* 定义注解MyAutowired，MyTranscational
* 定义接口和实现类
* 定义代理类
* 扫描包路径得到被代理类进行增强
* 进行代理类注入

注解MyAutowired，MyTranscational

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTranscational {
}
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAutowired {
    String pageName() default "";
}
```

接口及其实现类

```java
public interface MyService {
    void execute();
}
public class MyServiceImp implements MyService {
    @Override
    @MyTranscational
    public void execute() {
        System.out.println("执行SQL");
    }
}
```

代理类，这里需要注意的是invoke中method是接口的方法，并非实现类，如果单纯通过method判断是否
存在MyTranscational注解，那么可能会无效(接口方法没使用了MyTranscational注解)

```java
public class MyProxy implements InvocationHandler {
    private Object obj;
    public MyProxy(Object obj) {
        this.obj = obj;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (obj.getClass().getMethod(method.getName()).
                isAnnotationPresent(MyTranscational.class)
                ||
                method.isAnnotationPresent(MyTranscational.class)
        ) {
            System.out.println("开启事务");
        }
        Object invoke = null;
        try {
            invoke = method.invoke(obj, args);
        } catch (Exception e) {
            System.out.println("回滚");
        }
        return invoke;
    }
}

```

注入的核心是利用反射将增加的对象赋值到字段中，

下方代码中main函数可以理解为一次接口请求，exe方法中：
首先取到Test类中使用MyAutowired注解的字段，
如果发现使用MyAutowired注解的字段是一个接口，
那么遍历指定的包路径，寻找其子类(也可以是用JDK提供的SPI：ServiceLoader)。
默认情况下接口只有一个实现类，如果多个实现类，我们需要MyAutowired注解中指定实现类，否则抛出异常。

完整代码：
```java
public class Test {

    @MyAutowired
    private MyService myService;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException, InstantiationException {
        //理解为这里是接口的调用
        new Test().exe();
    }
   
    public void exe() throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException, InstantiationException {
        //扫描包-得到myService的实现类
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            //需要注入
            if (field.isAnnotationPresent(MyAutowired.class)) {
                //子类
                Class<?> c = null;
                //如果是接口，则找到其实现类，默认一个实现类，如果是多个MyAutowired需要指定
                if (field.getType().isInterface()) {
                    //扫描包
                    String packageName = "com.studyjava.email.test.proxy";
                    String packageDirName = packageName.replace('.', '/');
                    Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(
                            packageDirName);
                    while (dirs.hasMoreElements()) {
                        // 获取下一个元素
                        URL url = dirs.nextElement();
                        // 得到协议的名称
                        String protocol = url.getProtocol();
                        if ("file".equals(protocol)) {
                            //物理路径
                            String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                            File dir = new File(filePath);
                            File[] dirfiles = dir.listFiles(new FileFilter() {
                                // 自定义过滤规则 取到以.class结尾的文件(编译好的java类文件)
                                public boolean accept(File file) {
                                    return file.getName().endsWith(".class");
                                }
                            });
                            for (File file : dirfiles) {
                                String className = file.getName().substring(0,
                                        file.getName().length() - 6);
                                Class<?> temp = Thread.currentThread().getContextClassLoader()
                                        .loadClass(packageName + '.' + className);
                                if (temp.getSuperclass() != null)
                                    for (Class<?> inter : temp.getInterfaces()) {
                                        if (inter.getName().equals(field.getType().getName())) {
                                            c = temp;
                                            break;
                                        }
                                    }
                            }
                        } else if ("jar".equals(protocol)) {
                            //jar包处理逻辑和file处理逻辑不同
                        }
                    }
                }
                //
                if (c == null) {
                    return;
                }
                Object instance = c.newInstance();
                //增加
                Object o = Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                        instance.getClass().getInterfaces(),
                        new MyProxy(instance));
                //赋值
                field.setAccessible(true);
                field.set(this, o);
                this.myService.execute();
            }
        }

    }
    
}
```

#### 失效场景(动态代理实现的前提)
* 底层数据库支持事务
* 使用动态代理实现AOP，代理类必须实现接口，由于接口定义的方法是public的，所以java要求实现类所实现接口的方法必须是public的
（不能是protected，private等），同时不能使用static的修饰符。所以，可以实施接口动态代理的方法只能是使用“public”或“public final”修饰符的方法，
其它方法不可能被动态代理，相应的也就不能实施AOP增强，也不能进行Spring事务增强。
* 异常不能被方法内部消化