package webapp.app1;

import annotation.JerryAutowired;
import annotation.JerryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.interceptor.InterceptorRegistry;
import web.resource.ResourceHandlerRegistry;
import web.support.WebMvcSupport;


import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:46
 */
@JerryConfig
public class WebMvcSupportConfig extends WebMvcSupport {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @JerryAutowired
    private MyInterceptor myInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        List<String> includePatterns = new ArrayList<>();
        logger.info("自定义注册拦截");
        includePatterns.add("/test/sy*");
        registry.addInterceptor(myInterceptor).addPathPatterns(includePatterns);
        super.addInterceptors(registry);
    }


    @Override
    protected void addResource(ResourceHandlerRegistry registry) {
        registry.addResource("/doc/**/*.md");
        registry.addResource("/html/**/*.js");
        registry.addResource("/html/**/*.css");
        registry.addResource("/image/**/*.png");
        super.addResource(registry);
    }
}
