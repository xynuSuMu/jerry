package webapp.app1;

import annotation.JerryAutowired;
import annotation.JerryJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import webapp.app1.mapper.UserMapper;

/**
 * @Auther: chenlong
 * @Date: 2020/8/16 18:44
 * @Description:
 */
@JerryJob
public class MyJob implements Job {

    @JerryAutowired
    UserMapper userMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(userMapper.selectUser().size());
        System.out.println("任务调度===");
    }
}
