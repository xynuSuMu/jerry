package server.http;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.util.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 14:47
 */
public class GenericJerryHttpServletRequest implements JerryHttpServletRequest {

    private final FullHttpRequest fullHttpRequest;

    private Map<String, String> params = new HashMap<>();
    private List<String> list = new ArrayList<>();
    private ByteBuf byteBuf;
    private String contentType;
    private Boolean isSSl;

    public GenericJerryHttpServletRequest(FullHttpRequest fullHttpRequest,boolean isSSl) {
        this.fullHttpRequest = fullHttpRequest;
        QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.getUri());
        decoder.parameters().entrySet().forEach(entry -> {
            list.add(entry.getKey());
            params.put(entry.getKey(), entry.getValue().get(0));
//            System.out.println(entry.getKey() + "-->" + entry.getValue().get(0));
        });
        byteBuf = fullHttpRequest.content();
        this.isSSl =isSSl;
//        System.out.println("-----header-----");
//        for (Map.Entry<String, String> entry : fullHttpRequest.headers().entries()) {
//            System.out.println(entry.getKey() + "-->" + entry.getValue());
//        }
        contentType = fullHttpRequest.headers().get("Content-Type");
    }

    @Override
    public HttpMethod getMethod() {
        return fullHttpRequest.getMethod();
    }

    @Override
    public HttpHeaders headers() {
        return fullHttpRequest.headers();
    }

    @Override
    public Boolean isSSL() {
        return isSSl;
    }

    @Override
    public String getProtocol() {
        return fullHttpRequest.getProtocolVersion().protocolName();
    }

    @Override
    public String getHost() {
        return fullHttpRequest.getUri();
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public String getUri() {
        String url = fullHttpRequest.getUri();
        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }
        return url;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public String getParameter(String paramName) {
        return params.get(paramName);
    }

    @Override
    public List<String> getParameterNames() {
        return list;
    }

    @Override
    public ByteBuf getByteBuf() {
        return byteBuf;
    }
}
