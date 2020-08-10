package webapp.app1;

import annotation.JerryAutowired;
import annotation.JerryService;
import annotation.JerryTranscational;
import mapper.User;
import mapper.UserMapper;
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
    public String sys() {
        testServiceInter2.sysV2();
        List<User> list = userMapper.selectUser();
        logger.info(list.size() + "");
        logger.info("di");
        return "DI";
    }
}
