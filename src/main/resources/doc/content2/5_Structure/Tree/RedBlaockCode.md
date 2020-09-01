#### 红黑树实现代码

```html

package com.studyjava.email.test;


import com.alibaba.fastjson.JSONObject;

public class RedBlockTree<K extends Comparable, V> {

    //https://www.jianshu.com/p/e136ec79235c
    //https://blog.csdn.net/qq_41854763/article/details/82694873

    private Node<K, V> root;

    private boolean isShow = false;

  

    public V search(K k) {
        return search(root, k);
    }

    public void editColor(K k) {
        Node<K, V> temp = this.root;
        while (temp != null) {
            if (k.compareTo(temp.k) == 0) {
                temp.isRed = false;
                break;
            } else if (k.compareTo(temp.k) > 0) {
                temp = temp.rightChild;
            } else
                temp = temp.leftChild;
        }
    }

    private V search(Node<K, V> root, K k) {
        Node<K, V> temp = root;
        while (temp != null) {
            if (k.compareTo(temp.k) == 0)
                return temp.v;
            else if (k.compareTo(temp.k) > 0)
                temp = temp.rightChild;
            else
                temp = temp.leftChild;
        }
        return null;
    }

    public boolean remove(K k) {
        return remove(this.root, k);
    }

    /**
     * 红黑树结点的删除：
     * 从结点颜色上来看，删除分为红黑两种
     * 从结点构造上来看，分为，孩子结点Nil，一个孩子结点为Nil,孩子结点不为Nil。
     * 从颜色上说删除红色结点对于树的平衡是没影响的，但是删除黑色结点就会失衡
     * 从结构上说孩子：
     * 结点为Nil容易删除，但是如果颜色为红色，那么平衡操作比较复杂，需要借结点
     * 结点一个孩子为Nil，那么该结点颜色必然是黑色且非Nil的结点颜色为红，此时删除，将孩子结点颜色变黑然后将父结点指针指向孩子结点
     * 孩子结点都不为Nil，那么就需要使用其后继结点来替代删除，替代的结果就是回到上面的两种情况
     *
     * @param root
     * @param k
     * @return
     */
    private boolean remove(Node<K, V> root, K k) {
        Node<K, V> deleteNode = root;
        while (deleteNode != null) {
            if (deleteNode.k.compareTo(k) > 0)
                deleteNode = deleteNode.leftChild;
            else if (deleteNode.k.compareTo(k) < 0)
                deleteNode = deleteNode.rightChild;
            else
                break;
        }
        //不存在Key
        if (deleteNode == null)
            return false;
        //如果删除结点，有两个孩子，寻找替代结点删除
        if (deleteNode.leftChild != null && deleteNode.rightChild != null) {
            Node<K, V> replace = deleteNode.rightChild;
            while (replace.leftChild != null) {
                replace = replace.leftChild;
            }
            //替换当前结点，
            deleteNode.k = replace.k;
            System.out.println("替代结点" + replace.k);
            //删除替换结点
            adjustDeleteNode(replace);
        } else {
            adjustDeleteNode(deleteNode);
        }
        return true;
    }

    //不可能有两个孩子
    private void adjustDeleteNode(Node<K, V> deleteNode) {
        Node<K, V> rightChild = deleteNode.rightChild;
        Node<K, V> leftChild = deleteNode.leftChild;
        if (rightChild != null && leftChild != null) {
            throw new RuntimeException("删除结点左右孩子都非Nil");
        }
        Node<K, V> parent = deleteNode.parent;
        //情况一:删除结点无
        if (rightChild == null && leftChild == null) {
            if (parent == null) {//删除后空树
                this.root = null;
                return;
            }
            int pos = -1;
            if (deleteNode.k.compareTo(parent.k) > 0) {
                pos = 1;
            }
            //先把删除结点干掉，如果是红色，删除完，程序就结束了
//            deleteNode.parent = null;
            if (pos == 1) {//删除结点再父结点的右侧
                parent.rightChild = null;
            } else {
                parent.leftChild = null;
            }
            //再讨论平衡的情况
            while (!deleteNode.isRed && deleteNode != this.root) {//如果删除结点是黑色，那么需要平衡
                System.out.println(deleteNode.k + "--" + deleteNode.parent.k);
                parent = deleteNode.parent;//==null?parent:deleteNode.parent;
                //兄弟结点
                int brotherPos = 1;
                Node<K, V> brother = parent.rightChild;
                if (deleteNode.k.compareTo(parent.k) > 0) {
                    brother = parent.leftChild;
                    brotherPos = -1;
                }
                //1.1 最复杂的情况，父结点P为黑色，当前结点为黑色，兄弟结点为黑色，此时删除，包含P结点的树必然失衡，需要递归向上平衡
                if (!parent.isRed && !deleteNode.isRed && !brother.isRed &&
                        ((brother.leftChild == null || !brother.leftChild.isRed) && (brother.rightChild == null || !brother.rightChild.isRed))) {
                    System.out.println("需要递归删除");
                    System.out.println(deleteNode.parent.k + "...");
                    brother.isRed = true;//变红
                    deleteNode = deleteNode.parent;//继续循环替代

                    continue;
                }
                //1.2 ，父结点P为黑色，当前结点为黑色，兄弟结点为黑色，单兄弟结点存在颜色
                else if (!parent.isRed && !deleteNode.isRed && !brother.isRed &&
                        (brother.leftChild != null && brother.leftChild.isRed ||
                                brother.rightChild != null && brother.rightChild.isRed
                        )
                ) {
                    System.out.println("情况1.2");
                    if (brotherPos == 1) {//兄弟结点在右边
                        if ((brother.leftChild != null && brother.rightChild != null) || brother.leftChild != null) {
                            LLRotating(brother);
                            brother.parent.isRed = false;
                            brother.parent.leftChild.isRed = false;
                        } else {
                            LRotating(brother, brother.parent);
                            //变色
                            brother.isRed = false;
                            brother.rightChild.isRed = false;
                            brother.leftChild.isRed = false;
                        }
                    } else {
                        if ((brother.leftChild != null && brother.rightChild != null) || brother.leftChild != null) {
                            rightRotating(brother, brother.parent);
                            brother.leftChild.isRed = false;
                        } else {
                            LRRotating(brother);
                            //变色
                            brother.parent.isRed = false;
                        }
                    }
                }
                //1.3 父结点为红色，且兄弟结点孩子结点为Nil，此时处理方式较为简单，将父亲结点变黑，兄弟结点变红即可
                else if (parent.isRed && (brother.leftChild == null && brother.rightChild == null)) {
                    System.out.println("1.3");
                    parent.isRed = false;
                    brother.isRed = true;

                }
                //1.4 父结点为红色，兄弟结点的孩子结点不为Nil(不为Nil则为红),此时兄弟结点无法直接变红，需要进行旋转才能保证树的平衡
                else if (parent.isRed && (brother.leftChild != null || brother.rightChild != null)) {
                    if (brotherPos == 1) {//兄弟结点在右边
                        System.out.println("兄弟结点在右边");
                        if ((brother.leftChild != null && brother.rightChild != null) || brother.leftChild != null) {
                            //对BL进行旋转 brother 变成 father
                            LLRotating(brother);
                            brother.parent.isRed = true;
                            brother.parent.leftChild.isRed = false;
                        } else {//brother.rightChild!=null
                            LRotating(brother, brother.parent);
                            //变色
                            brother.isRed = true;
                            brother.rightChild.isRed = false;
                            brother.leftChild.isRed = false;
                        }
//                        System.out.println(brother.k+"-----");

                    } else {
                        System.out.println("兄弟结点在左边");
                        if ((brother.leftChild != null && brother.rightChild != null) || brother.leftChild != null) {
                            rightRotating(brother, brother.parent);
                            brother.isRed = true;
                            brother.rightChild.isRed = false;
                            brother.leftChild.isRed = false;
                        } else {//brother.leftChild!=null
                            //对BL进行旋转 brother 变成 father
                            LRRotating(brother);
                            brother.parent.isRed = true;
                            //这里是有孩子
                            brother.parent.rightChild.isRed = false;
                        }
                    }
                }
                break;

            }
        }
        //情况二:删除结点存在一个孩子
        else {//此时deleteNode颜色必然为黑，且非Nil的孩子结点颜色必然为红，否则无法达到平衡
            Node<K, V> child = rightChild == null ? rightChild : leftChild;
//            Node<K, V> parent = deleteNode.parent;
            if (parent == null) {
                this.root = child;
            } else {
                if (deleteNode.k.compareTo(parent.k) > 0) {
                    //右侧
                    parent.rightChild = child;
                } else {
                    //左侧
                    parent.leftChild = child;
                }
            }
            //绑定新的关联
            child.parent = parent;
            child.isRed = false;
            //删除关联
            deleteNode.rightChild = null;
            deleteNode.leftChild = null;
            deleteNode.parent = null;
        }

    }


    public boolean insert(K k, V v) {
        if (root == null) {
            root = new Node<>(k, v, false, null);
            return true;
        }
        return insert(root, null, k, v);
    }

    private boolean insert(Node<K, V> root, Node<K, V> parent, K k, V v) {

        if (root == null) {
            Node<K, V> newNode = new Node<>(k, v, true, parent);
            if (k.compareTo(parent.k) > 0) {
                parent.rightChild = newNode;
            } else {
                parent.leftChild = newNode;
            }
            return true;
        }

        int pos = 0;
        if (k.compareTo(root.k) > 0) {
            insert(root.rightChild, root, k, v);
            pos = 1;
        } else if (k.compareTo(root.k) < 0) {
            insert(root.leftChild, root, k, v);
            pos = -1;
        } else {
            root.k = k;
        }

        //是否失衡
        if (isShow)
            System.out.println("--" + JSONObject.toJSONString(root));
        if (root.isRed && ((root.rightChild != null && root.rightChild.isRed) || (root.leftChild != null && root.leftChild.isRed))) {
            Node<K, V> rootParent = root.parent;
            if (isShow)
                System.out.println(pos + ",失衡" + root.parent.v + "->" + root.v + "->" + v);
            /**
             * 调整情况如下:
             * 情况一:         黑 -> 无右子树
             *   root      红
             *  tempNode红
             *  调整          root       黑
             *           tempNode红  红      红
             *
             *  情况二:       黑
             *            红
             *               红
             *
             *               黑
             *             红
             *           红
             */
            if (root.k.compareTo(rootParent.k) > 0) {//root在右侧
                if (isShow)
                    System.out.println("root在右侧" + root.k);
                if (rootParent.leftChild == null || !rootParent.leftChild.isRed) {
                    if (pos == 1) {
                        if (isShow)
                            System.out.println("需要左旋");
                        LRotating(root, root.parent);
//                        System.out.println(JSONObject.toJSONString(rootParent));
                    } else {
                        if (isShow)
                            System.out.println("左-二次旋转");
                        LLRotating(root);
                    }
                } else if (rootParent.leftChild.isRed) {
//                    if (isShow)
//                        System.out.println("root和rootParent变黑即可");
                    root.isRed = false;
                    rootParent.leftChild.isRed = false;
                    if (rootParent.parent != null) {
                        rootParent.isRed = true;
                    }
                }
            } else {//root在左侧
//                System.out.println("root在左侧" + root.k);
                if (rootParent.rightChild == null || !rootParent.rightChild.isRed) {//右旋
                    if (pos == -1) {
//                        System.out.println("需要右旋转" + root.parent.k);
                        rightRotating(root, root.parent);
//                        System.out.println(JSONObject.toJSONString(this.root));
                    } else {
//                        System.out.println("右-需要二次旋转");
                        LRRotating(root);
                    }

//                    System.out.println(JSONObject.toJSONString(root));
                } else if (rootParent.rightChild.isRed) {
                    //root和rootParent变黑即可
//                    System.out.println("root和rootParent变黑即可");
                    root.isRed = false;
                    rootParent.rightChild.isRed = false;
                    if (rootParent.parent != null) {
                        rootParent.isRed = true;
                    }
                }
            }
        }

        return true;
    }

    //右旋
    private void rightRotating(Node<K, V> root, Node<K, V> parent) {
        Node<K, V> temp = parent;
        //父结点变更为子根结点，变黑
        parent = root;
        if (temp.parent != null) {
            if (temp.k.compareTo(temp.parent.k) > 0) {//parent 在 pp 的右侧
                temp.parent.rightChild = parent;
            } else {
                temp.parent.leftChild = parent;
            }
        } else {
            this.root = root;
        }
        parent.parent = temp.parent;
        parent.isRed = false;
        //原父结点右孩子 变更为现结点的左孩子,颜色变红
        temp.leftChild = parent.rightChild;
        if (parent.rightChild != null)
            parent.rightChild.parent = temp;
        temp.isRed = true;
        temp.parent = parent;
        parent.rightChild = temp;
//        System.out.println(JSONObject.toJSONString(parent)+"---");
    }

    private void LRRotating(Node<K, V> root) {
        Node<K, V> temp = root;
        //右孩子结点 变更为当前结点
        root = root.rightChild;
        root.parent = temp.parent;
        temp.parent = root;
        temp.rightChild = null;
        root.leftChild = temp;
        root.rightChild = null;
//        System.out.println(JSONObject.toJSONString(root));
        rightRotating(root, root.parent);
    }

    /**
     * B
     * R
     * R
     * B
     * R     R
     *
     * @param root
     * @param parent
     */
    private void LRotating(Node<K, V> root, Node<K, V> parent) {
        Node<K, V> temp = parent;
        //父结点设置为当前结点
        parent = root;
        if (temp.parent != null) {
//            temp.parent.rightChild = parent;
            if (temp.k.compareTo(temp.parent.k) > 0) {//parent 在 pp 的右侧
                temp.parent.rightChild = parent;
            } else {
                temp.parent.leftChild = parent;
            }
        } else {
            this.root = parent;
        }
        parent.parent = temp.parent;
        parent.isRed = false;
        //
        temp.parent = parent;
        temp.rightChild = parent.leftChild;
        if (parent.leftChild != null)
            parent.leftChild.parent = temp;
        temp.isRed = true;
        parent.leftChild = temp;
    }

    /**
     * B
     * R
     * R
     *
     * @param root
     */
    private void LLRotating(Node<K, V> root) {
        Node<K, V> temp = root;
        root = root.leftChild;
        root.parent = temp.parent;
        temp.parent = root;
        temp.leftChild = null;
        root.rightChild = temp;
        root.leftChild = null;
        LRotating(root, root.parent);
    }

    private class Node<K extends Comparable, V> {
        private K k;
        private V v;
        private boolean isRed;
        private Node<K, V> leftChild;
        private Node<K, V> rightChild;
        private Node<K, V> parent;

        public Node(K k, V v, boolean isRed, Node<K, V> parent) {
            this.k = k;
            this.v = v;
            this.isRed = isRed;
            this.parent = parent;
            leftChild = null;
            rightChild = null;
        }

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

        public boolean isRed() {
            return isRed;
        }

        public void setRed(boolean red) {
            isRed = red;
        }

        public Node<K, V> getLeftChild() {
            return leftChild;
        }

        public void setLeftChild(Node<K, V> leftChild) {
            this.leftChild = leftChild;
        }

        public Node<K, V> getRightChild() {
            return rightChild;
        }

        public void setRightChild(Node<K, V> rightChild) {
            this.rightChild = rightChild;
        }

        public Node<K, V> getParent() {
            return parent;
        }

        public void setParent(Node<K, V> parent) {
            this.parent = parent;
        }
    }
}


```