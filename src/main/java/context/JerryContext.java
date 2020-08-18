package context;

import handler.JerryControllerHandlerMethod;
import org.apache.ibatis.session.SqlSessionFactory;
import util.CopyAntPathMatcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
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

    private JerryContext() {

    }

    public synchronized static JerryContext getInstance() {
        return jerryContext;
    }

    //存储Bean
    private Map<String, Object> bean = new ConcurrentHashMap<>();


    //存储URL对应的控制层方法
    private Map<String, JerryControllerHandlerMethod> controllerMethod = new ConcurrentHashMap<>();

    //Mybatis
    private SqlSessionFactory sqlSessionFactory;


    public void setSqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
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

    public JerryControllerHandlerMethod getRequestMethod(String path) {
        CopyAntPathMatcher copyAntPathMatcher = new CopyAntPathMatcher();
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, JerryControllerHandlerMethod> entry : controllerMethod.entrySet()) {
            if (copyAntPathMatcher.match(entry.getKey(), path)) {
                list.add(entry.getKey());
            }
        }
        if (list.isEmpty())
            return null;
        int index = 0;
        Comparator<String> comparator = copyAntPathMatcher.getPatternComparator(path);
        for (int i = 1; i < list.size(); i++) {
            String pattern = list.get(index);
            int res = comparator.compare(pattern, list.get(i));
            if (res == 0) {
                break;
            } else if (res == 1) {
                index = i;
            }
        }

        return controllerMethod.get(list.get(index));
    }

    public JerryControllerHandlerMethod getMethod(String requestMapping) {
        return controllerMethod.get(requestMapping);
    }

    public void setControllerMethod(String requestMapping, JerryControllerHandlerMethod method) {
        controllerMethod.put(requestMapping, method);
    }
}
