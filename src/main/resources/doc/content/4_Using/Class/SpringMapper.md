> 前几天，有粉丝问我非Spring项目如何使用Mybatis，并且像Spring项目将Mapper进行注入？这篇文章就带大家看一下如何实现。

### 非Spring项目集成Mybatis
* Maven引入外部依赖
```pom
<!--驱动依赖-->
<dependency>
<groupId>mysql</groupId>
<artifactId>mysql-connector-java</artifactId>
<version>5.1.45</version>
</dependency>

<!-- mybatis jar 依赖 -->
<dependency>
<groupId>org.mybatis</groupId>
<artifactId>mybatis</artifactId>
<version>3.4.6</version>
</dependency>
```
* resorce目录下配置db.properties和mybatis-config.xml

```properties
jdbc.driverClass=com.mysql.jdbc.Driver
jdbc.jdbcUrl=
jdbc.user=
jdbc.password=
```
**下面mappers标签下的sql/testMapper.xml同样是在resource目录下**，这里不支持通配符，Spring配置支持通配符是因为使用**Ant通配符匹配**，这里大家可以了解一下。
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <properties resource="db.properties"></properties>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driverClass}"/>
                <property name="url" value="${jdbc.jdbcUrl}"/>
                <property name="username" value="${jdbc.user}"/>
                <property name="password" value="${jdbc.password}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="sql/testMapper.xml"/>
    </mappers>
</configuration>
```

testMapper.xml和相应的Mapper就不展示了，大家应该都很熟了，记住存放目录就可以，这里直接写如何使用，下面代码的注释很清楚，了解之后对于下面实现注入很有帮助。
```java
package mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Mybatis {
    public static void main(String[] args) throws IOException {
        // 定义配置文件，相对路径，文件直接放在resources目录下
        String resource = "mybatis-config.xml";
        // 读取文件字节流
        InputStream inputStream = Resources.getResourceAsStream(resource);
        // mybatis 读取字节流，利用XMLConfigBuilder类解析文件
        // 将xml文件解析成一个 org.apache.ibatis.session.Configuration 对象
        // 然后将 Configuration 对象交给 SqlSessionTemplate 接口实现类 DefaultSqlSessionFactory 管理
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        // openSession 有多个重载方法， 比较重要几个是
        // 1 是否默认提交 SqlSession openSession(boolean autoCommit)
        // 2 设置事务级别 SqlSession openSession(TransactionIsolationLevel level)
        // 3 执行器类型   SqlSession openSession(ExecutorType execType)
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // mybatis 内部其实已经解析好了 mapper 和 mapping 对应关系，放在一个map中，这里可以直接获取
        // 如果看源码可以发现userMapper 其实是一个代理类MapperProxy，
        // 通过 sqlSession、mapperInterface、mechodCache三个参数构造的
        // MapperProxyFactory 类中 newInstance(MapperProxy<T> mapperProxy)方法
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        /* select */
        List<User> users = userMapper.selectUser();
        for (User user : users) {
            System.out.println(user.getId());
        }
        sqlSession.close();
    }
}
```

### 注入Mapper

在Spring项目中，当我们使用Autowired注解后，会将Mapper自动注入，并不需要像上面的代码一样，需要我们自己去获取SqlSession，使用完之后手动关闭sqlSession，**这里的注入和前面普通的Service注入不同，在前面的注入中，我们通过反射将实例注入字段即可，但是Mybatis的Mapper无法直接实例，而是需要通过Mybaits得到相应的代理类(MapperProxy)，Spring完成这一功能最核心的点就是将Mybatis的SqlSession进行管理，这种管理实际上大家应该都很清楚，是使用动态代理来完成的**。下面我们来实现Mapper的注入(功能上肯定没有Spring完善，但是可以保证注入的Mapper正常使用)。

* 定义MapperScan注解，注解pkg值就是我们要扫描的Mapper文件
```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperScan {
    String[] pkg();
}
```

* 模仿Spring定义SqlSessionTemplate实现对SqlSession的管理，实现的方法这里省略，大家可以参考Spring中义SqlSessionTemplate源码，或者通过文章末尾git地址去浏览，这里重点看SqlSessionTemplate的构造和SqlSession代理类SqlSessionInterceptor，注入的Mapper无需手动关闭SqlSession也是代理类的功劳。
```java
public class SqlSessionTemplate implements SqlSession {
 
    //...省略实现代码
 
    //工厂类
    private final SqlSessionFactory sqlSessionFactory;
    //执行期类型
    private final ExecutorType executorType;
    //SqlSession，在构造器中，实际上是代理类
    private final SqlSession sqlSessionProxy;
    //构造器
    public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.executorType = sqlSessionFactory.getConfiguration().getDefaultExecutorType();
        this.sqlSessionProxy = (SqlSession) newProxyInstance(
                SqlSessionFactory.class.getClassLoader(),
                new Class[]{SqlSession.class},
                new SqlSessionInterceptor());
        ;
    }
    //SqlSession的代理类，在这里关闭SqlSession
    private class SqlSessionInterceptor implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("----进入SqlSession代理----");
            SqlSession sqlSession = SqlSessionTemplate.this.sqlSessionFactory.
                    openSession(SqlSessionTemplate.this.executorType);
            try {
                Object result = method.invoke(sqlSession, args);
                return result;
            } catch (Throwable t) {
                sqlSession.close();
                Throwable unwrapped = unwrapThrowable(t);
                throw unwrapped;
            } finally {
                sqlSession.close();
            }
       
}
```

* 扫描，将bean存入jerryContext(类似于Spring的BeanFactory)，下面代码前面步骤和一开始集成Mybatis没什么不同，但是SqlSession的不再是使用sqlSessionFactory.openSession()，而是通过SqlSessionTemplate，这样得到的MapperProxy类的SqlSession实际上就是我们的代理类。

```java
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
                    jerryContext.setMapper(beanId, o);
                }
            }
        }
        classes.clear();
    }
```

* 完成。启动项目后，下面userMapper就会生效。
```java

    @JerryAutowired
    private UserMapper userMapper;

    @JerryRequestMapping(value = "/sys", method = RequestMethod.GET)
    public String sys(@Param(value = "url") String url) {
        logger.info("我是测试数据，😄哈哈" + url);
        List<User> list = userMapper.selectUser();
        System.out.println(list.size());
        return testService.sys();
    }
```

如果上面描述不够清楚大家可通过下面的Git地址将项目下载下来
git地址：https://github.com/xynuSuMu/jerry.git

也欢迎大家关注公众号，回复 "加群" 一起来讨论、学习、进步。
