package proxy;

import annotation.db.DataSourceSwitch;
import annotation.mapper.JerryTranscational;
import context.JerryContext;
import database.JerrySqlSessionFactory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import database.TransactionManage;

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
        SqlSession sqlSession;
        SqlSessionFactory sqlSessionFactory;
        boolean openTranscational = false;
        if (method.isAnnotationPresent(DataSourceSwitch.class)) {
            System.out.println("切换数据源");
            DataSourceSwitch dataSourceSwitch = method.getAnnotation(DataSourceSwitch.class);
            sqlSessionFactory = JerrySqlSessionFactory.getSqlSessionFactory(dataSourceSwitch.value());
        } else {
            sqlSessionFactory = JerrySqlSessionFactory.getSqlSessionFactory(null);
        }
        sqlSession = TransactionManage.getResources();
        if (sqlSession == null) {
            System.out.println("获取新的sqlSession");
            sqlSession = sqlSessionFactory.
                    openSession(sqlSessionFactory.getConfiguration().getDefaultExecutorType());
            TransactionManage.setResources(sqlSession);
        }
        if (method.isAnnotationPresent(JerryTranscational.class)
        ) {
            openTranscational = true;
        }
        Object invoke;
        try {
            invoke = methodProxy.invokeSuper(obj, args);
        } catch (Exception e) {
            if (openTranscational) {
                sqlSession.rollback();
                sqlSession.close();
            }
            throw e;
        }
        if (openTranscational) {
            sqlSession.commit();
            sqlSession.close();
        }

        return invoke;
    }
}
