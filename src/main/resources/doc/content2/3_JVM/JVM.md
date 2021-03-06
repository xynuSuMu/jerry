> JVM(Java虚拟机)对于每一位Java开发人员来说都是再熟悉不过的名词，很多公司在招Java开发时，其招聘要求往往会有一条：掌握JVM。
这一篇文章目的就是为了解释JVM对于Java而言到底意味着什么。

#### 关于JVM

在计算机世界里，程序执行的实质 是通过高低电位来触发电子元件的(**数字电路的知识**)，
而高低电位可以认为是我们所说的二进制编码，所以最开始的编程其实就是写0、1组合。这种方式编写程序简单而又变态，所以为了更容易编写程序，后面使用
字母、单词来代替一个特定的指令来编程，也就是汇编语言，这种编程方式比直接输入二进制要容易理解，汇编之后，出现更加适合人们理解的语言，
像C语言，以C为基础引入面向对象的的C++，Java等等。

汇编语言是用字母、单词替代特定的指令，所以执行汇编语言的程序就需要进行编译，将其字母、单词转为二进制指令，
而C/C++ 这些语言也是一样的，他们也需要通过**编译器**将C/C++语言编写的
程序转为可执行的指令，像最开始C语言编译器就是使用汇编语言来实现的。

但是Java和C/C++ 又有一些不同，首先编译器编译C/C++ 程序后，其指令可以直接
面向硬件，所以他们执行效率很高，基本上可以随意操控硬件，但是Java通过编译器(javac编译器)编译之后其形成的二进制代码是提供给JVM，由JVM来将二进制文件代码转为
与机器适配的机器码，且该机器码不是直接应用到硬件上的，而是应用到操作系统层面的，所以Java的执行效率是比C/C++低的

注：操作系统和JVM实际上也是使用计算机语言写出来，比如Linux、WINDOWS系统内核都以C语言为主来编写的，而JVM跟操作系统很类似，它通常是用汇编、C、C++ 语言混合实现的

上面说到Java是面向操作系统的，这决定Java语言是无法直接操控硬件，而是需要JVM通过调用操作系统提供的API，再由操作系统去调用驱动是硬件的操控(驱动程序主要是通过汇编、C、C++ 实现)，
，这样一来通过JVM就隔离了物理机器、底层操作系统与Java语言规范实现。
所以我们也可以认为Java虚拟机是操作系统和Java语言实现的中间层，或者说适配器，它屏蔽了与具体平台相关的信息，使得我们开发人员通过虚拟机实现跨平台，不关心底层对硬件处理的细节。

上面对于JVM做了一个简单的说明，其重要性和复杂度就不言而喻了，也正是因为如此，我们可以认为Java的整个应用体系可是基于Java虚拟机来构建的。