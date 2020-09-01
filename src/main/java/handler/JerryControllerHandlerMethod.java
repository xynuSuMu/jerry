package handler;

import annotation.JerryRestController;
import annotation.Param;
import annotation.RequestMethod;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
//import com.github.houbb.asm.tool.reflection.AsmMethods;
import io.netty.buffer.Unpooled;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.apache.ibatis.io.Resources;
import org.objectweb.asm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.*;
import web.fileupload.DefaultMultipartFile;
import web.fileupload.JerryMultipartFile;
import web.support.InterceptorSupport;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Properties;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;


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
        //Http请求类型不匹配
        if (requestMethods != RequestMethod.EMPTY &&
                !httpMethod.name().equals(requestMethods.name())) {
            httpJerryResponse.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
            httpJerryResponse.writeString("请求类型不匹配");
            return;
        }
        //调用参数
        Object[] params = null;
        //获取请求
        if (requestMethods == RequestMethod.GET) {
            params = getReq(httpJerryRequest, httpJerryResponse);
        } else if (requestMethods == RequestMethod.POST) {//POST请求
            params = postReq(httpJerryRequest, httpJerryResponse);
        }
        Object o1;
        try {
            o1 = method.invoke(o, params);
            response(httpJerryRequest, httpJerryResponse, o1);
        } catch (Exception e) {
            httpJerryResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } finally {

        }
    }


    private Object[] getReq(JerryHttpServletRequest httpJerryRequest, JerryHttpServletResponse httpJerryResponse) throws IOException {
        Object[] params = new Object[parameterTypes.length];
//        List<String> paramNamesByAsm = AsmMethods.getParamNamesByAsm(method);
        String[] paramNamesByAsm = getMethodParamNames(method);
        int i = 0;
        for (Parameter parameter : parameterTypes) {
            Param param;
            if (parameter.getType() == JerryHttpServletRequest.class) {
                params[i++] = httpJerryRequest;
            } else if (parameter.getType() == JerryHttpServletResponse.class) {
                params[i++] = httpJerryResponse;
            }
            //参数，如果有注解则取注解，无注解使用ASM进行参数名称的解析

            else if ((param = parameter.getDeclaredAnnotation(Param.class)) != null) {
                params[i++] = httpJerryRequest.getParameter(param.value());
            } else {
                params[i] = httpJerryRequest.getParameter(paramNamesByAsm[i++]);
            }
        }
        return params;
    }

    private Object[] postReq(JerryHttpServletRequest httpJerryRequest, JerryHttpServletResponse httpJerryResponse) throws IOException {
        Object[] params = new Object[parameterTypes.length];
        String[] paramNamesByAsm = getMethodParamNames(method);//AsmMethods.getParamNamesByAsm(method);
        for (String p : paramNamesByAsm) {
            System.out.println(p);
        }
        int i = 0;
        for (Parameter parameter : parameterTypes) {
            Param param;
            if (parameter.getType() == JerryHttpServletRequest.class) {
                params[i] = httpJerryRequest;
            } else if (parameter.getType() == JerryHttpServletResponse.class) {
                params[i] = httpJerryResponse;
            } else if (parameter.getType() == JerryMultipartFile.class) {
                String name;
                if ((param = parameter.getDeclaredAnnotation(Param.class)) != null) {
                    name = param.value();
                } else {
                    name = paramNamesByAsm[i];
                }
                JerryMultipartFile jerryMultipartFile = null;
                for (FileUpload fileUpload : httpJerryRequest.getFileUpload()) {
                    if (fileUpload.getName().equals(name)) {
                        InputStream inputStream = new ByteArrayInputStream(fileUpload.getByteBuf().array());
                        jerryMultipartFile =
                                new DefaultMultipartFile(inputStream,
                                        fileUpload.getMaxSize(),
                                        fileUpload.getFilename());
                        break;
                    }
                }
                params[i++] = jerryMultipartFile;
            } else if ((param = parameter.getDeclaredAnnotation(Param.class)) != null) {
                String p = param.value();
                //对象类型数据
                if (parameter.getType() != String.class
                        && !parameter.getType().isPrimitive()
                        && !isWrapClass(parameter.getType())) {
                    Object json = null;
                    if (httpJerryRequest.getByteBuf() != null) {
                        String jsonStr = httpJerryRequest.getByteBuf().toString(CharsetUtil.UTF_8);
                        json = JSONObject.parse(jsonStr);
                        json = JSONObject.toJavaObject((JSON) json, parameter.getType());
                    }
                    params[i++] = json;
                } else {//基本数据类型+String
                    if (parameter.getType() == Integer.class || parameter.getType() == int.class) {
                        params[i++] = Integer.parseInt(httpJerryRequest.getParameter(p));
                    } else if (parameter.getType() == Double.class || parameter.getType() == double.class) {
                        params[i++] = Double.parseDouble(httpJerryRequest.getParameter(p));
                    } else if (parameter.getType() == Float.class || parameter.getType() == float.class) {
                        params[i++] = Float.parseFloat(httpJerryRequest.getParameter(p));
                    } else {
                        params[i++] = parameter.getType().cast(httpJerryRequest.getParameter(p));
                    }
                }
            } else {
                params[i] = httpJerryRequest.getParameter(paramNamesByAsm[i++]);
            }
        }
        return params;
    }

    private void response(JerryHttpServletRequest httpJerryRequest, JerryHttpServletResponse httpJerryResponse, Object o) throws IOException {
        if (returnType == void.class) {
            httpJerryResponse.setContentLength(0);
        } else {
            if (!isRestController(o.getClass())) {//非Rest接口，返回Controller对应的资源
                Properties properties = Resources.getResourceAsProperties("jerry.properties");
                String dir = properties.getProperty("html.dir");
                String resource = dir + "/" + o.toString();
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
                String contentType = InterceptorSupport.getInstance().getResource().getContentType(temp);
                httpJerryResponse.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                httpJerryResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                httpJerryResponse.setContentType(contentType);
                if (httpJerryRequest.isSSL())
                    httpJerryResponse.writeAndFlush(new ChunkedFile(file));
                else
                    httpJerryResponse.writeAndFlush(new DefaultFileRegion(file.getChannel(), 0, file.length()));
                file.close();
                temp.delete();
            } else {
                httpJerryResponse.writeAndFlush(Unpooled.copiedBuffer(o.toString(), CharsetUtil.UTF_8));
            }
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

//    public static void main(String[] args) throws NoSuchMethodException {
//        String[] s = getMethodParamNames(Test.class.getDeclaredMethod("sys",String.class));
//        System.out.println(Arrays.toString(s));
//
//    }
    private static boolean sameType(Type[] types, Class<?>[] clazzes) {
        // 个数不同
        if (types.length != clazzes.length) {
            return false;
        }

        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * <p>
     * 获取方法的参数名
     * </p>
     *
     * @param m
     * @return
     */
    public static String[] getMethodParamNames(final Method m) {
        final String[] paramNames = new String[m.getParameterTypes().length];
        final String n = m.getDeclaringClass().getName();
        ClassReader cr ;
        try {
            cr = new ClassReader(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(new ClassVisitor(Opcodes.ASM7) {
            @Override
            public MethodVisitor visitMethod(final int access,
                                             final String name, final String desc,
                                             final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                // 方法名相同并且参数个数相同
                if (!name.equals(m.getName())
                        || !sameType(args, m.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature,
                            exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, desc,
                        signature, exceptions);
                return new MethodVisitor(Opcodes.ASM7, v) {
                    @Override
                    public void visitLocalVariable(String name, String desc,
                                                   String signature, Label start, Label end, int index) {
                        int i = index - 1;
                        // 如果是静态方法，则第一就是参数
                        // 如果不是静态方法，则第一个是"this"，然后才是方法的参数
                        if (Modifier.isStatic(m.getModifiers())) {
                            i = index;
                        }
                        if (i >= 0 && i < paramNames.length) {
                            paramNames[i] = name;
                        }
                        super.visitLocalVariable(name, desc, signature, start,
                                end, index);
                    }

                };
            }
        }, 0);
        return paramNames;
    }

}
