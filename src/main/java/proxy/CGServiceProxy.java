package proxy;

import annotation.JerryTranscational;
import context.JerryContext;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import transaction.TransactionManage;

import java.lang.reflect.Method;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-12 14:28
 */
public class CGServiceProxy implements MethodInterceptor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        SqlSession sqlSession = null;
        if (method.isAnnotationPresent(JerryTranscational.class)
        ) {
//            logger.info("开启事务，关闭自动提交");
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
            invoke = methodProxy.invokeSuper(obj, args);
        } catch (Exception e) {
            if (sqlSession != null) {
//                logger.info("回滚");
                sqlSession.rollback();
                sqlSession.close();
            }
            throw e;
        }
        if (sqlSession != null) {
//            logger.info("提交事务");
            sqlSession.commit();
            sqlSession.close();
        }

        return invoke;
    }
}
