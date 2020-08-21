package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.*;
import server.servlet.Servlet;


import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.PropertyResourceBundle;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:45
 * @desc 支持Get, Post请求
 */
public class JerryServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String SERVLET = "server.servlet.Servlet";

    public void start(int port) throws Exception {
        //事件驱动
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        //SSL证书
//        KeyStore ks = KeyStore.getInstance("JKS");
//        InputStream ksInputStream = new FileInputStream("/Users/chenlong/Documents/xcx/dream/jerry/src/main/resources/studyjava.jks");
//        ks.load(ksInputStream, "123456".toCharArray());
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//        kmf.init(ks, "654321".toCharArray());
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(kmf.getKeyManagers(), null, null);
//        SSLEngine sslEngine = sslContext.createSSLEngine();
//        sslEngine.setUseClientMode(false); //服务器端模式
//        sslEngine.setNeedClientAuth(false); //不需要验证客户端
        //
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new HttpServerCodec());// http 编解码
                            pipeline.addLast("httpAggregator", new HttpObjectAggregator(512 * 1024)); // http 消息聚合器                                                                     512*1024为接收的最大contentlength
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpHandler());// 请求处理器

//                            pipeline.addLast("ssl", new SslHandler(sslEngine));

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

    private class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
            JerryHttpServletRequest httpServletRequest = new GenericJerryHttpServletRequest(fullHttpRequest);
            JerryHttpServletResponse httpServletResponse =
                    new JerryHttpResponse(ctx, fullHttpRequest.getProtocolVersion(), HttpResponseStatus.OK);

            ChannelFuture channelFuture = ctx.write(httpServletResponse);
            //读取配置
            PropertyResourceBundle propertyResourceBundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle("jerry");
            String pkg = propertyResourceBundle.getString(SERVLET);
            Class clazz = Class.forName(pkg);
            Servlet servlet = (Servlet) clazz.newInstance();
            servlet.service(httpServletRequest, httpServletResponse);
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


}
