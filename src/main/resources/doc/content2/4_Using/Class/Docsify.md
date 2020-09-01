### 关于Docsify

官网地址:https://docsify.js.org/

在Docsify官网对Docsify是这样的描述的，
> docsify 是一个动态生成文档网站的工具。不同于 GitBook、Hexo 的地方是它不会生成将 .md 转成 .html 文件，所有转换工作都是在运行时进行

### 使用

官网推荐使用npm的方式去安装使用，但是如果不是公司级别的需求(文档统一管理)，个人觉得没有必要去搭建一个项目去做这件事，就好像我写了一个
接口，我更加希望在该项目中直接写好文档，然后可以直接看。所以这里我不介绍官网使用，而是介绍在SpringBoot如何使用Docsify生成文档。
下面附上效果图:

![图片](http://p3.pstatp.com/large/pgc-image/d888ad6997ab49b9b134000274be170b)

### 过程

* 新建SpringBoot项目，记得勾选thymeleaf，如果有现成的。Spring Boot项目，引入下面依赖

```xml
    <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
```

* 在resource下新建html。如下，这里大家可以将view下的目录移到static中，根据个人的爱好来

```
resouce
│   
│      
└───static
│
│
└───view
│   │   
│   │   
│   │
│   └───doc
│       │   _sidebar.md（侧边栏目录）
│       │   README.md （文档内容）
│       │   ...
│   
└───templates
    │   doc.html
    │   ...

```

* 编写doc.html，这里和文档介绍相似

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>文档案例</title>
    <!--CSS样式    -->
    <link href="https://cdn.bootcss.com/docsify/4.9.4/themes/vue.css" rel="stylesheet">
    <!--自定义样式-->
    <style>
        .sidebar .sidebar-nav {
            padding: 20px;
        }
        .sidebar{
            padding: 0;
        }
        .content{
            padding-top: 0;
        }
    </style>
</head>
<body>
<div id="app">加载中...</div>
<script>
// 加载侧边栏
    window.$docsify = {
        loadSidebar: true
    }
</script>
<!--docsify插件的CDN地址-->
<script src="https://cdn.bootcss.com/docsify/4.9.4/docsify.min.js"></script>
<!--Java代码高亮插件的CDN地址-->
<script src="https://cdn.bootcss.com/prism/9000.0.1/components/prism-java.min.js"></script>
</body>
</html>
```
* 配置Controller

```java
@Controller
public class View {
    @RequestMapping("/view/doc")
    public String viewDoc(HashMap<String, Object> map) {
        return "doc";
    }
}
```
* 如果设置了拦截器，注意放开这些静态资源，否则会出现404，如果没有设置拦截器，应该会正常访问
```java
@Configuration
public class MyInterceptorConfig extends WebMvcConfigurationSupport {
    @Autowired
    private MyInterceptor loginInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/view/**")
                .addResourceLocations("classpath:/view/");
    }
}
```

* MD文件

_sidebar.md

```markdown
<center>每天学Java</center>

## 文章介绍
* [文章介绍](/)

## Java运用
* [ThreadLocal三种使用场景](case/ThreadLocal)
* [Stream在集合中的8种应用案例](case/Stream)
* [阻塞队列的使用](case/queue)
* [SpringBoot整合docsify](case/Docsify)
```
README.md

```markdown
## 说明
每天学Java(小程序和公众号)做到今日，仍然是籍籍无名，一开始想的很多，到如今只当作自己的兴趣
来弄，但是想到做到这一步，停下来也没有意义，不如继续折腾折腾，提升提升自己能力水平。于是
每天学Java的网站也就出来了，网站将小程序的题库数据抽离出来，在加上新的模块:功能集锦，用
于将自己工作中使用的技术进行一次整理封装和优化。

```

到这里就完成SpringBoot结合Docsify生成文档，笔者觉得在一些小项目中，这样可以非常快速的
生成网站文档，不用另外新建项目。
