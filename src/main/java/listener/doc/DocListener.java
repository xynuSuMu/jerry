package listener.doc;


import context.Resource;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.resource.ResourceHandlerRegistry;
import web.support.InterceptorSupport;

import java.io.File;
import java.io.IOException;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-09-01 09:03
 */
public class DocListener extends FileAlterationListenerAdaptor {

    private final String PATH = "path";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DocListener() {
        super();
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        super.onStart(observer);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        super.onDirectoryCreate(directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        super.onDirectoryChange(directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        super.onDirectoryDelete(directory);
    }

    @Override
    public void onFileCreate(File file) {
        //创建的文件不考虑，让其自动读取
    }

    @Override
    public void onFileChange(File file) {
        try {
            String path = Resource.getJerryCfg(PATH);
            logger.info("监听文件发生变动,文件名,{},文件路径{}", file.getName(), file.getAbsolutePath());
            logger.info("相对替换后,{}", file.getAbsolutePath().replace(path, ""));
//            ResourceHandlerRegistry resourceHandlerRegistry = InterceptorSupport.getInstance().getResource();
//            resourceHandlerRegistry.setTempFile(file.getAbsolutePath().replace(path, ""));
            logger.info("更新完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFileDelete(File file) {
        try {
            String path = Resource.getJerryCfg(PATH);
//            ResourceHandlerRegistry resourceHandlerRegistry = InterceptorSupport.getInstance().getResource();
//            resourceHandlerRegistry.setTempFile(file.getAbsolutePath().replace(path, ""), null);
            logger.info("文件删除，更新完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        super.onStop(observer);
    }
}

