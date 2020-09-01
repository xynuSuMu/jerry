### Trie实现自动补全功能

对于百度,谷歌的关键词提示功能我们应该很熟悉，
这个自动提示的功能对于用户来说十分方便，且节省时间，而这种功能的实现
离不开Trie树这种数据结构。

#### Trie树

相比之前我们介绍的红黑树和B树，Trie树是一种什么样的树形结构？

Trie树，也叫字典树，又称单词查找树，是一种树形结构，
是一种哈希树的变种。典型应用是用于统计，
排序和保存大量的字符串（但不仅限于字符串），
所以经常被搜索引擎系统用于文本词频统计。
它的优点是：利用字符串的公共前缀来减少查询时间，
最大限度地减少无谓的字符串比较，查询效率比哈希树高

它有3个基本性质：

根节点不包含字符，除根节点外每一个节点都只包含一个字符； 
从根节点到某一节点，路径上经过的字符连接起来，为该节点对应的字符串； 
每个节点的所有子节点包含的字符都不相同。

如下图:

![image](https://p3.pstatp.com/origin/pgc-image/ded5e07f53e34f91867c12ca10f1bd19)


#### 自动补全

要实现这种功能，我们首先需要构建Trie树，然后通过深度优先算法得到完整的字符串。

首先定义结点
```javascript
    class Node {
        constructor(value) {
            this.val = value;//数值
            this.child = [];//孩子结点
            this.end = false;//是否是独立的单词
            this.height = 0;//深度
            this.num = 1;//经过单词数量
        }

        search(key) {
            for (let i = 0; i < this.child.length; ++i) {
                if (this.child[i].val == key) {
                    return this.child[i];
                }
            }
            return null;
        }
    }

```
构造完结点，就是构建Trie树了
```javascript
class TrieTree {
        constructor() {
            this.root = new Node(null);
            this.size = 1;
        }
        insert(value) {
            let node = this.root;
            for (let ch of value) {
                let son = node.search(ch);
                if (son == null) {
                    son = new Node(ch);//构建
                    son.height = node.height + 1;
                    node.child.push(son);
                } else {
                    son.num++;
                }
                node = son;//向下递归
            }

            if (!node.end) {//不是结束标志，则增加结束标志
                this.size++;
                node.end = true;
            }
        }

    }
```

构建完之后就是自动补全了，核心是深度优先的递归算法

```javascript
        //自动补全
        relate(value) {
            let node = this.root;
            let arr = [];
            let word = "";
            $("#relate").html("");
            for (let ch of value) {
                let son = node.search(ch);
                if (son == null) {
                    //不存在前缀
                    return arr;
                }
                node = son;
            }
            //存在公共前缀，将该分支下数据输出
            console.log("存在" + value + "公共前缀")
            //深搜
            this.DFS(arr, value, node);
            console.log(arr)

            for (let w of arr) {
                $("#relate").append("<p>" + w + "</p>")
            }
        }

        DFS(arr, value, node) {
            if (node != null) {
                for (var i = 0; i < node.child.length; ++i) {
                    if (node.child[i].end) {
                        arr.push(value + node.child[i].val)
                    }
                    this.DFS(arr, value + node.child[i].val, node.child[i])
                }
            }
        }
```

#### 总结

* 目前的实现不支持中文，如果中文可以对中文进行定长编码(UTF16)，展示的时候进行解码，
或者也可以使用前面我们提及的哈夫曼编码(但是由于需要统计构建哈夫曼树，效率可能达不到我们的要求)。

* 百度谷歌的搜索引擎还不仅能够可以自动纠错（百度有相关API可以对文本进行纠错）
