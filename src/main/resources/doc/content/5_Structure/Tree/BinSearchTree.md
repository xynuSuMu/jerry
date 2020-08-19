> 二叉搜索树是一种特殊的二叉树，在前面我们实现二叉树的时候，构建左右子结点的时候是随意构建的，但是二叉搜索树规定了左右子结点的构造:
若它的左子树不空，则左子树上所有结点的值均小于它的根结点的值； 若它的右子树不空，则右子树上所有结点的值均大于它的根结点的值； 它的左、右子树也分别为二叉排序树

#### 

二叉排序树的查找过程和次优二叉树类似，通常采取二叉链表作为二叉排序树的存储结构。
中序遍历二叉排序树可得到一个关键字的有序序列，一个无序序列可以通过构造一棵二叉排序树变成一个有序序列，构造树的过程即为对无序序列进行排序的过程。
每次插入的新的结点都是二叉排序树上新的叶子结点，在进行插入操作时，不必移动其它结点，只需改动某个结点的指针，由空变为非空即可。
搜索,插入,删除的复杂度等于树高，O(log(n)).

#### 实现(C++)

```c++
//
//  main.cpp
//  BST
//
//  Created by 陈龙 on 2019/02/04.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <queue>
using namespace std;

struct Node{
    int data;
    Node* lChild;
    Node* rChild;
}node;
Node* root=NULL;

Node* newNode(int data){
    Node* node = new Node;
    node->data=data;
    node->lChild=NULL;
    node->rChild=NULL;
    return node;
}
void insert(Node* &root,int data){
    if (data<root->data) {
        if(root->lChild==NULL){
            root->lChild = newNode(data);
            return;
        }
        insert(root->lChild, data);
    }else if(data>root->data){
        if(root->rChild==NULL){
            root->rChild = newNode(data);
            return;
        }
        insert(root->rChild, data);
    }
    return;
}
void createTree(int data){
    if (root==NULL) {
        root = newNode(data);
        return;
    }
    insert(root,data);
}

void inOrder(Node* root){
    if (root ==NULL) {
        return;
    }
    inOrder(root->lChild);
    cout<<root->data<<" ";
    inOrder(root->rChild);
}

int search(Node* root,int data){
    if (root == NULL) {
        return -1;
    }
    if (root->data==data) {
        return data;
    }
    if (root->data>data) {
         return search(root->lChild,data);
    }else{
        
         return search(root->rChild,data);
    }
}

int N;
queue<int> que;
int main(int argc, const char * argv[]) {
    // insert code here...
    cin>>N;
    
    int data;
    for (int i=0; i<N; i++) {
        cin>>data;
        que.push(data);
    }
    
    while (!que.empty()) {
        createTree(que.front());
        que.pop();
    }
    inOrder(root);
    
    int d =  search(root,3);
    cout<<d<<endl;
    return 0;
}

```
