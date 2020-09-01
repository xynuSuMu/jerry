> 完成平衡二叉树的构建之后，继续研究一种新的树:哈夫曼树，哈夫曼树又称最优二叉树。
哈夫曼树是一种带权路径长度最短的二叉树。所谓树的带权路径长度，就是树中所有的叶结点的权值乘上其到根结点的路径长度（若根结点为0层，叶结点到根结点的路径长度为叶结点的层数）。树的带权路径长度记为WPL=(W1*L1+W2*L2+W3*L3+...+ Wn*Ln)，N个权值Wi(i=1,2,...n)构成一棵有N个叶结点的二叉树，相应的叶结点的路径长度为Li(i=1,2,...n)。可以证明哈夫曼树的WPL是最小的。


#### 构造

特性:对于具有n个叶子节点的哈夫曼树，共有2*n-1个节点(哈夫曼树不存在度为1的结点)

构造哈夫曼树的算法如下：

1）对给定的n个权值{W1,W2,W3,...,Wi,...,Wn}构成n棵二叉树的初始集合F={T1,T2,T3,...,Ti,..., Tn}，
其中每棵二叉树Ti中只有一个权值为Wi的根结点，它的左右子树均为空。

2）在F中选取两棵根结点权值最小的树作为新构造的二叉树的左右子树，新二叉树的根结点的权值为其左右子树的根结点的权值之和。

3）从F中删除这两棵树，并把这棵新的二叉树同样以升序排列加入到集合F中。

4）重复2）和3），直到集合F中只有一棵二叉树为止。

结构体中对于结点的构建增加了next，用于我们构建链表(排序后)，每次将链表前两位取出，构建新的树放入链表中

#### 实现代码(C++)

```c++
//
//  main.cpp
//  huffman
//
//  Created by 陈龙 on 2019/02/05.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
using namespace std;

struct Node{
    int data;
    Node *rChild,*lChild;
    Node *next;
};

Node* newNode(int data){
    Node *node = new Node;
    node->data = data;
    node->rChild=NULL;
    node->lChild=NULL;
    node->next=NULL;
    return node;
};

//每次取出链表的前两位构建二叉树，并移除结点，增加新的二叉树
Node* haffman(Node* root){
    Node* header  = root;
    Node* haffman = NULL;
 
    while (header!=NULL) {
        if (header->next==NULL) {
            //哈夫曼树构建完成
            cout<<"哈夫曼树构建完成";
            break;
        }else{
            Node* first = header;
            Node* second = header->next;
            Node* temp = newNode(first->data+second->data);
            cout<<temp->data<<" "<<first->data<<" "<<second->data<<endl;
            temp->lChild=first;
            temp->rChild=second;
            haffman = temp;
            //移除-QC:这里应该按照大小放入链表合适位置，可以采用优先级队列
            haffman->next = second->next;
            
            header = haffman;
        }
    }
    return haffman;
}

void inOrder(Node* root){
    if (root ==NULL) {
        return;
    }
    inOrder(root->lChild);
    cout<<root->data<<" "<<endl;
    inOrder(root->rChild);
}

int N;
Node* root = NULL;
int main(int argc, const char * argv[]) {
    // insert code here...
    cin>>N;//结点个数
    int power;
    //构建链表并排序
    for(int i=0;i<N;i++){
        cin>>power;
        Node* node = newNode(power);
        if (root==NULL || power<=root->data) {
            node->next = root;
            root = node;
        }else{
            Node *header = root;
            while (header!=NULL) {
                if (header->next==NULL) {
                    header->next = node;
                     break;
                }
                if(node->data<=header->next->data){
                    //
                    node->next = header->next;
                    header ->next = node;
                    break;
                }
                header = header->next;
            }
        }

    }
    //
    Node* foreach = root;
    while (foreach!=NULL) {
        cout<<foreach->data<<endl;
        foreach = foreach->next;
    }
    
    //
    Node* haff = haffman(root);
    
    //
    
    inOrder(haff);
    
    return 0;
}

```
