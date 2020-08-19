> 上一篇文章使用C++对于二叉树数据结构进行简单的实现，附带将前中后序的遍历实现了，
这一篇文章主要来看一下层序遍历

#### 层序遍历

层序遍历是比较接近人的思维方式的一种遍历方法，将二叉树的每一层分别遍历，
直到最后的叶子节点被全部遍历完，这里要用到的辅助数据结构是队列，队列具有先进先出的性质。


#### 实现(C++)

```c++
#include <queue>
void levelOrder(Node* node){
    queue<Node*> q;
    if (node==NULL) {
        return;
    }
    q.push(node);
    while (!q.empty()) {
        cout<<q.front()->data<<" ";
        if (q.front()->lChildren!=NULL) {
           q.push(q.front()->lChildren);
        }
        if (q.front()->rChildren!=NULL) {
           q.push(q.front()->rChildren);
        }
        q.pop();
    }
}
```
             


