#### Java实现一个简单的栈

> 代码来源:https://www.cnblogs.com/midiyu/p/8168618.html

#### 基于数组实现的顺序栈
```java
/**
 * 基于数组实现的顺序栈
 * @param <E>
 * 
 * top:栈顶指针，初始化top=-1
 * 入栈：data[++top]=e
 * 出栈：(E)data[top--]
 * 得到栈顶元素：(E)data[top]
 * 空：top=-1
 */
public class Stack<E> {
    private Object[] data = null;
    private int maxSize=0;   //栈容量
    private int top =-1;  //栈顶指针
    
    /**
     * 构造函数：根据给定的size初始化栈
     */
    Stack(){
        this(10);   //默认栈大小为10
    }
    
    Stack(int initialSize){
        if(initialSize >=0){
            this.maxSize = initialSize;
            data = new Object[initialSize];
            top = -1;
        }else{
            throw new RuntimeException("初始化大小不能小于0：" + initialSize);
        }
    }
    
    
    
    //进栈,第一个元素top=0；
    public boolean push(E e){
        if(top == maxSize -1){
            throw new RuntimeException("栈已满，无法将元素入栈！");
        }else{
            data[++top]=e;
            return true;
        }    
    }
    
    //查看栈顶元素但不移除
    public E peek(){
        if(top == -1){
            throw new RuntimeException("栈为空！");
        }else{
            return (E)data[top];
        }
    }
    
    //弹出栈顶元素
    public E pop(){
        if(top == -1){
            throw new RuntimeException("栈为空！");
        }else{
            return (E)data[top--];
        }
    }
    
    //判空
    public boolean empty(){
        return top==-1 ? true : false;
    }
    
    //返回对象在堆栈中的位置，以 1 为基数
    public int search(E e){
        int i=top;
        while(top != -1){
            if(peek() != e){
                top --;
            }else{
                break;
            }
        }
        int result = top+1;
        top = i;
        return result;      
    }
    
    public static void main(String[] args) {
        Stack<Integer> stack=new Stack<>();
        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }
        for (int i = 0; i < 5; i++) {
            System.out.print(stack.pop()+" ");
        }
    }
}
```

#### 栈的链式存储结构实现

```java
package com.myutil.stack;

public class LinkStack<E> {
    //链栈的节点
    private class Node<E>{
        E e;
        Node<E> next;
        
        public Node(){}
        public Node(E e, Node next){
            this.e = e;
            this.next = next;
        }
    }
    
    private Node<E> top;   //栈顶元素
    private int size;  //当前栈大小
    
    public LinkStack(){
        top = null;
    }
    
    
    //入栈：让top指向新创建的元素，新元素的next引用指向原来的栈顶元素
    public boolean push(E e){
        top = new Node(e,top);
        size ++;
        return true;
    }
    
    //查看栈顶元素但不删除
    public Node<E> peek(){
        if(empty()){
            throw new RuntimeException("空栈异常！");
        }else{
            return top;
        }
    }
    
    //出栈
    public Node<E> pop(){
        if(empty()){
            throw new RuntimeException("空栈异常！");
        }else{
            Node<E> value = top; //得到栈顶元素
            top = top.next; //让top引用指向原栈顶元素的下一个元素 
            value.next = null;  //释放原栈顶元素的next引用
            size --;
            return value;
        }
    }
    
  //当前栈大小
    public int length(){
        return size;
    }
    
    //判空
    public boolean empty(){
        return size==0;
    }
    
    public static void main(String[] args) {
        LinkStack<Integer> stack=new LinkStack<>();
        for (int i = 0; i < 5; i++) {
            stack.push(i);
        }
        for (int i = 0; i < 5; i++) {
            System.out.print(stack.pop().e+" ");
        }
    }
}
```
