package web.interceptor;


import server.http.JerryHttpServletRequest;
import server.http.JerryHttpServletResponse;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-11 12:37
 */
public interface HandlerInterceptor {

    default boolean preHandle(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {
    }

    default void afterCompletion(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {

    }
}
