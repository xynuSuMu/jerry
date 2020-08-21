package web.interceptor;

import io.netty.handler.codec.http.HttpResponseStatus;
import server.http.JerryHttpServletRequest;
import server.http.JerryHttpServletResponse;
import util.CopyAntPathMatcher;

import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:23
 */
public class Chain {

    public static boolean chain(List<Object> interceptorRegistrations,
                                JerryHttpServletRequest request,
                                JerryHttpServletResponse response,
                                Object o) {
        CopyAntPathMatcher copyAntPathMatcher = new CopyAntPathMatcher();
        for (Object temp : interceptorRegistrations) {
            InterceptorRegistration interceptorRegistration = (InterceptorRegistration) temp;
            HandlerInterceptor handlerInterceptor = (HandlerInterceptor) interceptorRegistration.getInterceptor();
            MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptorRegistration.getInterceptor();
            for (String url : mappedInterceptor.getIncludePatterns()) {
                if (copyAntPathMatcher.match(url, request.getUri())) {
                    boolean res = false;
                    try {
                        res = handlerInterceptor.preHandle(request, response, o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!res) {
                        response.setStatus(HttpResponseStatus.FORBIDDEN);
                        response.writeString("用户禁止访问");
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
