# Jerry

#### 背景

模拟Spring开发个人应用框架，为个人的小程序和网站的搭建提供接口

* 公众号:每天学Java

* 小程序:每天学Java

* 个人网站:http://www.study-java.cn

#### 实现的功能:

* 提供Http服务(基于Netty、支持Https)
* 实现依赖注入
* 实现Controller/RestController
* 集成Mybatis
* 事务注解
* 多数据源切换
* 请求拦截器(支持通配符)
* 请求路径支持通配符匹配(支持通配符)
* 支持html请求/MarkDown资源请求
* 集成Quartz定时任务调度
* 集成ASM-识别无Param的注解参数(开启-g 或 -parameters(JDK8有效)参数)
* 文件上传/下载功能 -> 上传POST请求有效
* 支持重定向

#### 部署
* Maven打包
* 启动:java -Djava.ext.dirs=./lib -cp jerry-1.0-SNAPSHOT.jar Main > 1.out

#### 相关应用(webapp目录下)
* app1和app4为测试使用数据
* app2为个人文章 访问地址:http://127.0.0.1/doc 请求路径和文章路径不宜修改
* app3为任务管理 
 
#### Http服务实现

server包下JerryServer类负责Netty获取Http请求，并对请求进行封装，构造JerryHttpServletRequest和JerryHttpServletResponse。
server.servlet包含Servlet、JerryServletRequest、JerryServletResponse接口，Servlet接口由外部Web进行实现，实现类可通过jerry.properties
解析配置:
```properties
server.servlet.Servlet:web.WebAppManager
```
server.http包下类结构参考Netty中DefaultHttpRequest继承关系

#### 请求处理
提供Http服务前，scan.ComponentScan类会对项目包进行一次扫描，对于项目类进行管理，
当遇到JerryController，JerryRestController，JerryService，JerryConfig，JerryJob注解时负责将类注入到容器BeanFactory中。
将JerryController，JerryRestController对应的路径和对应方法进行保存，当JerryServer类接收到请求后，会交给Servlet实现类处理，
路径请求支持通配符匹配(采用ant路径匹配规则)

#### Mybatis集成

参考Spring对Mybatis的集成，通过继承SqlSession的方式，使用代理类对事务的管理以及数据源的切换

#### Quartz集成

通过Quartz提供的JobFactory实现Job实例的控制

#### TODO:

* controller支持数据类型完善，目前只支持GET和POST