package webapp.app3.controller;

import annotation.JerryRequestMapping;
import annotation.JerryRestController;
import annotation.Param;
import annotation.RequestMethod;
import context.JerryContext;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quartz.SchedulerManage;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-19 17:44
 */
@JerryRestController
@JerryRequestMapping("scheduler")
public class SchedulerController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryRequestMapping("/getGroup")
    public String getGroup() throws SchedulerException {
        Scheduler scheduler = SchedulerManage.getScheduler();

        //
        Map<String, List<String>> map = new HashMap<>();
        //一个Tiger对应一个Job
        GroupMatcher matcher = GroupMatcher.anyTriggerGroup();
        Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(matcher);
        for (TriggerKey key : triggerKeys) {
            Trigger trigger = scheduler.getTrigger(key);
            System.out.println(key.getName() + "-" + key.getGroup());
            if (!map.containsKey(trigger.getJobKey().getGroup()))
                map.put(trigger.getJobKey().getGroup(), new ArrayList<>());
            map.get(trigger.getJobKey().getGroup()).add(key.getName());
//            if (trigger instanceof CronTrigger) {
//                logger.info("cron,{}", ((CronTrigger) trigger).getCronExpression());
//            } else {
//                logger.info("非 cron 触发");
//            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            stringBuffer.append(entry.getKey() + ":");
            for (String string : entry.getValue()) {
                stringBuffer.append(string + " ");
            }
            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }


    @JerryRequestMapping(value = "/pause", method = RequestMethod.GET)
    public Boolean pause(@Param("triggerName") String triggerName, @Param("triggerGroup") String triggerGroup) throws SchedulerException {
        Scheduler scheduler = SchedulerManage.getScheduler();
        scheduler.pauseTrigger(new TriggerKey(triggerName, triggerGroup));
        return true;
    }

    @JerryRequestMapping(value = "/resume", method = RequestMethod.GET)
    public void resumeTrigger(@Param("triggerName") String triggerName, @Param("triggerGroup") String triggerGroup) throws SchedulerException {
        Scheduler scheduler = SchedulerManage.getScheduler();
        scheduler.resumeTrigger(new TriggerKey(triggerName, triggerGroup));
    }

    @JerryRequestMapping(value = "/exe", method = RequestMethod.GET)
    public void exe(@Param("triggerName") String triggerName, @Param("triggerGroup") String triggerGroup) throws SchedulerException, IllegalAccessException, InstantiationException {
        Scheduler scheduler = SchedulerManage.getScheduler();
        Trigger trigger = scheduler.getTrigger(new TriggerKey(triggerName, triggerGroup));
        Class jobClass = scheduler.getJobDetail(trigger.getJobKey()).getJobClass();
        String name = jobClass.getName();
        String beanID = name.substring(0, 1).toLowerCase() + name.substring(1);
        Job jobObj = (Job) JerryContext.getInstance().getBean(beanID);
        try {
            Method targetMethod = jobClass.getDeclaredMethod("execute", new Class[]{JobExecutionContext.class});
            targetMethod.setAccessible(true);
            targetMethod.invoke(jobObj, new Object[]{null});
            targetMethod.setAccessible(false);
        } catch (Exception e) {
            throw new SchedulerException(e);
        }
//        }

    }

}
