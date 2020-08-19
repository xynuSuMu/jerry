# Jerry

#### 背景

模拟Spring开发个人应用框架，为个人的小程序和网站的搭建提供接口

* 公众号:每天学Java

* 小程序:每天学Java

* 个人网站:http://www.study-java.cn

#### 支持的功能:

* 提供Http服务(基于Netty)
* 实现依赖注入
* 实现Controller和RestController
* 集成Mybatis
* Mapper注入
* 事务注解
* 多数据源切换
* 请求拦截器(支持通配符)
* 请求路径支持通配符匹配(支持通配符)
* 支持html请求/MarkDown资源请求
* 集成Quartz定时任务调度
* Jar包启动:java -Djava.ext.dirs=./lib -cp jerry-1.0-SNAPSHOT.jar Main > 1.out

#### 应用(webapp目录下)
* app1为测试使用数据
* app2为个人文章 访问地址:http://127.0.0.1/doc 请求路径和文章路径不宜修改
* app3为任务管理 
* app4为小程序接口 
 

TODO:

* controller支持数据类型完善，目前只支持GET和POST
* 支持https请求


maven打包后，启动：java -Djava.ext.dirs=./lib -cp jerry-1.0-SNAPSHOT.jar Main > 1.out