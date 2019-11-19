package ds.force;

import java.util.Random;

public class Treap {

    transient Random random = new Random();

    transient Node root;

    public void insert(int value){
        Node t = root;
        if (t == null){
            root = new Node(value,random.nextInt());
            return;
        }
        Node parent;
        int cmp;
        do {
            parent = t;
            cmp = value - parent.value;
            if (cmp > 0){
                t = t.right;
            } else if (cmp < 0){
                t = t.left;
            } else {
                return;
            }
        } while (t != null);
        Node e = new Node(value,random.nextInt(),parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        shiftUp(e);
        return;
    }

    public boolean remove(int value){
        Node node = search(value);
        if (node == null){
            return false;
        }
        while (node.left != null && node.right != null){
            if (node.left.priority >= node.right.priority){
                rotateLeft(node);
            } else {
                rotateRight(node);
            }
        }
        if (node.left == null && node.right == null){
            if (node == root){
                root = null;
            } else if (node == node.parent.left){
                node.parent.left = node.parent =null;
            } else {
                node.parent.right = node.parent =null;
            }
            return true;
        }
        Node swap = node.left == null ? node.right : node.left;
        swap.parent = node.parent;
        if (node == root){
            root = swap;
        }else if (node == node.parent.left){
            node.parent.left = swap;
        } else {
            node.parent.right = swap;
        }
        node.left = node.right = node.parent = null;
        return true;
    }

    public boolean contain(int value){
        Node node = search(value);
        if (node == null){
            return false;
        }
        return true;
    }

    /**
     * 搜索节点
     * @param value
     * @return
     */
    private Node search(int value){
        Node node = root;
        int cmp;
        do {
            cmp = value - node.value;
            if (cmp > 0){
                node = node.right;
            } else if (cmp < 0){
                node = node.left;
            } else {
                return node;
            }
        } while (node != null);
        return null;
    }

    private void shiftUp(Node node) {
        while(node != root){
            Node parent = node.parent;
            if (node.priority >= parent.priority)
                break;
            if (node == parent.right){
                rotateLeft(parent);
            } else {
                rotateRight(parent);
            }
        }
    }

    /** From CLR */
    private void rotateLeft(Node p) {
        if (p != null) {
            Node r = p.right;
            p.right = r.left;
            if (r.left != null)
                r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null)
                root = r;
            else if (p.parent.left == p)
                p.parent.left = r;
            else
                p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
    }

    /** From CLR */
    private void rotateRight(Node p) {
        if (p != null) {
            Node l = p.left;
            p.left = l.right;
            if (l.right != null) l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null)
                root = l;
            else if (p.parent.right == p)
                p.parent.right = l;
            else p.parent.left = l;
            l.right = p;
            p.parent = l;
        }
    }

    static class Node {

        int value;

        int priority;

        Node left,right,parent;

        Node(int value,int priority){
            this.value = value;
            this.priority = priority;
        }

        Node(int key,int priority,Node parent){
            this.value = key;
            this.priority = priority;
            this.parent = parent;
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        Treap treap = new Treap();
        int[] data = new int[]{1010,4977,4878,4979,5000,5001,5002,5920,9912,9997,9999};
        for (int i : data){
            treap.insert(i);
        }
//        for (int i = 0; i < 1000000;i++){
//            treap.insert(random.nextInt(1000000));
//        }
//        for (int i = 0; i < 100000;i++){
//            treap.insert(random.nextInt(1000000));
//        }
        Treap test = new Treap();
        test.root = new Node(2,10);
        test.root.left = new Node(1,18,test.root);
        test.root.right = new Node(5,20,test.root);
        test.root.right.left = new Node(3,30,test.root.right);
        test.root.right.right = new Node(6,40,test.root.right);
        test.remove(6);
        test.insert(4);
        test.remove(5);
    }
}

