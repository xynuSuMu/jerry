
昨天使用二叉堆中最小堆构建最小优先级队列，但是遗漏掉一个面试题，因为二叉堆还可以用于解决TopK问题。

#### TopK问题

问题：从n个未排序的数中得到的最大的k个数，称为TopK问题。

对于这类问题，最简单的方案就是排序，比如快排(时间复杂度：O(nlogn))。

但是除此之外，我们还有一些其他的方案：

方案一：在内存空间能够完全存储数据的情况下(基于内存排序)，
可以改进快排，使用快排的分区(partition)函数，加上二分的思想找到TopK的数据，过程如下:

1.根据快排的思想，Partition函数可以得到索引值index，index索引前数据都比index对应数据大，索引后的数据都比index对应的数据小。

2.如果 如果index = K-1，说明划分完成，数组前k个数据即为最大的k个数

3.如果不等于，根据二分的思想，如果index > K - 1，那么使用Partition函数从[start = index+1,end]开始再次分区，
如果 index < K - 1，那么使用Partition函数到 [start ,end = index-1 ]再次分区，循环进入步骤一。

方案二：如果数据量过大，数据无法一次性加载进入内存，那么可以使用最小堆来解决，过程如下:

1.源数据取出前K个数据，构建成最小堆

2.依次从源数据取出K以后的数据，同堆顶元素heapTop 进行对比，如果小于heapTop，则丢弃；
如果大于则替换堆顶元素，然后执行下沉操作，使得堆顶元素heapTop变得最小。

3.当数据源数据全部取出后，堆中的元素就是Top K问题所需要的数据

代码(详细方法可参考优先级队列的构造)：
```java

    public static void main(String[] args) {
      PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
        int K = 3;
        Integer arrs[] = {10, 9, 1, 3, 4, 7, 8,15};
        //构成最小堆
        for (int i = 0; i < K; ++i) {
            priorityQueue.push(arrs[i]);
        }
        //
        for (int i = K; i < arrs.length; ++i) {
            if ((Integer) priorityQueue.elementData[0] < arrs[i])
                priorityQueue.elementData[0] = arrs[i];
            priorityQueue.downAdjust();
        }
        for (int i = 0; i < priorityQueue.size; ++i) {
            System.out.print(priorityQueue.elementData[i] + " ");
        }
    }


```
