package webapp.app1;

import annotation.*;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.http.JerryHttpServletRequest;
import server.http.JerryHttpServletResponse;
import web.fileupload.JerryMultipartFile;

import java.io.*;


/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 16:20
 */
@JerryController
@JerryRequestMapping()
public class Test {

    @JerryAutowired()
    private TestServiceInter testService;

    @JerryAutowired(name = "test")
    private TestServiceInter testService2;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //测试文件上传，并不使用Param注解
    @JerryRequestMapping(value = "/test/upload", method = RequestMethod.POST)
    public String upload(JerryMultipartFile multipartFile1, JerryMultipartFile multipartFile2, String url) {
        String resource = "/Users/chenlong/Documents/xcx/dream/jerry/src/main/resources/";
        try {
            if (multipartFile1 != null)
                writeToLocal(resource + "/" + multipartFile1.getFileName(), multipartFile1.getInputStream());
            if (multipartFile2 != null)
                writeToLocal(resource + "/" + multipartFile2.getFileName(), multipartFile2.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("multipartFile1+" + multipartFile1.getFileName() + "multipartFile2" + multipartFile2.getFileName() + "我是测试数据，😄哈哈" + url);
        return "index.html";
    }

    //测试文件下载
    @JerryRequestMapping(value = "/test/download", method = RequestMethod.GET)
    public String download(JerryHttpServletRequest request, JerryHttpServletResponse response) throws IOException {
        System.out.println(request.getUri());
        File file = new File("/Users/chenlong/Documents/xcx/dream/jerry/src/main/resources/doc/content/4_Using/1.md");

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, fileLength);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/octet-stream");
        response.headers().add("Content-disposition", String.format("attachment; filename=\"%s\"", file.getName()));
        ChannelFuture sendFileFuture = response.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), null);
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
//                log.info("file {} transfer complete.", file.getName());
                raf.close();
            }

            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                                            long progress, long total) throws Exception {
                if (total < 0) {
//                    log.warn("file {} transfer progress: {}", file.getName(), progress);
                } else {
//                    log.debug("file {} transfer progress: {}/{}", file.getName(), progress, total);
                }
            }
        });
        response.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
//        response.sendRedirect("http://www.study-java.cn");
        return "index.html";
    }

    //测试重定向
    @JerryRequestMapping(value = "/test/send", method = RequestMethod.GET)
    public String send(JerryHttpServletResponse response, @Param(value = "url") String url) throws IOException {
        response.sendRedirect(url);
        return "";
    }

    //测试使用对象
    @JerryRequestMapping(value = "/test/sys?", method = RequestMethod.POST)
    public String sys2(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Integer x) {
        logger.info("sys?我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));

        return "index.html";
    }

    @JerryRequestMapping(value = "/test/sys*", method = RequestMethod.POST)
    public String sys3(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Double x) {
        logger.info("sys*我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));

        return "index.html";
    }

    @JerryRequestMapping(value = "/sys2", method = RequestMethod.POST)
    public String sys4(@Param(value = "code") String code, @Param(value = "email") String email, @Param(value = "JSON") TestView testView, @Param(value = "x") Integer x) {
        logger.info("sys2我是测试数据，😄哈哈2、" + x + "->" + code + "->" + email + "->" + JSONObject.toJSONString(testView));

        return "testMapper.xml";
    }

    public static void writeToLocal(String destination, InputStream input)
            throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        File file = new File(destination);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        input.close();
        downloadFile.close();

    }
}


