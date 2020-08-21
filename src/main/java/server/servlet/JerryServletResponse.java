package server.servlet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 13:44
 */
public interface JerryServletResponse extends HttpResponse {

    String getCharacterEncoding();

    String getContentType();

    OutputStream getOutputStream() throws IOException;

    PrintWriter getWriter() throws IOException;

    ChannelFuture write(Object o);
    ChannelFuture writeString(String o);

    ChannelFuture writeAndFlush(Object o);

    void setCharacterEncoding(String var1);

    void setContentLength(int var1);

    void setContentLengthLong(long var1);

    void setContentType(String var1);

    void setBufferSize(int var1);

    int getBufferSize();

    void flushBuffer() throws IOException;

    void resetBuffer();

    boolean isCommitted();

    void reset();
}
