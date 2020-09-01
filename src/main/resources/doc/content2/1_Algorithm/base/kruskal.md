> 上一节我们看了最小生成树的Prim算法，其算法的实现核心和Dijkstra算法十分类似。这一篇来看一下同为最小生成树的kruskal算法。

#### 克鲁斯卡尔算法
Kruskal算法是一种用来查找最小生成树的算法，由Joseph Kruskal在1956年发表，同Prim算法一样，Kruskal算法也是贪心算法的应用。

#### 算法思想

Kruskal算法将图中所有的边进行由小到大的排序，然后按照边权从小到大测试所有的边，如果当前边的两个顶点不在一个连通块(即两个顶点相连不会
因为形成环导致树形成环)，将边加入最小生成树中，否则抛弃该测试边。

```text

0->1 1
1->2 2
0->2 3

```

上面就是排好序的边，0->1连接不会形成连通块，所有可以构建最小生成树，1->2 也不会形成连通块，所以可以构建最小生成树。
但是0->2则会形成连通块，所以不能构建最小生成树。

判断是否会形成连通块，可以通过并查集来判断两个顶点的根结点是否在同一个集合中。

#### 实现代码(C++)

```c++
//
//  main.cpp
//  kruskal
//
//  Created by 陈龙 on 2019/2/15.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
using namespace std;

const int N=200;

struct Edge{
    int u,v,w;
//    Edge(int _u,int _v,int _w):u(_u),v(_v),w(_w){};
};

//并查集合
int father[N];
int findFather(int v){
    int temp = v;
    while (v != father[v]) {//v==father 表明根结点
        v = father[v];
    }
    while (temp!=father[temp]) {//路径压缩，使得时间复杂度升级为O(1)
        father[temp] = v;
        temp = father[temp];
    }
    return v;
}
//
int tmp(Edge a,Edge b){
    return a.w<b.w;
}

int main(int argc, const char * argv[]) {
    // insert code here...
    int N,E;
    cin>>N>>E;
    Edge edge[E];
    for(int i = 0;i<E;i++){
        cin>>edge[i].u>>edge[i].v>>edge[i].w;
    }
    //排序
    sort(edge, edge+E, tmp);
    //初始化并查集
    for(int i=0;i<N;i++){
        father[i] = i;
    }
    //kruskal
    int ans = 0;
    int edgeNum = 0;
    for (int i=0; i<E; i++) {
        int uFather =findFather(edge[i].u);
        int vFather =findFather(edge[i].v);
        if (uFather != vFather) {//不成连通图
            father[uFather] = vFather;//连通两个节点
            ans +=edge[i].w;
            edgeNum++;
            if (edgeNum == N-1) {
                break;
            }
        }
    }
//  cout<<edgeNum;
    if (edgeNum != N-1){
        cout<<"图不连通"<<endl;
        return -1;
    }
    cout<<ans;
    
    
    return 0;
}

/*
6 10
0 1 4
0 4 1
0 5 2
1 2 1
1 5 3
2 3 6
2 5 5
3 4 5
3 5 4
4 5 3
*/
```
