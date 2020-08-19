#### 题目

A linked list consists of a series of structures, which are not necessarily adjacent in memory. 
We assume that each structure contains an integer key and a Next pointer to the next structure.
Now given a linked list, you are supposed to sort the structures according to their key values in increasing order.

链表由一系列在内存中不一定相邻的结构组成。
我们假设每个结构都包含一个整数键和指向下一个结构的下一个指针。
现在给定一个链表，您应该根据键值按递增顺序对结构进行排序。

**Input Specification:**

Each input file contains one test case. 

For each case, the first line contains a positive N (< 105) and an address of the head node, 
where N is the total number of nodes in memory and the address of a node is a 5-digit positive integer. 
NULL is represented by -1.

对于每种情况，第一行包含一个正的N(< 105)和一个head节点的地址，
其中N为内存中的节点总数，节点地址为5位正整数。
NULL由-1表示。

Then N lines follow, each describes a node in the format:

Address Key Next

where Address is the address of the node in memory, Key is an integer in [-105, 105], 
and Next is the address of the next node. 

It is guaranteed that all the keys are distinct and there is no cycle in the linked list starting from the head node.

它保证所有的键都是不同的，并且链表中没有从head节点开始的循环。

**Output Specification:**

For each test case, the output format is the same as that of the input, 
where N is the total number of nodes in the list and all the nodes must be sorted order.

**Sample Input:**

```text

5 00001
11111 100 -1
00001 0 22222
33333 100000 11111
12345 -1 33333
22222 1000 12345
```
**Sample Output:**
```text
5 12345
12345 -1 00001
00001 0 11111
11111 100 22222
22222 1000 33333
33333 100000 -1
```

#### 解析
题目的意思不难理解，就是对链表进行排序，输入格式第一行节点数:N，头节点位置。
后面每一行 第一个是节点地址，第二个为节点存储的值，第三个为指向节点的地址。

考虑到无效的结点，所以另其数组，将有效节点进行保存。

防止大数据量的运行超时，使用：ios::sync_with_stdio(false);

#### 答案(C++) PAT已验证通过

这里采用静态链表的实现方式

```c++
//
//  main.cpp
//  LinkedListSort
//
//  Created by 陈龙 on 2019/12/23.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <cmath>
#include <iomanip>
#include<algorithm>
using namespace std;

const int maxN = 100001;
struct Node{
    int address;
    int key;
    int next;
}node[maxN];

bool cmp(Node a,Node b){
    return a.key<b.key;
}
int main(int argc, const char * argv[]) {
    // insert code here...
    int headAddress,N;
    //防止大数据超时
    ios::sync_with_stdio(false);
    cin>>N>>headAddress;
    
    int address,key,next;
    for (int i=0; i<N; i++) {
        cin>>address>>key>>next;
        node[address].address= address;
        node[address].key = key;
        node[address].next = next;
    }
    
    int p = headAddress;
    //构建有效节点
    int count = 0;
    Node v[N];
    while (p!=-1) {
        v[count] = node[p];
        count++;
        p = node[p].next;
    }
    //sort
    if (count==0) {
        cout<<"0 -1";
    }else{
    sort(v, v+count, cmp);
    cout<<count<<" "<<setw(5)<<setfill('0')<<v[0].address<<endl;
    for(int i=0;i<count;i++){
        if (i==count - 1) {
        cout<<setw(5)<<setfill('0')<<v[i].address<<" "<<v[i].key<<" "<<-1<<endl;
        }else{
        cout<<setw(5)<<setfill('0')<<v[i].address<<" "<<v[i].key<<" "<<setw(5)<<setfill('0')<<v[i+1].address<<endl;
        }
      }
    }
    return 0;
}


```
