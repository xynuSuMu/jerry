package webapp.app2.api;

import annotation.JerryAutowired;
import annotation.JerryRequestMapping;
import annotation.JerryRestController;
import annotation.RequestMethod;
import context.Resource;
import webapp.app1.mapper.UserMapper;
import webapp.app2.obj.DirView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-09-01 17:29
 */
@JerryRestController
@JerryRequestMapping("article")
public class ArticleApi {

    @JerryAutowired
    UserMapper userMapper;

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
                getFile(file1, res, "/directory");
            }
        }
        //
        return res;
    }

    @JerryRequestMapping(value = "/getDirContent", method = RequestMethod.POST)
    public String getDirContent(String token, String path, String content) throws IOException {
        String realtoken = userMapper.getUUID();
        if (!realtoken.equals(token)) {
            return "Token过期";
        }
        System.out.println(token);
        System.out.println(path);
        System.out.println(content);
        String dirPath = Resource.getJerryCfg(LISTENER) + path;
        File file = new File(dirPath);
        FileWriter fw = new FileWriter(file, false);//设置成true就是追加
        fw.write(content);
        fw.close();
        return path;
    }

    @JerryRequestMapping(value = "/getContent", method = RequestMethod.GET)
    public List<DirView> getContent() throws IOException {
        List<DirView> res = new ArrayList<>();
        //获取
        String path = Resource.getJerryCfg(LISTENER);
        //目录
        String dirPath = path + "/content";
        //
        File file = new File(dirPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            //文件名称排序
            List fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                }
            });
            //
            for (File file1 : files) {
                getFile(file1, res, "/content");
            }
        }
        //
        return res;
    }

    @JerryRequestMapping(value = "/createArticle", method = RequestMethod.POST)
    public String createArticle(String token, String path, String name) throws IOException {
        String realtoken = userMapper.getUUID();
        if (!realtoken.equals(token)) {
            return "Token过期";
        }
        if (!name.endsWith(".md"))
            return "只能创建md格式文件";
        //获取
        String parentPath = Resource.getJerryCfg(LISTENER);
        //
        path = parentPath + path + "/" + name;

        File file = new File(path);
        if (file.exists()) {
            //
            return "文件存在";
        }
        file.createNewFile();
        return "创建成功";
    }

    //递归
    public void getFile(File file, List<DirView> res, String path) {
        if (file.isDirectory()) {
            DirView dirView = new DirView(file.getName(), true);
            dirView.setPath(path + "/" + file.getName());
            List<DirView> sub = new ArrayList<>();
            dirView.setList(sub);
            res.add(dirView);
            for (File file1 : file.listFiles()) {
                getFile(file1, sub, path + "/" + file.getName());
            }
        } else {
            DirView dirView = new DirView(file.getName(), false);
            dirView.setPath(path + "/" + file.getName());
            res.add(dirView);
        }
    }
}
