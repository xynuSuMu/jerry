package context;

import handler.JerryHandlerMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:14
 */
public class JerryContext {
    private static final JerryContext jerryContext = new JerryContext();


    //存储Bean -> Service注解
    private Map<String, Object> bean = new ConcurrentHashMap<>();

    //beanID 转换-> Service注解中写明name
    private Map<String, String> beanID = new ConcurrentHashMap<>();

    //存储URL对应的控制层方法
    private Map<String, JerryHandlerMethod> controllerMethod = new ConcurrentHashMap<>();

    private JerryContext() {

    }

    public synchronized static JerryContext getInstance() {
        return jerryContext;
    }

    public List<Object> getBeans() {
        List<Object> res = new ArrayList<>();
        for (Map.Entry<String, Object> entry : bean.entrySet()) {
            res.add(entry.getValue());
        }
        return res;
    }

    public Object getBean(String name) {
        return bean.get(name);
    }

    public void setBean(String beanId, Object o) {
        bean.put(beanId, o);
    }

    public String getBeanID(String name) {
        return beanID.get(name);
    }

    public void setBeanID(String beanId, String o) {
        beanID.put(beanId, o);
    }

    public JerryHandlerMethod getMethod(String requestMapping) {
        return controllerMethod.get(requestMapping);
    }

    public void setControllerMethod(String requestMapping, JerryHandlerMethod method) {
        controllerMethod.put(requestMapping, method);
    }
}
