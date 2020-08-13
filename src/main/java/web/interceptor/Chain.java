package web.interceptor;

import io.netty.handler.codec.http.HttpResponseStatus;
import server.modal.HttpJerryRequest;
import server.modal.HttpJerryResponse;

import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:23
 */
public class Chain {

    public static void chain(List<Object> interceptorRegistrations,
                             HttpJerryRequest request,
                             HttpJerryResponse response,
                             Object o) throws Exception {

        for (Object temp : interceptorRegistrations) {
            InterceptorRegistration interceptorRegistration = (InterceptorRegistration) temp;
            HandlerInterceptor handlerInterceptor = (HandlerInterceptor) interceptorRegistration.getInterceptor();
            MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptorRegistration.getInterceptor();
            for (String url : mappedInterceptor.getIncludePatterns()) {
                if (request.getUrl().equals(url)) {
                    boolean res = handlerInterceptor.preHandle(request, response, o);
                    if (!res) {
                        response.setResponseStatus(HttpResponseStatus.FORBIDDEN);
                        break;
                    }
                }
            }
        }

    }

}