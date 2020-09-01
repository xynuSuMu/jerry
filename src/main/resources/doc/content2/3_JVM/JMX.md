> JMX(Java Management Extensions，Java管理扩展)在Java编程语言中定义了应用程序以及网络管理和监控的体系结构、设计模式、应用程序接口以及服务。通常使用JMX来监控系统的运行状态或管理系统的某些方面，比如清空缓存、重新加载配置文件

引言部分摘自百度百科，实际上JMX是java5开始提供的对java应用进行监控的一套接口，或者我们也可以像理解JUC包一样理解JMX，把它当成一个框架。JMX这一套接口/框架实现了jvm的一些监控，比如将操作系统信息，内存使用情况，线程情况，gc情况包装为bean，我们使用的jconsole工具就是对这些包装的bean进行图形化的展示，但是我们常用的jstat，jmap等监控工具是由虚拟机直接支持的，并不是通过JMX。


我们经常通过Jconsole来进行JVM调优，但是不知道大家是否注意到可视化界面中有MBean的功能，通过MBean可以看到在JVM中运行的组件的一些属性和操作，如下图。

![image](https://p3.pstatp.com/origin/pgc-image/5597d00b6b7b4783bc3c4d58a6ea4975)

通过这个MBean我们可以发现Bean属性的值，比如上图的Verbose其值为false、除了属性之外还有操作功能，通过这个功能我们可以直接调用MBean的方法。

Jconsole监控工具实际上是基于JMX对一些封装Bean进行可视化，实际上这些Bean并不是固定不变的，开发人员也可以通过JMX提供的接口将自定义的Bean展示到Jconsole上，这些接口主要在javax.management包下，我们来看一下如何注册一个自己的MBean。

首先我们定义接口，接口后缀必须是MBean否则执行会报错，我们这里接口有一个属性，一个动作。判断属性还是动作的依据是根据方法名，get开头的方法会被当作属性。

```java
public interface StudyJavaMBean {
    String getApplicationName();
    void closeJerryMBean();
}
```

编写MBean的实现类
```java
public class StudyJavaMBean implements JerryMBean {

    public String getApplicationName() {
        return "每天学Java";
    }

    public void closeJerryMBean() {
        System.out.println("关闭Jerry应用");
    }
}

```

定义完MBean后，我们开始注册。
简单的说一下代码含义：
* 这里使用synchronized是为为了对当前线程上锁然后调用wait使线程进行等待，方便测试，防止程序终止。
* ManagementFactory是一个工厂类，通过它我们可以获取虚拟提供的Server以及一系列的MBean(这些MBean下面详细的描述)
* 获取到Server之后，就可以将我们自定义的Bean进行注册，LocateRegistry 用于获取特定主机（包括本地主机）上的远程对象注册表的引用，或用于创建一个接受对特定端口调用的远程对象注册表
* ObjectName 是 MBean 的唯一标示，一个 MBeanServer 不能有重复，完整的格式「自定义命名空间:type=自定义类型,name=自定义名称
* 构造JMXServiceURL，其中service:jmx是JMX URL的标准前缀、rmi是jmx connector server的传输协议、jndi/rmi://localhost:1099/jmxrmi    这个是jmx connector server的路径
```java
    public void monitor() throws Exception {
        synchronized (Thread.currentThread()) {
            try {
                //获取当前 JVM 的 MBeanServer，
                // ObjectName 是 MBean 的唯一标示，一个 MBeanServer 不能有重复。
                // 完整的格式「自定义命名空间:type=自定义类型,name=自定义名称」。当然你可以只声明 type ，不声明 name。
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName objectName = new ObjectName("StudyJava:type=customer,name=customerJerryBean");
                server.registerMBean(new StudyJava(), objectName);
                LocateRegistry.createRegistry(8999);
                JMXServiceURL url = new JMXServiceURL
                        ("service:jmx:rmi:///jndi/rmi://localhost:8999/jmxrmi");
                JMXConnectorServer jcs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
                System.out.println("begin rmi start");
                jcs.start();
                System.out.println("rmi start");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.currentThread().wait();
        }
    }
```

注册之后，我们打开jconsole就可以看到我们注册的MBean了。首先我们可以看到MBean的属性值
![image](https://p3.pstatp.com/origin/pgc-image/ab1cffa545534d15803ac67346e390fa)
然后我们也可以可以调用MBean的方法，调用之后我们会在控制台发现输出
![image](https://p3.pstatp.com/origin/pgc-image/329950e85b754c00b95ba3d30db5d7b2)

完成MBean功能之后，我们可以发现这个功能可能并不是太实用，首先我们有其他方式去查看Bean的对象属性和方法(比如日志或者Http接口)，其次这种方法个人觉得仿佛一个后门，操作方法很有可能没有链路留存会存在一些风险。但是JMX提供的一些MBean还是很有用处的，比如获取当前的操作系统信息，内存信息等等。

1.获取操作系统信息

```java
OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();     
System.out.println("操作系统名称："+operatingSystemMXBean.getName());       
System.out.println("操作系统版本："+operatingSystemMXBean.getVersion());      
System.out.println("处理器数量:"+operatingSystemMXBean.getAvailableProcessors());    
System.out.println("操作系统架构:"+operatingSystemMXBean.getArch());
System.out.println("最后一分钟的系统平均负载:"+operatingSystemMXBean.getSystemLoadAverage());
```

2.运行时数据信息(中间省略了一部分方法，大家可以自行查看)

```java
       //运行时数据
RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
System.out.println("虚拟机名称：" + runtimeMXBean.getName());
System.out.println("虚拟机供应商：" + runtimeMXBean.getVmVendor());
System.out.println("虚拟机版本：" + runtimeMXBean.getVmVersion());
System.out.println("虚拟机规范名称：" + runtimeMXBean.getSpecName());
System.out.println("虚拟机规范供应商：" + runtimeMXBean.getSpecVendor());
System.out.println("虚拟机规范版本：" + runtimeMXBean.getSpecVersion());
System.out.println("管理接口的规范版本：" + runtimeMXBean.getManagementSpecVersion());
System.out.println("系统类装入器使用的Java类路径：" + runtimeMXBean.getClassPath());
//......
System.out.println("启动参数:" + runtimeMXBean.getInputArguments());
```

3.内存信息

```java
        //内存
MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
System.out.println("堆内存使用情况"+memoryMXBean.getHeapMemoryUsage());
System.out.println("非堆内存使用情况"+memoryMXBean.getNonHeapMemoryUsage());
```

4.线程信息

```java
     //线程
ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
System.out.println("线程数量："+threadMXBean.getThreadCount());
long[] ids = threadMXBean.getAllThreadIds();
for (long id : ids) {
System.out.println("线程名称："+threadMXBean.getThreadInfo(id).getThreadName());
}
System.out.println("当前线程的CPU总时间(以纳秒为单位):"+threadMXBean.getCurrentThreadCpuTime());
System.out.println("当当前线程执行的CPU时间:"+threadMXBean.getCurrentThreadUserTime());
}
```

5.类加载信息

```java
//
ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
System.out.println("加载类数量:"+classLoadingMXBean.getLoadedClassCount());
System.out.println("未加载类数量:"+classLoadingMXBean.getUnloadedClassCount());
System.out.println("类总数量:"+classLoadingMXBean.getTotalLoadedClassCount());
```

6.GC信息

```java
List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
System.out.println("GC数量："+garbageCollectorMXBeans.size());
for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
System.out.println("GC名称:"+garbageCollectorMXBean.getName());
System.out.println("收集次数:"+garbageCollectorMXBean.getCollectionCount());
System.out.println("收集时间:"+garbageCollectorMXBean.getCollectionTime());
 }
```

到这里关于JMX的介绍就结束了，通过javax.management包下提供的接口，我们其实也可以实现Jconsole一些简单功能。