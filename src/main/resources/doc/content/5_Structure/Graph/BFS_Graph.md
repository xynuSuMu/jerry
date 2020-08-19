#### 广度优先搜索

广度优先搜索每次以扩散的方式向外访问顶点。和树的遍历一样，使用BFS遍历图，需要
使用队列，通过反复取出队首顶点，将该顶点可达到的但未曾达到的顶点入队列，直到队列为空
时遍历结束

#### 实现过程

对于图采用广度优先遍历和二叉树遍历方式类似，我们首先判断顶点是否遍历过，如果没有加入队列，
然后将其可达的节点进行入队(需要判断是否已经经过)，遍历队列中的元素。

#### 实现代码(C++)

```c++
//
//  main.cpp
//  BFSGraph
//
//  Created by 陈龙 on 2019/02/08.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <queue>
#include <vector>
using namespace std;

//最多节点
const int  N =100;
//图顶点
struct graph{
    int v;
    int w;
    graph(int _v,int _w):v(_v),w(_w){};
};
//vectot
vector<graph> ver[N];
queue<int> que;
queue<int> res;
//
int vis[N];
//
void BFS(int v){
    //设置为已路过
    que.pop();
    if (vis[v]==1) {
         return;
    }
    vis[v] = 1;
    //放入结果队列
    res.push(v);
    //将节点可达且未l经过的顶点入队列
    for (int i=0; i<ver[v].size(); i++) {
        if (vis[ver[v][i].v]==0) {
            que.push(ver[v][i].v);
        }
    }
    while (!que.empty()) {
        BFS(que.front());
    }
}
void BFSTrave(){
    for (int i=0; i<N; i++) {
        if (ver[i].size()>0) {
            que.push(i);
            BFS(i);
        }
    }
}
int main(int argc, const char * argv[]) {
    // insert code here...
    ver[0].push_back(graph(4,2));
    ver[0].push_back(graph(2,2));
    ver[1].push_back(graph(2,2));
    ver[1].push_back(graph(3,2));
    ver[1].push_back(graph(4,2));
    ver[2].push_back(graph(3,2));
    ver[4].push_back(graph(5,2));
    ver[6].push_back(graph(5,2));
    ver[7].push_back(graph(8,2));
    
    BFSTrave();
    while (!res.empty()) {
        cout<<res.front()<<endl;
        res.pop();
    }
    return 0;
}

```
