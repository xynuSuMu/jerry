#### Java实现一个简单的队列

> 代码来源:https://blog.csdn.net/jsc123581/article/details/81986714

#### 基于数组实现一个队列
```java
public class SqQueue<T>{
    private T[] datas;//使用数组作为队列的容器
    private int maxSize;//队列的容量
    private int front;//头指针
    private int rear;//尾指针

    //初始化队列
    public SqQueue(int maxSize){
        if(maxSize<1){
            maxSize = 1;
        }
        this.maxSize = maxSize;
        this.front = 0;
        this.rear = 0;
        this.datas = (T[])new Object[this.maxSize];
    }

    //两个状态:队空&队满
    public boolean isNull(){
        if(this.front == this.rear)
            return true;
        else
            return false;
    }

    public boolean isFull(){
        if((rear+1)%this.maxSize==front)
            return true;
        else
            return false;
    }

    //初始化队列
    public void initQueue(){
        this.front = 0;
        this.front = 0;
    }

    //两个操作:进队&出队
    public boolean push(T data){
        if(isFull())
            return false;//队满则无法进队
        else{
            datas[rear] = data;//进队
            rear = (rear+1) % maxSize;//队尾指针+1.
            return true;
        }
    }
    public T pop(){
        if(isNull())
            return null;//对空无法出队
        else{
            T popData = datas[front++];//出队
            front = (front+1) % maxSize;//队头指针+1
            return popData;
        }
    }

    //get()
    public T[] getDatas() {
        return datas;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getFront() {
        return front;
    }

    public int getRear() {
        return rear;
    }
}
```

#### 栈的链式存储结构实现

```java
public class LinkQueue<T>{
     private QNode<T> front;//队头指针
     private QNode<T> rear;//队尾指针
     private int maxSize;//为了便于操作，使用这个变量表示链队的数据容量
 
     //初始化
     public LinkQueue(){
         this.front = new QNode<T>();
         this.rear = new QNode<T>();
         this.maxSize = 0;
     }
 
     //初始化队列
     public void initQueue(){
         front.next = null;
         rear.next = null;
         maxSize = 0;
     }
 
     //队空判断
     public boolean isNull(){
         if(front.next==null || rear.next==null)
             return true;
         else
             return false;
     }
 
     //进队
     public void push(QNode<T> node){
         if(isNull()){
             //第一次
             front.next = node;
             rear.next = node;
             maxSize++;
         }
         else{
             node.next = front.next;
             front.next = node;
             maxSize++;
         }
     }
     //出队
     public QNode<T> pop(){
         if(isNull())
             return null;//队为空时，无法出队
         else if(maxSize==1){
             //队只有一个元素时直接初始化即可
             QNode<T> node  = front.next;
             initQueue();
             return node;
         }
         else{
             //准备工作
             QNode<T> p = front;//使用p指针来遍历队列
             for(int i=1;i<maxSize-1;i++)
                 p = p.next;
             //pop
             QNode<T> node = rear.next;
             rear.next = p.next;
             maxSize--;
             return node;
         }
     }
 
 }
 
 //链队结点
 class QNode<T>{
     private T data;//数据域
     public QNode<T> next;//指针域
 
     //初始化1
     public QNode(){
         this.data = null;
         this.next = null;
     }
     //初始化2
     public QNode(T data){
         this.data = data;
         this.next = null;
     }
     
     public T getData() {
         return data;
     }
     public void setData(T data) {
         this.data = data;
     }
 
 }
```
