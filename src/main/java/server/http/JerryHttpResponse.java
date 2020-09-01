package server.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-21 08:43
 */
public class JerryHttpResponse extends JerryHttpMessage implements JerryHttpServletResponse {

    private HttpResponseStatus status;

    private final ChannelHandlerContext ctx;

    private boolean response = false;

    public JerryHttpResponse(ChannelHandlerContext ctx, HttpVersion version) {
        this(ctx, version, null, true);
    }

    public JerryHttpResponse(ChannelHandlerContext ctx, HttpVersion version, HttpResponseStatus status) {
        this(ctx, version, status, true);
    }


    public JerryHttpResponse(ChannelHandlerContext ctx, HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
        super(version, validateHeaders);
        if (status == null) {
            throw new NullPointerException("status");
        } else {
            this.status = status;
            this.ctx = ctx;
        }
    }

    public HttpResponseStatus getStatus() {
        return this.status;
    }

    public HttpResponse setStatus(HttpResponseStatus status) {

        if (status == null) {
            throw new NullPointerException("status");
        } else {
            this.status = status;
            return this;
        }
    }


    public HttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.simpleClassName(this));
        buf.append("(decodeResult: ");
        buf.append(this.getDecoderResult());
        buf.append(')');
        buf.append(StringUtil.NEWLINE);
        buf.append(this.getProtocolVersion().text());
        buf.append(' ');
        buf.append(this.getStatus().toString());
        buf.append(StringUtil.NEWLINE);
        this.appendHeaders(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }

    @Override
    public void sendError(String code, String msg) throws IOException {

    }

    @Override
    public void sendRedirect(String url) throws IOException {
        this.setStatus(HttpResponseStatus.TEMPORARY_REDIRECT);
        headers().set("Location", url);
        ctx.writeAndFlush(this)
                .addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public ChannelFuture write(Object o) {
        check();
        return ctx.write(o);
    }

    @Override
    public ChannelFuture writeString(String o) {
        check();
        return ctx.write(Unpooled.copiedBuffer(o, CharsetUtil.UTF_8));
    }

    @Override
    public ChannelFuture writeAndFlush(Object o) {
        check();
        return ctx.writeAndFlush(o);
    }

    @Override
    public ChannelFuture write(Object o, ChannelPromise channelPromise) {
        check();
        if (channelPromise == null)
            return ctx.write(o, ctx.newProgressivePromise());
        else
            return ctx.write(o, channelPromise);
    }

    @Override
    public void setCharacterEncoding(String var1) {

    }

    @Override
    public void setContentLength(int var1) {

    }

    @Override
    public void setContentLengthLong(long var1) {

    }

    @Override
    public void setContentType(String contentType) {
        headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
    }

    @Override
    public void setBufferSize(int var1) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    private synchronized void check() {
        if (!response) {
            response = true;
            ctx.write(this);
        }
    }
}
