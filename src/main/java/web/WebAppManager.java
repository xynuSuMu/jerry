package web;

import context.JerryContext;
import handler.JerryControllerHandlerMethod;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.modal.HttpJerryRequest;
import server.modal.HttpJerryResponse;
import server.modal.ParamModal;
import web.interceptor.Chain;
import web.support.InterceptorSupport;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-23 10:38
 */
public class WebAppManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static JerryContext jerryContext = JerryContext.getInstance();

    public void response(ParamModal modal) throws Exception {
        HttpJerryRequest httpJerryRequest = modal.getHttpJerryRequest();
        HttpMethod httpMethod = modal.getHttpMethod();
        HttpJerryResponse httpJerryResponse = modal.getHttpJerryResponse();
        Map<Object, Object> param = modal.getParam();
        ChannelHandlerContext context = modal.getContext();
        String url = modal.getUrl();
        //拦截器链
        Chain.chain(
                InterceptorSupport.getInstance().getRegistry().getInterceptors(),
                modal.getHttpJerryRequest(),
                httpJerryResponse,
                null);
        //响应
        HttpResponse response;
        if (httpJerryResponse.getResponseStatus() == HttpResponseStatus.FORBIDDEN) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpJerryResponse.getResponseStatus(),
                    Unpooled.copiedBuffer("Fail:" + httpJerryResponse.getResponseStatus().code(), CharsetUtil.UTF_8));
            context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        //寻找RequestMapping对应的控制层方法
        JerryControllerHandlerMethod jerryControllerHandlerMethod = jerryContext.getRequestMethod(url);
        if (jerryControllerHandlerMethod == null) {
            //TODO:判断资源路径中是否存在

            boolean temp = InterceptorSupport.getInstance().getResource().isResource(url);
            if (temp) {
                System.out.println("资源请求" + url);
                httpJerryResponse.setFile(InterceptorSupport.getInstance().getResource().getResource(url));
            } else {
                httpJerryResponse.setResponseStatus(HttpResponseStatus.NOT_FOUND);
            }
        } else {
            jerryControllerHandlerMethod.handlerRequestMethod(httpMethod, httpJerryRequest, httpJerryResponse, param);
        }
        if (httpJerryResponse.getFile() != null) {
            File html = httpJerryResponse.getFile();//new File(path);
//            FileInputStream file = new FileInputStream(html);
            RandomAccessFile file = new RandomAccessFile(html, "r");
            response = new DefaultHttpResponse(modal.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, httpJerryResponse.getCONTENT_TYPE());
            boolean keepAlive = modal.isKeepAlive();
            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }
            context.write(response);
            if (context.pipeline().get(SslHandler.class) == null) {
                context.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                context.write(new ChunkedNioFile(file.getChannel()));
            }
            // 写入文件尾部
            ChannelFuture future = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            file.close();
            html.delete();
        } else if (httpJerryResponse.getResponseStatus() != HttpResponseStatus.OK) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpJerryResponse.getResponseStatus(),
                    Unpooled.copiedBuffer("Fail:" + httpJerryResponse.getResponseStatus().code(), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, httpJerryResponse.getCONTENT_TYPE());
            context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpJerryResponse.getResponseStatus(),
                    Unpooled.copiedBuffer(httpJerryResponse.getO().toString(), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, httpJerryResponse.getCONTENT_TYPE());
            context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

    }

}
