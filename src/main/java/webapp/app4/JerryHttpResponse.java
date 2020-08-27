package webapp.app4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-24 10:47
 */
public class JerryHttpResponse extends DefaultHttpResponse implements FullHttpResponse {

    private ByteBuf content;
    private final HttpHeaders trailingHeaders;
    private final boolean validateHeaders;
  private final   ChannelHandlerContext ctx;

    public JerryHttpResponse(ChannelHandlerContext ctx,HttpVersion version, HttpResponseStatus status) {
        this(ctx,version, status, Unpooled.buffer(0));
    }

    public JerryHttpResponse(ChannelHandlerContext ctx,HttpVersion version, HttpResponseStatus status, ByteBuf content) {
        this(ctx,version, status, content, true);
    }

    public JerryHttpResponse(ChannelHandlerContext ctx,HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
        super(version, status, validateHeaders);
        if (content == null) {
            throw new NullPointerException("content");
        } else {
            this.content = content;
            this.trailingHeaders = new DefaultHttpHeaders(validateHeaders);
            this.validateHeaders = validateHeaders;
            this.ctx = ctx;
        }
    }

    public void setContent(ByteBuf byteBuf) {
        this.content = byteBuf;
    }

    public void write(Object o){
        this.ctx.writeAndFlush(o);
    }

    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }

    public ByteBuf content() {
        return this.content;
    }

    public int refCnt() {
        return this.content.refCnt();
    }

    public FullHttpResponse retain() {
        this.content.retain();
        return this;
    }

    public FullHttpResponse retain(int increment) {
        this.content.retain(increment);
        return this;
    }

    public boolean release() {
        return this.content.release();
    }

    public boolean release(int decrement) {
        return this.content.release(decrement);
    }

    public FullHttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public FullHttpResponse setStatus(HttpResponseStatus status) {
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
