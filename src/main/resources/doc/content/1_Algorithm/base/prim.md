> 最小生成树指的是在给定的一个无向图G中，求一棵树。该树拥有图G中所有的顶点，且满足整棵树的边权最小。

#### 普里姆算法

普里姆算法（Prim算法），图论中的一种算法，可在加权连通图里搜索最小生成树。
意即由此算法搜索到的边子集所构成的树中，不但包括了连通图里的所有顶点（英语：Vertex (graph theory)），且其所有边的权值之和亦为最小。
该算法于1930年由捷克数学家沃伊捷赫·亚尔尼克（英语：Vojtěch Jarník）发现；并在1957年由美国计算机科学家罗伯特·普里姆（英语：Robert C. Prim）独立发现；
1959年，艾兹格·迪科斯彻再次发现了该算法(Dijkstra算法的创造者)

#### 算法思想

Prim算法的思想和Dijkstra算法类似，采用贪心算法实现，对于图G设置集合V，S，
每次为顶点找寻最短边权，加入到集合V中，S保存已经被访问的顶点的数据。


设置V={},S={}。初始化出V为无穷大，S为空。

将最小生成树跟结点保存到V中。然后从V中未被访问顶点，找寻最短边权的顶点作为中介点，向图其他顶点扩展。

即:

1).输入：一个加权连通图，其中顶点集合为V，边集合为E；

2).初始化：Vnew = {x}，其中x为集合V中的任一节点（起始点），Enew = {},为空；

3).重复下列操作，直到Vnew = V：

a.在集合E中选取权值最小的边<u, v>，其中u为集合Vnew中的元素，而v不在Vnew集合当中，并且v∈V（如果存在有多条满足前述条件即具有相同权值的边，则可任意选取其中之一）；

b.将v加入集合Vnew中，将<u, v>边加入集合Enew中；

4).输出：使用集合Vnew和Enew来描述所得到的最小生成树。


#### 实现代码(C++)

```c++
//
//  main.cpp
//  Prim
//
//  Created by 陈龙 on 2019/2/14.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <vector>
using namespace std;
struct Edge{
    int v,w;
    Edge(int _v,int _w):v(_v),w(_w){};
};

int main(int argc, const char * argv[]) {
    // insert code here...
    int N,E,root;
    cin>>N>>E>>root;
    //构建图
    vector<Edge> edge[N];
    int u,v,w;
    for (int i=0; i<E; i++) {
        cin>>u>>v>>w;
        //无向图
        edge[u].push_back(Edge(v,w));
        edge[v].push_back(Edge(u,w));
    }
    int dis[N];
    bool vis[N];
    int ans = 0;//最小生成树权值
    //初始化
    for (int i=0; i<N; i++) {
        dis[i] = INT_MAX;
        vis[i] = false;
    }
    dis[root] = 0;
    //构建最小生成树
    for (int i=0; i<N; i++) {//遍历N次
        int u=-1,MIN = INT_MAX;//中介顶点和最小权值，初始化最大
        for (int j=0; j<N; j++) {//找到最短路径的顶点
            if (!vis[j] && MIN > dis[j] ) {
                u = j;
                MIN = dis[j];
            }
        }
        if (u == -1) {
            break;
        }
        vis[u] = true;
        ans +=MIN;
        for (int j=0; j<edge[u].size();j++) {//找到中介点u可达的顶点
            cout<<u<<" "<<j<<" "<<edge[u].size()<<endl;
            if (!vis[edge[u][j].v] && dis[edge[u][j].v] >  edge[u][j].w) {
                dis[edge[u][j].v] = edge[u][j].w;
                cout<<"-->"<<edge[u][j].v<<" "<<j<<endl;
            }
        }
    }
    cout<<ans<<endl;
    
    
    return 0;
}
/*
5 6 1
0 1 1
0 2 2
0 4 3
1 3 3
2 3 4
2 4 2
*/

```
