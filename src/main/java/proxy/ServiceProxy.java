package proxy;

import annotation.JerryTranscational;
import context.JerryContext;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import transaction.TransactionManage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-10 10:52
 */
public class ServiceProxy implements InvocationHandler {
    private Object obj;


    public ServiceProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlSession sqlSession = null;
        if (obj.getClass().getMethod(method.getName()).
                isAnnotationPresent(JerryTranscational.class)
                ||
                method.isAnnotationPresent(JerryTranscational.class)
        ) {
            System.out.println("开启事务，关闭自动提交");
            //线程绑定sqlSession
            sqlSession = TransactionManage.getResources();
            if (sqlSession == null) {
                SqlSessionFactory sqlSessionFactory = JerryContext.getInstance().getSqlSessionFactory();
                sqlSession = sqlSessionFactory.
                        openSession(sqlSessionFactory.getConfiguration().getDefaultExecutorType());
                TransactionManage.setResources(sqlSession);
            }
        }
        Object invoke = null;
        try {
            invoke = method.invoke(obj, args);
        } catch (Exception e) {
            if (sqlSession != null) {
                System.out.println("回滚");
                sqlSession.rollback();
            }
        }
        if (sqlSession != null) {
            System.out.println("提交事务");
            sqlSession.commit();
            sqlSession.close();
        }

        return invoke;
    }
}