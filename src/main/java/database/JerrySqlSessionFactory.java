package database;

import context.Resource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-18 16:14
 */
public class JerrySqlSessionFactory {

    private final static Map<String, SqlSessionFactory> map;

    static {
        map = new HashMap<>();
        SqlSessionFactory sqlSessionFactory = null;
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resource.getMybatisCfg());
            //默认数据源
            map.put(sqlSessionFactory.getConfiguration().getEnvironment().getId(), sqlSessionFactory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JerrySqlSessionFactory() {

    }

    public static SqlSessionFactory getSqlSessionFactory(String key) throws IOException {
        if (key == null) {
            return map.get("dataSource");
        } else if (!map.containsKey(key)) {
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resource.getMybatisCfg(), key);
            if (sqlSessionFactory != null) {
                map.put(key, sqlSessionFactory);
                return sqlSessionFactory;
            } else {
                throw new IllegalArgumentException("数据源未配置");
            }
        }
        return map.get(key);
    }
}
