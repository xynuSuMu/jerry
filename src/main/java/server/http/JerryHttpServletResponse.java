package server.http;

import server.servlet.JerryServletResponse;

import java.io.IOException;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-20 17:56
 */
public interface JerryHttpServletResponse extends JerryServletResponse {

    void sendError(String code, String msg) throws IOException;

    void sendRedirect(String url) throws IOException;
}
