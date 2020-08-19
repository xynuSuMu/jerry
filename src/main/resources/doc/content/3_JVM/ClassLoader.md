#### Java底层-类加载子系统(-)

> 在前一节文章中中我们聊到Java虚拟机的实现中HotSpot是由三个子系统和两大组件组成，那么这篇文章是针对
三个子系统中类加载子系统的首篇文章，重点介绍一下类加载。

我们首先看一段代码，思考一下输出的是什么：

```java
public class A {
    protected final static String a = "A";
    static {
        System.out.println("static A");
    }
}
class C {
    static {
        System.out.println("static C");
    }
    public static void main(String[] args) {
        System.out.println(A.a);
    }
}
``` 

答案是：
```text
static C
A
```

我们再看一题，如下：
```java
public class A {
    protected final static String a = "A";
    static {
        System.out.println("static A");
    }
}
public class B extends A {
    static {
        System.out.println("static B");
    }
}
class C {
    static {
        System.out.println("static C");
    }
    public static void main(String[] args) {
        System.out.println(B.a);
    }
}
```

答案仍然是：
```text
static C
A
```

不知道大家对于答案是否存在疑问，如果存在疑问不妨继续看下去，
上一节我们提到，当我们创建JVM实例后，我们需要指定一个要执行的主类，虚拟机会先**初始化**这个主类，
而初始化是**类加载**的一个环节。

类加载到内存到到卸载出内存的整个生命周期如下：

![image](https://p3.pstatp.com/origin/pgc-image/3f2ebf5f6846479d8922d20b61bf2d04)

注:  

**1.加载是通过类的全限定名来获取二进制字节流，然后将字节流转为方法区的运行时数据结构，最终在内存中生成一个代表该类的Class对象，作为
方法区中该类的各种数据访问入口。**

**2.验证是确保Class文件的字节流信息符合当前JVM的要求且不会危害虚拟机安全。**

**3.准备是为类变量分配内存并设置类变量的初始值(通常是零值，final修饰的除外)。**

**4.解析是将常量池中符号引用转换为直接引用的过程**

**5.使用和卸载不属于类加载的过程，初始化是类加载的最后一步。**

**6.解析动作在某些情况下可以在初始化阶段之后再开始，所以上图是常见的生命周期，并非一定如此**

所以在最上面的代码案例中C的main函数作为程序的入口，首先肯定会被**加载**，**验证**、**准备**、

然后**初始化**。初始化的过程中，**会真正意义上的开始执行类中的Java程序，这个执行指的是
初始化类变量和其他资源(所有的类变量赋值动作和静态语句块)**，所以「**static C**」
会首先被控制台输出出来，然后开始执行main函数，那么为什么第一题的「**static A**」和
第二题的「**static A**」以及「**static B**」没有被输出呢？这就涉及到**初始化**的触发条件，
JVM的规范中规定了只有五种情况才会对类进行初始化

1.遇到new、getstatic、putstatic或invokestatic这四条字节码指令时，如果类没有
进行过实例化，则需要先触发其初始化。

2.使用java.lang.reflect包的方法对类进行反射调用时，如果类没有进行过实例化，则需要先触发其初始化。

3.当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化

4.虚拟机启动时，用户需指定一个要执行的主类，虚拟机会先初始化这个主类

5.当使用JDK 1.7的动态语言支持时候，如果如果一个java.lang.invoke.MethodHandle实例
最后的解析结果是REF_getStatic、REF_putStatic、REF_invokeStatic的方法句柄，
且方法句柄对应的类没有进行过实例化，则需要先触发其初始化。

其中1、2、3、4不难理解，情况1通常指的是实例化对象，静态字段或者方法(但是被final修饰、
编译期把结果放入常量池中的静态字段除外)、
情况2使用反射构建对象，情况三是构造器链的原理、情况四我们再上一节已经说过、
情况5比较复杂一些，由于Java虚拟机层面对动态类型语言的支持一直都有所欠缺，
因此JDK1.7中引入invokedynamic指令实现类型检查的主体过程是在运行期而不是在编译期。

那么到这里其实上面答案就很明显了，第一题是因为虽然A.a调用了A的静态字段，但是由于
a字段是被final修饰，所以不会触发A的初始化，所以静态代码块并不会被执行，第二题同样
如此 B.a 既没有触发B的初始化，也没有事达到A初始化的条件，所以**static A**」以及「**static B**」没有被输出。

到这里对于类加载有没有一个新的认知呢？关于类加载的全过程，在后面文章会逐一进行描述。





