package web.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:14
 */
public class InterceptorRegistration {
    private final HandlerInterceptor interceptor;
    private final List<String> includePatterns = new ArrayList();
    private final List<String> excludePatterns = new ArrayList();
    private int order = 0;

    public InterceptorRegistration(HandlerInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public InterceptorRegistration addPathPatterns(List<String> patterns) {
        this.includePatterns.addAll(patterns);
        return this;
    }

    public InterceptorRegistration excludePathPatterns(String... patterns) {
        return this.excludePathPatterns(Arrays.asList(patterns));
    }

    public InterceptorRegistration excludePathPatterns(List<String> patterns) {
        this.excludePatterns.addAll(patterns);
        return this;
    }


    public InterceptorRegistration order(int order) {
        this.order = order;
        return this;
    }

    protected int getOrder() {
        return this.order;
    }

    protected Object getInterceptor() {
        if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) {
            return this.interceptor;
        } else {
            String[] include = toStringArray(this.includePatterns);
            String[] exclude = toStringArray(this.excludePatterns);
            MappedInterceptor mappedInterceptor = new MappedInterceptor(include, exclude, this.interceptor);

            return mappedInterceptor;
        }
    }

    public static String[] toStringArray(Collection<String> collection) {
        return collection != null ? (String[]) collection.toArray(new String[0]) : new String[0];
    }
}
