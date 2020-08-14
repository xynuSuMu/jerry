package server.modal;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.File;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-30 09:15
 * @date 响应体
 */
public class HttpJerryResponse {
    //响应状态
    private HttpResponseStatus responseStatus;
    //
    private File file;
    //响应体
    private Object o;
    //响应头
    private String CONTENT_TYPE;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

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

    public String getCONTENT_TYPE() {
        if (CONTENT_TYPE == null) {
            return "text/json; charset=UTF-8";
        }
        return CONTENT_TYPE;
    }

    public void setCONTENT_TYPE(String CONTENT_TYPE) {
        this.CONTENT_TYPE = CONTENT_TYPE;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
