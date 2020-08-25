package server.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.IOException;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 14:47
 */
public class GenericJerryHttpServletResponse extends JerryHttpResponse {

    private ByteBuf content;
    private final HttpHeaders trailingHeaders;
    private final boolean validateHeaders;

    public GenericJerryHttpServletResponse(ChannelHandlerContext ctx, HttpVersion version, HttpResponseStatus status) throws IOException {
        this(ctx, version, status, Unpooled.buffer(0));

    }

    public GenericJerryHttpServletResponse(ChannelHandlerContext ctx, HttpVersion version, HttpResponseStatus status, ByteBuf content) throws IOException {
        this(ctx, version, status, content, true);
    }

    public GenericJerryHttpServletResponse(ChannelHandlerContext ctx, HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) throws IOException {
        super(ctx, version, status, validateHeaders);
        if (content == null) {
            throw new NullPointerException("content");
        } else {
            this.content = content;
            this.trailingHeaders = new DefaultHttpHeaders(validateHeaders);
            this.validateHeaders = validateHeaders;

        }
    }

    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    public void setContent(ByteBuf content) {
        this.content = content;
    }

    public ByteBuf content() {
        return this.content;
    }

    public int refCnt() {
        return this.content.refCnt();
    }

    public HttpResponse retain() {
        this.content.retain();
        return this;
    }

    public HttpResponse retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    public boolean release() {
        return this.content.release();
    }

    public boolean release(int decrement) {
        return this.content.release(decrement);
    }

    public HttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public HttpResponse setStatus(HttpResponseStatus status) {
        super.setStatus(status);
        return this;
    }

    public FullHttpResponse copy() {
        DefaultFullHttpResponse copy = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().copy(), this.validateHeaders);
        copy.headers().set(this.headers());
        copy.trailingHeaders().set(this.trailingHeaders());
        return copy;
    }

    public FullHttpResponse duplicate() {
        DefaultFullHttpResponse duplicate = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().duplicate(), this.validateHeaders);
        duplicate.headers().set(this.headers());
        duplicate.trailingHeaders().set(this.trailingHeaders());
        return duplicate;
    }
}
