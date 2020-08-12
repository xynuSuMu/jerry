package server.modal;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-30 09:15
 * @date 响应体
 */
public class HttpJerryResponse {
    //响应状态
    private HttpResponseStatus responseStatus;
    //响应体
    private Object o;

    public HttpResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(HttpResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
