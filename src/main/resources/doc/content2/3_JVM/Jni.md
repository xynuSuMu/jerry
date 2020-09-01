> 前面的文章我们讲述了关于类加载子系统、执行引擎、GC子系统、运行数据区的内容，那么在HotSpot虚拟中就剩下本地接口没有涉及到了，所以这一篇文章对于本地接口进行介绍。

为什么会有JNI的概念呢？我们在之前的文章说过Java语言不是面向硬件的，它无法直接调用操作系统API访问硬件，Java于硬件的交互正常都是通过JVM提供的API来完成的，但是当虚拟机提供的API不足以实现我们个别需求的时候，就需要本地接口了。因为JVM本身就有调用C++程序，所以JVM也提供了JNI技术作为其它语言(主要是C/C++)通信的API。所以关于本地接口知识里面，其他语言成为了主角，Java成为了一个调用方，所以这篇文章我们以C++为例，看一下如何调用本地接口，关于C++的部分尽可能简单的说，如果大家还有疑问可以学一点C++的基本知识。

首先编写调用本地方法的类，方法定义为native，表明调用的是本地接口。
```java
package com.studyjava.email.jni.main;

public class Main {
    //加载Library，将Main资源库加载到内存中
    static {
        System.loadLibrary("Main");
    }
    public native void studyJava();
    public static void main(String[] args) {
        new Main().studyJava();
    }
}
```

Java调用的本地接口并不是随意调用的，它是有规律的，我们通过javah命令将.java文件生成一个头文件(.h后缀的文件)，这种转换会将native方法抽离到头文件中，如果对于C/C++的头文件不了解，我们可以认为它很像一个接口，我们可以通过头文件来调用库功能，在一些特殊场合下，C/C++的源代码如果不便（或不准）向用户公布，那么只要向用户提供头文件和二进制的库即可。

```java
javah -d jni com.studyjava.email.jni.main.Main
```
当然我们可以不使用javah命令，这里也可以自己去写.h文件，但是要保证格式是正确的，如下代码。
注：代码include就类似我们Java的import一样、
Java_com_studyjava_email_jni_main_Main_studyJava就是我们要实现的方法
```java
#include <jni.h>
/* Header for class com_studyjava_email_jni_main_Main */

#ifndef _Included_com_studyjava_email_jni_main_Main
#define _Included_com_studyjava_email_jni_main_Main
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_studyjava_email_jni_main_Main
 * Method:    studyJava
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_studyjava_email_jni_main_Main_studyJava
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
```

头文件有了之后，我们我们编写它的实现，首先定义.cpp文件(可以使用C++开发工具或者纯文本编写也是可以的)，
cpp文件中首先引入我们生成的头文件，引入头文件之后，我们就对上面头文件的方法
JNIEXPORT void JNICALL Java_com_studyjava_email_jni_main_Main_studyJava 进行实现，这里
使用print函数打印了"每天学Java"。

```c++
#include <jni.h>
#include <iostream>
#include "com_studyjava_email_jni_main_Main.h"
JNIEXPORT void JNICALL Java_com_studyjava_email_jni_main_Main_studyJava(JNIEnv *, jobject)
{
    printf("每天学Java");
    return;
}
int main(){
    return 1;
}
```

编写.cpp文件就类似我们写接口的实现类，当我们编写完之后，需要将cpp文件编译成.jnilib文件(我这里是Mac OS，如果是Windows需要转为.dll文件，Linux/Unix则为.so文件，这个命令的具体含义在文末)。

```java
g++ -shared -I /Users/.../include /Users/.../include/main.cpp -o libMain.jnilib

```
如果大家没有C++环境可以百度部署下，MacOS中如果安装XCode，会自带C++环境。

有C++环境之后，编译过程中可能会出现下面报错，

```text
 'jni.h' file not found
```

这是因为我们引入的jni.h找不到，我们可以将jdk路径下的include复制出来，于上面编译的文件同级，
如下图，其中jni_md是从darwin中复制到include目录下的：

![image](https://p3.pstatp.com/origin/pgc-image/7df64b8e93154b38b3505dd47527b898)

其中lib中存放的就是libMain.jnilib，也就是我们编译的资源库。到这里我们本地接口就算编写完成了，调用的时候需要指定Library。
```text
-Djava.library.path=/Users/.../java/lib
```
![image](https://p3.pstatp.com/origin/pgc-image/8b0b842699384e8a9fe27483142b2e74)

否则会有如下报错:

```text
java.lang.UnsatisfiedLinkError: no XXX in java.library.path
```



关于g++命令的参数这里简单的说下
，-shared是说明要生成动态库，而两个 -I的选项，是因为我们用到<jni.h>相关的头文件，放在<jdk>/include目录下。
最后 -o 选项，我们在java代码中调用的是System.loadLibrary("Main"),那么生成的动态链接库的名称就必须是libMain.jnilib的形式,否则在执行java代码的时候，同样会报 java.lang.UnsatisfiedLinkError: no XXX in java.library.path 的错误

然后头文件具体用处：

（1）通过头文件来调用库功能。在很多场合，源代码不便（或不准）向用户公布，只要向用户提供头文件和二进制的库即可。用户只需要按照头文件中的接口声明来调用库功能，而不必关心接口怎么实现的。编译器会从库中提取相应的代码。

（2）头文件能加强类型安全检查。如果某个接口被实现或被使用时，其方式与头文件中的声明不一致，编译器就会指出错误，这一简单的规则能大大减轻程序员调试、改错的负担。

关于本地接口到这里就讲完了，这篇文章只讲述了我们如何去调用本地接口，但是原理并没有提及，大家可以追踪System.loadLibrary()方法进行学习。


参考文章:

https://blog.csdn.net/hackooo/article/details/48395765/

https://www.cnblogs.com/canacezhang/p/9320508.html