import annotation.JerryController;
import context.JerryContext;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import server.JerryServer;
import util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-22 15:27
 * @desc 程序入口
 */
public class Main {
    private static String url;
    private static Map<String, Object> bean = JerryContext.getInstance().getJerryContext();

    public static void main(String[] args) throws IOException {
        String pkg = "";
        url = Main.class.getResource("/").getPath();
        pkg = pkg.replace(".", File.separator);

        scanClass(url, pkg);
        //启动服务
        new JerryServer().start(8088);
    }

    public static void scanClass(String url, String pkg) {
        //然后把classpath和basePack合并
        String searchPath = url + pkg;
        dfs(new File(searchPath));
    }

    public static void dfs(File file) {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                dfs(f1);
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                //如果是class文件我们就放入我们的集合中。
//                System.out.println(file.getPath());
                String pkg = file.getPath()
                        .replace(url, "")
                        .replace("/", ".");
                pkg = pkg.substring(0, pkg.length() - 6);
//                System.out.println(pkg);
                try {
                    Class<?> clazz = Class.forName(pkg);
                    JerryController jerryController = clazz.getAnnotation(JerryController.class);
                    if (jerryController != null) {
                        bean.put(clazz.getName(), clazz.newInstance());
                        System.out.println(clazz.getName());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void scanDir(File cf) throws IOException {
        // 指定要扫描目录
        List<File> classFiles = searchByFileDuff(cf, "class");

        for (File file : classFiles) {
            scanAnnocation(new FileInputStream(file));
//            System.out.println(file.getName());
        }


    }

    //https://github.com/daleyzou/ScanAnnotations.git
    public static List<File> searchByFileDuff(File folder, String duff) {
        List<File> result = new ArrayList<>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = FileUtil.getFile(folder, duff);
        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {
                    // 如果是文件则将文件添加到结果列表中
                    System.out.println(file.getPath());
                    result.add(file);
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    result.addAll(searchByFileDuff(file, duff));
                }
            }
        }
        return result;
    }

    // 扫描所有的注解
    private static void scanAnnocation(InputStream inputStream) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
        ClassFile cls = new ClassFile(dis);

        //获取属性
        List<FieldInfo> fields = cls.getFields();

//        System.out.println("-------------------------------------------------------------");
//        System.out.println("类的名称：" + cls.getName());
        //获取类的Runtime注解
        AnnotationsAttribute attribute = (AnnotationsAttribute) cls.getAttribute(AnnotationsAttribute.visibleTag);
        if (attribute != null) {
            Annotation[] annotations = attribute.getAnnotations();
            for (Annotation annotation : annotations) {
                System.out.println("类的注解名称： " + annotation.getTypeName());
            }
        }

//        for (FieldInfo field : fields) {
//            //获取属性的Runtime注解
//            AnnotationsAttribute attribute1 = (AnnotationsAttribute) field.getAttribute(AnnotationsAttribute.visibleTag);
//            if (attribute1 != null) {
//                Annotation[] annotations = attribute1.getAnnotations();
//                for (Annotation annotation : annotations) {
//                    System.out.println("属性的名称：" + field.getDescriptor());
//                    System.out.println("属性的类型： " + annotation.getTypeName());
//                }
//            }
//        }
    }
}
