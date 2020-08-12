package web.interceptor;

import server.modal.HttpJerryRequest;
import server.modal.HttpJerryResponse;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-11 12:37
 */
public interface HandlerInterceptor {

    default boolean preHandle(HttpJerryRequest request, HttpJerryResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(HttpJerryRequest request, HttpJerryResponse response, Object handler) throws Exception {
    }

    default void afterCompletion(HttpJerryRequest request, HttpJerryResponse response, Object handler) throws Exception {

    }
}
