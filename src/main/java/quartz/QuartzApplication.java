package quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.MutableTrigger;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;

import java.util.Date;

/**
 * @Auther: chenlong
 * @Date: 2020/8/16 18:43
 * @Description:文档:https://www.w3cschool.cn/quartz_doc/quartz_doc-2put2clm.html
 */
public class QuartzApplication {
    public void start(Object o) throws SchedulerException {
        System.out.println("开启定时任务===");
        //1.从工厂中获取调度器实例
        //2.自己构建JobDetail
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        //2.创建JobDetail
        JobDetail job = JobBuilder
                .newJob((Class<? extends Job>) o.getClass())
                .withIdentity("myjob", "group1")//job的name和group
                .build();

        //3.创建Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger", "Tgr_group")//trigger的name和group
                .startNow()//设置开启触发器
                .withSchedule(// 定义调度触发规则
                        SimpleScheduleBuilder
                                .repeatSecondlyForTotalCount(5, 15)//重复5次,每次间隔5秒
                )
                .build();

//        TriggerFiredBundle bndle = new TriggerFiredBundle(job,
//                (OperableTrigger) trigger, null, false, new Date(),
//                trigger.getPreviousFireTime(),
//                trigger.getPreviousFireTime(), trigger.getNextFireTime());
//                //把作业和触发器注册到任务调度中
                scheduler.scheduleJob(job, trigger);
        //☆启动调度
        scheduler.start();
    }
}
