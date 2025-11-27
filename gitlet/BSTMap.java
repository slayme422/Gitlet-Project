package gitlet;

import java.util.Iterator;
import java.util.Set;

//树框架本身是一个value，左子树和右子树是两个孩子
//通过key value查找Tree
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K,V> {
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        int size;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            size = 1;
        }
    }

    /**
     * 根节点
     */
    Node<K, V> root;

    int size;


    /**
     * 构造器建立一个root
     */
    public BSTMap() {
        root = null;
    }


    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key);
    }

    private boolean containsKey(Node<K, V> node, K key) {
        if (node == null) {
            return false;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return true;
        }
        if (cmp < 0) {
            return containsKey(node.left, key);

        }
        return containsKey(node.right, key);
    }

    //获得value
    @Override
    public V get(K key) {
        Node<K, V> cur = root;
        while (cur != null) {
            //如果cur<0 当前的key比目标key小，往右子树走,
            //如果cur>0,当前的key比目标key大，往左子树走
            int cmp = cur.key.compareTo(key);
            if (cmp < 0) {
                cur = cur.right;
            } else if (cmp > 0) {
                cur = cur.left;
            } else {
                return cur.value;
            }
        }
        return null;
    }

    /*返回当前所有节点的size**/
    @Override
    public int size() {
        return size;
    }

    /**
     * 私有内部的put功能
     *
     * @param node  当前节点，用来判断key的作用
     * @param key   当前key
     * @param value 要存放的值
     *              base case: 如果要存放的节点是空的，就直接原地建立一个new Node
     *              判断当前node的key和插入的key的大小
     */
    private Node<K, V> put(Node<K, V> node, K key, V value) {
        // 递归基底：找到空位，插入节点
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }

        int cmp = node.key.compareTo(key);

        if (cmp > 0) { // 新key比当前小 -> 去左子树
            node.left = put(node.left, key, value);
        } else if (cmp < 0) { // 新key比当前大 -> 去右子树
            node.right = put(node.right, key, value);
        } else { // key已存在 -> 更新value
            node.value = value;
        }

        return node;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    @Override
    public Set<K> keySet() {
        return Set.of();
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }


    public static void main(String[] args) {
        BSTMap<Integer,String> bst=new BSTMap<>();
        bst.put(5,"a");
        bst.put(9,"b");
        bst.put(3,"c");
        bst.put(4,"d");
        System.out.println(bst.size);
    }
}