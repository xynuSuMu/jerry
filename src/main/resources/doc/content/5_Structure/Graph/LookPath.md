> 在前面我们讲了DFS和BFS遍历图的顶点，那么如果我们想要得到两个顶点之间所有的路径该如何实现呢？

在深度优先搜索中，就好比走迷宫，我们每次选择一个岔路口往下继续走，直到无法继续时，
我们回退到上一个岔路口选择新的路(需要判断新选择的路是否走过，因为环通可能会在上一次走过)，
直至所有的顶点都遍历(如果存在结点没有遍历，说明图不是连通图)。

而如果约定起点s和终点e，我们对DFS稍作改动，就可以得到s到e的所有路径。

改动很简单，当我们递归到终点的时候，会进行一次回溯，将之前经过的顶点设置为未走过。


以下面图为例

```text

   1-----2---5
    \     \ /
     3-----4
```

定义vector

第一步:找到起点1

第二步:1会选择2,3来走，这里按照大小来选，所以先走2。vector存储2

第三步:2会选择1，4，5，因为1已经走过，所以排除

第四步:2会选择4，vector存储4

第五步:4选择2，3，5，因为2已经走过，所以排除

第六步:4会选择3，vector存3

第七步:3会选择1和4，因为1和4都走过，递归结束，回溯到第五步，此时将3设置为未走过，vector丢弃3，

第八步:4选择5，因为5是终点，所以将该轮路径存起来即1-2-4-5。回溯到第三步，将4设置为未读，vector丢弃4。

第九步:2选择5，因为5是终点，所以将该轮路径存起来即1-2-5。回溯到第二步，将2设置未未读，vector丢弃2。

....依次类推，最终得到所有的路径


#### 实现代码(C++)

```c++

#include <iostream>
#include <vector>
#include <algorithm>
using namespace std;

struct Vertex{
    int index;
};

struct Edge{
    Vertex u;
    Vertex v;
};
int endv;
vector<vector<int>> temp2;
bool vis[505];
void dfs(vector<Edge> e[],vector<int> &temp,int v){
    vis[v] = true;
    if (v == endv) {
        temp2.push_back(temp);
    }else{
        for (int i=0; i<e[v].size(); i++) {;
            //cout<<"顶点"<<v<<"走向"<<e[v][i].v.index<<",是否走过:"<<vis[e[v][i].v.index]<<endl;
            if (!vis[e[v][i].v.index]) {
                temp.push_back(e[v][i].v.index);
                dfs(e, temp, e[v][i].v.index);
                temp.pop_back();
                //cout<<"回溯顶点："<<e[v][i].v.index<<endl;
                vis[e[v][i].v.index] = false;
            }
        }
    }
}

int main(int argc, const char * argv[]) {
    int N,M;
    cin>>N>>M;
    Vertex vs[N+1];
    Vertex ver;
    ver.index = 0;
    vs[0] = ver;
    for (int i=1; i<N+1; i++) {
        ver.index = i;
        vs[i] = ver;
    }
    //边
    vector<Edge> vec[N+1];
    int u,v;
    Edge edge;
    for (int i=0; i<M; i++) {
        cin>>u>>v;
        edge.u = vs[u];
        edge.v = vs[v];
        vec[u].push_back(edge);
        edge.u =vs[v];
        edge.v =vs[u];
        vec[v].push_back(edge);
    }
    
    endv = 5;
    vector<int> temp;
    dfs(vec,temp,1);
    for (int i=0; i<temp2.size(); i++) {
        vector<int> vec =  temp2[i];
        cout<<"1->";
        for (int j=0; j<vec.size(); j++) {
            cout<<vec[j];
            if (j!=vec.size()-1) {
                cout<<"->";
            }
        }
        cout<<endl;
    }
    return 0;
}


```







