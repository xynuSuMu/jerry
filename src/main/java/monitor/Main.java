package monitor;

import monitor.bean.StudyJava;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.*;
import java.rmi.registry.LocateRegistry;
import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 13:41
 */
public class Main {
    //https://zhuanlan.zhihu.com/p/86154144


    public static void main(String[] args) throws Exception {
//        new Main().monitor();
//        操作系统
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("操作系统名称：" + operatingSystemMXBean.getName());
        System.out.println("操作系统版本：" + operatingSystemMXBean.getVersion());
        System.out.println("处理器数量:" + operatingSystemMXBean.getAvailableProcessors());
        System.out.println("操作系统架构:" + operatingSystemMXBean.getArch());
        System.out.println("最后一分钟的系统平均负载:" + operatingSystemMXBean.getSystemLoadAverage());
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
        //内存
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println("堆内存使用情况"+memoryMXBean.getHeapMemoryUsage());
        System.out.println("非堆内存使用情况"+memoryMXBean.getNonHeapMemoryUsage());
        //线程
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        System.out.println("线程数量："+threadMXBean.getThreadCount());
        long[] ids = threadMXBean.getAllThreadIds();
        for (long id : ids) {
            System.out.println("线程名称："+threadMXBean.getThreadInfo(id).getThreadName());
        }
        System.out.println("当前线程的CPU总时间(以纳秒为单位):"+threadMXBean.getCurrentThreadCpuTime());
        System.out.println("当当前线程执行的CPU时间:"+threadMXBean.getCurrentThreadUserTime());
        //
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        System.out.println("加载类数量:"+classLoadingMXBean.getLoadedClassCount());
        System.out.println("未加载类数量:"+classLoadingMXBean.getUnloadedClassCount());
        System.out.println("类总数量:"+classLoadingMXBean.getTotalLoadedClassCount());
        //
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        System.out.println("GC数量："+garbageCollectorMXBeans.size());
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            System.out.println("GC名称:"+garbageCollectorMXBean.getName());
            System.out.println("收集次数:"+garbageCollectorMXBean.getCollectionCount());
            System.out.println("收集时间:"+garbageCollectorMXBean.getCollectionTime());
        }


    }

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
//release jar
}
