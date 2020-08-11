package server.modal;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-30 09:06
 * @desc 封装请求
 */
public class HttpJerryRequest {

    private String url;

    private HttpHeaders httpHeaders;

    private Map<String, Object> params;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
