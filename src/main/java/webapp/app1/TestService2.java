package webapp.app1;

import annotation.JerryAutowired;
import annotation.JerryService;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-10 14:24
 */
@JerryService
public class TestService2 implements TestServiceInter2 {

    @JerryAutowired
    private TestServiceInter testService;

    @Override
    public void sysV2() {
        System.out.println("sysV2");
    }
}
