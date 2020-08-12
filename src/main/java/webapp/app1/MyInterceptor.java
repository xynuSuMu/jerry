package webapp.app1;

import annotation.JerryConfig;
import annotation.JerryService;
import server.modal.HttpJerryRequest;
import server.modal.HttpJerryResponse;
import web.interceptor.HandlerInterceptor;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:47
 */
@JerryConfig
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpJerryRequest request, HttpJerryResponse response, Object handler) throws Exception {
        System.out.println("拦截生效");
        //根据项目需求，自定义拦截需求
        return true;
    }

    @Override
    public void postHandle(HttpJerryRequest request, HttpJerryResponse response, Object handler) throws Exception {

    }

    @Override
    public void afterCompletion(HttpJerryRequest request, HttpJerryResponse response, Object handler) throws Exception {

    }
}
