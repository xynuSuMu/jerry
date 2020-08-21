> 上一节我们使用贝尔曼-福特算法实现了负权图的的单源最短路径，但是由于其低效，所以可以引入队列优化贝尔曼-福特算法，
即SPFA算法(网上对这一算法争论挺多的，可以自行百度了解一下)。该算法的最坏复杂度为 O(VE)


#### SPFA算法

在贝尔曼-福特算法中，使用N-1遍历，保证每次遍历中，都能寻求至少一个最短路径进行松弛，但是每次松弛操作的顶点u，
我们可以将其存入队列中，比如在第一轮遍历中，其可松弛的终点v，必然是源点可直达的顶点，后面每次松弛操作，都可以
以这些点做为中介的点继续进行松弛操作。

#### 实现代码(C++)

```c++

//
//  main.cpp
//  SPFA
//
//  Created by 陈龙 on 2019/2/11.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <queue>
using namespace std;

struct Edge{
    int u,v,w;
};


int main(int argc, const char * argv[]) {
    //顶点个数，边，源点
    int N,E,S;
    cin>>N>>E>>S;
    Edge edge[E];
    //最短距离
    int dis[N];
    //记录负权环图
    int g[N];
    //记录是否入队列
    bool flag[N];
    for (int i=0; i<E; i++) {
        cin>>edge[i].u>>edge[i].v>>edge[i].w;
    }
    queue<int> que;
    //初始化
    for (int i=0; i<N; i++) {
        dis[i] = INT_MAX;
        g[i] = 0;
        flag[i]=false;
    }
    dis[S] = 0;
    //源点S
    que.push(S);
    //进入队列
    flag[S] = true;
    g[S]++;
    
    while (!que.empty()) {
        for (int i=0; i<E; i++) {
            if (edge[i].u == que.front() && dis[edge[i].v]> dis[edge[i].u]+edge[i].w) {
                dis[edge[i].v] = dis[edge[i].u]+edge[i].w;
                g[edge[i].v]++;
                if (g[edge[i].v] > N-1) {
                    cout<<"存在负环权图"<<endl;
                    return -1;
                }
                if (!flag[edge[i].v]) {
                    que.push(edge[i].v);
                    flag[edge[i].v] = true;
                }
            }
        }
        flag[que.front()]=false;
        que.pop();
    }
    for(int i=0;i<N;i++){
        cout<<dis[i]<<endl;
    }
    return 0;
}



```
