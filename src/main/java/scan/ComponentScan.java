package scan;

import annotation.*;
import context.JerryContext;
import exception.JerryException;
import handler.JerryControllerHandlerMethod;
import net.sf.cglib.proxy.Enhancer;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.CGServiceProxy;
import quartz.QuartzApplication;
import transaction.SqlSessionTemplate;
import web.interceptor.support.InterceptorSupport;
import web.interceptor.support.WebMvcSupport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    //缓存解决循环依赖
    private Map<Class<?>, Object> cacheObj = new HashMap<>();
    //所有的class
    private List<Class<?>> classes = new CopyOnWriteArrayList<>();
    //接口
    private Map<Class<?>, List<Class<?>>> inter = new ConcurrentHashMap<>();

    private String url;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ComponentScan(String url) {
        this.url = url;
    }

    public void scanMapper(String[] pkgs) throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        jerryContext.setSqlSession(sqlSessionFactory);
        SqlSession sqlSession = new SqlSessionTemplate(sqlSessionFactory);
        if (url.endsWith("jar")) {//扫描jar包
            findClassJar(url, pkgs);
        } else {
            for (String pkg : pkgs) {
                String searchPath = url + pkg;
                //扫描文件夹
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
            findClassJar(searchPath, null);
        } else {//扫描文件夹
            scanClasses(new File(searchPath));
        }
        //DI
        for (Class<?> clazz : classes) {
            di(clazz);
        }
        //销毁
        classes = null;
        cacheObj = null;
    }

    private void findClassJar(final String path, final String[] finalPkgs) throws Exception {
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
                        .replace(url, "");
                if (finalPkgs == null) {
                    pkg = pkg.replace("/", ".").substring(0, pkg.length() - 6);
                    Class<?> clazz = Class.forName(pkg);
                    recordInterFace(clazz);
                } else {
                    for (String finalPkg : finalPkgs) {
                        if (pkg.startsWith(finalPkg)) {
                            pkg = pkg.replace("/", ".").substring(0, pkg.length() - 6);
                            Class<?> clazz = Class.forName(pkg);
                            recordInterFace(clazz);
                        }
                    }
                }
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
                recordInterFace(clazz);
            }
        }
    }

    private void recordInterFace(Class<?> clazz) {
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


    private void di(Class<?> clazz) throws Exception {
        JerryConfig jerryConfig = clazz.getAnnotation(JerryConfig.class);
        if (jerryConfig != null) {
            Object o = handlerComponent(clazz);
            if (o instanceof WebMvcSupport) {
                WebMvcSupport webMvcSupport = (WebMvcSupport) o;
                InterceptorSupport interceptorSupport = InterceptorSupport.getInstance();
                interceptorSupport.mvcConfig(webMvcSupport);
            }
        }
        //如果是存在Controller注解
        JerryController jerryController = clazz.getAnnotation(JerryController.class);
        JerryRestController restController = clazz.getAnnotation(JerryRestController.class);
        if (jerryController != null || restController != null) {
            handlerController(clazz);
        }
        //Service注解
        JerryService service = clazz.getAnnotation(JerryService.class);
        if (service != null) {
            handlerComponent(clazz);
        }

        //Job注解
        JerryJob jerryJob = clazz.getAnnotation(JerryJob.class);
        if (jerryJob != null) {
            handlerJob(clazz);
        }
    }

    QuartzApplication quartzApplication = new QuartzApplication();

    private Object handlerComponent(Class<?> clazz) throws Exception {
        if (cacheObj.containsKey(clazz)) {
            logger.info("存在循环依赖" + clazz.getTypeName());
            return cacheObj.get(clazz);
        }
        Object o = null;
        String name = clazz.getName();
        String beanID = name.substring(0, 1).toLowerCase() + name.substring(1);
        String cusBeanID = null;
        //Service组件
        JerryService jerryService = clazz.getAnnotation(JerryService.class);
        String annotationValue;
        if (jerryService != null && !"".equals(annotationValue = jerryService.value())) {
            cusBeanID = annotationValue;
        }
        //Config组件
        JerryConfig jerryConfig = clazz.getAnnotation(JerryConfig.class);
        if (jerryConfig != null) {

        }
        if ((o = jerryContext.getBean(beanID)) != null) {
            return o;
        }
        if (cusBeanID != null && (o = jerryContext.getBean(cusBeanID)) != null) {
            return o;
        }
        //判断是否为接口
        boolean isInterface = clazz.isInterface();
        if (isInterface) {
            //寻找其实现类
            if (inter.containsKey(clazz)) {
                List<Class<?>> classes = inter.get(clazz);
                //暂时获取第一个
                String filedName = classes.get(0).getName();
                if (jerryContext.getBean(filedName) != null) {
                    return jerryContext.getBean(filedName);
                } else {
                    return handlerComponent(classes.get(0));
                }
            }
            //不存在实现类
            return null;
        }
        Object instance = clazz.newInstance();
        if (instance != null) {
            //使用 CGLIB
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(new CGServiceProxy());
            o = enhancer.create();
            //使用代理类
//            o = Proxy.newProxyInstance(instance.getClass().getClassLoader(),
//                    instance.getClass().getInterfaces(),
//                    new ServiceProxy(instance));
            //加入缓存
            cacheObj.put(clazz, o);
            //字段注入
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                JerryAutowired jerryAutowired = field.getDeclaredAnnotation(JerryAutowired.class);
                if (jerryAutowired != null) {
                    String fieldBeanName = jerryAutowired.name();
                    String fieldBeanId;
                    field.setAccessible(true);
                    Object value = null;
                    //如果指定了注入的beanID
                    if (fieldBeanName != null && !"".equals(fieldBeanName)) {
                        value = jerryContext.getBean(fieldBeanName);
                    }
                    //
                    String fieldName = field.getType().getName();
                    fieldBeanId = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                    //如果未在Autowired指定name,则根据全限定名来找
                    if (value == null) {
                        value = jerryContext.getBean(fieldBeanId);
                    }
                    //为null,表明该字段需要注入
                    if (value == null) {
                        value = handlerComponent(field.getType());
                    }
                    try {
                        field.set(o, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (jerryContext.getBean(beanID) != null) {
            throw new JerryException(beanID + "重复");
        } else {
            jerryContext.setBean(beanID, o);
        }
        if (cusBeanID != null && jerryContext.getBean(cusBeanID) != null) {
            throw new JerryException(cusBeanID + "重复");
        } else if (cusBeanID != null) {
            jerryContext.setBean(cusBeanID, o);
        }
        //移除缓存
        cacheObj.remove(clazz);
        return o;
    }

    //处理控制层方法
    private void handlerController(Class<?> clazz) throws Exception {
        //todo:后续改造为代理类
        Object o = clazz.newInstance();
        //为字段赋值
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(o) == null) {
                Object proxy = handlerComponent(field.getType());
                field.set(o, proxy);
            }
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
                JerryControllerHandlerMethod jerryControllerHandlerMethod =
                        new JerryControllerHandlerMethod(method,
                                o,
                                method.getParameters(),
                                method.getReturnType(),
                                jerryRequestMapping.method());
                String requestMapping = requestMethodUrl + jerryRequestMapping.value();
                logger.info("request:{}", requestMapping);
                if (jerryContext.getMethod(requestMapping) != null) {
                    throw new JerryException("requestMapping重复:" + requestMapping);
                } else {
                    logger.info("注入:" + requestMapping);
                    jerryContext.setControllerMethod(requestMapping,
                            jerryControllerHandlerMethod);
                }
            }
        }
    }

    //处理控制层方法
    private void handlerJob(Class<?> clazz) throws Exception {

        Object o = clazz.newInstance();
        //为字段赋值
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(o) == null) {
                Object proxy = handlerComponent(field.getType());
                field.set(o, proxy);
            }
        }
        if (o instanceof Job) {
            quartzApplication.start(o);
        }


    }

}
