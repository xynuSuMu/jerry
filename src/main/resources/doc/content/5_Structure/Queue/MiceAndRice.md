#### 题目

Mice and Rice is the name of a programming contest in which each programmer must write a piece of code to control the movements of a mouse in a given map. The goal of each mouse is to eat as much rice as possible in order to become a FatMouse.

First the playing order is randomly decided for N​P​​ programmers. Then every N​G​​ programmers are grouped in a match. The fattest mouse in a group wins and enters the next turn. All the losers in this turn are ranked the same. Every N​G​​winners are then grouped in the next match until a final winner is determined.

For the sake of simplicity, assume that the weight of each mouse is fixed once the programmer submits his/her code. Given the weights of all the mice and the initial playing order, you are supposed to output the ranks for the programmers.

**Input Specification:**

Each input file contains one test case. For each case, the first line contains 2 positive integers: N​P​​ and N​G​​ (≤), the number of programmers and the maximum number of mice in a group, respectively. If there are less than N​G​​ mice at the end of the player's list, then all the mice left will be put into the last group. The second line contains N​P​​ distinct non-negative numbers W​i​​ (,) where each W​i​​ is the weight of the i-th mouse respectively. The third line gives the initial playing order which is a permutation of 0 (assume that the programmers are numbered from 0 to N​P​​−1). All the numbers in a line are separated by a space.

**Output Specification:**

For each test case, print the final ranks in a line. The i-th number is the rank of the i-th programmer, and all the numbers must be separated by a space, with no extra space at the end of the line.

**Sample Input:**

```text
11 3
25 18 0 46 37 3 19 22 57 56 10
6 0 8 7 10 5 9 1 4 2 3
```

**Sample Output:**

```text
5 5 5 2 5 5 5 3 1 3 5
```


#### 解析

```text
11 3 # 11表明输入 11只老鼠的体重 3 表明3只老鼠为一组
25 18 0 46 37 3 19 22 57 56 10 # 输入体重
6 0 8 7 10 5 9 1 4 2 3 # 老鼠体重的序号 6-->19 0 --> 25
```

题目第一遍挺难理解的。翻译后的大意是:给定NP只老鼠，对NP只老鼠进行分组，每组NG只，最后不足NG的单独为一组。
然后从每组老鼠中选出体重最大的一只作为胜利者，然后这些胜出的老鼠再次以NG只为一组进行体重的比较，直到最后剩下一只。
最后输出排名。

第一次分组: 
6  0  8 一组   体重分别为:19 25 57
7 10  5 二组   体重分别为:22 10 3
9  1  4 三组   体重分别为:56 18 37
2  3    四组   体重分别为:0  46
第一次竞选：57 22 56 46 
第二次分组:
8 7 9 一组 体重分别为:57 22 56
3     二组 体重分别为:46
第二次竞选:57 46
第三次分组:
8 3   一组 体重分别为:57 46
第三次竞选:57

所以排名如下 8号第一 3号第二 ......

所以结果就是
```text
5 5 5 2 5 5 5 3 1 3 5
```



#### 答案(C++)

```C++
//
//  main.cpp
//  mouse
//
//  Created by 陈龙 on 2019/01/27.
//  Copyright © 2019 陈龙. All rights reserved.
//

#include <iostream>
#include <queue>
#include <cmath>
using namespace std;
struct mouse{
    int r,weight;
}m[101];
int main(int argc, const char * argv[]) {
    int np,ng,order;
    cin>>np>>ng;
    for(int i=0;i<np;i++) cin>>m[i].weight;
    queue<int> q;
    for(int i=0;i<np;i++){
        cin>>order;
        q.push(order);
    }
    int temp=np,group;
    while (q.size()>1) {
        group=(ceil)(temp*1.0/ng);
        //遍历每一组老鼠
        for (int i=0; i<group; i++) {
            int k = q.front();
            //比较每一组老鼠的最大值
            for (int j=0; j<ng;j++) {
                if(i*ng+j>=temp) break;
                if (m[q.front()].weight>m[k].weight) {
                    k=q.front();
                }
                m[q.front()].r= group+1;
                q.pop();
            }
            q.push(k);
        }
        //新一轮的老鼠数量
        temp = group;
    }
    m[q.front()].r=1;
    for (int i=0;i<np;i++) {
        cout<<m[i].r<<" ";
    }
    return 0;
//11 3
//25 18 0 46 37 3 19 22 57 56 10
//6 0 8 7 10 5 9 1 4 2 3
}
```

#### 答案(Java)

```java
public class MiceAndRice {

    static int[] arr = new int[101];
    static Mounse[] mounses = new Mounse[101];
    static ArrayQueue<Integer> queue = new ArrayQueue();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int np = sc.nextInt();
        int ng = sc.nextInt();
        System.out.println("输入老鼠体重");
        for (int i = 0; i < np; i++) {
            int weight = sc.nextInt();
            Mounse mounse = new Mounse();
            mounse.weight = weight;
            mounses[i] = mounse;
        }
        System.out.println("指定序号并入栈");
        for (int i = 0; i < np; i++) {
            int order = sc.nextInt();
            arr[i] = order;
            queue.add(order);
        }
        System.out.println("开始排名");

        int temp = np, group;
        //淘汰队列中老鼠，直到最后一只
        while (queue.size() > 1) {
            group = temp % ng == 0 ? temp / ng : temp / ng + 1;
            System.out.println("分" + group + "组");
            //获取每一组中的获胜者
            for (int i = 0; i < group; i++) {
                int index = queue.peek();
                System.out.println("第" + i + "组的" + index);
                for (int j = 0; j < ng; j++) {
                    if (i * ng + j >= temp) break;
                    //对淘汰的老鼠指定Order
                    if (mounses[queue.peek()].weight <= mounses[index].weight) {
                        mounses[queue.peek()].r = group + 1;
                    } else {
                        mounses[index].r = group + 1;
                        index = queue.peek();
                    }
                    queue.poll();
                }
                queue.add(index);
            }
            //开启新一轮比较
            temp = group;
        }
        mounses[queue.poll()].r = 1;
        for (int i = 0; i < np; i++) {
            System.out.printf(mounses[i].r + " ");
        }
    }

    static class Mounse {
        int r;
        int weight;
    }
}
```
