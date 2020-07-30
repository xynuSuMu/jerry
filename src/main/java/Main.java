
import scan.ComponentScan;
import server.JerryServer;

import java.io.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:27
 * @desc 程序入口
 */
public class Main {

    private static String url = "/Users/chenlong/Documents/xcx/dream/jerry/src/main/java/webapp";

    public static void main(String[] args) throws Exception {
        String pkg = "";
        String url = Main.class.getResource("/").getPath();
        pkg = pkg.replace(".", File.separator);
        //扫描
        ComponentScan componentScan = new ComponentScan(url, pkg);
        componentScan.scanComponent(url, pkg);
        //启动服务
        new JerryServer().start(8088);
    }

    //首先判断webApp下有几个jar包，其次分配类加载器加载，根据URL和类加载器找到响应的请求方法，顶层核心类不能打破双亲委派
}
