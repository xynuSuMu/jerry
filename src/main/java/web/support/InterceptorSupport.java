package web.support;

import web.interceptor.InterceptorRegistry;
import web.resource.ResourceHandlerRegistry;

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
        support.addResource(resource);
    }

    public InterceptorRegistry getRegistry() {
        return registry;
    }

    private final static ResourceHandlerRegistry resource = new ResourceHandlerRegistry();

    public ResourceHandlerRegistry getResource() {
        return resource;
    }
}
