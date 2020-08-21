package server.servlet;

import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 13:44
 */
public interface JerryServletRequest {
    //获取协议
    String getProtocol();

    //获取IP
    String getHost();

    //获取端口
    int getServerPort();

    //获取请求路径
    String getUri();

    //Content-Type
    String getContentType();

    //获取属性
    Object getAttribute(String name);

    //赋予属性
    void setAttribute(String name, Object value);

    //获取参数
    String getParameter(String paramName);

    //获取参数列表
    List<String> getParameterNames();

    //请求body字节流
    ByteBuf getByteBuf();
}
