package web.interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 09:12
 */
public class InterceptorRegistry {
    private final List<InterceptorRegistration> registrations = new ArrayList();


    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
        InterceptorRegistration registration = new InterceptorRegistration(interceptor);
        this.registrations.add(registration);
        return registration;
    }

    public List<Object> getInterceptors() {
        return (List) this.registrations;
    }
}

