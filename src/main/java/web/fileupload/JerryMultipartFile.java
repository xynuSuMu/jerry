package web.fileupload;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-26 17:51
 */
public interface JerryMultipartFile {
    InputStream getInputStream() throws IOException;

    String getFileName();

    long getSize();
}
