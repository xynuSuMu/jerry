package webapp.app4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-25 17:24
 */
public class UploadServer {


    static final int PORT = 80;


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
                    .childHandler(new HttpsServerInitializer());
            Channel channel = serverBootstrap.bind(PORT).sync().channel();
            System.out.println("HttpServer listening on port " + PORT);
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    static class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

        private HttpRequest request;

        private static final String uploadUrl = "/up";

        private static final String fromFileUrl = "/post_multipart";

        private static final HttpDataFactory factory =
                new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if size exceed

        private HttpPostRequestDecoder decoder;

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpRequest) {

                this.request = (HttpRequest) msg;

                URI uri = new URI(request.getUri());

                System.out.println(uri);

                urlRoute(ctx, uri.getPath());

            }

            if (decoder != null) {

                if (msg instanceof HttpContent) {

                    // 接收一个新的请求体
                    decoder.offer((HttpContent) msg);
                    // 将内存中的数据序列化本地
                    readHttpDataChunkByChunk();

                }

                if (msg instanceof LastHttpContent) {

                    System.out.println("LastHttpContent");

                    reset();

                    writeResponse(ctx, "<h1>上传成功</h1>");

                }
            }
        }

        // url路由
        private void urlRoute(ChannelHandlerContext ctx, String uri) {

            StringBuilder urlResponse = new StringBuilder();

            // 访问文件上传页面
            if (uri.startsWith(uploadUrl)) {

                urlResponse.append(getUploadResponse());

            } else if (uri.startsWith(fromFileUrl)) {

                decoder = new HttpPostRequestDecoder(factory, request);

                return;

            } else {

                urlResponse.append(getHomeResponse());

            }

            writeResponse(ctx, urlResponse.toString());

        }

        private void writeResponse(ChannelHandlerContext ctx, String context) {

            ByteBuf buf = Unpooled.copiedBuffer(context, CharsetUtil.UTF_8);

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=utf-8");

            //设置短连接 addListener 写完马上关闭连接
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        }

        private String getHomeResponse() {

            return " <h1> welcome home </h1> ";

        }

        private String getUploadResponse() {

            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<form action=\"http://127.0.0.1:8080/post_multipart\" enctype=\"multipart/form-data\" method=\"POST\">\n" +
                    "\n" +
                    "\n" +
                    "    <input type=\"file\" name=" +
                    " " +
                    "" +
                    "\"YOU_KEY\">\n" +
                    "\n" +
                    "    <input type=\"submit\" name=\"send\">\n" +
                    "\n" +
                    "</form>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";

        }


        private void readHttpDataChunkByChunk() throws IOException {

            // while 是为了接受完整数据后处理
            try {
                while (decoder.hasNext()) {

                    // 获得文件数据
                    InterfaceHttpData data = decoder.next();

                    if (data != null) {

                        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {

                            FileUpload fileUpload = (FileUpload) data;

                            if (fileUpload.isCompleted()) {

                                System.out.println(fileUpload.isInMemory());// tells if the file is in Memory
                                String path = "/Users/chenlong/Documents/xcx/dream/jerry/src/main/resources";
                                // or on File
                                fileUpload.renameTo(new File(path + "/" + fileUpload.getFilename())); // enable to move into another
                                // File dest
                                decoder.removeHttpDataFromClean(fileUpload); //remove

                            }


                        }

                    }
                }
            } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
                // end
//                res.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");
            }

        }

        private void reset() {

            request = null;

            // destroy the decoder to release all resources
            decoder.destroy();

            decoder = null;

        }

    }

    static class HttpsServerInitializer extends ChannelInitializer {


        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline()
                    .addLast("streamer", new ChunkedWriteHandler())
                    .addLast("codec", new HttpServerCodec())
//                    .addLast(new HttpObjectAggregator(1024 * 10240))
                    .addLast(new HttpUploadServerHandler());
        }
    }
}
