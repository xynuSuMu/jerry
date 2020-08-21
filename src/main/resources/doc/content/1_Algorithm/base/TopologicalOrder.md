#### 拓扑排序
对一个有向无环图(Directed Acyclic Graph简称DAG)G进行拓扑排序，是将G中所有顶点排成一个线性序列，使得图中任意一对顶点u和v，
若边<u,v>∈E(G)，则u在线性序列中出现在v之前。
通常，这样的线性序列称为满足拓扑次序(Topological Order)的序列，简称拓扑序列。
简单的说，由某个集合上的一个偏序得到该集合上的一个全序，这个操作称之为拓扑排序。

#### 应用
拓扑排序常用来确定一个依赖关系集中，事物发生的顺序。
例如，在日常工作中，可能会将项目拆分成A、B、C、D四个子部分来完成，
但A依赖于B和D，C依赖于D。为了计算这个项目进行的顺序，可对这个关系集进行拓扑排序，得出一个线性的序列，则排在前面的任务就是需要先完成的任务。

拓扑排序的结果并不是唯一的。

#### 实现步骤

1.在有向图中选一个没有前驱的顶点并且输出
2.从图中删除该顶点和所有以它为尾的弧（白话就是：删除所有和它有关的边）
3.重复上述两步，直至所有顶点输出，或者当前图中不存在无前驱的顶点为止，后者代表我们的有向图是有环的，
因此，也可以通过拓扑排序来判断一个图是否有环。

#### 实现代码(C++)

```c++
//
//  main.cpp
//  Topological
//
//  Created by 陈龙 on 2019/12/16.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <queue>
using namespace std;

//有向无环图
struct Graph{
    int u,v,w;
};

int main(int argc, const char * argv[]) {
    // insert code here...
    int N,E;
    cin>>N>>E;
    
    Graph edge[E];
    //入度
    int t[N];
    int vis[N];
    //
    int u,v,w;
    for (int i=0; i<N; i++) {
        t[i] = 0;
        vis[i]=0;
    }
    for (int i=0; i<E; i++) {
        cin>>u>>v>>w;
        edge[i].u= u;
        edge[i].v= v;
        edge[i].w= w;
        t[v]++;
    }
    //查询入度为0的顶点放入队列
    queue<int> que;
    for (int i=0; i<N; i++) {
//        cout<<t[i]<<endl;
        if (t[i]==0) {
            que.push(i);
            vis[i]=1;
        }
    }
    int count = 0;
    while (!que.empty()) {
        int u = que.front();
        count++;
        for (int i=0; i<E; i++) {
            if (edge[i].u == u) {//删除这条边
                t[edge[i].v]--;
            }
        }
        for (int i=0; i<N; i++) {
//            cout<<i<<" "<<t[i]<<endl;
            if (t[i]==0 && vis[i]==0) {
                que.push(i);
                vis[i]=1;
            }
        }
        cout<<u<<endl;
        que.pop();
    }
    if (count!=N) {
        cout<<"非有向无环图";
    }
    
    /*
     6 7
     0 1 1
     0 2 2
     1 3 1
     2 3 1
     2 4 1
     4 3 1
     3 5 1
     */
    /*
     6 8
     0 1 1
     0 2 2
     1 3 1
     2 3 1
     2 4 1
     4 3 1
     3 5 1
     5 3 1
     
     */
    return 0;
}

```
