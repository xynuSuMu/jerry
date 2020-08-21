package handler;

import annotation.JerryRestController;
import annotation.Param;
import annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.*;
import web.support.InterceptorSupport;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    public void handlerRequestMethod(
            JerryHttpServletRequest httpJerryRequest,
            JerryHttpServletResponse httpJerryResponse
    ) throws IOException {
        HttpMethod httpMethod = httpJerryRequest.getMethod();
        Object o = object;
        if (requestMethods != RequestMethod.EMPTY &&
                !httpMethod.name().equals(requestMethods.name())) {
            httpJerryResponse.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        Object[] params = new Object[parameterTypes.length];
        //get请求
        if (requestMethods == RequestMethod.GET) {
            int i = 0;
            for (Parameter parameter : parameterTypes) {
                //参数
                Param param;
                if ((param = parameter.getDeclaredAnnotation(Param.class)) != null) {
                    params[i++] = httpJerryRequest.getParameter(param.value());
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
                        String jsonStr = httpJerryRequest.getByteBuf().toString(CharsetUtil.UTF_8);
                        Object json = JSONObject.parse(jsonStr);
                        params[i++] = JSONObject.toJavaObject((JSON) json, parameter.getType());
                    } else {//基本数据类型+String
                        if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                            params[i++] = Integer.parseInt(httpJerryRequest.getParameter(p));
                        } else if (parameter.getType() == Double.class || parameter.getType() == double.class) {
                            params[i++] = Double.parseDouble(httpJerryRequest.getParameter(p));
                        } else {
                            params[i++] = parameter.getType().cast(httpJerryRequest.getParameter(p));
                        }
                    }
                }
            }
        }
        Object o1 = null;
        try {
            o1 = method.invoke(o, params);
            if (returnType == void.class) {
                httpJerryResponse.setContentLength(0);
            } else {
                if (!isRestController(o.getClass())) {
                    httpJerryResponse.setContentType("text/html; charset=UTF-8");
                    Properties properties = Resources.getResourceAsProperties("jerry.properties");
                    String dir = properties.getProperty("html.dir");
                    String resource = dir + "/" + o1.toString();
                    InputStream inputStream = null;
                    try {
                        inputStream = Resources.getResourceAsStream(resource);
                    } catch (IOException e) {
                        logger.info("e", e);
                    }
                    if (inputStream == null) {
                        inputStream = Resources.getResourceAsStream(properties.getProperty("not.find.html"));
                    }
                    File temp = File.createTempFile(resource, properties.getProperty("html.suffix"));
                    InterceptorSupport.getInstance().getResource().getTempResource(inputStream, temp);
                    RandomAccessFile file = new RandomAccessFile(temp, "r");
                    String contentType;
                    if (temp.getName().endsWith(".md") || temp.getName().endsWith(".html")) {
                        contentType = "text/html; charset=UTF-8";
                    } else {
                        contentType = "text/json; charset=UTF-8";
                    }
                    httpJerryResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    httpJerryResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                    httpJerryResponse.setContentType(contentType);
                    httpJerryResponse.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
                    httpJerryResponse.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                    file.close();
                    temp.delete();
                } else {
                    httpJerryResponse.write(Unpooled.copiedBuffer(o1.toString(), CharsetUtil.UTF_8));
                }
            }
            httpJerryResponse.setStatus(HttpResponseStatus.OK);
        } catch (Exception e) {
            httpJerryResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } finally {

        }
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
