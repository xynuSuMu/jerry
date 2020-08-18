package webapp.app1;

import annotation.JerryAutowired;
import annotation.JerryService;
import annotation.db.DataSourceSwitch;
import annotation.mapper.JerryTranscational;
import webapp.app1.mapper.User;
import webapp.app1.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-29 10:04
 */
@JerryService("test")
public class TestService implements TestServiceInter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryAutowired
    private TestServiceInter2 testServiceInter2;

    @JerryAutowired
    private UserMapper userMapper;

    @Override
    @JerryTranscational
    @DataSourceSwitch(value = "dataSource2")
    public String sys() {
        testServiceInter2.sysV2();
        System.out.println(userMapper + "userMapper");
        List<User> list = userMapper.selectUser();
        return list.size() + "";
    }

    @Override
    public String sys2() {
        List<User> list = userMapper.selectUser();
        return list.size() + "";
    }
}
