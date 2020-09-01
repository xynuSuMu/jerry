
B+树源码
```html
package com.studyjava.email.test;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BTreeV2<K extends Comparable, V> {

    private Node<K, V> root = null;

    private int M;

    private int minKeySize;

    private int maxKeySize;

    public BTreeV2() {
        init(3);
    }

    public BTreeV2(int order) {
        init(order);
    }

    private void init(int M) {
        M = M + 1;
        this.M = M;
        root = new Node<>(M, true);
        maxKeySize = M - 1;
        if ((M & 1) == 0)
            minKeySize = (int) Math.ceil(M / 2.0);
        else
            minKeySize = (int) Math.ceil(M / 2.0)-1;
        System.out.println(minKeySize + "--");
    }

    //层序遍历使用
    private Queue<Node<K, V>> queue = new LinkedBlockingQueue<>();

    public void levelOrder() {
        queue.add(root);
        while (!queue.isEmpty()) {
            Node<K, V> root = queue.poll();
            for (Entry<K, V> entry : root.entries) {
                System.out.print(entry.k + " ");
            }
            System.out.print("->");
            for (Node<K, V> node : root.child) {
                queue.add(node);
            }
        }
    }


    public boolean remove(K k) {
        Boolean temp = remove(root, k);
        if (temp && root.entries.size() == 1 && !root.leaf) {
//            System.out.println("根结点失去平衡");
            root = root.child.get(0);
        }
        return temp;
    }

    private boolean remove(Node<K, V> root, K k) {
        SearchResult<K, V> result = root.search(k);
        int index = result.index;
        if (root.leaf && result.exist) {//删除
            root.entries.remove(index);
            return true;
        } else if (root.leaf || index >= root.child.size()) {
            return false;
        }
        remove(root.child.get(index), k);
        //判断是否失衡：
        Node<K, V> nodeTemp = root.child.get(index);
        if (nodeTemp.entries.size() < minKeySize) {
//            System.out.println("结点失衡");
            if (index > 0 && root.child.get(index - 1).entries.size() > minKeySize) {
//                System.out.println("借左孩子结点");
                Node<K, V> leftChild = root.child.get(index - 1);
                int leftChildSize = leftChild.entries.size();
                //增加孩子
                if (leftChild.child.size() > 0) {
                    Node<K, V> leftChildSub = leftChild.child.get(leftChildSize - 1);
                    nodeTemp.child.add(0, leftChildSub);
                    leftChild.child.remove(leftChildSize - 1);
                }
                Entry<K, V> maxEntry = leftChild.entries.get(leftChildSize - 1);
                //移除
                leftChild.entries.remove(leftChildSize - 1);
                //增加Entry
                nodeTemp.entries.add(0, maxEntry);
                //更新左孩子索引值
                updateIndexNodeByRemove(root, index - 1);
                //更新右孩子索引值
                updateIndexNodeByRemove(root, index);
            } else if (index >= 0 && root.child.size() - 1 > index && root.child.get(index + 1).entries.size() > minKeySize) {
//                System.out.println("借右孩子结点");
                Node<K, V> rightChild = root.child.get(index + 1);
                //带上孩子
                if (rightChild.child.size() > 0) {
                    Node<K, V> rightChildSub = rightChild.child.get(0);
                    nodeTemp.child.add(rightChildSub);
                    rightChild.child.remove(0);
                }
                //获取最小的
                Entry<K, V> minEntry = rightChild.entries.get(0);
                rightChild.entries.remove(0);
                nodeTemp.entries.add(minKeySize - 1, minEntry);
                //更新当前结点的索引值
                updateIndexNodeByRemove(root, index);
                //更新右孩子索引结点
                updateIndexNodeByRemove(root, index + 1);
            } else {
                if (index == 0) {
//                    System.out.println("合并右兄弟结点，将右兄弟加入左侧");
                    //右兄弟
                    Node<K, V> rightChild = root.child.get(index + 1);
                    //删除当前索引值
                    root.entries.remove(index);
                    //移除右孩子
                    root.child.remove(index + 1);
                    if (rightChild.leaf) {//叶子结点
                        nodeTemp.rightNode = rightChild.rightNode;
                        rightChild.rightNode = null;
                    }
                    nodeTemp.entries.addAll(rightChild.entries);
                    nodeTemp.child.addAll(rightChild.child);
                    updateIndexNodeByRemove(root, index);
                } else {
//                    System.out.println("合并左结点，将当前结点加入左兄弟");
                    Node<K, V> leftChild = root.child.get(index - 1);
                    //删除左结点索引
                    root.entries.remove(index - 1);
                    //移除当前结点
                    root.child.remove(index);
                    if (leftChild.leaf) {//叶子结点
                        leftChild.rightNode = nodeTemp.rightNode;
                        nodeTemp.rightNode = null;
                    }
                    leftChild.entries.addAll(nodeTemp.entries);
                    leftChild.child.addAll(nodeTemp.child);
                    updateIndexNodeByRemove(root, index - 1);
                }
            }
        } else {
//            System.out.println("结点未失衡");
            updateIndexNodeByRemove(root, index);
        }
        return true;

    }


    public V get(K k) {
        return get(root, k);
    }

    private V get(Node<K, V> root, K k) {
        SearchResult<K, V> result = root.search(k);
        if (root.leaf && result.exist) {
            return result.value;
        } else if (result.exist || !root.leaf) {
            return get(root.child.get(result.index), k);
        }
        return null;
    }

    public void insert(K k, V v) {
        insert(root, k, v);
        if (root.entries.size() > maxKeySize) {
//            System.out.println("根结点分裂");
            Node<K, V> newRoot = new Node<>(M, false);
            newRoot.child.add(root);
            spilt(newRoot, root, 0, true);
            root = newRoot;

        }
    }

    private void insert(Node<K, V> root, K k, V v) {
        //叶子结点直接插入
        SearchResult<K, V> result = root.search(k);
        int index = result.index;
        //叶子结点，且key不存在
        if (root.leaf && !result.exist) {
            root.insertEntry(index, k, v);
        } else if (!root.leaf) {//非叶子结点,K个关键字，对应K个孩子
            if (index > root.child.size() - 1) {
                index = root.child.size() - 1;
            }
            insert(root.child.get(index), k, v);
            //判断是否满结点上升
            if (root.child.get(index).entries.size() > maxKeySize) {
                spilt(root, root.child.get(index), index, false);
                //分裂后，孩子加1，索引加1
                updateIndexNode(root, index + 1);
            } else {
                updateIndexNode(root, index);
            }
        }
    }

    //判断索引结点是否需要替换
    private void updateIndexNode(Node<K, V> root, int index) {
        Node<K, V> child = root.child.get(index);
        if (child.entries.get(child.entries.size() - 1).k.compareTo(
                root.entries.get(root.entries.size() - 1).k) > 0
        ) {
            root.entries.get(root.entries.size() - 1).k = child.entries.get(child.entries.size() - 1).k;
        }
    }

    private void updateIndexNodeByRemove(Node<K, V> root, int index) {
        Node<K, V> child = root.child.get(index);
        if (child.entries.get(child.entries.size() - 1).k.compareTo(
                root.entries.get(index).k) != 0
        ) {
            root.entries.get(index).k = child.entries.get(child.entries.size() - 1).k;
        }
    }

    private void spilt(Node<K, V> parent, Node<K, V> child, int childIndex, boolean isRoot) {
        //要提取的结点下标
        int index;
        if ((M & 1) == 1) {//奇数
            index = M / 2 - 1;
        } else {
            index = M / 2;
        }
        //
        if (isRoot) {//根结点上升两个
            parent.insertEntry(childIndex, child.entries.get(index - 1).k, null);
            parent.insertEntry(childIndex + 1, child.entries.get(M - 1).k, null);
        } else {
            parent.insertEntry(childIndex, child.entries.get(index - 1).k, null);
        }
        //增加右孩子
        Node<K, V> rightNode = new Node<>(M, child.leaf);
        //右孩子增加Entry
        for (int i = index, ii = 0; i < M; i++, ii++) {
            Entry<K, V> entry = child.entries.get(i);
            rightNode.insertEntry(ii, entry);
        }
        //左孩子删除Entry
        for (int i = M - 1; i >= index; i--) {
            child.entries.remove(i);
        }

        //非叶子结点则转移孩子
        if (!child.leaf) {
            //右孩子增加child
            for (int i = index; i < M; i++) {
                rightNode.child.add(child.child.get(i));
            }
            //左孩子删除child
            for (int i = M - 1; i >= index; i--) {
                child.child.remove(i);
            }
        } else {
            //关联叶子
            Node<K, V> temp = child.rightNode;
            child.rightNode = rightNode;
            rightNode.rightNode = temp;
        }
        //增加
        parent.child.add(childIndex + 1, rightNode);
    }

    private class Node<K extends Comparable, V> {
        //是否叶子/索引结点
        private boolean leaf;

        private List<Entry<K, V>> entries;

        private List<Node<K, V>> child;

        //叶子结点，关联形成链表
        private Node<K, V> rightNode = null;

        private Node(int order, boolean isLeaf) {
            this.entries = new ArrayList<>(order);
            this.child = new ArrayList<>(order);
            this.leaf = isLeaf;
        }

        private SearchResult<K, V> search(K k) {
            //二分搜索
            int left = 0;
            int right = entries.size() - 1;
            int mid = 0;
            while (left <= right) {
                mid = (left + right) / 2;
                K k1 = entries.get(mid).k;
                if (k.compareTo(k1) > 0) {//k大于mid
                    left = mid + 1;
                } else if (k.compareTo(k1) < 0) {//k小于mid
                    right = mid - 1;
                } else {
                    break;
                }
            }
            if (left <= right) {//k存在
                return new SearchResult<>(true, mid, entries.get(mid).v, this);
            } else {
                return new SearchResult<>(false, left, null, null);
            }
        }

        private void insertEntry(int index, K k, V v) {
            entries.add(index, new Entry(k, v));
        }

        private void insertEntry(int index, Entry<K, V> entry) {
            entries.add(index, entry);
        }

        public void setLeaf(boolean leaf) {
            this.leaf = leaf;
        }

        public void setEntries(List<Entry<K, V>> entries) {
            this.entries = entries;
        }

        public void setChild(List<Node<K, V>> child) {
            this.child = child;
        }

        public void setRightNode(Node<K, V> rightNode) {
            this.rightNode = rightNode;
        }

        public boolean isLeaf() {
            return leaf;
        }

        public List<Entry<K, V>> getEntries() {
            return entries;
        }

        public List<Node<K, V>> getChild() {
            return child;
        }

        public Node<K, V> getRightNode() {
            return rightNode;
        }
    }

    private class Entry<K, V> {
        private K k;
        private V v;

        public Entry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        public K getK() {
            return k;
        }

        public V getV() {
            return v;
        }

        public void setK(K k) {
            this.k = k;
        }

        public void setV(V v) {
            this.v = v;
        }
    }

    private class SearchResult<K extends Comparable, V> {
        private Node<K, V> node;
        private boolean exist;
        private int index;
        private V value;

        public SearchResult(boolean exist, int index, V value, Node<K, V> node) {
            this.exist = exist;
            this.index = index;
            this.value = value;
            this.node = node;
        }

        public boolean isExist() {
            return exist;
        }

        public void setExist(boolean exist) {
            this.exist = exist;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
```