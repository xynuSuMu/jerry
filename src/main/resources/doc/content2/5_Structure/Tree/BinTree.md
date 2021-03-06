#### 二叉树

```text
二叉树是n(n>=0)个结点的有限集合，该集合或者为空集（称为空二叉树），或者由一个根结点和两棵互不相交的、分别称为根结点的左子树和右子树组成
```

特点:

```text
由二叉树定义以及图示分析得出二叉树有以下特点：
1）每个结点最多有两颗子树，所以二叉树中不存在度大于2的结点。
2）左子树和右子树是有顺序的，次序不能任意颠倒。
3）即使树中某结点只有一棵子树，也要区分它是左子树还是右子树。

```

性质:

```text
在二叉树的第i层上最多有2^(i-1) 个节点 （满二叉树的每一层都符合这个条件）
二叉树中如果深度为k,那么最多有(2^k)-1个节点。(k>=1）  (最多的情况:2^0+2^1+...+2^(k-1)  =(2^k)-1  推导公式:S=2S-S)
n0=n2+1 n0表示度数为0的节点数，n2表示度数为2的节点数 (N=N0+N1+N2   n-1条边= 2*n2 + 1*n1   )


```

#### 特殊的二叉树

* 满二叉树:每一层的节点个数都达到该层最大节点数 即2^(i-1)
* 完全二叉树:除了最底层外，其他每一层都达到该层的最大节点数，且最底层从左至右连续存在若干节点

二叉树的存储结构:

顺序表存储，通常适用于完全二叉树

二叉树的顺序存储结构就是使用一维数组存储二叉树中的结点，并且结点的存储位置，就是数组的下标索引，如果不是完全二叉树，造成空间浪费。


二叉链表存储:一般采用二叉链表作为存储结构

动态链表:

```c++
typedef struct BiTNode{
    TElemType data;//数据
    struct BiTNode *lchild, *rchild;//左右孩子指针
} BiTNode, *BiTree;
```

静态链表:
```c++
struct Node{
typename data;
int lchild;
int rchild;
}node[maxn]
```


