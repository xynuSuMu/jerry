package web;

import context.JerryContext;
import handler.JerryControllerHandlerMethod;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.*;
import server.servlet.Servlet;
import web.interceptor.Chain;
import web.resource.ResourceHandlerRegistry;
import web.support.InterceptorSupport;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 10:38
 */
public class WebAppManager implements Servlet {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static JerryContext jerryContext = JerryContext.getInstance();

    public void service(JerryHttpServletRequest request, JerryHttpServletResponse response) throws IOException {
        String url = request.getUri();
        //请求前的拦截
        boolean next = Chain.chain(InterceptorSupport.getInstance().getRegistry().getInterceptors(),
                request,
                response,
                null);
        if (!next)
            return;
        JerryControllerHandlerMethod jerryControllerHandlerMethod = jerryContext.getRequestMethod(url);
        if (jerryControllerHandlerMethod == null) {
            //非接口，判断是否为外部资源
            ResourceHandlerRegistry resourceHandlerRegistry = InterceptorSupport.getInstance().getResource();
            boolean temp = resourceHandlerRegistry.isResource(url);
            if (temp) {//是外部资源
                File resource = resourceHandlerRegistry.getTempResource(url);
                RandomAccessFile file = new RandomAccessFile(resource, "r");
                String contentType = resourceHandlerRegistry.getContentType(resource);
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.setContentType(contentType);

                if (request.isSSL())
                    response.writeAndFlush(new ChunkedFile(file));
                else
                    response.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
                response.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                file.close();
                resource.delete();
            } else {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
            }
        } else {
            jerryControllerHandlerMethod.handlerRequestMethod(request, response);
        }
    }
}
