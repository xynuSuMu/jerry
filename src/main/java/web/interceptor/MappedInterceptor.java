package web.interceptor;

import server.http.JerryHttpServletRequest;
import server.http.JerryHttpServletResponse;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:30
 */
public class MappedInterceptor implements HandlerInterceptor {

    private final String[] includePatterns;

    private final String[] excludePatterns;
    private final HandlerInterceptor interceptor;

    public MappedInterceptor(String[] includePatterns, String[] excludePatterns, HandlerInterceptor interceptor) {
        this.includePatterns = includePatterns;
        this.excludePatterns = excludePatterns;
        this.interceptor = interceptor;
    }

    public String[] getIncludePatterns() {
        return includePatterns;
    }

    public String[] getExcludePatterns() {
        return excludePatterns;
    }

    @Override
    public boolean preHandle(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {
        return this.interceptor.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {
        this.interceptor.postHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(JerryHttpServletRequest request, JerryHttpServletResponse response, Object handler) throws Exception {
        this.interceptor.afterCompletion(request, response, handler);
    }
}
