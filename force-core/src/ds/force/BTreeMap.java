package ds.force;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

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
        this.root = new BTreeNode<>(degree);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        BTreeNode node = this.root;
        while(node != null){
            int i = 0;
            in:for (; i < node.keys.length; i++){
                if (node.keys[i] == null) break in;
//                if (key < node.keys[i].key) break in;
            }
            node = node.childes[i];
        }
        return null;
    }

    private V putUsingComparator(K key, V value){
        BTreeNode<K,V> node = this.root;
        while(node != null){
            int index = 0;
            for (int i = 0; i < node.keys.length; i++) {
                if (node.keys[i] == null) break;
                int cmp = comparator.compare(key, node.keys[i].key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
            }
            node = node.childes[index];
        }
        return null;
    }

    final void split(BTreeNode<K,V> node){
        BTreeNode<K,V> left = new BTreeNode<>(degree);
        int keysHalf = node.keys.length >> 1;
        int childesHalf = node.childes.length >> 1;
        System.arraycopy(node.keys, 0, left.keys, 0,
               keysHalf);
        System.arraycopy(node.childes, 0, left.childes, 0,
                childesHalf);
        BTreeNode<K,V> right = new BTreeNode<>(degree);
        System.arraycopy(node.keys, keysHalf+1, right.keys, 0,
                keysHalf);
        System.arraycopy(node.childes, keysHalf+1, right.childes, 0,
                childesHalf);
        NodeEntry<K,V> half = node.keys[keysHalf];
        if (node.parent == null){
            node.clear();
            node.keys[0] = half;
            node.childes[0] = left;
            node.childes[1] = right;
            return;
        }

    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

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

    int keyPoint(BTreeNode<K,V> node, K key){
        int index = 0;
        for (int i = 0; i < node.keys.length; i++) {
            if (node.keys[i] == null) break;
            int cmp = comparator.compare(key, node.keys[i].key);
            if (cmp < 0) break;
            else if (cmp > 0) index++;
        }
        return 0;
    }

    private int keyPointComparable(){
        return 0;
    }

    private int keyPointUsingComparator(){
        return 0;
    }

    private static class NodeEntry<K,V> implements Map.Entry<K,V> {

        K key;

        V value;

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

    private static class BTreeNode<K,V> {

        NodeEntry<K,V>[] keys;

        BTreeNode<K,V>[] childes;

        BTreeNode<K,V> parent;

        int keySize;

        BTreeNode(int degree) {
            this.keys = new NodeEntry[(degree<<1) - 1];
            this.childes = new BTreeNode[degree<<1];
        }

        void clear(){
            for (int i = 0; i < keys.length; i++) {
                keys[i] = null;
                childes[i] = null;
            }
            childes[keys.length] = null;
        }

    }
}
