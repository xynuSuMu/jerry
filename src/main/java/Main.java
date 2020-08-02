
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


    public static void main(String[] args) throws Exception {
        new Main().init();
    }

    public void init() throws Exception {
        String pkg = "";
//        String url = this.getClass().getResource("/").getPath();
        String url = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        pkg = pkg.replace(".", File.separator);
//        System.out.println("pkg:" + pkg);
//        System.out.println("扫描URL:" + url);
//        System.out.println("扫描URL:" + this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
//        System.out.println("扫描URL:" + this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        //扫描
        ComponentScan componentScan = new ComponentScan(url, pkg);
        componentScan.scanComponent(url, pkg);
        //启动服务
        new JerryServer().start(8088);
    }


    //首先判断webApp下有几个jar包，其次分配类加载器加载，根据URL和类加载器找到响应的请求方法，顶层核心类不能打破双亲委派
}

//https://blog.csdn.net/ystyaoshengting/article/details/50698865

//关于TomCat https://blog.csdn.net/varyall/article/details/81610620