package webapp.app1;

import annotation.JerryAutowired;
import annotation.job.JerryJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import webapp.app1.mapper.UserMapper;

/**
 * @Auther: chenlong
 * @Date: 2020/8/16 18:44
 * @Description:
 */
@JerryJob(cron = "0 0/2 * * * ?", name = "testJob2", group = "testGroup2")
public class MyJob2 implements Job {

    @JerryAutowired
    UserMapper userMapper;

    private int i = 0;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("testJob2 - testGroup2");
    }
}
