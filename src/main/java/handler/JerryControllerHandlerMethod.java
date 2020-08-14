package handler;

import annotation.JerryRestController;
import annotation.Param;
import annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import context.JerryContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.stream.ChunkedNioFile;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.modal.HttpJerryRequest;
import server.modal.HttpJerryResponse;
import server.modal.ParamModal;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-28 09:09
 */
public class JerryControllerHandlerMethod {

    private Method method;

    private Object object;

    private Parameter[] parameterTypes;

    private Class<?> returnType;

    private RequestMethod requestMethods;


    private static Logger logger = LoggerFactory.getLogger(JerryControllerHandlerMethod.class);


    public JerryControllerHandlerMethod(Method method, Object o, Parameter[] parameterTypes, Class<?> returnType, RequestMethod requestMethods) {
        this.method = method;
        this.object = o;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.requestMethods = requestMethods;
    }

    public void handlerRequestMethod(HttpMethod httpMethod,
                                     HttpJerryRequest httpJerryRequest,
                                     HttpJerryResponse httpJerryResponse,
                                     Map<Object, Object> requestParam) {
        Object o = object;
        if (requestMethods != RequestMethod.EMPTY &&
                !httpMethod.name().equals(requestMethods.name())) {
            httpJerryResponse.setResponseStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
        }
        Object[] params = new Object[parameterTypes.length];
        //get请求
        if (requestMethods == RequestMethod.GET) {
            int i = 0;
            for (Parameter parameter : parameterTypes) {
                //参数
                Param param;
                if ((param = parameter.getDeclaredAnnotation(Param.class)) != null) {
                    params[i++] = requestParam.get(param.value());
                }
            }
        } else if (requestMethods == RequestMethod.POST) {//POST请求
            int i = 0;
            for (Parameter parameter : parameterTypes) {
                Param param;
                if ((param = parameter.getDeclaredAnnotation(Param.class)) != null) {
                    String p = param.value();
                    //对象
                    if (parameter.getType() != String.class && !parameter.getType().isPrimitive() && !isWrapClass(parameter.getType())) {
                        String jsonStr = requestParam.get("JSON").toString();
                        Object json = JSONObject.parse(jsonStr);
                        params[i++] = JSONObject.toJavaObject((JSON) json, parameter.getType());
                    } else {//基本数据类型+String
                        if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                            params[i++] = Integer.parseInt(requestParam.get(p).toString());
                        } else if (parameter.getType() == Double.class || parameter.getType() == double.class) {
                            params[i++] = Double.parseDouble(requestParam.get(p).toString());
                        } else {
                            params[i++] = parameter.getType().cast(requestParam.get(p));
                        }
                    }
                }
            }
        }
        Object o1 = null;
        try {
            o1 = method.invoke(o, params);
            if (returnType == void.class) {
                httpJerryResponse.setO("");
            } else {
                if (!isRestController(o.getClass())) {
                    httpJerryResponse.setCONTENT_TYPE("text/html; charset=UTF-8");
                    String resource = "html/" + o1.toString();
                    InputStream inputStream = null;
                    try {
                        inputStream = Resources.getResourceAsStream(resource);
                    } catch (IOException e) {
                        logger.info("e", e);
                    }
                    if (inputStream == null) {
                        inputStream = Resources.getResourceAsStream("html/404.html");
                    }
                    Properties properties = Resources.getResourceAsProperties("jerry.properties");

                    File temp = File.createTempFile(resource, properties.getProperty("suffix"));
                    BufferedInputStream bis = null;
                    BufferedOutputStream bos = null;
                    try {
                        bis = new BufferedInputStream(inputStream);
                        bos = new BufferedOutputStream(new FileOutputStream(temp));                            //把文件流转为文件，保存在临时目录
                        int len = 0;
                        byte[] buf = new byte[10 * 1024];                                                    //缓冲区
                        while ((len = bis.read(buf)) != -1) {
                            bos.write(buf, 0, len);
                        }
                        bos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (bos != null) bos.close();
                            if (bis != null) bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
//                    File file = Resources.getResourceAsFile(resource);
                    httpJerryResponse.setFile(temp);
                } else {
                    httpJerryResponse.setO(o1);
                }
            }
            httpJerryResponse.setResponseStatus(HttpResponseStatus.OK);
        } catch (Exception e) {
            httpJerryResponse.setResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } finally {

        }

//        return httpJerryResponse;
    }

    private boolean isRestController(Class<?> clazz) {
        if (clazz.getAnnotation(JerryRestController.class) != null) {
            return true;
        }
        return false;
    }

    private boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }


}
