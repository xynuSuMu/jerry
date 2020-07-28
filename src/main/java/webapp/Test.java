package webapp;

import annotation.JerryController;
import annotation.JerryRequestMapping;
import annotation.RequestMethod;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:20
 */
@JerryController
public class Test {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryRequestMapping(value = "/sys", method = RequestMethod.GET)
    public String sys(String url) {
        logger.info("我是测试数据，😄哈哈" + url);
        return "12";
    }

    @JerryRequestMapping(value = "/sys2", method = RequestMethod.POST)
    public String sys2(String code, String email, TestView testView, Integer x) {
        logger.info("我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));
        return "13";
    }
}
