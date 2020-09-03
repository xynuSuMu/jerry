package webapp.app1;

import annotation.JerryAutowired;
import annotation.job.JerryJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.app1.mapper.User;
import webapp.app1.mapper.UserMapper;

import java.util.List;
import java.util.UUID;

/**
 * @Auther: chenlong
 * @Date: 2020/8/16 18:44
 * @Description:
 */
@JerryJob(cron = "0 0/5 * * * ?", name = "testJob", group = "testGroup1")
public class MyJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryAutowired
    UserMapper userMapper;

    private int i = 0;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        userMapper.crateUUID(UUID.randomUUID().toString().replace("-", ""));
        logger.info("Token更新完成");
    }
}
