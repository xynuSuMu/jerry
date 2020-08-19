> 上一篇文章，对于图有了一个简单的描述，这一篇文章使用邻接表和邻接矩阵实现图


#### 实现代码(C++)

```c++
//
//  main.cpp
//  Graph
//
//  Created by 陈龙 on 2019/02/06.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <vector>
using namespace std;

//最大顶点数
const int maxV = 1000;
//邻接矩阵，二维数组实现
int G[maxV][maxV];
//边
int edge = 0;
//创建图
void createGraph(int x,int y,bool isDirection){
    if (G[x][y]==1) {
        return;
    }
    G[x][y]=1;
    edge++;
    if (!isDirection) {
        G[y][x]=1;
    }
}
//某个节点的出度
int outEdge(int x){
    int num = 0;
    for (int i=0; i<maxV; i++) {
        if (G[x][i]==1) {
            num++;
        }
    }
    return num;
}
//==邻接表
struct Node{
    int v;//终点编号
    int w;//边权
    Node(int _v,int _w):v(_v),w(_w){};
};

vector<Node> adj[maxV];
int edgeForTable = 0;
void createGraphForTable(int x,int y,int w,bool isDirection){
    adj[x].push_back(Node(y,w));
    if (!isDirection) {
        adj[y].push_back(Node(x,w));
    }
    edgeForTable++;
}


int main(int argc, const char * argv[]) {
    //初始化邻接矩阵
    int isDirection = false;
    createGraph(1,3,isDirection);
    createGraph(2,3,isDirection);
    createGraph(2,4,isDirection);
    createGraph(2,5,isDirection);
    cout<<edge<<endl;
    //出度
    cout<<outEdge(2)<<endl;
    cout<<outEdge(3)<<endl;
    //初始化邻接表
    createGraphForTable(1,3,2,isDirection);
    createGraphForTable(2,3,3,isDirection);
    createGraphForTable(2,4,3,isDirection);
    createGraphForTable(2,5,3,isDirection);
    cout<<edgeForTable<<endl;
    //出度
    cout<<adj[2].size()<<endl;
    cout<<adj[3].size()<<endl;
    
    return 0;
}

```

#### 实现代码(Java)

```java
public class Graph {

    private static List<List<Node>> lists = new ArrayList<>();

    public static void main(String[] args) {
        Graph graph = new Graph();
        for (int i = 0; i < 10; i++) {
            lists.add(new ArrayList<>());
        }
        graph.createGraphForTable(1, 3, 2);
        graph.createGraphForTable(2, 3, 3);
        graph.createGraphForTable(2, 4, 3);
        graph.createGraphForTable(2, 5, 3);

        System.out.println("顶点2的度为=" + lists.get(2).size());
        System.out.println("顶点1的度为=" + lists.get(1).size());
    }


    private List<List<Node>> createGraphForTable(int u, int v, int w) {
        Node node = new Node(v, w);
        if (!lists.get(u).isEmpty()) {
            for (Node nodeTemp : lists.get(u)) {
                if (nodeTemp.v == v) {
                    //替换
                    nodeTemp.w = w;
                    return lists;
                }
            }
        }
        lists.get(u).add(node);
        return lists;
    }

    static class Node {
        int v;
        int w;

        public Node(int v, int w) {
            this.v = v;
            this.w = w;
        }
    }
}

```
