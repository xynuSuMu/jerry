package server;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:45
 * @desc 支持Get, Post请求
 */
public class JerryServer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void start(int port) {
        //事件驱动
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
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

    private class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
            String url = fullHttpRequest.getUri();
            if (fullHttpRequest.getMethod() == HttpMethod.GET) {
                QueryStringDecoder decoder = new QueryStringDecoder(fullHttpRequest.getUri());
                decoder.parameters().entrySet().forEach(entry -> {
                    logger.info("请求Key:{},Value:{}", entry.getKey(), entry.getValue().get(0));
                });
                url = url.split("\\?")[0];
            } else if (fullHttpRequest.getMethod() == HttpMethod.POST) {
                ByteBuf jsonBuf = fullHttpRequest.content();
                String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
                logger.info("消息体,{}", jsonStr);
            } else {
                logger.info("暂不支持{}请求", fullHttpRequest.getMethod());
                sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            }
            logger.info("请求URL,{}", url);
            searchApplication(ctx, url);
        }

        private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                    Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        private void searchApplication(ChannelHandlerContext ctx, String url) {
            String res = "相应" + url;
            //do-sometiong
//            int status = 200;
            HttpResponseStatus status = HttpResponseStatus.OK;
            JSONObject responseJson = new JSONObject();
            responseJson.put("code", status.code());
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                    Unpooled.copiedBuffer(responseJson.toString(), CharsetUtil.UTF_8));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/json; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
