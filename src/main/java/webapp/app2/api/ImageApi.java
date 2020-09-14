package webapp.app2.api;

import annotation.JerryAutowired;
import annotation.JerryRequestMapping;
import annotation.JerryRestController;
import annotation.RequestMethod;
import context.Resource;
import web.fileupload.JerryMultipartFile;
import webapp.app1.mapper.UserMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-09-14 09:34
 */
@JerryRestController
@JerryRequestMapping("image")
public class ImageApi {

    @JerryAutowired
    UserMapper userMapper;

    @JerryRequestMapping(value = "/createImage", method = RequestMethod.POST)
    public String createImage(JerryMultipartFile image, String token) throws IOException {
        String realtoken = userMapper.getUUID();
        if (!realtoken.equals(token)) {
            return "Token过期";
        }
        //获取
        String parentPath = Resource.getJerryCfg("path");
        String name = System.currentTimeMillis() + image.getFileName();
        //
        String path = parentPath + "/image/" + name;


        writeToLocal(path, image.getInputStream());
        return "![image](http://www.study-java.cn/v2/image/" + name + ")";
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
