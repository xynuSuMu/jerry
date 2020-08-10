package scan;

import annotation.JerryAutowired;
import annotation.JerryController;
import annotation.JerryRequestMapping;
import annotation.JerryService;
import context.JerryContext;
import exception.JerryException;
import handler.JerryHandlerMethod;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import proxy.ServiceProxy;
import proxy.SqlSessionTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-29 10:49
 */
public class ComponentScan {

    private static JerryContext jerryContext = JerryContext.getInstance();

    private final String suffix = ".class";

    private Map<Class<?>, Object> cacheObj = new HashMap<>();

    //
    private List<Class<?>> classes = new CopyOnWriteArrayList<>();
    //接口
    private Map<Class<?>, List<Class<?>>> inter = new ConcurrentHashMap<>();
    //Controller
    private List<Class<?>> cls = new CopyOnWriteArrayList<>();
    //接口
    private List<Entry> entries = new CopyOnWriteArrayList<>();

    private String url;


    public ComponentScan(String url) {
        this.url = url;
    }

    public void scanMapper(String[] pkgs) throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = new SqlSessionTemplate(sqlSessionFactory);
        for (String pkg : pkgs) {
            String searchPath = url + pkg;
            if (searchPath.endsWith("jar")) {//扫描jar包
                findClassJar(searchPath);
            } else {//扫描文件夹
                scanClasses(new File(searchPath));
            }
        }
        for (Class<?> clazz : classes) {
            if (clazz.isInterface()) {
                Object o = sqlSession.getMapper(clazz);
                if (o != null) {
                    String name = clazz.getName();
                    String beanId = name.substring(0, 1).toLowerCase() + name.substring(1);
                    jerryContext.setBean(beanId, o);
                }
            }
        }
        classes.clear();
    }

    public void scanComponent(String pkg) throws Exception {
        //然后把classpath和basePack合并
        String searchPath = url + pkg;
        if (searchPath.endsWith("jar")) {//扫描jar包
            findClassJar(searchPath);
        } else {//扫描文件夹
            scanClasses(new File(searchPath));
        }
        //DI
        for (Class<?> clazz : classes) {
            di(clazz);
        }
        //处理请求
        handlerController();
        //处理接口注入
        handlerInterface();
        //销毁
        classes = null;
        cls = null;
        entries = null;
        cacheObj = null;
    }

    private void findClassJar(final String path) throws Exception {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("未找到策略资源");
        }

        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName();
            if (jarEntryName.endsWith(suffix)) {
                //如果是class文件我们就放入我们的集合中,替换url是获取包名
                String pkg = jarEntryName
                        .replace(url, "")
                        .replace("/", ".");
                pkg = pkg.substring(0, pkg.length() - 6);
                Class<?> clazz = Class.forName(pkg);
                classes.add(clazz);
            }

        }

    }

    private void scanClasses(File file) throws Exception {
        if (file.isDirectory()) {//文件夹
            //文件夹我们就递归
            File[] files = file.listFiles();
            for (File f1 : files) {
                scanClasses(f1);
            }
        } else {
            //判断是否是class文件
            if (file.getName().endsWith(suffix)) {
                //如果是class文件我们就放入我们的集合中,替换url是获取包名
                String pkg = file.getPath()
                        .replace(url, "")
                        .replace("/", ".");
                pkg = pkg.substring(0, pkg.length() - 6);
                Class<?> clazz = Class.forName(pkg);
                classes.add(clazz);
                if (clazz.getAnnotation(JerryService.class) != null) {
                    Class<?>[] classes = clazz.getInterfaces();
                    for (Class<?> c : classes) {
                        if (inter.containsKey(c)) {
                            inter.get(c).add(clazz);
                        } else {
                            List<Class<?>> list = new ArrayList<>();
                            list.add(clazz);
                            inter.put(c, list);
                        }
                    }
                }
            }
        }
    }

    private void di(Class<?> clazz) throws Exception {
        //如果是存在Controller注解-暂存
        JerryController jerryController = clazz.getAnnotation(JerryController.class);
        if (jerryController != null) {
            cls.add(clazz);
        } else {
            handlerService(clazz);
        }

    }

    //处理Service层
    private Object handlerService(Class<?> clazz) throws Exception {
        JerryService jerryService = clazz.getAnnotation(JerryService.class);
        boolean isInterface = clazz.isInterface();
        if (cacheObj.containsKey(clazz)) {
            //
            System.out.println("存在循环依赖" + clazz.getTypeName());
            return cacheObj.get(clazz);
        }
        //判断是否为接口
        if (isInterface) {
            //寻找其实现类
            if (inter.containsKey(clazz)) {
                List<Class<?>> classes = inter.get(clazz);
                //暂时获取第一个
                String name = classes.get(0).getName();
                if (jerryContext.getBean(name) != null) {
                    return jerryContext.getBean(name);
                } else {
                    return handlerService(classes.get(0));
                }
            }
            //不存在实现类
            return null;
        }
        if (jerryService != null) {
            String annotationValue = jerryService.value();
            Object instance;
            Object o = null;
            instance = clazz.newInstance();
            if (instance != null) {
                //使用代理类
                o = Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                        instance.getClass().getInterfaces(),
                        new ServiceProxy(instance));
                //加入缓存
                cacheObj.put(clazz, o);
                //字段注入
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    JerryAutowired jerryAutowired = field.getDeclaredAnnotation(JerryAutowired.class);
                    if (jerryAutowired != null) {
                        String beanName = jerryAutowired.name();
                        String beanId;
                        field.setAccessible(true);
                        Object value = null;
                        //如果指定了注入的beanID
                        if (beanName != null && !"".equals(beanName)) {
                            value = jerryContext.getBean(beanName);
                        }
                        //
                        String name = field.getType().getName();
                        beanId = name.substring(0, 1).toLowerCase() + name.substring(1);
                        //如果未在Autowired指定name,则根据全限定名来找
                        if (value == null) {
                            value = jerryContext.getBean(beanId);
                        }
                        //为null,表明该字段需要注入
                        if (value == null) {
                            value = handlerService(field.getType());
                        }
                        try {
                            field.set(instance, value);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            String beanId;
            String cusBeanId = null;
            String name = clazz.getName();
            //包名+类名，首字母小写
            beanId = name.substring(0, 1).toLowerCase() + name.substring(1);
            //如果编写了Service的beanID
            if (annotationValue != null && !"".equals(annotationValue)) {
                cusBeanId = annotationValue;
            }
            if (jerryContext.getBean(beanId) != null) {
//                throw new JerryException(beanId + "重复");
            } else {
                jerryContext.setBean(beanId, o);
            }

            if (cusBeanId != null && jerryContext.getBean(cusBeanId) != null) {
//                throw new JerryException(cusBeanId + "重复");
            } else if (cusBeanId != null) {
                jerryContext.setBean(cusBeanId, o);
            }
            //移除缓存
            cacheObj.remove(clazz);
            return o;
        }
        return null;
    }

    //处理控制层方法
    private void handlerController() throws Exception {
        for (Class<?> clazz : cls) {
            Object o = clazz.newInstance();
            //为字段赋值
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                handlerDI(field, o);
            }
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
                if ((jerryRequestMapping = method.getAnnotation(JerryRequestMapping.class)) != null) {
                    JerryHandlerMethod jerryHandlerMethod =
                            new JerryHandlerMethod(method,
                                    o,
                                    method.getParameters(),
                                    method.getReturnType(),
                                    jerryRequestMapping.method());
                    String requestMapping = requestMethodUrl + jerryRequestMapping.value();
                    System.out.println(requestMapping);
                    if (jerryContext.getMethod(requestMapping) != null) {
                        throw new JerryException("requestMapping重复:" + requestMapping);
                    } else {
                        System.out.println("注入:" + requestMapping);
                        jerryContext.setControllerMethod(requestMapping,
                                jerryHandlerMethod);
                    }
                }
            }
        }
    }

    //进行DI
    private void handlerDI(Field field, Object o) {
        JerryAutowired jerryAutowired = field.getDeclaredAnnotation(JerryAutowired.class);
        if (jerryAutowired != null) {
            String beanName = jerryAutowired.name();
            String beanId;
            field.setAccessible(true);
            Object instance = null;
            //
            String name = field.getType().getName();
            beanId = name.substring(0, 1).toLowerCase() + name.substring(1);

            //如果指定了注入的beanID
            if (beanName != null && !"".equals(beanName)) {
                instance = jerryContext.getBean(beanName);
            }

            //如果未在Autowired指定name,则根据包名来找
            if (instance == null) {
                instance = jerryContext.getBean(beanId);
                if (instance == null) {
                    //使用Type包含包名，预防不同包下相同名称的类
                    boolean isInterface = field.getType().isInterface();
                    if (isInterface) {
                        //接口字段，暂存
                        Entry entry = new Entry(field, o);
                        entries.add(entry);
                        return;
                    }
                }
            }

            try {
                field.set(o, instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //处理接口注入的字段
    private void handlerInterface() {
        List<Object> list = jerryContext.getBeans();
        entries.stream().forEach(entry -> {
            try {
                entry.handler(list);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    class Entry {
        private Field field;
        private Object object;

        public Entry(Field field, Object object) {
            this.field = field;
            this.object = object;
        }

        public void handler(List<Object> list) throws IllegalAccessException {
            String beanName = field.getAnnotation(JerryAutowired.class).name();

            if (beanName != null && !"".equals(beanName)) {
                //寻找Bean
                field.set(object, jerryContext.getBean(beanName));
            } else {
                for (Object o : list) {
                    for (Class<?> cls : o.getClass().getInterfaces()) {
                        if (cls == field.getType()) {
                            field.set(object, o);
                            return;
                        }
                    }
                }
            }
        }
    }
}
