
import annotation.MapperScan;
import scan.ComponentScan;
import server.JerryServer;

import java.io.*;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:27
 * @desc 程序入口
 */
@MapperScan(pkg = {"webapp/app1/mapper"})
public class Main {


    public static void main(String[] args) throws Exception {
        new Main().init();
    }

    public void init() throws Exception {
        String pkg = "";
        String url = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        pkg = pkg.replace(".", File.separator);
        ComponentScan componentScan = new ComponentScan(url);
        //扫描Mapper
        MapperScan mapperScan =this.getClass().getAnnotation(MapperScan.class);
        if (mapperScan != null) {
            componentScan.scanMapper(mapperScan.pkg());
        }
        //扫描组件
        componentScan.scanComponent(pkg);
        //启动服务
        new JerryServer().start(8088);
    }


}
//首先判断webApp下有几个jar包，其次分配类加载器加载，根据URL和类加载器找到响应的请求方法，顶层核心类不能打破双亲委派


//String url = this.getClass().getResource("/").getPath();¬

//https://blog.csdn.net/ystyaoshengting/article/details/50698865

//关于TomCat https://blog.csdn.net/varyall/article/details/81610620