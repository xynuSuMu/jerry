import server.JerryServer;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:27
 * @desc 程序入口
 */
public class Main {
    public static void main(String[] args) {
        //启动服务
        int port = 8088;
        new JerryServer().bind(port);
        //加载Web项目
    }
}
