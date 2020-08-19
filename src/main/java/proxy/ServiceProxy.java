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


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {

        return null;
    }
}