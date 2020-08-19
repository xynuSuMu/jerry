#### 构建二叉树

对于普通二叉树的构建，在左右子树的插入时候，需要根据实际情况和要求去做对应的调整。
这里只考虑最简单的情况:即知道父结点以及是左子树还是在右子树

```c++
//
//  main.cpp
//  动态链表实现BinTree
//
//  Created by 陈龙 on 2019/01/31.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
using namespace std;

struct Node{
    int data;
    Node *lChildren,*rChildren;
}BinNode;
Node* root = NULL;
Node* newNode(int data){
    Node* node = new Node;
    node->data = data;
    node->lChildren=NULL;
    node->rChildren=NULL;
    return node;
}
Node* search(Node* node,int currentData){
    if (node == NULL) {
        return NULL;
    }
    if (node->data == currentData) {
        cout<<"parenNode="<<node->data;
        return node;
    }

   Node* left =  search(node->lChildren,currentData);
   Node* right = search(node->rChildren,currentData);
   return left==NULL?right:left;
}
void insert(Node* &node,int parentData,int currentData,bool isLeft){
    if(root==NULL){
        cout<<"创建头结点";
        root = newNode(currentData);
        return;
    }
    Node* parentNode = search(root,parentData);
    if (isLeft) {
        //覆盖左子节点
        if (parentNode->lChildren==NULL) {
            parentNode->lChildren = newNode(currentData);
        }else{
            parentNode->lChildren->data = currentData;
        }
    }else{
        //覆盖画右子节点
        if (parentNode->rChildren==NULL) {
            parentNode->rChildren = newNode(currentData);
        }else{
            parentNode->rChildren->data = currentData;
        }
    }
    
}
//先序遍历
void preorder(Node* root){
    if (root ==NULL) {
        return;
    }
    cout<<root->data<<" ";
    preorder(root->lChildren);
    preorder(root->rChildren);
}
//中序遍历
void inOrder(Node* root){
    if (root ==NULL) {
        return;
    }
    inOrder(root->lChildren);
    cout<<root->data<<" ";
    inOrder(root->rChildren);
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
    // insert code here...
    // 创建二叉树 （1，-1） （2,1） (3,1) (4,2) (5,2) (6,3) (7,3) (8,4)
    // 创建头结点
    insert(root, -1, 1, true);
    // 第二层
    insert(root, 1, 2, true);
    insert(root, 1, 3, false);
    //第三层
    insert(root, 2, 4, true);
    insert(root, 2, 5, false);
    insert(root, 3, 6, true);
    insert(root, 3, 7, false);
    //第四层
    insert(root,4, 8,true);
    cout<<" "<<endl;
    preorder(root);
    cout<<" "<<endl;
    inOrder(root);
    cout<<" "<<endl;
    postorder(root);
}

```
