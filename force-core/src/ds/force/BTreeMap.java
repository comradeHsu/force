package ds.force;

import com.sun.istack.internal.NotNull;
import ds.force.util.ArrayUtil;

import java.util.*;
import java.util.function.ToIntBiFunction;

public class BTreeMap<K,V> implements Map<K,V>{
    /**
     * default minmum degree
     */
    private static final int DEFAULT_DEGREE = 2;
    /**
     * minmum degree,min t-1,max 2t-1
     */
    private int degree;

    /**
     * The comparator used to maintain order in this map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super K> comparator;

    transient BTreeNode<K,V> root;

    private int size;

    public BTreeMap(){
        this.comparator = null;
        this.degree = DEFAULT_DEGREE;
        this.root = new BTreeNode<>(degree,null);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        BTreeNode<K,V> target;
        K k = (K) key;
        if (comparator != null)
            target = getNode(k,comparator::compare);
        else target = getNode(k,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
        return target != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        BTreeNode<K,V> target;
        K k = (K) key;
        if (comparator != null)
            target = getNode(k,comparator::compare);
        else target = getNode(k,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
        if (target != null) return getEntry(target,k).value;
        return null;
    }

    private BTreeNode<K,V> getNode(K key,final ToIntBiFunction<K,K> compare){
        BTreeNode<K,V> node = this.root;
        while(node != null){
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                if (node.keys.get(i) == null) break;
                int cmp = compare.applyAsInt(key, node.keys.get(i).key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else return node;
            }
            node = node.childes.get(index);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
       if (comparator != null)
           return putUsingFunction(key, value, comparator::compare);
       return putUsingFunction(key, value, (k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    private V putUsingFunction(K key, V value, final ToIntBiFunction<K,K> compare){
        BTreeNode<K,V> node = this.root, preNode = this.root;
        int index = 0;
        int last = (degree << 1) - 1;
        while(node != null){
            index = 0;
            if (node.keys.size() == last){
                int preIndex = split(node);
                node = preNode;
                index = preIndex;
            }
            for (int i = index; i < node.keys.size(); i++) {
                if (node.keys.get(i) == null) break;
                int cmp = compare.applyAsInt(key, node.keys.get(i).key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else return node.keys.get(i).setValue(value);
            }
            preNode = node;
            node = node.childes.size() == 0 ? null : node.childes.get(index);
        }
        NodeEntry<K,V> newNode = new NodeEntry<>(key,value);
        preNode.keys.add(index,newNode);
        size++;
        return null;
    }

    final int split(BTreeNode<K,V> node){
        BTreeNode<K,V> left = new BTreeNode<>(degree,null);
        int keysHalf = node.keys.size() >> 1;
        int childesHalf = node.childes.size() >> 1;
        left.keys.addAll(node.keys.subList(0,keysHalf));
        left.childes.addAll(node.childes.subList(0,childesHalf));
        BTreeNode<K,V> right = new BTreeNode<>(degree,null);
        right.keys.addAll(node.keys.subList(keysHalf+1,node.keys.size()));
        right.childes.addAll(node.childes.subList(childesHalf,node.childes.size()));
        NodeEntry<K,V> half = node.keys.get(keysHalf);
        for (int i = 0; i < childesHalf; i++){
            if (left.childes.get(i) != null) left.childes.get(i).parent = left;
            if (right.childes.get(i) != null) right.childes.get(i).parent = right;
        }
        if (node.parent == null){
            node.clear();
            node.keys.add(half);
            node.childes.add(left);
            node.childes.add(right);
            left.parent = right.parent = node;
            return 0;
        }
        int insertIndex = keyPoint(node.parent,half.key);
        node.parent.keys.add(insertIndex,half);
        node.parent.childes.set(insertIndex,left);
        node.parent.childes.add(insertIndex+1,right);
        left.parent = right.parent = node.parent;
        return insertIndex;
    }

    final void merge(BTreeNode<K,V> node,int keyPoint){
        NodeEntry<K,V> k = node.keys.get(keyPoint);
        BTreeNode<K,V> left = node.childes.get(keyPoint);
        BTreeNode<K,V> right = node.childes.get(keyPoint+1);
        node.keys.remove(keyPoint);
        left.keys.add(k);
        left.keys.addAll(right.keys.subList(0,degree-1));
        right.childes.addAll(degree,right.childes);
        node.childes.remove(keyPoint+1);
        if (node.parent == null && node.keys.size() == 0){
            this.root = left;
            left.parent = null;
        }
    }

    @Override
    public V remove(Object key) {
        if (comparator != null)
            return remove(key,comparator::compare);
        return remove(key,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
    }

    public V remove0(Object key, final ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> target = null;
        K k = (K) key;
        V value = null;
        int keyPoint = 0;
        BTreeNode<K,V> node = this.root;
        loop:while(node != null){
            int index = 0;
            if (node != root && node.keys.get(degree-1) == null && !node.isLeaf()){
                int nodePoint = nodePoint(node);
                List<BTreeNode<K,V>> childes = node.parent.childes;
                if (nodePoint != 0 && childes.get(nodePoint-1).keys.get(degree-1) != null){
                    NodeEntry<K,V> point = node.parent.keys.get(nodePoint-1);
                    int lastKeyIndex = getLastKeyIndex(childes.get(nodePoint-1));
                    node.parent.keys.set(nodePoint-1,childes.get(nodePoint-1).keys.get(lastKeyIndex));
                    node.childes.add(0,childes.get(nodePoint-1).childes.get(lastKeyIndex+1));
                    node.keys.add(0,point);
                    childes.get(nodePoint-1).keys.set(lastKeyIndex,null);
                    childes.get(nodePoint-1).childes.set(lastKeyIndex+1,null);
                }
                else if (childes.get(nodePoint+1) != null && childes.get(nodePoint+1).keys.get(degree-1) != null){
                    NodeEntry<K,V> point = node.parent.keys.get(nodePoint);
                    int lastKeyIndex = degree - 2;
                    node.parent.keys.set(nodePoint,childes.get(nodePoint+1).keys.get(0));
                    childes.get(nodePoint+1).keys.remove(0);
                    node.keys.set(lastKeyIndex+1,point);
                    node.childes.set(lastKeyIndex+2,childes.get(nodePoint+1).childes.get(0));
                    childes.get(nodePoint+1).childes.remove(0);
                }
                else {
                    int point = nodePoint != 0 ? nodePoint-1 : nodePoint;
                    merge(node.parent,point);
                }
            }
            for (int i = index; i < node.keys.size(); i++) {
                if (node.keys.get(i) == null) break;
                int cmp = compare.applyAsInt(k, node.keys.get(i).key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else {
                    target = node;
                    value = node.keys.get(i).value;
                    keyPoint = i;
                    break loop;
                }
            }
            node = node.childes.get(index);
        }
        while (target != null){
            if (target.isLeaf()){
                target.keys.remove(keyPoint);
                size--;
                break;
            } else {
                if (target.childes.get(keyPoint).keys.get(degree-1) != null){
                    int lastKeyIndex = target.childes.get(keyPoint).keys.size()-1;
                    target.keys.set(keyPoint,target.childes.get(keyPoint).keys.get(lastKeyIndex));
                    target = target.childes.get(keyPoint);
                    keyPoint = lastKeyIndex;
                }
                else if (target.childes.get(keyPoint+1).keys.get(degree-1) != null){
                    target.keys.set(keyPoint,target.childes.get(keyPoint+1).keys.get(0));
                    target = target.childes.get(keyPoint+1);
                    keyPoint = 0;
                }
                else {
                    merge(target,keyPoint);
                    target = target.childes.get(keyPoint);
                    keyPoint = degree - 1;
                }
            }
        }
        return value;
    }

    public V remove(Object key, final ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> target = null, replaceNode = null;
        K k = (K) key;
        V value = null;
        int keyPoint = 0;
        BTreeNode<K,V> node = this.root;
        loop:while(node != null){
            int index = 0;
            if (node != root && node.keys.size() == degree-1){
                int nodePoint = nodePoint(node);
                List<BTreeNode<K,V>> childes = node.parent.childes;
                if (nodePoint != 0 && childes.get(nodePoint-1).keys.get(degree-1) != null){
                    NodeEntry<K,V> point = node.parent.keys.get(nodePoint-1);
                    int lastKeyIndex = getLastKeyIndex(childes.get(nodePoint-1));
                    node.parent.keys.set(nodePoint-1,childes.get(nodePoint-1).keys.get(lastKeyIndex));
                    node.childes.add(0,childes.get(nodePoint-1).childes.get(lastKeyIndex+1));
                    node.keys.add(0,point);
                    childes.get(nodePoint-1).keys.set(lastKeyIndex,null);
                    childes.get(nodePoint-1).childes.set(lastKeyIndex+1,null);
                }
                else if (childes.get(nodePoint+1) != null && childes.get(nodePoint+1).keys.get(degree-1) != null){
                    NodeEntry<K,V> point = node.parent.keys.get(nodePoint);
                    int lastKeyIndex = degree - 2;
                    node.parent.keys.set(nodePoint,childes.get(nodePoint+1).keys.get(0));
                    childes.get(nodePoint+1).keys.remove(0);
                    node.keys.set(lastKeyIndex+1,point);
                    node.childes.set(lastKeyIndex+2,childes.get(nodePoint+1).childes.get(0));
                    childes.get(nodePoint+1).childes.remove(0);
                }
                else {
                    int point = nodePoint != 0 ? nodePoint-1 : nodePoint;
                    merge(node.parent,point);
                }
            }
            for (int i = index; i < node.keys.size(); i++) {
                if (node.keys.get(i) == null) break;
                int cmp = compare.applyAsInt(k, node.keys.get(i).key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else {
                    target = node;
                    value = node.keys.get(i).value;
                    keyPoint = i;
                    if (target.isLeaf()) break loop;
                    if (node.childes.get(i).keys.get(degree-1) != null){
                        node = node.childes.get(i);
                    } else if (target.childes.get(i+1).keys.get(degree-1) != null){
                        node = node.childes.get(i+1);
                    } else {
                        merge(node,i);
                        node = node.childes.get(i);
                    }
                    continue loop;
                }
            }
            replaceNode = node;
            node = node.childes.size() == 0 ? null : node.childes.get(index);
        }
        if (target != null){
            if (target.isLeaf()){
                target.keys.remove(keyPoint);
                size--;
            } else {
                int replaceIndex = compare.applyAsInt(replaceNode.keys.get(0).key, target.keys.get(keyPoint).key) > 0 ?
                        0 : replaceNode.keys.size()-1;
                target.keys.set(keyPoint,replaceNode.keys.get(replaceIndex));
                replaceNode.keys.set(replaceIndex,null);
                size--;
            }
        }
        return value;
    }

    final NodeEntry<K,V> getPrecursorEntry(@NotNull BTreeNode<K,V> precursorNode){
        NodeEntry<K,V> target = null;
        while (precursorNode != null){
            for (int i = 0; i < precursorNode.keys.size(); i++) {
                if (precursorNode.keys.get(i) == null) {
                    target = precursorNode.keys.get(i-1);
                    precursorNode = precursorNode.childes.get(i);
                    break;
                }
            }
        }
        return target;
    }

    final NodeEntry<K,V> getSuccessorEntry(@NotNull BTreeNode<K,V> successorNode){
        NodeEntry<K,V> target = null;
        while (successorNode != null){
            target = successorNode.keys.get(0);
            successorNode = successorNode.childes.get(0);
        }
        return target;
    }

    /**
     * only non-root node
     * @param node
     * @return
     */
    private int getLastKeyIndex(BTreeNode<K,V> node){
        for (int i = degree-1; i < node.keys.size(); i++) {
            if (node.keys.get(i) == null)
                return i-1;
        }
        return -1;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        this.root.clear();
        this.size = 0;
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    int nodePoint(BTreeNode<K,V> node){
        List<BTreeNode<K,V>> childes = node.parent.childes;
        for (int i = 0; i < childes.size(); i++) {
            if (childes.get(i) == node) return i;
        }
        return -1;
    }

    int keyPoint(BTreeNode<K,V> node, K key){
        if (comparator != null)
            return keyPointUsingComparator(node,key);
        return keyPointComparable(node,key);
    }

    private int keyPointComparable(BTreeNode<K,V> node, K key){
        int index = 0;
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i) == null) break;
            int cmp = k.compareTo(node.keys.get(i).key);
            if (cmp < 0) break;
            else if (cmp > 0) index++;
            else break;
        }
        return index;
    }

    private int keyPointUsingComparator(BTreeNode<K,V> node, K key){
        int index = 0;
        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i) == null) break;
            int cmp = comparator.compare(key, node.keys.get(i).key);
            if (cmp < 0) break;
            else if (cmp > 0) index++;
            else break;
        }
        return index;
    }

    protected static class NodeEntry<K,V> implements Map.Entry<K,V> {

        K key;

        V value;

        NodeEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    private NodeEntry<K,V> getEntry(BTreeNode<K,V> node,K key){
        if (comparator != null) return getEntryUsingComparator(node,key);
        return getEntryComparable(node,key);
    }

    private NodeEntry<K,V> getEntryComparable(BTreeNode<K,V> node,K key){
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        for (int i = 0; i < node.keys.size(); i++) {
            if (key == node.keys.get(i).key || k.compareTo(node.keys.get(i).key) == 0)
                return node.keys.get(i);
        }
        return null;
    }

    private NodeEntry<K,V> getEntryUsingComparator(BTreeNode<K,V> node,K key){
        for (int i = 0; i < node.keys.size(); i++) {
            if (key == node.keys.get(i).key || comparator.compare(node.keys.get(i).key,key) == 0)
                return node.keys.get(i);
        }
        return null;
    }

    protected static class BTreeNode<K,V> {

        List<NodeEntry<K,V>> keys;

        List<BTreeNode<K,V>> childes;

        BTreeNode<K,V> parent;

        @SuppressWarnings("unchecked")
        BTreeNode(int degree, BTreeNode<K,V> parent) {
            this.keys = new ArrayList<>((degree<<1) - 1);
            this.childes = new ArrayList<>(degree<<1);
            this.parent = parent;
        }

        void clear(){
            keys.clear();
            childes.clear();
        }

        final boolean isLeaf(){
            return childes.size() == 0;
        }
    }
}
