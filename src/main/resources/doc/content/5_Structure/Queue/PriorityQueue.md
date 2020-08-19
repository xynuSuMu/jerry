#### 队列

队列是一种受限的线性表，对于大部分线性表而言，通常除了第一个和最后一个数据元素之外，其它数据元素都是首尾相接的，对于队列而言，与普通的线性表有两点不同，其一，先来的元素在队列首,后来的只能在末尾,不允许插队。其二，只允许在表的前端（front）进行删除操作，而在表的后端（rear）进行插入操作(也就是FIFO，先进先出)。

实现一个队列方式一般有两种：数组和链表。

如下图：

![image](https://p3.pstatp.com/large/pgc-image/1657cc27ed654218a9b014a215858caa)

![image](https://p3.pstatp.com/large/pgc-image/c6696105a83d481c94a1437075ce3499)

使用数组实现的队列，在移除队首元素后，需要将后续元素整体前移，而使用链表只需要让队首元素的下一个元素充当
头节点即可。

#### 优先级队列

优先级队列与普通队列的不同，优先级队列不再遵循FIFO的规则，而是按照自定义规则(优先级高低)将对应元素取出队列，比如取出优先级高的元素，或者淘汰优先级低的元素。

要实现这种功能，一般有两种方案，一种是在入队列时，根据入队元素的优先级，按规则放入相应位置，比如一个最大优先级数据/最小优先级数据即使入队列最晚，但是要放在队列的首位；另一种方案，入队列时依旧放在队列的末尾，在出队列的时候，再按照优先级比较，然后将优先级高的取出队列。

按照第一种方案，使用最小优先的规则，那么入队列如下：

![image](https://p3.pstatp.com/origin/pgc-image/021b8466424144eea30435335a29c7c2)

要达到这种效果，我们通常可以在入队列时，使用比较插入的方法实现，但是最坏的情况时间复杂度为O(n);

所以通常优先级队列并不选用线性表来实现，而是使用二叉堆(可以认为是完全二叉树结构)来实现。

注：也有使用其他堆实现优先队列，比如左式堆和d-堆(d-Heaps)，但是二叉堆实现简单，所以需要优先队列的时候几乎总是使用二叉堆。

二叉堆分为最小堆和最大堆，其中最小堆的堆顶元素是整个堆的最小元素(任一节点元素都小于左右孩子节点，至于左右孩子谁大谁小则不关心)，而最大堆的堆顶元素是最大元素(任一节点元素都大于左右孩子节点)

如下(最小堆):

![image](https://p3.pstatp.com/origin/pgc-image/db3db61469e84401858be88c21cafa49)

而出列过程如下:

![image](https://p3.pstatp.com/origin/pgc-image/dc84420169df4a429ad1dad76940c54f)

既然堆是一种完全二叉树，那么可以使用数组来实现(如果父节点是第一个元素size = 1，那么左孩子是 第2 * size 个元素，右孩子是第2 * size+1个元素，注意这里size不是下标)，这里以最小堆实现优先级别队列为例。

```java

public class PriorityQueue<E extends Comparable> {

    private int size;

    private Object[] elementData;

    public PriorityQueue() {
        //默认16
        elementData = new Object[16];
        size = 0;
    }

}
```

入队列：

首先考虑扩容的情况，如果数组容量达到最大，将其扩容两倍(或者按照ArrayList的方式，原来基础上扩容一半
oldCapacity + (oldCapacity >> 1))。

其次执行上滤操作，如果元素比父节点大，则向上替换，代码如下：
```java

    public boolean push(E e) {
        if (size >= elementData.length)
            //扩容
            resize();
        elementData[size++] = e;
        //大于1则上滤调整
        if (size > 1)
            percolateUp();
        return true;
    }
    
    
    private void resize() {
        elementData = Arrays.copyOf(elementData, size << 1);
    }


    private void percolateUp() {
        int childIndex = size - 1;
        //完全二叉树特征，
        int parentIndex = (size >> 1) - 1;
        E temp = (E) elementData[childIndex];
        while (childIndex > 0 && temp.compareTo(elementData[parentIndex]) < 0) {
            elementData[childIndex] = elementData[parentIndex];
            childIndex = parentIndex;
            parentIndex = childIndex >> 1;
        }
        elementData[childIndex] = temp;
    }

```

出队列：

首先考虑队列是否存在元素，不存在则抛出异常；
其次将堆顶元素和最右叶子结点替换，选用最右叶子的原因是维护完全二叉树的结构
最后就是下沉，最右叶子结点升为堆顶时，比较左右孩子，如果优先级比最小的孩子大，那么下沉，
如果优先级不大于最小的孩子，那么说明满足最小堆的性质，下沉结束。

```java

    public E front() throws Exception {
        if (size == 0)
            throw new Exception("the priorityQueue is empty !");
        //堆顶和最后元素交换位置，如此才能保证完全二叉树的结构
        E heapTop = (E) elementData[0];
        elementData[0] = elementData[--size];
        elementData[size] = null;
        if (size > 1)//下沉调整
            downAdjust();
        return heapTop;
    }
    
    
    
    private void downAdjust() {
        int parentIndex = 0;
        E temp = (E) elementData[parentIndex];
        int childIndex = 1;
        while (childIndex < size) {
            //如果有右孩子，且右孩子更小，则优先下沉到右
            if (childIndex + 1 < size && ((E) elementData[childIndex]).compareTo(elementData[childIndex + 1]) > 0) {
                childIndex++;
            }
            //父节点小于任何孩子，则完成下沉
            if (temp.compareTo(elementData[childIndex]) < 0)
                break;
            elementData[parentIndex] = elementData[childIndex];
            parentIndex = childIndex;
            //左孩子
            childIndex = (childIndex << 1) + 1;
        }
        elementData[parentIndex] = temp;
    }
    
```

#### 总结

1.优先级队列并不遵循FIFO规则，除非入队优先级是有序的(根据最大优先级队列或者最小优先级性质有序)

2.优先级队列的首先不一定是二叉堆，也可以是左序堆或者d-堆

3.完全二叉树不会浪费数组空间，所以可以使用数组表示
