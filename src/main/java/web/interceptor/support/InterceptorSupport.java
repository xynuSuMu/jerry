package web.interceptor.support;

import annotation.JerryConfig;
import web.interceptor.InterceptorRegistration;
import web.interceptor.InterceptorRegistry;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 13:42
 */
public class InterceptorSupport {

    private final static InterceptorSupport interceptorSupport = new InterceptorSupport();

    private InterceptorSupport() {
    }

    public static InterceptorSupport getInstance() {
        return interceptorSupport;
    }

    private final static InterceptorRegistry registry = new InterceptorRegistry();

    public void mvcConfig(WebMvcSupport support) {
        support.addInterceptors(registry);
    }

    public InterceptorRegistry getRegistry() {
        return registry;
    }

}
