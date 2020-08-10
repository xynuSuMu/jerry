package proxy;

import annotation.JerryTranscational;

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
        if (obj.getClass().getMethod(method.getName()).
                isAnnotationPresent(JerryTranscational.class)
                ||
                method.isAnnotationPresent(JerryTranscational.class)
        ) {
            System.out.println("开启事务");
        }
        Object invoke = null;
        try {
            invoke = method.invoke(obj, args);
        } catch (Exception e) {
            System.out.println("回滚");
        }
        return invoke;
    }
}