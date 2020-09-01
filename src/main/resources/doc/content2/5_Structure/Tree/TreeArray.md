#### 树状数组

在PAT刷到1057题时，按照自己的逻辑5的测试点出现3个超时，于是百度该题的答案，
发现大家都采用一种新的数据结构来解决这个问题，也就是树状数组。

我们可以认为树状数组是一种对数组的巧妙运用。

这种结构有什么用呢，我拿PAT1057举例，
给一组N个无序的正整数，比如：1 3 2 5 4 2 6。
如果N是偶数，那么求中位数N/2的数据
如果N是基数，那么求中位数 (N+1)/2的数据。

我采用方案是对对无序的整数进行排序，然后输出中位数，于是就超时了。

那么树状数组是怎么解决这个问题的呢？

首先将无序正整数放入数组A中：{1,3,2,5,4,2,6}。

但是然后换一种思路去存储，我们数值当作索引下标，存储的值为数据出现的次数，那么就变成下面这种情形:

```text

array 1 2 1 1 1 1 数组存储的值

index 1 2 3 4 5 6 索引

注:不考虑0的情况
```
这种情况下，如果是连续的，且出现次数都为1，那么也很好取中位数，但是如果不是这种情况，我们仍然无法得到中位数，
针对上面的情况，树状数组作用就出来了，我们新建的一个数组C，令:
```text
C<sub>1</sub> = A<sub>1</sub>
C<sub>2</sub> = A<sub>1</sub> + A<sub>2</sub>
C<sub>3</sub> = A<sub>3</sub>
C<sub>4</sub> = A<sub>1</sub> + A<sub>2</sub> + A<sub>3</sub> + A<sub>4</sub>
C<sub>5</sub> = A<sub>5</sub>
C<sub>6</sub> = A<sub>5</sub> + A<sub>6</sub>

```

首先这种公式并不是规律，而是人为约定的，这种约定看似毫无规律，但是我们将下标转成二进制来看:
```text
C[1] = C[0001] = A<sub>1</sub>
C[2] = C[0010] = A<sub>1</sub> + A<sub>2</sub>
C[3] = C[0011] = A<sub>3</sub>
C[4] = C[0100] = A<sub>1</sub> + A<sub>2</sub> + A<sub>3</sub> + A<sub>4</sub>
C[5] = C[0101] = A<sub>5</sub>
C[6] = C[0110] = A<sub>5</sub> + A<sub>6</sub>
```
我们会发现，如果二进制末尾是1(奇数)，那么C[i] = A[i]。如果是0，那么需要看末尾连续出现几个0，如果一个0，那就是
2<sup>1</sup> = 2 也就是 C[i] = A[i-1]+A[i]。

根据上面的这结论，就得到一个方法

```code
 n & (-n)
 
 有下面几个例子可以参考：
 8 & -8  1000 & (0111)+0001 = 1000 & 1000 = 1000 = 8
 7 & -7  0111 & (1000)+0001 = 0111 & 1001 = 0001 = 1
 6 & -6  0110 & (1001)+0001 = 0110 & 1010 = 0010 = 2
 
 1111 0001
```

看完推导过程，那么C如何构建呢，因为如果改动A[1]，不仅仅要修改C[1]还要修改C[2],C[4]，可以理解为修改父节点。
如何知道父节点呢？那就是 index+ (index&(-index))，直到index达到数组索引的上限

```java
    private static int lowBit(int n) {
        return n & (-n);
    }
    
    private static void add(int index, int value) {
        while (index <= maxN) {
            c[index] += value;
            index += lowBit(index);
        }
    }
```

走到这一步，我们继续看如何找中位数

数组A：

```text

array 1 2 1 1 1 1 数组存储的值

index 1 2 3 4 5 6 索引


```

树状数组C:

```text

array 1 3 1 6 1 2 

index 1 2 3 4 5 6 

```

那么中位数索引对应为 （7+1）/2 = 4。

采用二分法在数组C寻找，可以得到中位数为3。

```java

    private static int find(int value) {
        int left = 0, right = maxN - 1, mid;
        while (left < right) {
            mid = (left + right) / 2;
            int res = sum(mid);
            if (res < value) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

```

其中sum方法

```java
    private static int sum(int index) {
        int res = 0;
        while (index > 0) {
            res += c[index];
            //减去覆盖的最远节点，就会得到下个需要累加的节点
            index -= lowBit(index);
        }
        return res;
    }
```

这种形式首先不需要每次都对数组排序，只需要改动数组A的值即可，再采用二分，大数的情况下响应很快。

