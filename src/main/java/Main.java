
import annotation.mapper.MapperScan;
import context.Resource;
import listener.doc.DocListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import scan.ComponentScan;
import server.JerryServer;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:27
 * @desc 程序入口
 */
@MapperScan(pkg = {"webapp/app1/mapper"})
//@OpenSSL
public class Main {

    private final String LISTENER = "listener.path";

    public static void main(String[] args) throws Exception {
        new Main().init();
    }

    public void init() throws Exception {
//        String pkg = "";
        String url = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        pkg = pkg.replace(".", File.separator);
        ComponentScan componentScan = new ComponentScan(url);
        //扫描Mapper
        MapperScan mapperScan = this.getClass().getAnnotation(MapperScan.class);
        String[] mapperPath = {};
        if (mapperScan != null) {
            mapperPath = mapperScan.pkg();
        }
        //扫描组件(包含Mapper、Controller、RestController、Service、Job)
        componentScan.scanComponent(mapperPath);
        //启动文件监听
        DocListener docListener = new DocListener();
        String path = Resource.getJerryCfg(LISTENER);
        FileAlterationObserver observer = new FileAlterationObserver(path);
        observer.addListener(docListener);
        FileAlterationMonitor monitor = new FileAlterationMonitor(10000);
        monitor.addObserver(observer);
        monitor.start();
        //启动服务
        new JerryServer().start(Main.class);

    }


}
