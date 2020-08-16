package quartz;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import java.lang.reflect.Method;

/**
 * @Auther: chenlong
 * @Date: 2020/8/16 19:20
 * @Description:
 */
public class JobFactoryAdapt implements JobFactory {
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
        // Reflectively adapting to differences between Quartz 1.x and Quartz 2.0...
        Method getJobDetail = bundle.getClass().getMethod("getJobDetail");
        Object jobDetail = getJobDetail.invoke(bundle);// ReflectionUtils.invokeMethod(getJobDetail, bundle);
        Method getJobClass = jobDetail.getClass().getMethod("getJobClass");
        Class jobClass = (Class) getJobClass.invoke(jobDetail);
        return jobClass.newInstance();
    }

    protected Job adaptJob(Object jobObject) throws Exception {
        return (Job) jobObject;
    }

}
