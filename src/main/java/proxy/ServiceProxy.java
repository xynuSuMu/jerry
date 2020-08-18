package proxy;

import annotation.mapper.JerryTranscational;
import context.JerryContext;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import database.TransactionManage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-10 10:52
 */
public class ServiceProxy implements InvocationHandler {
    private Object obj;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServiceProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        SqlSession sqlSession = null;
        if (obj.getClass().getMethod(method.getName()).
                isAnnotationPresent(JerryTranscational.class)
                ||
                method.isAnnotationPresent(JerryTranscational.class)
        ) {
            logger.info("开启事务，关闭自动提交");
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
                logger.info("异常回滚");
                sqlSession.rollback();
                sqlSession.close();
            }
            throw e;
        }
        if (sqlSession != null) {
            logger.info("提交事务");
            sqlSession.commit();
            sqlSession.close();
        }

        return invoke;
    }
}