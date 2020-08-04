package webapp.app1;

import annotation.JerryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-29 10:04
 */
@JerryService()
public class TestService implements TestServiceInter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String sys() {
        logger.info("di");
        return "DI";
    }
}
