package server.modal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 15:57
 */
public class ParamModal {

    HttpVersion protocolVersion;
    boolean keepAlive;
    //请求方法
    private HttpMethod httpMethod;
    private String url;
    private HttpJerryRequest httpJerryRequest;
    private HttpJerryResponse httpJerryResponse;
    //
    private ChannelHandlerContext context;

    private Map<Object, Object> param = new ConcurrentHashMap<>();

    public Map<Object, Object> getParam() {
        return param;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public HttpVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(HttpVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpJerryRequest getHttpJerryRequest() {
        return httpJerryRequest;
    }

    public void setHttpJerryRequest(HttpJerryRequest httpJerryRequest) {
        this.httpJerryRequest = httpJerryRequest;
    }

    public HttpJerryResponse getHttpJerryResponse() {
        return httpJerryResponse;
    }

    public void setHttpJerryResponse(HttpJerryResponse httpJerryResponse) {
        this.httpJerryResponse = httpJerryResponse;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }
}
