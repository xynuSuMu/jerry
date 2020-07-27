package util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-07-27 17:58
 */
public class FileUtil {

    private String duff = "class";

    public static File[] getFile(File file, String duff) {
        return file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                }
                if (file.getName().toLowerCase().endsWith(duff)) {
                    return true;
                }
                return false;
            }
        });
    }


}
