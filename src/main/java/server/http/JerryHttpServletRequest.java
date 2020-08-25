package server.http;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import server.servlet.JerryServletRequest;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 14:24
 */
public interface JerryHttpServletRequest extends JerryServletRequest {
    HttpMethod getMethod();

    HttpHeaders headers();

    Boolean isSSL();
}
