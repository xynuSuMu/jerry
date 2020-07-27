package monitor.bean;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 13:47
 */
public class StudyJava implements StudyJavaMBean {

    int x = 1;

    public String getApplicationName() {
        return "StudyJava";
    }


    public void closeJerryMBean() {
        System.out.println("关闭Jerry应用");
    }
}
