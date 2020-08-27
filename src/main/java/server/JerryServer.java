package server;

import context.Resource;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.ibatis.io.Resources;
import server.annotation.OpenSSL;
import server.http.*;
import server.servlet.Servlet;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.KeyStore;
import java.util.PropertyResourceBundle;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:45
 * @desc 支持Get, Post请求
 */
public class JerryServer {


    private final String SERVLET = "server.servlet.Servlet";

    private final String PORT = "port";

    private final String MAXSIZE = "max.size";

    private final int defaultSize = 1024;

    private boolean isSSL = false;

    public void start(Class<?> clazz) throws Exception {
        //配置文件
        PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(Resource.getJerryCfg());
        //获取端口
        int port = Integer.parseInt(propertyResourceBundle.getString(PORT));
        //Content_length
        String maxSize = propertyResourceBundle.getString(MAXSIZE);
        int size = defaultSize;
        if (maxSize.endsWith("M")) {
            size = Integer.parseInt(maxSize.replaceAll("M", "")) * defaultSize * defaultSize;
        } else if (maxSize.endsWith("KB")) {
            size = Integer.parseInt(maxSize.replaceAll("KB", "")) * defaultSize;
        }
        //事件驱动
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        //
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            int finalSize = size;
            bootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //开启了SSL
                            pipeline.addLast("streamer", new ChunkedWriteHandler());
                            if (clazz.getAnnotation(OpenSSL.class) != null) {
                                isSSL = true;
                                SSLContext sslContext = new SSLContextFactory().getSslContext();
                                SSLEngine sslEngine = sslContext.createSSLEngine();
                                sslEngine.setUseClientMode(false);
                                sslEngine.setNeedClientAuth(false);
                                pipeline.addLast("ssl", new SslHandler(sslEngine));
                            }
                            pipeline.addLast(new HttpServerCodec());// http 编解码
                            pipeline.addLast("httpAggregator", new HttpObjectAggregator(finalSize)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
                            pipeline.addLast(new HttpHandler());// 请求处理器
                        }
                    });

            //绑定
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            //等待服务关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {

        private HttpRequest request;


        @Override
        protected void messageReceived(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {
            request = (HttpRequest) httpObject;
            JerryHttpServletRequest httpServletRequest = new GenericJerryHttpServletRequest(httpObject, isSSL);
            JerryHttpServletResponse httpServletResponse =
                    new JerryHttpResponse(ctx, request.getProtocolVersion(), HttpResponseStatus.OK);
            //
            ctx.write(httpServletResponse);
            //读取配置
            PropertyResourceBundle propertyResourceBundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle("jerry");
            String pkg = propertyResourceBundle.getString(SERVLET);
            Class clazz = Class.forName(pkg);
            Servlet servlet = (Servlet) clazz.newInstance();
            servlet.service(httpServletRequest, httpServletResponse);
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private class SSLContextFactory {

        public SSLContext getSslContext() throws Exception {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(Resources.getResourceAsStream("studyjava.jks"), "123456".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "654321".toCharArray());
            sslContext.init(kmf.getKeyManagers(), null, null);
            return sslContext;
        }
    }

}
