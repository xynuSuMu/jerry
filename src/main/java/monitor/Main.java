package monitor;

import monitor.bean.Jerry;

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
        //操作系统
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        System.out.println(operatingSystemMXBean.getName());
        System.out.println(operatingSystemMXBean.getVersion());
        System.out.println(operatingSystemMXBean.getAvailableProcessors());
        System.out.println(operatingSystemMXBean.getArch());
        //运行时数据
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        System.out.println(runtimeMXBean.getVmVersion());
        System.out.println(runtimeMXBean.getName());
        System.out.println(runtimeMXBean.getVmVendor());
        System.out.println(runtimeMXBean.getInputArguments());
        //内存
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        System.out.println(memoryMXBean.getHeapMemoryUsage());
        System.out.println(memoryMXBean.getNonHeapMemoryUsage());
        //线程
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        System.out.println(threadMXBean.getThreadCount());
        long[] ids = threadMXBean.getAllThreadIds();
        for (long id:ids){
            System.out.println(threadMXBean.getThreadInfo(id).getThreadName());
        }
        //
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        System.out.println(classLoadingMXBean.getLoadedClassCount());
        System.out.println(classLoadingMXBean.getUnloadedClassCount());
        System.out.println(classLoadingMXBean.getTotalLoadedClassCount());
        //
        List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            System.out.println(garbageCollectorMXBean.getName());
        }

    }

    public void monitor() throws Exception {
        Thread t = new Thread(() -> {
            try {
                //获取当前 JVM 的 MBeanServer，ObjectName 是 MBean 的唯一标示，一个 MBeanServer 不能有重复。完整的格式「自定义命名空间:type=自定义类型,name=自定义名称」。当然你可以只声明 type ，不声明 name。
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                ObjectName objectName = null;
                objectName = new ObjectName("StudyJava:type=customer,name=customerJerryBean");
                server.registerMBean(new Jerry(), objectName);
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
        });
        t.wait();
    }
    //release jar
}
