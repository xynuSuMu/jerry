package webapp.app2.api;

import annotation.JerryRequestMapping;
import annotation.JerryRestController;
import annotation.RequestMethod;
import context.Resource;
import webapp.app2.obj.DirView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public List<DirView> getDir() throws IOException {
        List<DirView> res = new ArrayList<>();
        //获取
        String path = Resource.getJerryCfg(LISTENER);
        //目录
        String dirPath = path + "/directory";
        //
        File file = new File(dirPath);
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                if (file1.isDirectory()) {
                    DirView dirView = new DirView(file1.getName());
                    res.add(dirView);
                }
            }
        }
        return res;
    }
}
