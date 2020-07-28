package context;

import handler.JerryHandlerMethod;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:14
 */
public class JerryContext {
    private static final JerryContext jerryContext = new JerryContext();
    private Map<String, Object> bean = new ConcurrentHashMap<>();
    private Map<String, JerryHandlerMethod> controllerMethod = new ConcurrentHashMap<>();

    private JerryContext() {

    }

    public synchronized static JerryContext getInstance() {
//        if (jerryContext == null)
//            jerryContext = new JerryContext();
        return jerryContext;
    }

    public Object getBean(String name) {
        return bean.get(name);
    }

    public Map<String, Object> getJerryContext() {
        return bean;
    }

    public JerryHandlerMethod getMethod(String requestMapping) {
        return controllerMethod.get(requestMapping);
    }

    public void setControllerMethod(String requestMapping, JerryHandlerMethod method) {
        controllerMethod.put(requestMapping, method);
    }
}
