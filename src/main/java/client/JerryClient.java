package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.JerryServer;

import java.util.Date;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 19:12
 */
public class JerryClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void connect(String host, int port) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new JerryClientHandlerAdapter());
                        }
                    });

            //绑定
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            //等待服务关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private class JerryClientHandlerAdapter extends ChannelHandlerAdapter {

        private final ByteBuf byteBuf;

        public JerryClientHandlerAdapter() {
            byte[] req = "query".getBytes();
            byteBuf = Unpooled.buffer(req.length);
            byteBuf.writeBytes(req);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String body = new String(bytes, "UTF-8");
            logger.info("客户端接受,{}",body);
            System.out.println(body);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(byteBuf);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.info(cause.getMessage());
            ctx.close();
        }
    }

    public static void main(String[] args) {
        new JerryClient().connect("127.0.0.1", 8088);
    }
}
