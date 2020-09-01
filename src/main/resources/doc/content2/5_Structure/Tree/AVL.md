> 前面一篇文章实现了对于二叉搜索树的构建，通常情况下，二叉搜索树时间复杂度为O(logN)，但是如果构建二叉搜索树的数据相对有序，
那么最坏的情况，时间复杂的会变成O(N)，也就是链表的结构。所幸的是，还有一种平衡二叉树能解决这个问题，即AVL树，
AVL树中任何节点的两个子树的高度最大差别为1，所以它也被称为高度平衡树。增加和删除可能需要通过一次或多次树旋转来重新平衡这个树。
这一篇文章主要来说明下增加节点的旋转


#### 推导

AVL树的插入和二叉搜索树唯一的区别在于，当结点左右子树的深度最大差别为1时，我们需要一次旋转调整。

第一种:
```text


      9
     /
    7
   /
  6  
  
```

显然作为根结点的9左右子树深度已经超过1，那么此时就需要对该树进行调整，调整的过程很简单，将7作为根结点，9作为7的右结点。
由于二叉搜索树的特性，调整后其仍然符合二叉搜索树的性质

```text
     7
    / \
   6   9
```

第二种:

```text
     9
    / \
   8   11
         \
          12
            \
             13
```
这种情况，结点11首先失衡，将其安装第一种方式进行调整后，整体仍然是平衡二叉树

```text
     9
    / \
   8   12
      /  \
     11   13
```

第三种

```text
     9
    / \
   8   12
      /  \
     11   13
    /
   10
```
这种情况，结点12没有失衡，但是结点9失衡，这种情况我们无法直接将按照上面的方式调整称为平衡二叉树，因为如果将结点11挂在结点9的右子树，
结点12充当根结点，其整体仍然失衡，因为结点13深度太低，所以我们可以先提高结点13深度，达到类似第二种情况，在进行调整，即：

```text

     9                      9                        11
    / \                    / \                      / \
   8   12                 8   11                   9   12
      /  \   --->            /  \     --->        / \    \
     11   13                10   12              8   10   13
    /                              \
   10                               13

```

第四种:

```text
     9
    / \
   8   12
      /  \
     11   13
           \
           14
```

这种情况同第三种第二步处理方式即可

第五种，第六种

```text
     9                      9                     
    / \                    / \                   
   7   12                 6   12                 
  / \     --->           / \    
 6   8                  2   7           
/                          /     
4                         4        
```

这两种同第四种和第三种，只不过调整右子树换成了左子树

#### 实现代码(C++)

```c++
//
//  main.cpp
//  AVL
//
//  Created by 陈龙 on 2019/02/04.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
using namespace std;

struct Node{
    int data,height;
    Node *lChild,*rChild;
};
Node* newNode(int data){
    Node* node = new Node();
    node->data = data;
    node->height = 1;
    node->lChild = NULL;
    node->rChild = NULL;
    return node;
}
void updateHeight(Node* &root){
    root->height = max(root->lChild==NULL?0:root->lChild->height,
                       root->rChild==NULL?0:root->rChild->height)+1;
}
int getBalanceFactor(Node* root){
    int lh = 0;
    if (root->lChild!=NULL) {
        lh=root->lChild->height;
    }
    int rh=0;
    if (root->rChild!=NULL) {
        rh=root->rChild->height;
    }
    return lh-rh;
}
//返回左旋后的根节点
Node* L(Node* &root){
    Node *temp = root->lChild;
    root->lChild = temp->rChild;
    temp->rChild = root;
    //更新结点高度
    updateHeight(root);
    updateHeight(temp);
    return temp;
}
//返回右旋后的根节点
Node* R(Node* &root){
    Node *temp = root->rChild;
    root->rChild = temp->lChild;
    temp->lChild = root;
    //更新结点高度
    updateHeight(root);
    updateHeight(temp);
    return temp;
}
void insertNode(Node* &root,int data){
    if (root == NULL) {
         cout<<"初始化结点="<<data<<endl;
        root = newNode(data);
        return;
    }
    if (data>=root->data) {
       //插入右子树
        cout<<"插入右子树"<<data<<endl;
        insertNode(root->rChild, data);
        //更新树的高度
        updateHeight(root);
        //失衡
        if (getBalanceFactor(root)==-2) {
            cout<<"失衡"<<root->data<<endl;
            if(getBalanceFactor(root->rChild)>0){
              root->rChild= L(root->rChild);
              root=  R(root);
            }else{
           root=  R(root);
            }
        }
    }else{
        //插入左子树
         cout<<"插入左子树"<<data<<endl;
        insertNode(root->lChild, data);
        updateHeight(root);
        if (getBalanceFactor(root)==2) {
            cout<<"失衡"<<root->data<<endl;
            if(getBalanceFactor(root->lChild)<0){
                root->lChild=R(root->lChild);
               root= L(root);
            }else{
                root= L(root);
            }
        }
    }

}
void inOrder(Node* root){
    if (root ==NULL) {
        return;
    }
    inOrder(root->lChild);
    cout<<root->data<<" "<<root->height<<endl;
    inOrder(root->rChild);
}

int N;
int arr[1001];
Node* root=NULL;
int main(int argc, const char * argv[]) {
    // insert code here...
    cin>>N;
    for (int i=0; i<N; i++) {
        cin>>arr[i];
    }
    for (int i=0; i<N; i++) {
        insertNode(root, arr[i]);
    }
    
    inOrder(root);
    return 0;
}

```
