package context;

import handler.JerryHandlerMethod;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

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


    //
//    private SqlSessionTemplate sqlSession;
//
//    public void setSqlSession(SqlSessionTemplate sqlSession) {
//        this.sqlSession = sqlSession;
//    }
//
//    public SqlSessionTemplate getSqlSession() {
//        return sqlSession;
//    }

    //存储Bean -> Service注解
    private Map<String, Object> bean = new ConcurrentHashMap<>();

    //存储Mapper
    private Map<String, Object> mapper = new ConcurrentHashMap<>();

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

    public Object getMapperBean(String name) {
        return mapper.get(name);
    }

    public void setMapper(String beanId, Object o) {
        mapper.put(beanId, o);
    }


    public JerryHandlerMethod getMethod(String requestMapping) {
        return controllerMethod.get(requestMapping);
    }

    public void setControllerMethod(String requestMapping, JerryHandlerMethod method) {
        controllerMethod.put(requestMapping, method);
    }
}
