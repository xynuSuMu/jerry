> 队列是一种常见的数据结构，Java自然也存在这种数据结构，即Queue(继承Collection，所以我们将队列归属到集合的范围内)。
而阻塞队列BlockingQueue(继承Queue)又是队列中一种具有阻塞线程功能的特殊队列。


### 关于BlockingQueue

BlockingQueue即我们所说的阻塞队列，它的实现基于ReentrantLock，通常我们谈及到阻塞队列，都会和生产者/消费者模式关联起来(这是最常用的场景)，和一般的非阻塞队列区别在于实现生产者/消费者模式中不需要额外的实现线程同步和唤醒。

标题中说的是五大实现类，但是该接口实际上拥有7个实现类，它们的区别主要体现在存储结构上或对元素操作上的不同，如下:

ArrayBlockingQueue ：一个由数组结构组成的有界阻塞队列。
LinkedBlockingQueue ：一个由链表结构组成的有界阻塞队列。
PriorityBlockingQueue ：一个支持优先级排序的无界阻塞队列。
DelayQueue：一个使用优先级队列实现的无界阻塞队列。
SynchronousQueue：一个不存储元素的阻塞队列。
LinkedTransferQueue：一个由链表结构组成的无界阻塞队列。
LinkedBlockingDeque：一个由链表结构组成的双向阻塞队列。

今天主要聊一聊前面5个类的使用场景，对于最后两个类，笔者没有在真实项目上使用过，所以也不妄加分析。

### ArrayBlockingQueue使用场景

**特征:**  
基于数组实现，队列容量固定。
存/取数据的操作共用一把锁(默认非公平锁)，无法实现真正意义上存/取操作并行执行。

**分析:**  
由于基于数组，容量固定所以不容易出现内存占用率过高，但是如果容量太小，取数据比存数据的速度慢，那么会造成过多的线程进入阻塞(也可以使用offer()方法达到不阻塞线程)，
此外由于存取共用一把锁，所以有高并发和吞吐量的要求情况下，我们也不建议使用ArrayBlockingQueue。

**使用场景:**  
在上述的情况下，笔者觉得它的使用场景更多应该放在项目的一些次级业务中，比如:

> **人事系统中员工离职/变更后，其他依赖应用进行数据同步。**

在一些项目中，可能同公司的其他部门的应用服务会要求同步我们人事系统的部分组织架构数据，但是当人事系统数据发生变更后，应用的依赖方需要进行数据的同步，
这种场景下，由于员工离职/变更操作不是非常频繁，所以能有效防止线程阻塞，也基本没有并发和吞吐量的要求，所以可以将数据存放到ArrayBlockingQueue中，
由依赖方应用服务进行获取同步。


### LinkedBlockingQueue使用场景

**特征:**
LinkedBlockingQueue基于链表实现，队列容量默认Integer.MAX_VALUE  
存/取数据的操作分别拥有独立的锁，可实现存/取并行执行。


**分析:**  
1.基于链表，数据的新增和移除速度比数组快，但是每次存储/取出数据都会有Node对象的新建和移除，所以也存在由于GC影响性能的可能  
2.默认容量非常大，所以存储数据的线程基本不会阻塞，但是如果消费速度过低，内存占用可能会飙升。  
3.读/取操作锁分离，所以适合有并发和吞吐量要求的项目中

**使用场景:**  
在项目的一些核心业务且生产和消费速度相似的场景中:

> **订单完成的邮件/短信提醒。**

订单系统中当用户下单成功后，将信息放入ArrayBlockingQueue中，由消息推送系统取出数据进行消息推送提示用户下单成功。

如果订单的成交量非常大，那么使用ArrayBlockingQueue就会有一些问题，固定数组很容易被使用完，此时调用的线程会进入阻塞，那么可能推送的消息无法几十出去，所以使用LinkedBlockingQueue比较合适，但是要注意消费速度不能太低，不然很容易内存被使用完(一般而言不会时时刻刻生产消息，
但是需要预防消息大量堆积)

**比较ArrayBlockingQueue:**  
实际上对于ArrayBlockingQueue和LinkedBlockingQueue在处理普通的生产者-消费者问题时，两者一般可互相替换使用。


这里也赘述下，有人可能会问为什么不用MQ，或者Redis
笔者认为：很多技术知识有相同的使用场景，是很常见的，使用MQ/Redis也好，阻塞队列也罢，我们需要考虑项目中采用哪种方案是最合适的的，如果我们有现成的MQ/Redis，且公司前辈对于功能的使用有一个很好的封装，或者业务要求必须使用MQ，那我们项目使用这些也没有问题，但是如果没有现成的MQ/Redis或者没有现成的使用封装，业务又相对单一，那我们用阻塞队列简单的写一个小功能去实现也是很不错的，当然如果你是为了学习这些中间件那就另当别论了。


### PriorityBlockingQueue使用场景

**特征:**  
基于数组实现，队列容量最大为Integer.MAX_VALUE - 8(减8是因为数组的对象头)。
根据传入的优先级进行排序，保证按优先级来消费

**分析**  
优先级阻塞队列中存在一次排序，根据优先级来将数据放入到头部或者尾部  
排序带来的损耗因素，由二叉树最小堆排序算法来降低

**使用场景:**  
在项目上存在优先级的业务

> **VIP排队购票**（实现代码在文章末尾）

用户购票的时候，根据用户不同的等级，优先放到队伍的前面，当存在票源的时候，根据优先级分配


### DelayQueue使用场景

**特征:**  
DelayQueue延迟队列，基于优先级队列来实现  
存储元素必须实现Delayed接口(Delayed接口继承了Comparable接口)

**分析:**  
由于是基于优先级队列实现，但是它比较的是时间，我们可以根据需要去倒叙或者正序排列(一般都是倒叙，用于倒计时)


**使用场景:**  
> **订单超时取消功能**

用户下订单未支付开始倒计时，超时则释放订单中的资源，如果取消或者完成支付，我们再讲队列中的数据移除掉。
> **网站刷题倒计时** （实现代码在文章末尾）

逻辑同上

### SynchronousQueue使用场景

**特征:**  
采用双栈双队列算法的无空间队列或栈  
任何一个对SynchronousQueue写需要等到一个对SynchronousQueue的读操作，任何一个个读操作需要等待一个写操作  
没有容量，是无缓冲等待队列，是一个不存储元素的阻塞队列，会直接将任务交给消费者。

**分析:**  
相当于是交换通道，不存储任何元素，提供者和消费者是需要组队完成工作，缺少一个将会阻塞线程，指导等到配对为止


**使用场景:**  
> 参考线程池newCachedThreadPool()。

如果我们不确定每一个来自生产者请求数量但是需要很快的处理掉，那么配合SynchronousQueue为每个生产者请求分配一个消费线程是最简洁的办法。Executors.newCachedThreadPool()就使用了SynchronousQueue，这个线程池根据需要（新任务到来时）创建新的线程，如果有空闲线程则会重复使用，线程默认空闲了60秒后会被回收。

> 轻量级别的任务转交

比如会话转交，通常坐席需要进行会话转交，如果有坐席在线那么会为我们分配一个客服，但是如果没有，那么阻塞请求线程，一段时间后会超时或者提示当前坐席已满。

### 源码一:优先级队列

```java
public class QueueTest {
static class Ticket implements Comparable<Ticket> {
        private int level;

        public Ticket(int level) {
            this.level = level;
        }


        @Override
        public int compareTo(Ticket o) {
            //优先级高的返回-1
            if (this.level > o.level)
                return -1;
            else
                return 1;
        }
}
public static void main(String[] args) {
        //VIP客户、各大机场的VIP客户的优先登机，加速抢票
        BlockingQueue<Ticket> queue1 = new PriorityBlockingQueue<>();
        Ticket ticket = new Ticket(0);
        Ticket ticket1 = new Ticket(1);
        Ticket ticket2 = new Ticket(2);
        Ticket ticket3 = new Ticket(-1);
        queue1.add(ticket);
        queue1.add(ticket1);
        queue1.add(ticket2);
        queue1.add(ticket3);
        for (; ; ) {
            try {
                System.out.println(queue1.take().level);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
```

### 源码二:延迟队列

```java
public class QueueTest {
    static class Work implements Delayed {
        //名称
        private String name;
        //时长
        private long time;
        public Work(String name, long time, TimeUnit unit) {
            this.name = name;
            this.time = System.currentTimeMillis() + (time > 0 ? unit.toMillis(time) : 0);
        }
        //时间
        @Override
        public long getDelay(TimeUnit unit) {
            return time - System.currentTimeMillis();
        }
        @Override
        public int compareTo(Delayed o) {
            Work work = (Work) o;
            long diff = this.time - work.time;
            if (diff <= 0) {// 改成>=会造成问题
                return -1;
            } else {
                return 1;
            }
        }
    }
public static void main(String[] args) {
        BlockingQueue<Work> queue3 = new DelayQueue<>();
        try {
            Work work = new Work("用户一", 25, TimeUnit.SECONDS);
            Work work2 = new Work("用户二", 5, TimeUnit.SECONDS);
            Work work3 = new Work("用户三", 15, TimeUnit.SECONDS);
            queue3.add(work);
            queue3.add(work2);
            queue3.add(work3);
            for (; ; ) {
                Work work1 = queue3.take();
                System.out.println(work1.name + "," + work1.time);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}    
```

以上是笔者对于阻塞线程5大实现类使用场景分析，不足之处，还请评论区留言指教。





