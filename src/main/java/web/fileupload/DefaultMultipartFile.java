package web.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-26 18:01
 */
public class DefaultMultipartFile implements JerryMultipartFile {

    public final InputStream inputStream;
    private final long size;
    private final String name;

    public DefaultMultipartFile(InputStream file, long size, String name) {
        this.inputStream = file;
        this.size = size;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public String getFileName() {
        return name;
    }


    @Override
    public long getSize() {
        return this.size;
    }
}
