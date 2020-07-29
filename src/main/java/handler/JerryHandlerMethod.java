package handler;

import annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import context.JerryContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import server.modal.HttpResponseModal;
import server.modal.ParamModal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-28 09:09
 */
public class JerryHandlerMethod {

    private Method method;

    private Object o;

    private Parameter[] parameterTypes;

    private Class<?> returnType;

    private RequestMethod requestMethods;

    private static JerryContext jerryContext = JerryContext.getInstance();

    public JerryHandlerMethod(Method method, Object o, Parameter[] parameterTypes, Class<?> returnType, RequestMethod requestMethods) {
        this.method = method;
        this.o = o;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.requestMethods = requestMethods;
    }

    public static HttpResponseModal handlerRequestMethod(ParamModal modal) {
        HttpResponseModal httpResponseModal = new HttpResponseModal();
        JerryHandlerMethod jerryHandlerMethod = jerryContext.getMethod(modal.getUrl());
        if (jerryHandlerMethod == null) {
            httpResponseModal.setResponseStatus(HttpResponseStatus.NOT_FOUND);
            return httpResponseModal;
        }
        Method method = jerryHandlerMethod.method;
        Object o = jerryHandlerMethod.o;
        if (jerryHandlerMethod.requestMethods != RequestMethod.EMPTY &&
                !modal.getHttpMethod().name().equals(jerryHandlerMethod.requestMethods.name())) {
            httpResponseModal.setResponseStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
            return httpResponseModal;
        }
        Object[] params = new Object[jerryHandlerMethod.parameterTypes.length];
        if (jerryHandlerMethod.requestMethods == RequestMethod.GET) {
            int i = 0;
            for (Parameter parameter : jerryHandlerMethod.parameterTypes) {
                //参数
                params[i++] = modal.getParam().get(parameter.getName());
            }
        } else if (jerryHandlerMethod.requestMethods == RequestMethod.POST) {
            int i = 0;
            for (Parameter parameter : jerryHandlerMethod.parameterTypes) {
                //对象
                if (parameter.getType() != String.class && !parameter.getType().isPrimitive() && !isWrapClass(parameter.getType())) {
                    String jsonStr = modal.getParam().get("JSON").toString();
                    Object json = JSONObject.parse(jsonStr);
                    params[i++] = JSONObject.toJavaObject((JSON) json, parameter.getType());
                } else {//基本数据类型+String
                    if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                        params[i++] = Integer.parseInt(modal.getParam().get(parameter.getName()).toString());
                    } else {
                        params[i++] = parameter.getType().cast(modal.getParam().get(parameter.getName()));
                    }
                }
            }
        }
        Object o1 = null;
        try {
            o1 = method.invoke(o, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        httpResponseModal.setO(o1);
        httpResponseModal.setResponseStatus(HttpResponseStatus.OK);
        return httpResponseModal;
    }

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }


}