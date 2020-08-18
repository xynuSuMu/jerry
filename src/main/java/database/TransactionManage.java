package database;

import org.apache.ibatis.session.SqlSession;

/**
 * @author 陈龙
 * @version 1.0
 * @date 2020-08-10 18:18
 */
public class TransactionManage {
    private static final ThreadLocal<SqlSession> resources = new ThreadLocal();

    public static void setResources(SqlSession sqlSession) {
        resources.set(sqlSession);
    }

    public static SqlSession getResources() {
        return resources.get();
    }
}
