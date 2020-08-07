package webapp.app1;

import annotation.*;
import com.alibaba.fastjson.JSONObject;
import context.JerryContext;
import mapper.User;
import mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:20
 */
@JerryController
@JerryRequestMapping("/test")
public class Test {

    @JerryAutowired()
    private TestService testService;

    @JerryAutowired(name = "test")
    private TestServiceInter testService2;

    @JerryAutowired
    private UserMapper userMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryRequestMapping(value = "/sys", method = RequestMethod.GET)
    public String sys(@Param(value = "url") String url) {
        logger.info("我是测试数据，😄哈哈" + url);
//       SqlSession sqlSession =  JerryContext.getInstance().getSqlSession().openSession();
        List<User> list = userMapper.selectUser();
        System.out.println(list.size());
//        sqlSession.close();
        return testService.sys();
    }

    @JerryRequestMapping(value = "/sys2", method = RequestMethod.POST)
    public String sys2(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Integer x) {
        logger.info("我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));
        List<User> list = userMapper.selectUser();
        System.out.println(list.size());
        return testService2.sys();
    }
}
