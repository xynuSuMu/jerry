#### Java实现一个简单的但链表

> 代码来源:https://www.cnblogs.com/karrya/p/10881591.html

```java


/**
 * 链表结构实体类
 */
public class Node {
    Node next = null; //下一节点
    int data;         //节点数据

    public Node(int data) {
        this.data = data;
    }
}
```

```java
/**
 * 链表功能实现
 */
public class Linked_List {

    Node head = null;    //链表头结点

    /**
     * 1、求链表长度：以备后续用
     * 思路：遍历一个节点，记录长度加一（注意：初始长度给0）
     *
     * @return int：返回长度
     */
    public int get_length() {
        int list_length = 0;
        Node cur_node;
        cur_node = head;
        while (cur_node != null) {
            list_length++;
            cur_node = cur_node.next;
        }
        return list_length;
    }

    /**
     * 2、添加
     * 思路：找到链表的末尾节点，把新的数据节点添加在后面
     *
     * @param date ：插入的数据
     */
    public void add_Node(int date) {
        Node newnode = new Node(date);
        if (head == null) {     //判断是否只有一个头结点
            head = newnode;
            return;
        } else {
            Node temp = head;
            while (temp.next != null) {  //不为头节点，找到最后一个节点
                temp = temp.next;
            }
            temp.next = newnode;
        }
    }

    /**
     * 3、删除
     * 思路：传入下标，根据下标，找到相应的节点,删除节点的前一节点指向删除节点下一节点
     *
     * @param index：删除的数据的下标
     * @return
     */
    public boolean delete_Node(int index) {
        if (index < 1 || index > get_length()) {
            System.out.println("你所要删除的元素下标输入有问题");
            return false;
        }
        if (index == 1) {
            head = head.next;
            return true;
        }
        Node pre_node = head;
        Node cur_node = pre_node.next;
        int i = 2;
        while (pre_node != null) {
            if (i == index) {
                pre_node.next = cur_node.next;
                return true;
            } else {
                pre_node = pre_node.next;
                cur_node = cur_node.next;
                i++;
            }
        }
        return true;
    }

    /**
     * 4、根据输入数据求下标
     * @param data:输入的数据
     * @return int：返回输入数据下标
     */
    public int get_data(int data) {
        Node node = head;
        int index = 0;
        while (node != null){
            if (node.data == data){
                return index;
            }
            node = node.next;
            index++;
        }
        return -1;
    }
    /**
     * 5、打印显示输出
     */
    public void print_linklist() {
        Node node = head;
        while (node != null) {
            System.out.print(node.data + " ");
            node = node.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        
        //1、创建链表
        Linked_List linked_list = new Linked_List();
        //2、添加链表元素
        linked_list.add_Node(4);
        linked_list.add_Node(8);
        linked_list.add_Node(7);
        linked_list.add_Node(1);
        //3、打印添加的元素
        linked_list.print_linklist();
        //4、删除下标元素
        linked_list.delete_Node(4);
        linked_list.print_linklist();
        
        //5、获取链表长度
        System.out.println("链表长度 "+ linked_list.get_length());
       
        int index = linked_list.get_data(1);
        
        if (index == -1){
            System.out.println("你要查找的数据不存在");
        }else{
            System.out.println("你要查找的数据元素下标 " + index);
        }
    }
}
```


