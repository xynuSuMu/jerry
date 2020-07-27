package context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:14
 */
public class JerryContext {
    private static JerryContext jerryContext = null;
    private Map<String, Object> bean = new ConcurrentHashMap<>();

    private JerryContext() {

    }

    public synchronized static JerryContext getInstance() {
        if (jerryContext == null)
            jerryContext = new JerryContext();
        return jerryContext;
    }

    public Object getBean(String name) {
        return bean.get(name);
    }

    public Map<String, Object> getJerryContext() {
        return bean;
    }
}
