package webapp.app2.api;

import annotation.JerryRequestMapping;
import annotation.JerryRestController;
import annotation.RequestMethod;
import context.Resource;

import java.io.IOException;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-09-01 17:29
 */
@JerryRestController
@JerryRequestMapping("article")
public class ArticleApi {


    private final String LISTENER = "listener.path";

    @JerryRequestMapping(value = "/getDir", method = RequestMethod.GET)
    public String getDir() throws IOException {
        //获取
        String path = Resource.getJerryCfg(LISTENER);
        //目录
        String dirPath = path + "/directory";
        return "dir";
    }
}
