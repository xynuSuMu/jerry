# Jerry

#### 背景

学习Spring相关功能，为个人的小程序和网站搭建提供接口

* 公众号:每天学Java

* 小程序:每天学Java

* 个人网站:http://www.study-java.cn

#### 学习Spring实现相关功能:

* 基于Netty提供Http服务
* 实现依赖注入
* 实现请求接入Controller层
* 集成Mybatis
* 非Spring项目注入Mapper
* 模拟实现Spring的事务注解
* 实现多数据源
* 模拟实现Spring的拦截器(Ant路径匹配方式来支持通配符)
* 请求路径支持通配符匹配
* 支持html请求/MarkDown资源请求
* 集成Quartz定时任务调度
* Jar包启动:java -Djava.ext.dirs=./lib -cp jerry-1.0-SNAPSHOT.jar Main > 1.out

#### 应用(webapp目录下)
* app1为测试应用 
* app2为个人文章
* app3为任务管理
* app4为小程序接口
 

TODO:

* controller支持数据类型完善，目前只支持GET和POST
* 支持https请求


maven打包后，启动：java -Djava.ext.dirs=./lib -cp jerry-1.0-SNAPSHOT.jar Main > 1.out