> 本篇文章将Java线程常见的功能实现进行整理，涉及地方包含:join的使用，yield的使用，isAlive，synchronized，
生产者-消费者模式(wait/notify实现 和 阻塞队列实现)，Lock+Condition模拟阻塞队列，线程同步工具，死锁代码，
jstack发现死锁程序。

#### join
join类似于同步，当A线程中调用了B线程的join()方法时，表示只有当B线程执行完毕时，A线程才能继续执行(如下代码)，
但是B线程必须已经调用start()方法，否则join就会失效

```java
    public static void join() throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("线程1start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程1end");
        });
        thread.start();
        thread.join();
        System.out.println("主线程");
    }
```

#### yield

yield，可以理解为让步，对于运行的线程(占有CPU资源)，可以通过yield方法
让出其占有的CPU，让其它具有相同优先级的等待线程获取执行权；但是，并不能保证
让出CPU后其他等待线程一定能拿到执行权。

这里是两个线程交叉打印。
```java
    public static void yield() {
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i += 2) {
                System.out.println("线程1：" + i);
                Thread.yield();
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 1; i < 10; i += 2) {
                System.out.println("线程2：" + i);
                Thread.yield();
            }
        });
        thread1.start();
        thread2.start();
    }
```

#### isAlive
isAlive()方法来检查线程是否已停止
```java
        Thread thread = new Thread(()->{
            while (true){
            }
        });
        thread.start();
        System.out.println("线程是否存活:"+thread.isAlive());
```

#### synchronized
synchronized是Java中的关键字，是一种同步锁，
我们使用多线程来达到异步的目的，但是异步也带来一些安全问题，
对于一些多线程访问的资源可能我们更多的是要求资源在某一时刻只被一个线程占有，这个时候就需要我们
做一些操作保证资源不被多个线程同时拥有。
```java
    public static void syn() {
        new Thread(() -> {
            System.out.println("syn1");
            exe();
            System.out.println("--");
        }).start();
        new Thread(() -> {
            System.out.println("syn2");
            exe();
            System.out.println("--");
        }).start();

    }
    static int i = 0;
    private static synchronized void exe() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + ":" + ++i);
    }
```

#### 生产者消费者模式

生产者消费者模式是一个多线程同步问题的经典案例，
其目的是解决当多个线程共同去操作同一份数据时,使用线程的同步保证信息的同步性和安全性。
这里使用 wait-notify 和  阻塞队列(ArrayBlockingQueue)实现。

需要注意的是如果使用LinkedBlockingQueue实现，需要考虑LinkedBlockingQueue默认Integer.MAX_VALUE大小容量，
如果生产者的速度一旦大于消费者的速度，也许还没有等到队列满阻塞产生，系统内存就有可能已被消耗殆尽了。

```java
    //wait-notify实现
    private static void produceCustom() {

        Queue<Long> queue = new LinkedList<>();

        Thread produce = new Thread(() -> {
            while (true) {
                //不上锁会IllegalMonitorStateException
                synchronized (queue) {
                    while (queue.size() == 10) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("开始生产");
                    queue.add(System.currentTimeMillis());
                    queue.notifyAll();
                }
            }
        });

        Thread customer = new Thread(() -> {
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("移除元素：" + queue.remove());
                    queue.notifyAll();
                }
            }
        });

        produce.start();
        customer.start();
    }

    //阻塞队列实现生产者消费者
    private static void produceCustomBlockQueue() {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(() -> {
            while (true) {
                boolean res = queue.offer(i);
                if (res) {
                    System.out.println("存放元素");
                    ++i;
                }
            }
        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("take:" + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

```
#### 模拟实现阻塞队列

在ArrayBlockingQueue源码中可以发现，放入数据和消费数据，都是共用同一个锁对象，由此也意味着两者无法真正并行运行，
其实现方式是利用ReentrantLock配合Condition完成，这里简单模拟一下。


```java
    //模拟阻塞队列
    Lock lock = new ReentrantLock();
    List<Integer> list = new ArrayList<>();
    Condition notEmpty = lock.newCondition();
    Condition empty = lock.newCondition();

    public void blockQueue() {
        new Thread(() -> {
            while (true) {
                offer(i++);
            }

        }).start();
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("take:" + take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Integer take() throws InterruptedException {
        try {
            lock.lock();
            if (list.isEmpty()) {
                empty.await();
            }
            return list.remove(0);
        } finally {
            notEmpty.signal();
            lock.unlock();
        }
    }

    public void offer(Integer i) {
        try {
            lock.lock();
            if (list.size() == 10) {
                System.out.println("最大值");
                notEmpty.await();
            }
            list.add(i);
            empty.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

```

#### 线程同步工具

CountDownLatch和CyclicBarrier的使用
```java
 private void latchUtil() throws InterruptedException {
        //CountDownLatch 一次控制
        Executor executor = threadPool();
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " 运行");
                latch.countDown();
            });
        }
        System.out.println("等待所有线程执行结束");
        latch.await(1, TimeUnit.MINUTES);
        System.out.println("所有线程执行结束");
        //CyclicBarrier 循环多次控制
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5);
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    System.out.println(Thread.currentThread().getName() + " 第一批次运行");
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName() + " 第二批次运行 ");
                    cyclicBarrier.await();
                    System.out.println("结束");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

            });
        }
        ((ExecutorService) executor).shutdown();
    }
```


#### 死锁
死锁的四个必要条件：
（1） 互斥条件：一个资源每次只能被一个进程使用。
（2） 请求与保持条件：一个进程因请求资源而阻塞时，对已获得的资源保持不放。
（3） 不剥夺条件:进程已获得的资源，在末使用完之前，不能强行剥夺。
（4） 循环等待条件:若干进程之间形成一种头尾相接的循环等待资源关系。

```java
    public static void deadLock() {
        Object o = new Object();
        Object o1 = new Object();

        new Thread(() -> {
            synchronized (o) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o1) {
                    System.out.println("+");
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (o1) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (o) {
                    System.out.println("-");
                }
            }
        }).start();
    }
```

#### jstack

利用jstack 可以 发现死锁线程

```text
//    jps -l 查看PID
//    jstack PID
控制台打印可以发现如下信息
//    Found one Java-level deadlock:
```

利用jstack寻找Linux系统中CPU占用高的线程：
* top -Hp Pid可以查看该进程下各个线程的cpu使用情况；
* 将该pid转成16进制的值
* jstack pid | grep 16进制
```text
top ##寻找进程
top -Hp pid ##寻找进程的线程
printf '%x\n' pid ##转为16进制
jstack pid | grep 16进制 ##定位代码
```