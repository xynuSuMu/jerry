package monitor.bean;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 13:47
 */
public class Jerry implements JerryMBean {

    public String getApplicationName() {
        return "Jerry";
    }

    public void closeJerryMBean() {
        System.out.println("关闭Jerry应用");
    }
}
