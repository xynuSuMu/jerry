#### POP Sequence

Given a stack which can keep M numbers at most. Push N numbers in the order of 1, 2, 3, …, N and pop randomly. 

You are supposed to tell if a given sequence of numbers is a possible pop sequence of the stack. 

For example, if M is 5 and N is 7, we can obtain 1, 2, 3, 4, 5, 6, 7 from the stack, but not 3, 2, 1, 7, 5, 6, 4.


**Input Specification:**

Each input file contains one test case. For each case, the first line contains 3 numbers (all no more than 1000): 
M (the maximum capacity of the stack),
N (the length of push sequence), 
and K (the number of pop sequences to be checked). Then K lines follow, each contains a pop sequence of N numbers. All the numbers in a line are separated by a space.

**Output Specification:**

For each pop sequence, print in one line “YES” if it is indeed a possible pop sequence of the stack, or “NO” if not.

**Sample Input:**

```text
5 7 5
1 2 3 4 5 6 7
3 2 1 7 5 6 4
7 6 5 4 3 2 1
5 6 4 3 7 2 1
1 7 6 5 4 3 2
```

**Sample Output:**

```text
YES
NO
NO
YES
NO
```

#### 解析

该题大意是：给定一个至多存放M元素的栈，按1、2、3、...N的顺序入栈，随机出栈，你是否可以判断给定的数字序列是否是出栈的序列。K为给定几组数。

简单的理解这道题，就是给你一组连续递增的数，必须按顺序入栈，任意时刻出栈，那么这一组递增的数会有很多种出栈的序列，然后让你判断系统给你的
一组出栈序列是不是正确的。

比如1，2，3的出栈可能： 
第一种：1入栈，然后出栈，2入栈然后出栈，3入栈然后出栈 ，那么出栈的序列就是 1 2 3 
第二种: 1入栈2入栈3入栈,然后依次出栈，那么出栈的序列就是 3 2 1
......

但是3 1 2 就是不正确的，因为3想第一个出栈，1和2必须先入栈，然后3出栈，鉴于入栈的顺序，那么3后面出栈必然是2，不可能是1，所以3 1 2 就不对。

理解了上面的例子，我们看如何解题:

第一步:将给定出栈序列存入数组，初始化索引值为0，指向第一个出栈的元素。
第二步:判断栈中元素数量是否大于规定数字M，如果大于，表明栈已经满了，但是此时仍没有按照规定顺序将元素出栈。也就是出栈失败
第三步:压栈，按照1-N的顺序依次进行压栈，判断当前的压栈元素是否等于索引值对应的元素，如果等于则表明该元素符合当前出栈顺序，则出栈，索引值加一，
继续判断栈中元素和所以位置的数据是否相同。如果不等于，那么回归到第二步
第四步:根据栈中是否存有元素判断是否成功出栈



#### 答案(C++)

```c++
//
//  main.cpp
//  Stack
//
//  Created by 陈龙 on 2019/01/26.
//  Copyright © 2019 陈龙. All rights reserved.
//  POP Sequence -- 弹出序列

#include <iostream>
#include <stack>
using namespace std;

int arr[100];
stack<int> st;
int main(int argc, const char * argv[]) {
    int m,n,k;
    cin>>m>>n>>k;
    while (k--) {
        //清空栈
        while (!st.empty()) {
            st.pop();
        }
        //输入出栈顺序
        for (int i=0; i<n; i++) {
            cin>>arr[i];
        }
        int current = 0;
        bool flag =true;
        //入栈，并且比对数组索引位置对应数值与栈顶元素是否一致
        for (int i=0; i<n; i++) {
            st.push(i+1);
            if (st.size()>m) {
                flag=false;
                break;
            }
            while (!st.empty()&&st.top() == arr[current]) {
                st.pop();
                //如果是，数组索引后移
                current++;
            }
        }
        if (st.empty() && flag) {
            cout<<"YES";
        }else{
            cout<<"No";
        }
    }
    return 0;
}
```
