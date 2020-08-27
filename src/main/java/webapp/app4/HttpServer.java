package webapp.app4;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import server.http.JerryHttpResponse;
import server.http.JerryHttpServletResponse;
import webapp.app1.TestView;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-21 18:10
 */
public class HttpServer {
    static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        start();
    }

    public static void start() throws Exception {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(10);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpsServerInitializer(SSLContextFactory.getSslContext()));
            Channel channel = serverBootstrap.bind(PORT).sync().channel();
            System.out.println("HttpServer listening on port " + PORT);
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    static class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        private static final String FAVICON_ICO = "/favicon.ico";

        private HttpHeaders headers;
        private HttpRequest request;
        private FullHttpRequest fullRequest;


        @Override
        protected void messageReceived(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
            if (msg instanceof HttpRequest) {
                request = (HttpRequest) msg;
                headers = request.headers();
                String uri = request.getUri();
                System.out.println("request uri:" + uri);
                if (FAVICON_ICO.equals(uri)) return;
                HttpMethod method = request.getMethod();
                if (HttpMethod.GET.equals(method)) {
                    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, CharsetUtil.UTF_8);
                    Map<String, List<String>> parameters = queryStringDecoder.parameters();
                    System.out.println("requestParam----> {}" + parameters);
                } else if (HttpMethod.POST.equals(method)) {
                    fullRequest = (FullHttpRequest) msg;
                    dealWithContentType();
                }

                TestView student = new TestView();
                student.setCode("12");
                student.setPhone(12);
                student.setPhone(12);
                String resp = JSONObject.toJSONString(student);
                TestView studen2t = new TestView();
                studen2t.setCode("13");
                studen2t.setPhone(13);
                studen2t.setPhone(13);
                String res2p = JSONObject.toJSONString(studen2t);

//                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                JerryHttpServletResponse response =
                        new JerryHttpResponse(channelHandlerContext, request.getProtocolVersion(), HttpResponseStatus.OK);

//                ChannelPipeline pipeline = channelHandlerContext.pipeline(); //1

//                ((JerryHttpResponse) response).setContent( Unpooled.wrappedBuffer(res2p.getBytes()));
//                response.headers().set("Content-Type", "application/text");
//                System.out.println(response.content());
//                response.headers().set("Content-Length", response.content().readableBytes());
                response.sendRedirect("http://www.baidu.com");
                ChannelFuture channelFuture =    channelHandlerContext.write(response);
//                channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty in Action", CharsetUtil.UTF_8));  //2

//                boolean keepAlive = HttpHeaders.isKeepAlive(request);
//                if (!keepAlive) {
                    channelFuture.addListener(ChannelFutureListener.CLOSE);
//                } else {
//                    response.headers().set("Connection", "true");
////                    channelHandlerContext.write(response);
//
//                }

            } else {
                System.out.println("非法请求");
            }
        }

        private void dealWithContentType() {
            String contentType = headers.get("Content-Type").split(";")[0];
            if (contentType.equals("application/json")) {
                String jsonStr = fullRequest.content().toString(CharsetUtil.UTF_8);
                System.out.println("requestContent--->{}" + jsonStr);
            } else if (contentType.equals("application/x-www-form-urlencoded")) {
                String requestStr = fullRequest.content().toString(CharsetUtil.UTF_8);
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(requestStr, CharsetUtil.UTF_8);
                System.out.println("requestContent--->{}" + queryStringDecoder);
            } else {
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }


    }

    static class HttpsServerInitializer extends ChannelInitializer {

        private SSLContext sslContext;

        public HttpsServerInitializer(SSLContext sslContext) {
            this.sslContext = sslContext;
        }

        @Override
        protected void initChannel(Channel channel) throws Exception {
            ChannelPipeline pipeline = channel.pipeline();
            SSLEngine sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            channel.pipeline().addLast("ssl", new SslHandler(sslEngine))
                    .addLast("codec", new HttpServerCodec())
                    .addLast(new HttpObjectAggregator(1024 * 1024))
                    .addLast(new HttpServerHandler());
        }
    }
}

class SSLContextFactory {

    public static SSLContext getSslContext() throws Exception {
        char[] filePass = "123456".toCharArray();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(Files.newInputStream(Paths.get("/Users/chenlong/Documents/xcx/dream/jerry/src/main/resources/studyjava.jks"), StandardOpenOption.READ), "123456".toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "654321".toCharArray());
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }
}
