package webapp.app1;

import annotation.*;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:20
 */
@JerryController
@JerryRequestMapping("/test")
public class Test {

    @JerryAutowired()
    private TestServiceInter testService;

    @JerryAutowired(name = "test")
    private TestServiceInter testService2;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryRequestMapping(value = "/sys", method = RequestMethod.GET)
    public String sys(@Param(value = "url") String url) {
        logger.info("我是测试数据，😄哈哈" + url);
        testService.sys();
        return "index.html";
    }

    @JerryRequestMapping(value = "/sys?", method = RequestMethod.POST)
    public String sys2(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Integer x) {
        logger.info("sys?我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));

        return testService2.sys();
    }

    @JerryRequestMapping(value = "/sys*", method = RequestMethod.POST)
    public String sys3(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Double x) {
        logger.info("sys*我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));

        return testService2.sys();
    }

    @JerryRequestMapping(value = "/sys2", method = RequestMethod.POST)
    public String sys4(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Integer x) {
        logger.info("sys2我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));

        return "testMapper.xml";
    }

    @JerryRequestMapping(value = "/doc", method = RequestMethod.POST)
    public String doc() {
        return "doc.html";
    }
}


