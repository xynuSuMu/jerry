package server.servlet;

import server.http.JerryHttpServletRequest;
import server.http.JerryHttpServletResponse;

import java.io.IOException;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-21 15:15
 */
public interface Servlet {
    void service(JerryHttpServletRequest request, JerryHttpServletResponse response) throws IOException;
}
