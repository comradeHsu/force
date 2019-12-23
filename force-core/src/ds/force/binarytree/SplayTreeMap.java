package ds.force.binarytree;

import ds.force.AbstractNavigableMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ToIntBiFunction;

public class SplayTreeMap<K,V> extends AbstractNavigableMap<K,V> {

    transient SplayEntry<K,V> root;

    @Override
    public int size() {
        return root == null ? 0 : root.size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean containsKey(Object key) {
        SplayEntry<K,V> entry = getEntry((K)key);
        return entry != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        SplayEntry<K,V> entry = getEntry((K)key);
        V value = null;
        if (entry != null){
            splay(entry);
            value = entry.value;
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
//        if (key == null)
//            throw new NullPointerException();
        SplayEntry<K,V> t = root;
        if (t == null){
            root = new SplayEntry<>(key, value);
            return null;
        }
        SplayEntry<K,V> parent;
        int cmp;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        do {
            parent = t;
            cmp = compare.applyAsInt(key,t.key);
            if (cmp < 0)
                t =  t.left;
            else if (cmp > 0)
                t =  t.right;
            else {
                splay(t);
                return t.setValue(value);
            }
        } while (t != null);
        SplayEntry<K,V> e = new SplayEntry<>(key,value,parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        splay(e);
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    protected SplayEntry<K, V> getEntry(K key) {
        if (key == null)
            throw new NullPointerException();
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        SplayEntry<K,V> node = root;
        int cmp;
        do {
            cmp = compare.applyAsInt(key,node.key);
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

    @Override
    protected NavigableEntry<K, V> successor(NavigableEntry<K, V> entry) {
        return null;
    }

    @Override
    protected NavigableEntry<K, V> predecessor(NavigableEntry<K, V> entry) {
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getCeilingEntry(K key) {
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getFloorEntry(K key) {
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getHigherEntry(K key) {
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getLowerEntry(K key) {
        return null;
    }

    @Override
    public NavigableEntry<K, V> firstEntry() {
        return null;
    }

    @Override
    public NavigableEntry<K, V> lastEntry() {
        return null;
    }

    @Override
    protected Iterator<K> keyIterator() {
        return null;
    }

    @Override
    protected Iterator<K> descendingKeyIterator() {
        return null;
    }

    @Override
    public void clear() {
        this.root = null;
    }

    /** From CLR */
    private void rotateLeft(SplayEntry<K,V> p) {
        if (p != null) {
            SplayEntry<K,V> parent =  p.parent;
            parent.right = p.left;
            if (p.left != null)
                p.left.parent = parent;
            p.parent = parent.parent;
            if (parent.parent == null)
                root = p;
            else if (parent.parent.left == parent)
                parent.parent.left = p;
            else
                parent.parent.right = p;
            p.left = parent;
            parent.parent = p;
        }
    }

    /** From CLR */
    private void rotateRight(SplayEntry<K,V> p) {
        if (p != null) {
            SplayEntry<K,V> parent =  p.parent;
            parent.left = p.right;
            if (p.right != null) p.right.parent = parent;
            p.parent = parent.parent;
            if (p.parent == null)
                root = p;
            else if (parent.parent.right == parent)
                parent.parent.right = p;
            else parent.parent.left = p;
            p.right = parent;
            parent.parent = p;
        }
    }

    final void splay(SplayEntry<K,V> entry){
        SplayEntry<K,V> parent,grandfather;
        while (entry.parent != root && entry != root){
            parent = entry.parent;
            grandfather = parent.parent;
            if (parent == leftOf(grandfather) && entry == leftOf(parent)){
                rotateRight(parent);
                rotateRight(entry);
            }
            else if (parent == rightOf(grandfather) && entry == rightOf(parent)){
                rotateLeft(parent);
                rotateLeft(entry);
            }
            else if (parent == rightOf(grandfather) && entry == leftOf(parent)){
                rotateRight(entry);
                rotateLeft(entry);
            }
            else {
                rotateLeft(entry);
                rotateRight(entry);
            }
        }
        if (entry == root) return;
        if (entry == leftOf(entry.parent))
            rotateRight(entry);
        else if (entry == rightOf(entry.parent))
            rotateLeft(entry);
    }

    private static class SplayEntry<K,V> extends AbstractNavigableMap.NavigableEntry<K,V> {

        int size;

        SplayEntry<K,V> left, right, parent;

        SplayEntry(K key, V value){
            this.key = key;
            this.value = value;
            this.size = 1;
        }

        SplayEntry(K key, V value, SplayEntry<K,V> parent){
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.size = 1;
        }

    }

    @Override
    public K firstKey() {
        return null;
    }

    @Override
    public K lastKey() {
        return null;
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

    static final <K,V> SplayEntry<K, V> leftOf(SplayEntry<K,V> entry){
        return entry.left;
    }

    static final <K,V> SplayEntry<K, V> rightOf(SplayEntry<K,V> entry){
        return entry.right;
    }

}
