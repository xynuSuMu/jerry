> 前面两篇文章实现了二叉树的前、中、后、层序遍历的方法，在这些遍历方法中中、后、层序遍历任意一种方法和中序遍历结合都能推导出树的结构，
鉴于其实现方式相似，这一篇文章实现了已知中、后序遍历来推导树的结构

#### 推导过程

这里以下面一组遍历结果作为推导基础

```text
中序:8 4 2 5 1 6 3 7
后序:8 4 5 2 6 7 3 1
```

一:通过后序我们可以知道根结点为1。

二:通过第二步的根结点，将中序分为左子树:8 4 2 5 ，右子树:6 3 7

三:通过第二步，得到左右子树的中/后序 8 4 2 5和 8 4 5 2 、6 3 7 和 6 7 3。

四:重复第一步。



#### 实现代码(C++)

```c++
//
//  main.cpp
//  PrePostToTree
//
//  Created by 陈龙 on 2019/02/02.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
using namespace std;
struct Node{
    int data;
    Node *lChildren,*rChildren;
}BinNode;
Node* root = NULL;
int N;
int inOrder[101];
int postOrder[101];
Node* newNode(int data){
    Node* node = new Node;
    node->data = data;
    node->lChildren=NULL;
    node->rChildren=NULL;
    return node;
}
Node* createTree(Node* &root,int postStart,int postEnd,int inStart,int inEnd){
    if (inStart>inEnd) {
        return NULL;
    }
    //跟结点
    cout<<" "<<postStart<<" "<<postEnd<<" "<<inStart<<" "<<inEnd<<endl;
    int rootData = postOrder[postEnd];
    if (root==NULL) {
        root = newNode(rootData);
    }
    //得到中序根结点索引位置
    int pos = 0;
    for(pos=inStart;pos<=inEnd;pos++){
        if (inOrder[pos]==rootData) {
            break;
        }
    }
    //左结点的个数
    int numLeft = pos-inStart;
    //左子树构建
    root->lChildren = createTree(root->lChildren,postStart, postStart+numLeft-1, inStart,pos-1);
    //右子树
    root->rChildren =createTree(root->rChildren,postStart+numLeft,postEnd-1,pos+1,inEnd);
    return root;
}
//中序遍历
void inOrderSort(Node* root){
    if (root ==NULL) {
        return;
    }
    inOrderSort(root->lChildren);
    cout<<root->data<<" ";
    inOrderSort(root->rChildren);
}
//后序遍历
void postorder(Node* root){
    if (root ==NULL) {
        return;
    }
    postorder(root->lChildren);
    postorder(root->rChildren);
    cout<<root->data<<" ";
}
int main(int argc, const char * argv[]) {
//    输入案例:
//    8
//    中序
//    8 4 2 5 1 6 3 7
//    后序
//    8 4 5 2 6 7 3 1
    cin>>N;
    cout<<"中序"<<endl;
    for (int i=0; i<N; i++) {
        cin>>inOrder[i];
    }
    cout<<"后序"<<endl;
    for (int i=0; i<N; i++) {
        cin>>postOrder[i];
    }
    createTree(root,0,N-1,0,N-1);
    return 0;
}

```
