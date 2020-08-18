package context;

import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-18 09:32
 */
public class Resource {

    public static InputStream getMybatisCfg() throws IOException {
        String resource = "mybatis-config.xml";
        return Resources.getResourceAsStream(resource);
    }

    public static InputStream getQuartzCfg() throws IOException {
        String resource = "quartz.properties";
        return Resources.getResourceAsStream(resource);
    }

    public static InputStream getJerryCfg() throws IOException {
        String resource = "jerry.properties";
        return Resources.getResourceAsStream(resource);
    }

    public static String getJerryCfg(String key) throws IOException {
        PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(getJerryCfg());
        return propertyResourceBundle.getString(key);
    }

    public static String getValue(String resource, String key) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        PropertyResourceBundle propertyResourceBundle = new PropertyResourceBundle(inputStream);
        return propertyResourceBundle.getString(key);
    }
}
