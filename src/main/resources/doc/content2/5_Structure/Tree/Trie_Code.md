```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Trie树</title>
    <link href="https://cdn.bootcss.com/twitter-bootstrap/4.3.1/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/twitter-bootstrap/4.3.1/js/bootstrap.min.js"></script>
    <script src="https://cdn.bootcss.com/echarts/4.4.0-rc.1/echarts.min.js"></script>
    <style>
        p {
            margin: 0;
        }
    </style>
</head>
<body style="display: flex;justify-content: center;align-items: center;align-self: center;height: 100%;height: 500px">
<div class="form-group" style="margin: 10px;display: flex;align-items: center;">
    <div style="position: relative">
        <input id="search" autocomplete="off" oninput="search()" onfocus="show()" onblur="hide()">
        <div id="show" style="position: absolute;display: none">
            <div id="relate">

            </div>
        </div>
    </div>
    <input type="button" id="i-check" value="构建Trie树" class="btn btn-primary btn-sm"
           onclick="submit()">
</div>
</body>
<script>
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

            if (!node.end) {//不是结束标志
                this.size++;
                node.end = true;
            }
        }

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
            //存在公共前缀，将该分支下最近的五条数据输出
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

    }

    class Node {
        constructor(value) {
            this.val = value;
            this.child = [];
            this.end = false;
            this.height = 0;
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

    const trie = new TrieTree()

    function show() {
        $("#show").show();
    }

    function hide() {
        $("#show").hide();
    }

    function search(e) {
        var key = $("#search").val();
        if (key)
            trie.relate(key);
        else
            $("#relate").html("");
    }

    function submit() {
        let key = $("#search").val();
        trie.insert(key)
        console.log(trie)
    }


</script>


```