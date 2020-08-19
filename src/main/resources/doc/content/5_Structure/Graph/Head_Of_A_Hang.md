#### 题目

One way that the police finds the head of a gang is to check people’s phone calls. If there is a phone call between A and B, we say that A and B is related. The weight of a relation is defined to be the total time length of all the phone calls made between the two persons. A “Gang” is a cluster of more than 2 persons who are related to each other with total relation weight being greater than a given threshold K. In each gang, the one with maximum total weight is the head. Now given a list of phone calls, you are supposed to find the gangs and the heads.

**输入格式**

Each input file contains one test case. For each case, the first line contains two positive numbers N and K (both less than or equal to 1000), the number of phone calls and the weight threshold, respectively. Then N lines follow, each in the following format:

Name1 Name2 Time

where Name1 and Name2 are the names of people at the two ends of the call, and Time is the length of the call. A name is a string of three capital letters chosen from A-Z. A time length is a positive integer which is no more than 1000 minutes.

**输出格式**

For each test case, first print in a line the total number of gangs. Then for each gang, print in a line the name of the head and the total number of the members. It is guaranteed that the head is unique for each gang. The output must be sorted according to the alphabetical order of the names of the heads.


**Sample Input 1:**
```text
8 59
AAA BBB 10
BBB AAA 20
AAA CCC 40
DDD EEE 5
EEE DDD 70
FFF GGG 30
GGG HHH 20
HHH FFF 10
```

**Sample Output 1:**
```text
2
AAA 3
GGG 3
```


**Sample Input 2:**
```text
8 70
AAA BBB 10
BBB AAA 20
AAA CCC 40
DDD EEE 5
EEE DDD 70
FFF GGG 30
GGG HHH 20
HHH FFF 10
```

**Sample Output 2:**
```text
0
```


#### 答案(C++)

```c++
//
//  main.cpp
//  HeadOfAHang
//
//  Created by 陈龙 on 2019/02/06.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <vector>
#include <map>
using namespace std;
//最大顶点
const int N=100;
struct Node{
    int v;//顶点
    int weight;//权重
     Node(int _v,int _weight):v(_v),weight(_weight){};
};
//邻接表
vector<Node> ver[N];
//记录是否遍历
int visit[N];
//姓名转索引位置
int nameIndex = 0;
map<string,int> mapName;
map<int,string> mapIndex;
//保存每个顶点权重合

int headers[N];
//
int totalGang = 0;

//输入数量
int number;
//权重
int K;
void DFS(int v,int &member,int &weightTemp,int &header){
    
    
    for (int i=0; i<ver[v].size(); i++) {
        if (visit[ver[v][i].v]==false) {
            visit[v] = true;
            //成员加一
            member++;
            //权重
            weightTemp +=ver[v][i].weight;
            //存储该节点的权重,因为是无向图，每个节点都增加权重
            headers[v] = headers[v]+ver[v][i].weight;
            headers[ver[v][i].v] += ver[v][i].weight;
            if(v!=header && headers[v]>headers[header]){
                header =v;
            }
            DFS(ver[v][i].v,member,weightTemp,header);
        }
        
    }
}
struct GangHeader{
    string name;
    int number;
};
GangHeader arr[N];
int cmp(GangHeader a,GangHeader b){
    return a.name<b.name;//字典序从小到大排序
}

void DFSTrave(){
    int index=0;
    for (int i=0; i<number; i++) {
        if (visit[i]==false) {
        int member =0;
        int weightTemp = 0;
        int header = i;
        DFS(i,member,weightTemp,header);
//        cout<<i<<" "<<member<<" "<<weightTemp<<endl;
        if (member>2 && weightTemp>K) {
            totalGang ++;
            arr[index].number = member;
            arr[index].name = mapIndex[header];
            sort(arr, arr+index, cmp);
            index++;
         }
        }
    }
}
int main(int argc, const char * argv[]) {
    // insert code here...
    cin>>number>>K;
    string name1,name2;
    int weight;
    for (int i=0; i<number; i++) {
        cin>>name1>>name2>>weight;
        int s,e;
        if (mapName.find(name1)!=mapName.end()) {
            s  = mapName[name1];
        }else{
            mapName[name1] = nameIndex;
            mapIndex[nameIndex] = name1;
            s=nameIndex;
            nameIndex++;
        }
//        cout<<"姓名name1"+name1<<s<<endl;
        if (mapName.find(name2)!=mapName.end()) {
           e  = mapName[name2];
        }else{
            mapName[name2] = nameIndex;
            mapIndex[nameIndex] = name2;
            e=nameIndex;
            nameIndex++;
        }
//        cout<<"姓名name2"+name2<<e<<endl;
        //构建边和边权
        ver[s].push_back(Node(e,weight));
        ver[e].push_back(Node(s,weight));
    }
    DFSTrave();
    cout<<totalGang<<endl;
    for (int i=0; i<totalGang; i++) {
        cout<<arr[i].name<<" "<<arr[i].number<<endl;
    }
    return 0;
}

```
