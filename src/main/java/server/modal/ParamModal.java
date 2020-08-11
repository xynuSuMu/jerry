package server.modal;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 15:57
 */
public class ParamModal {

    private HttpMethod httpMethod;
    private String url;
    private HttpJerryRequest httpJerryRequest;

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

    public void setParam(Map<Object, Object> param) {
        this.param = param;
    }
}
