import annotation.JerryController;
import annotation.JerryRequestMapping;
import context.JerryContext;

import exception.JerryException;
import handler.JerryHandlerMethod;
import server.JerryServer;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:27
 * @desc 程序入口
 */
public class Main {
    private static String url;
    private static JerryContext jerryContext = JerryContext.getInstance();

    public static void main(String[] args) throws Exception {
        String pkg = "";
        url = Main.class.getResource("/").getPath();
        pkg = pkg.replace(".", File.separator);
        //Controller
        scanController(url, pkg);
        //启动服务
        new JerryServer().start(8088);
    }

    public static void scanController(String url, String pkg) throws Exception {
        //然后把classpath和basePack合并
        String searchPath = url + pkg;

        dfs(new File(searchPath));

    }

    public static void dfs(File file) throws Exception {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                dfs(f1);
            }
        } else {
            //判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件我们就放入我们的集合中。
                String pkg = file.getPath()
                        .replace(url, "")
                        .replace("/", ".");
                pkg = pkg.substring(0, pkg.length() - 6);
                Class<?> clazz = Class.forName(pkg);
                JerryController jerryController = clazz.getAnnotation(JerryController.class);
                if (jerryController != null) {
                    //方法路径
                    String requestMethodUrl = "";
                    //是否有RequestMapping注解
                    JerryRequestMapping jerryRequestMapping;
                    if ((jerryRequestMapping = clazz.getAnnotation(JerryRequestMapping.class)) != null) {
                        requestMethodUrl += jerryRequestMapping.value();
                    }
                    //获取该类中RequestMapping注解
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
//                        for (Parameter p : method.getParameters()) {
//                            System.out.println(p.getName());
//                        }
                        if ((jerryRequestMapping = method.getAnnotation(JerryRequestMapping.class)) != null) {
                            JerryHandlerMethod jerryHandlerMethod =
                                    new JerryHandlerMethod(method,
                                            clazz.newInstance(),
                                            method.getParameters(),
                                            method.getReturnType(),
                                            jerryRequestMapping.method());
                            String requestMapping = requestMethodUrl + jerryRequestMapping.value();
                            if (jerryContext.getMethod(requestMapping) != null) {
                                throw new JerryException("requestMapping重复:" + requestMapping);
                            } else {
                                jerryContext.setControllerMethod(requestMapping,
                                        jerryHandlerMethod);
                            }
                        }
                    }
                }

            }
        }
    }
    //https://github.com/daleyzou/ScanAnnotations.git
}
