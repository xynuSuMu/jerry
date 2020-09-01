B树源码
```html
package com.studyjava.email.test;

import com.alibaba.fastjson.JSONObject;

import java.util.*

public class BTree<K extends Comparable, V> {

    private final int DEAFULT_VALUE = 2;

    //默认为2，即二路平衡树/平衡二叉树，每个结点的关键字至多为M - 1
    private int M = DEAFULT_VALUE;

    private int minKeySize = (int) Math.ceil(M / 2.0) - 1;

    private int maxKeySize = M - 1;

    public Node<K, V> root = null;

    public BTree() {

    }

    //M阶，M一般为奇
    public BTree(int order) throws Exception {
        if (order < DEAFULT_VALUE) {
            throw new Exception("阶数不能小于2");
        }
        this.M = order;
        root = new Node<>(order);
        //构造时 是 叶子结点
        root.leaf = 1;
        minKeySize = (int) Math.ceil(M / 2.0) - 1;
        maxKeySize = M - 1;
    }

    public V search(K k) {
        return search(root, k);
    }

    private V search(Node<K, V> root, K k) {
//        System.out.println(JSONObject.toJSONString(root));
        SearchResult<V> searchResult = root.search(k);
        if (searchResult.exist) {
            return searchResult.value;
        } else {
            if (root.leaf == 1)//已经是叶子结点，但仍未查询到
                return null;
            return search(root.child.get(searchResult.index), k);
        }
    }

    //插入
    public void insert(K k, V v) {
        //插入
        insertNode(root, k, v);
        //根结点个数已满，需要拆分
        if (root.maps.size() > maxKeySize) {
            Node<K, V> newRoot = new Node<>(M);
            newRoot.leaf = 0;
            newRoot.child.add(root);
            spilt(newRoot, root, 0);
            root = newRoot;
        }
    }

    private boolean insertNode(Node<K, V> root, K k, V v) {
        Entry<K, V> newEntry = new Entry<>(k, v);
        if (root.leaf == 1) {//叶子结点，直接插入即可
            root.insertEntry(newEntry);
        } else {//非叶子结点，进行寻找
            SearchResult<V> result = root.search(k);
            if (result.exist) {
                //存在，插入失败
                return false;
            } else {
                Node<K, V> child = root.child.get(result.index);
                //递归进行叶子结点的插入
                insertNode(child, k, v);
                if (child.maps.size() > maxKeySize) { //孩子结点的结点个数已满，需要拆分
                    spilt(root, child, result.index);
                }
            }
        }
        return true;
    }

    //分裂一个满结点
    private void spilt(Node<K, V> parent, Node<K, V> node, int index) {
        //首先获取满结点的中间值
        int mid;
        if ((M & 1) == 0) {//偶数
            mid = M / 2 - 1;
        } else {//奇数
            mid = M / 2;
        }
        Entry<K, V> midEntry = node.maps.get(mid);
        //中间结点右边的新Node
        Node<K, V> rightNode = new Node<>(M);
        rightNode.leaf = node.leaf;
        for (int i = mid + 1; i < M; ++i) {
            //新的增加
            rightNode.insertEntry(new Entry<>(node.maps.get(i).k, node.maps.get(i).v));
        }
        for (int i = M - 1; i >= mid; --i) {
            //老的移除
            node.maps.remove(i);
        }
        //非叶子结点，将孩子结点进行分割，右结点加，左结点删
        if (node.leaf == 0) {
            //核心在于切割，当父级发生改动，那么孩子结点切割边界需要定义
            for (int i = mid + 1; i <= M; ++i) {
                rightNode.child.add(node.child.get(i));
            }
            for (int i = M; i > mid; --i) {
                node.child.remove(i);
            }
        }
        //增加分裂后的结点
        parent.insertEntry(midEntry);
        parent.child.add(index + 1, rightNode);
    }

    //删除结点
    public boolean remove(K k) {
        return remove(root, k);
    }

    //删除最终落到叶子结点
    private boolean remove(Node<K, V> root, K k) {
        SearchResult<V> result = root.search(k);
        if (result.exist && root.leaf == 1) {
            //叶子结点，直接移除
            root.maps.remove(result.index);
            return true;
        } else {
            if (root.leaf == 1) {//叶子结点
                return false;
            } else {
                //递归删除
                if (result.exist && root.leaf == 0) {//非叶子结点，找到前驱/后驱叶子，执行替换
                    System.out.println("走到非叶子结点");
                    //递归找到最大前驱
                    Node<K, V> preNode = preNodeReplace(root, result.index);
                    //最右替代
                    Entry<K, V> entry = preNode.maps.get(preNode.maps.size() - 1);
                    //移除需要删除非叶子结点
                    root.maps.remove(result.index);
                    Entry<K, V> newEntry = new Entry<>(entry.k, entry.v);
                    //插入前继结点
                    root.insertEntry(newEntry);
                    k = entry.k;
                }
                remove(root.child.get(result.index), k);
                if (root.child.get(result.index).maps.size() < minKeySize) {
                    System.out.println("需要调整该孩子结点:" + result.index);
                    //父结点
                    Node<K, V> parent = root;
                    if (result.index > 0 && root.child.get(result.index - 1).maps.size() > minKeySize) {//左兄弟
                        //需要下移的Entry
                        Entry<K, V> parentEntry = parent.maps.get(result.index - 1);
                        System.out.println("借左兄弟结点");
                        //左兄弟
                        Node<K, V> leftChild = root.child.get(result.index - 1);
                        //借最大的Entry
                        Entry<K, V> entry = leftChild.maps.get(leftChild.maps.size() - 1);
                        //调整结点，增加父结点
                        root.child.get(result.index).insertEntry(new Entry<>(parentEntry.k, parentEntry.v));
                        //删除父结点
                        root.maps.remove(result.index - 1);
                        //左孩子上移
                        root.insertEntry(new Entry<>(entry.k, entry.v));
                        //左孩子删除
                        leftChild.maps.remove(leftChild.maps.size() - 1);

                    } else if (root.child.size() > result.index + 1 && root.child.get(result.index + 1).maps.size() > minKeySize) {  //右兄弟
                        System.out.println("借右兄弟结点");
                        //需要下移的Entry
                        Entry<K, V> parentEntry = parent.maps.get(result.index);
                        //右兄弟
                        Node<K, V> rightChild = root.child.get(result.index + 1);
                        //借最小的Entry
                        Entry<K, V> entry = rightChild.maps.get(0);
                        //调整结点，增加父结点到需要调整的结点
                        root.child.get(result.index).insertEntry(new Entry<>(parentEntry.k, parentEntry.v));
                        //删除父结点
                        root.maps.remove(result.index);
                        //右孩子上移
                        root.insertEntry(new Entry<>(entry.k, entry.v));
                        //左孩子删除
                        rightChild.maps.remove(0);
                    } else {
                        if (result.index > 0) {//将缺失结点 加入 到左孩子中，父结点下移一位
                            System.out.println("合并左兄弟");
                            //下移的父结点Entry
                            Entry<K, V> parentEntry = parent.maps.get(result.index - 1);
                            //左兄弟
                            Node<K, V> leftChild = root.child.get(result.index - 1);
                            //父结点下移
                            leftChild.insertEntry(new Entry<>(parentEntry.k, parentEntry.v));
                            //合并结点
                            leftChild.maps.addAll(root.child.get(result.index).maps);
                            leftChild.child.addAll(root.child.get(result.index).child);
                            //删除父结点
                            parent.maps.remove(result.index - 1);
                            //删除被合并结点
                            root.child.remove(result.index);
                        } else {
                            System.out.println("合并右兄弟");
                            //下移的父结点Entry
                            Entry<K, V> parentEntry = parent.maps.get(result.index);
                            //右兄弟
                            Node<K, V> rightChild = root.child.get(result.index + 1);
                            //父结点下移
                            rightChild.insertEntry(new Entry<>(parentEntry.k, parentEntry.v));
                            //合并结点
                            root.child.get(result.index).maps.addAll(rightChild.maps);
                            root.child.get(result.index).child.addAll(rightChild.child);
                            //删除父结点
                            parent.maps.remove(result.index);
                            //删除被合并结点
                            root.child.remove(result.index + 1);
                        }
                    }
                    if (root.maps.size() == 0) {//向上合并造成跟结点丢失
                        System.out.println("父结点为空");
                        root.maps = root.child.get(0).maps;
                        root.child = root.child.get(0).child;
                    }
                }
                return true;
            }
        }

    }

    //index 为 root中对应entry的索引
    private Node<K, V> preNodeReplace(Node<K, V> root, int index) {
        if (root.leaf == 1) {//这里是需要替代的
            return root;
        } else {
            //最右边的结点
            Node<K, V> preNode = root.child.get(index);
            //最右 结点
            return preNodeReplace(preNode, preNode.maps.size() - 1);
        }
    }

    //调整结点
    private void adjust(Node<K, V> root, Node<K, V> adjNode) {
        //找到adjNode

    }

    //结点
    private static class Node<K extends Comparable, V> {
        //结点
        private List<Entry<K, V>> maps;
        //孩子
        private List<Node<K, V>> child;
        //是否为叶子
        private int leaf;

        private Node(int order) {
            maps = new ArrayList<>(order);
            child = new ArrayList<>(order);
            //默认非叶子结点
            this.leaf = 0;
        }

        public List<Entry<K, V>> getMaps() {
            return maps;
        }

        public void setMaps(List<Entry<K, V>> maps) {
            this.maps = maps;
        }

        public List<Node<K, V>> getChild() {
            return child;
        }

        public void setChild(List<Node<K, V>> child) {
            this.child = child;
        }

        public int getLeaf() {
            return leaf;
        }

        public void setLeaf(int leaf) {
            this.leaf = leaf;
        }

        //二分搜索k是否存在
        private SearchResult<V> search(K k) {
            int left = 0;
            int right = maps.size() - 1;
            int mid = 0;
            while (left <= right) {
                mid = (left + right) / 2;
                Entry<K, V> entryTemp = maps.get(mid);
                if (k.compareTo(entryTemp.k) > 0) {//left右移动
                    left = mid + 1;
                } else if (k.compareTo(entryTemp.k) < 0) {//right左移动
                    right = mid - 1;
                } else {//找到key退出
                    break;
                }
            }
            if (left <= right) {
                //查找成功，返回搜索结果
                return new SearchResult<>(true, mid, maps.get(mid).v);
            } else {
                //未查找到，此时left对应的孩子应该存放该结点
                return new SearchResult<>(false, left, null);
            }

        }

        private boolean insertEntry(Entry<K, V> entry) {
            SearchResult<V> result = search(entry.k);
            if (result.exist)
                return false;
            else {
                maps.add(result.index, entry);
                return true;
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "maps=" + maps +
                    ", child=" + child +
                    ", leaf=" + leaf +
                    '}';
        }
    }

    private static class Entry<K, V> {
        private K k;
        private V v;

        public K getK() {
            return k;
        }

        public void setK(K k) {
            this.k = k;
        }

        public V getV() {
            return v;
        }

        public void setV(V v) {
            this.v = v;
        }

        public Entry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return Objects.equals(k, entry.k);
        }

        @Override
        public int hashCode() {
            return Objects.hash(k);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "k=" + k +
                    ", v=" + v +
                    '}';
        }
    }

    private static class SearchResult<V> {
        private boolean exist;
        private int index;
        private V value;

        public SearchResult(boolean exist, int index, V value) {
            this.exist = exist;
            this.index = index;
            this.value = value;
        }
    }
}

```