package web.resource;

import context.Resource;
import handler.JerryControllerHandlerMethod;
import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CopyAntPathMatcher;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-18 11:16
 */
public class ResourceHandlerRegistry {

    private static Logger logger = LoggerFactory.getLogger(ResourceHandlerRegistry.class);

    //文件
    private Map<String, File> tempFile = new ConcurrentHashMap<>();


    private List<String> path = new CopyOnWriteArrayList<>();

    private final String PATH = "path";

    private final String PATH_404 = "html/404.html";

    public ResourceHandlerRegistry addResource(String path) {
        this.path.add(path);
        return this;
    }

    public List<String> getPath() {
        return path;
    }

    public boolean isResource(String url) {

        CopyAntPathMatcher copyAntPathMatcher = new CopyAntPathMatcher();
        List<String> list = new ArrayList<>();
        for (String temp : path) {
            if (copyAntPathMatcher.match(temp, url)) {
                list.add(temp);
                break;
            }
        }
        if (list.isEmpty())
            return false;

        return true;
    }

    public void setTempFile(String key, File o) {
        if (key.startsWith("/"))
            key = key.substring(1);
//        tempFile.put(key, o);
    }

    public void setTempFile(String key) throws IOException {
        if (key.startsWith("/"))
            key = key.substring(1);
        InputStream inputStream = null;
        try {
            String path = Resource.getJerryCfg(PATH);
            inputStream = new FileInputStream(path + "/" + key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream == null) {
            //
            inputStream = Resources.getResourceAsStream(PATH_404);
        }

        File temp = File.createTempFile(key, Resource.getJerryCfg("md.suffix"));
        getTempResource(inputStream, temp);
//        tempFile.put(key, temp);
    }

    public void getTempResource(InputStream inputStream, File temp) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(inputStream);
            bos = new BufferedOutputStream(new FileOutputStream(temp));
            int len = 0;
            byte[] buf = new byte[10 * 1024];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) bos.close();
                if (bis != null) bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getTempResource(String url) throws IOException {

        String resource = url;
        InputStream inputStream = null;
        while (resource.startsWith("/"))
            resource = resource.substring(1);
        logger.info("请求资源,{}", url);
//        if (tempFile.containsKey(resource))
//            return tempFile.get(resource);
        resource = URLDecoder.decode(resource, "UTF-8");
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            String path = Resource.getJerryCfg(PATH);

            String realPath;
            if (path.endsWith("/") || resource.startsWith("/")) {
                realPath = path + resource;
            } else if (path.endsWith("/") && resource.startsWith("/")) {
                realPath = path + resource.substring(1);
            } else {
                realPath = path + "/" + resource;
            }
            logger.info(realPath + ",资源不存在，寻找外部资源");
            if (path != null && path != "") {
                try {
                    inputStream = new FileInputStream(realPath);
                    logger.info("资源存在");
                } catch (IOException e1) {
                    logger.info("资源不存在，寻找外部资源失败，返回404");
                }
            }
        }
        if (inputStream == null) {
            //
            inputStream = Resources.getResourceAsStream(PATH_404);
        }

        File temp = File.createTempFile(resource, resource.substring(resource.lastIndexOf(".")));
        getTempResource(inputStream, temp);
//        tempFile.put(resource, temp);
        return temp;
    }

    public String getContentType(File file) {
        String contentType;
        if (file.getName().endsWith(".md") || file.getName().endsWith(".html")) {
            contentType = "text/html; charset=UTF-8";
        } else if (file.getName().endsWith(".css")) {
            contentType = "text/css; charset=UTF-8";
        } else if (file.getName().endsWith(".js")) {
            contentType = "application/javascript;charset=utf-8";
        } else if (file.getName().endsWith(".png")) {
            contentType = "image/png";
        } else {
            contentType = "text/json; charset=UTF-8";
        }
        return contentType;
    }
}
