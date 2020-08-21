package webapp.app1;

import annotation.JerryConfig;
import server.http.JerryHttpServletRequest;
import server.http.JerryHttpServletResponse;
import web.interceptor.HandlerInterceptor;

import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:47
 */
@JerryConfig
public class MyInterceptor implements HandlerInterceptor {

    private final String token = "_security_token_inc";

    @Override
    public boolean preHandle(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {

        //根据项目需求，自定义拦截需求
        boolean res = false;
        for (Map.Entry<String, String> entry : request.headers().entries()) {
            if (token.equals(entry.getKey())) {
                if ("91568948871536478".equals(entry.getValue())) {
                    System.out.println("请求通过");
                    return true;
                }
            }
        }
        System.out.println("请求拦截");
        return false;
    }

    @Override
    public void postHandle(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {

    }

    @Override
    public void afterCompletion(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {

    }
}
