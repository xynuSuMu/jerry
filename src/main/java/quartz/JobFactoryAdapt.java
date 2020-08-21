package quartz;

import context.JerryContext;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Auther: chenlong
 * @Date: 2020/8/16 19:20
 * @Description:
 */
public class JobFactoryAdapt implements JobFactory {

    //
    private JerryContext jerryContext = JerryContext.getInstance();

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        return newJob(triggerFiredBundle);
    }

    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
        try {
            Object jobObject = createJobInstance(bundle);
            return adaptJob(jobObject);
        } catch (Exception ex) {
            throw new SchedulerException("Job instantiation failed", ex);
        }
    }

    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Method getJobDetail = bundle.getClass().getMethod("getJobDetail");
        Object jobDetail = getJobDetail.invoke(bundle);
        Method getJobClass = jobDetail.getClass().getMethod("getJobClass");
        Class jobClass = (Class) getJobClass.invoke(jobDetail);
        String name = jobClass.getName();
        String beanID = name.substring(0, 1).toLowerCase() + name.substring(1);
        Object o = jerryContext.getBean(beanID);
//        jobClass.newInstance();
//        Field[] fields = jobClass.getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            if (field.get(o) == null) {
//                Object proxy = jerryContext.getBean(field.getType().getName());
//                if (proxy != null)
//                    field.set(o, proxy);
//            }
//        }
        return o;
    }

    protected Job adaptJob(Object jobObject) throws Exception {
        return (Job) jobObject;
    }

}
