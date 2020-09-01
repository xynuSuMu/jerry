公众号后台有人留言问我javac命令是怎么将java文件变成class文件，
这里写一篇文章具体来看下

首先javac命令在jre/bin目录下，其实质可以认为是一个脚本，在Mac系统中javac是一个可执行的
Unix文件，这里我们自己简单制作一个Unix可执行文件。

首先编写无格式文件:myjavaversion，
里面编写:
```text
java -version
```
然后控制台执行下面命令，将其转为可执行的文件
```text
sudo chmod u+x myjavaversion
```
执行该命令即可得到我们本机的java版本号
```text
chenlong:Desktop chenlong$ ./myjavaversion 
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
```
关于加 "./" 才能运行这里简单的说明下，执行一条Linux命令，本质是在运行一个程序，比如我们运行 javac 命令，那么
机器首先会去alias中查找命令的含义，然后去内置命令中查找，最后去环境变量的PATH中查找，所以如果不想加 "./" 我们可以
通过配置环境变量或者设置alias别名来达到这个目的。


知道命令如何执行之后，我们来实现一个自己的javac命令吧。

首先写一个编译主函数

```java
public class Compiler {
    public static void main(String[] args) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //编译Java文件的路径参数
        System.out.println(args[0]);
        int res = compiler.run(null,null,null,args[0]);
        System.out.println(res==0?"成功":"失败");
    }
}
```

将其打包成可执行的jar包。

然后将下面命令制作为Unix可执行文件

```text
java -jar compiler.jar /Users/XX/percase/Compiler.java
```

执行

```text
chenlong:Desktop chenlong$ ./myjavac 
/Users/XX/percase/Compiler.java
成功
chenlong:Desktop chenlong$ 
```
