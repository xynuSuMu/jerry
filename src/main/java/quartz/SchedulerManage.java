package quartz;

import org.quartz.Scheduler;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-19 17:25
 */
public class SchedulerManage {

    private static Scheduler scheduler;

    public static void setScheduler(Scheduler scheduler) {
        SchedulerManage.scheduler = scheduler;
    }

    public static Scheduler getScheduler() {
        return scheduler;
    }

}
