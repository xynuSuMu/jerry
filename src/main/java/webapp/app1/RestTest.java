package webapp.app1;

import annotation.*;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.app1.TestServiceInter;
import webapp.app1.TestView;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:20
 */
@JerryRestController
@JerryRequestMapping("/rest")
public class RestTest {

    @JerryAutowired()
    private TestServiceInter testService;

    @JerryAutowired(name = "test")
    private TestServiceInter testService2;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryRequestMapping(value = "/sys", method = RequestMethod.GET)
    public String sys(@Param(value = "url") String url) {
        logger.info("我是测试数据，😄哈哈" + url);
        return "index.html-" + testService.sys();
    }

    @JerryRequestMapping(value = "/sys?", method = RequestMethod.GET)
    public String sys2(@Param(value = "code") String code, @Param(value = "email") String email) {

        return testService2.sys2();
    }


}


