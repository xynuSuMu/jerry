> 本篇文章主要目的是讲述数据库索引的实现为什么选用B树(B-Tree)/B+树(B+Tree)，以及使用Java来实现一颗B树。

#### 数据库索引

对于多数的应用系统来说，查询数据的频率是远远高于写入或者更新数据的频率，在大数据量的场景中，常规的查询方式可能在效率上达不到预期，
此时我们需要对SQL查询语句做一些优化，或者对表做一些改动，比如增加索引字段，以此来达到我们想要的查询速度。

以MySQL的innodb存储引擎为例，在建立索引的时候，有两种选择，一种是BTree，一种是Hash。BTree指的是B+Tree，B+Tree是B-Tree的变种(优化)，而Hash
就容易理解，通过哈希值来查找记录，这种方式在特定场景下查询速度会比BTree要快很多，但是如果哈希冲突严重，或者范围查询的时候，性能就会下降，所以innodb存储引擎
默认索引是BTree。

#### B-Tree

上面我们说到MySQL索引方法采用B+Tree这种数据结构(常用的关系型数据库中，都是选择B+Tree的数据结构来存储数据)，
它是B-Tree数据结构的变种，所以了解B+Tree之前，还是需要对B-Tree做一些了解。我们先看下面一张图，它相对于其他的树：二叉搜索树，平衡二叉树，红黑树而言，
变胖了，所以B树也叫多路平衡树，因为在一个结点上它存储了更多的Key。
思考一下，为什么索引不用平衡二叉树或者红黑树？或者说变胖了的树比其他平衡树有哪些优势？

![图片](https://p3.pstatp.com/large/pgc-image/0cadbc112d4d4f1f9eaf9a1571107f63)


数据库将数据存储在磁盘中，读取磁盘数据速度要比内存要慢的多(无论是机械硬盘或者固态硬盘)，所以为了减少磁盘IO，通常会对数据进行预读
(根据局部性原理:当一个数据被用到时，其附近的数据也通常会马上被使用)，那么如果使用平衡二叉树这种数据结构来充当索引，会出现在平衡树中存储逻辑相近结点，
在物理地址上可能会相隔很远，就有可能会使得预读这一动作没有生效，而B树由于其结点存储多个Key，所以其结点大小可以设置为磁盘页的大小，来充分利用磁盘预读的功能。
但是我们需要注意的是内存中B树的查询不一定比其他平衡树要高效，只是它更合适数据库和文件系统。

为了测试B树的查询效率，这里我存储一百万的数据量，一个存储到B树，一个存储到List中，查询五十万的耗时
(B树一毫秒，遍历7毫秒。这个比较可能也没什么意义，但是这里我不清楚如何模拟磁盘操作，所以暂时先这样)。

![图片](https://p3.pstatp.com/large/pgc-image/a3061bac05bd43d0a504b72910d3b573)

#### B-Tree的定义

对于B树的定义通常有两种，一种是算法导论中的度数说；另一种是维基百科的阶数说。度与图论中的度(出度)很类似，指的是每个节点的子节点（子树）的个数就称为该节点的度，
阶定义为一个节点的子节点数目的最大值，这篇文章实现的B树是基于阶数来实现的，我们先来看一下相关概念

一颗m阶的B树定义如下：

1）根结点最少可以只有1个关键字。

2）每个结点最多有m-1个关键字。

3）非根结点至少有Math.ceil(m/2)-1个关键字。

4）每个结点中的关键字都按照从小到大的顺序排列，每个关键字的左子树中的所有关键字都小于它，而右子树中的所有关键字都大于它。

5）根结点到每个叶子结点的长度都相同。

从这5个定义我们可以得到一些结论：一个B树根结点可以只有一个关键字，当一个空树生成B树的时候，根结点不用考虑定义3，在新增关键字过程，需要对关键字进行排序，
来保证结点满足定义4，当结点新增关键字的个数达到m的时候，就违反了定义2，此时就需要进行操作，来保证结点满足定义2，这个操作我们叫做分裂，分裂过程中，我还需要注意，树的深度，
因为定义5规定根结点到每个叶子结点的长度都相同，

当我们对生成的B树进行删除Key时，就需要考虑定义3了，
当非根结点不满足至少有Math.ceil(m/2)-1个关键字时，我们需要从兄弟结点借关键字，或者与兄弟结点进行合并来保证至少有Math.ceil(m/2)-1个关键字。

下面我们来看具体如何实现一颗B-Tree(完整代码有点长，文章只附带部分代码，完整代码通过公众号加群获取)

#### 定义B-Tree实体

B-Tree组成:

Node：B-Tree的组成结点

Entry：结点中存储的关键字

SearchResult: 查询结果
```java
//K必须实现Comparable，用于二分查找的比较，
public class BTree<K extends Comparable, V> {
    
    //结点
    private static class Node<K extends Comparable, V> {
        //结点
        private List<Entry<K, V>> maps;
        //孩子
        private List<Node<K, V>> child;
        //是否为叶子
        private int leaf;

        private Node(int order) {
            maps = new ArrayList<>(order);
            child = new ArrayList<>(order);
            //默认非叶子结点
            this.leaf = 0;
        }
    }
    
    private static class Entry<K, V> {
        private K k;
        private V v;
        public Entry(K k, V v) {
            this.k = k;
            this.v = v;
        }            
    }
    private static class SearchResult<V> {
        private boolean exist;
        private int index;
        private V value;

        public SearchResult(boolean exist, int index, V value) {
            this.exist = exist;
            this.index = index;
            this.value = value;
        }
    }
}
```

#### 新增操作

我们以5阶B-Tree为例，插入3, 8, 31, 11, 23, 29, 50, 28, 1, 2，来看一下新增过程。

对于5阶的B树每个结点最多存储4个key，所以对于3，8，31，11的新增(排序)很好理解

![图片](https://p3.pstatp.com/large/pgc-image/496bcc5c81fc4893848c620a9bda81e0)

但是当我们新增23的时候，就会打破这种规则，所以我们需要对结点进行分裂，来保证定义2成立，分裂操作很简单，
将中间元素进行提取，充当父结点，左右元素一份为2，分别充当孩子结点。

![图片](https://p3.pstatp.com/large/pgc-image/0182d1491af34d7a9a0a1ea8292bcbb9)

我们继续新增29，50

![图片](https://p3.pstatp.com/large/pgc-image/c1bcf7c8a8ff4475ba4b0ba5d622d6dc)

新增到28的时候，又会出现分裂的动作，但是与第一次分裂不同，这个时候分裂出来的中间结点需要进行上升，然后左右元素，与父级上升的结点进行关联，
这样才能保证定义5成立:根结点到每个叶子结点的长度都相同

![图片](https://p3.pstatp.com/large/pgc-image/e6e8c662e782456b8164bb09ceeb4fc9)

继续新增1，2

![图片](https://p3.pstatp.com/large/pgc-image/0cadbc112d4d4f1f9eaf9a1571107f63)

到这里就新增完成了，可以发现为了保证关键字不超过m-1,需要进行分裂，而分裂的操作需要保证根结点到任意叶子结点距离是相等的

分裂代码:
```java
    //分裂一个满结点
    private void spilt(Node<K, V> parent, Node<K, V> node, int index) {
        //首先获取满结点的中间值
        int mid;
        if ((M & 1) == 0) {//偶数
            mid = M / 2 - 1;
        } else {//奇数
            mid = M / 2;
        }
        Entry<K, V> midEntry = node.maps.get(mid);
        //中间结点右边的新Node
        Node<K, V> rightNode = new Node<>(M);
        rightNode.leaf = node.leaf;
        for (int i = mid + 1; i < M; ++i) {
            //新的增加
            rightNode.insertEntry(new Entry<>(node.maps.get(i).k, node.maps.get(i).v));
        }
        for (int i = M - 1; i >= mid; --i) {
            //老的移除
            node.maps.remove(i);
        }
        //非叶子结点，将孩子结点进行分割，右结点加，左结点删
        if (node.leaf == 0) {
            //核心在于切割，当父级发生改动，那么孩子结点切割边界需要定义
            for (int i = mid + 1; i <= M; ++i) {
                rightNode.child.add(node.child.get(i));
            }
            for (int i = M; i > mid; --i) {
                node.child.remove(i);
            }
        }
        //增加分裂后的结点
        parent.insertEntry(midEntry);
        parent.child.add(index + 1, rightNode);
    }
```

#### 删除操作

相对于新增操作，删除操作比新增要复杂一些，因为新增只涉及到分裂，但是删除会涉及到替换结点，借结点，合并结点等操作。
这些操作的目的同样是为了保证结构满足定义

首先看替换结点，当我们要删除非叶子结点的时候，比如图上的29，我们使用它的前继或者后继的结点来替换它，这里前继/后继指的是，
中序遍历结果的前一个/后一个结点，而且在B树中，该结点必然是叶子结点，比如29的前继结点是28，后继结点是31。

当上面替换成功之后，我们就要把当前的叶子结点删掉，删除动作很简单，但是
当我们把关键字删掉之后，需要判断当前结点的关键字是否满足我们所说的至少有Math.ceil(m/2)-1个的要求，如果满足，删除成功，不满足时为了保证
满足这样的条件，我们看看兄弟结点有没有富裕(大于Math.ceil(m/2)-1)的关键字，给他借过来。这一步叫借结点。

那么合并结点就是当兄弟结点不足的时候，我们需要找兄弟结点来进行合并，从而满足B树的定义。

下面具体看下实现过程:

首先我们删除关键字8：因为是叶子结点，且删除后关键字满足 >=Math.ceil(m/2)-1 的条件

![图片](https://p3.pstatp.com/large/pgc-image/9a5f51172435449a877c1b5862f46bd6)

我们继续删除关键字29：首先找到他的前继结点关键字28来替换删除

![图片](https://p3.pstatp.com/large/pgc-image/0ddc00a3db1e4635a07661eb68369539)

删除之后我们会发现当前结点关键字已经不足2了，所以需要借结点或者合并结点，我们会发现，左兄弟结点是富裕的，所以可以向左兄弟结点借关键字。
借的过程：父结点下移一个关键字，左兄弟结点上移一个关键字，从而使B树平衡。

![图片](https://p3.pstatp.com/large/pgc-image/ebb3df004ec04d03be2a624f26ec1c48)

删完29之后，我们再来删除关键字11，此时兄弟结点没有富裕的关键字来让该结点满足 >=Math.ceil(m/2)-1 的条件，所以需要进行结点合并，
这里我们选择合并左孩子来进行合并，合并过程：将父结点关键字下移，到合并结点，失衡的被合并结点关键字插入到合并结点，父结点删除失衡结点。
由于父结点是根结点，所以即使一个关键字也符合要求。

![图片](https://p3.pstatp.com/large/pgc-image/85ac4cd9168242e39860f1c11b7e0524)


到这里就把B树的增加和删除操作给完成了，搜索的过程就较为简单了采用二分法+递归即可。

#### 总结

在实现B树的过程中，有一点容易让人误解的是孩子结点与父亲结点的关系，实际上孩子是整个结点的孩子，而不是结点某个关键字的孩子。

![图片](https://p3.pstatp.com/large/pgc-image/481d79c2e97547eb95b39256c1c9825d)

但是孩子结点与父亲结点关键字之间是存在一定的关系的，比如父结点有两个关键字，那么就会有是三个孩子，而父结点关键字所在的索引序号，比如下标是0，
那么孩子中下标为0的结点所有关键字都会小于父亲结点下标为0的关键字，如下图，这也是为什么使用二分可以找到相应关键字的原因。

![图片](https://p3.pstatp.com/large/pgc-image/055c06a236bc4d80812973aac961c01c)

代码这里就不放了，太长也没人看，需要完整代码可以通过公众号:每天学Java，来加群获取。


