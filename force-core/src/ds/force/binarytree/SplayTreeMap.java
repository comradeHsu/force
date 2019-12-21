package ds.force.binarytree;

import ds.force.AbstractNavigableMap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SplayTreeMap<K,V> extends AbstractNavigableMap<K,V> {

    transient SplayEntry<K,V> root;

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
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getEntry(K key) {
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

    }

    /** From CLR */
    private void rotateLeft(SplayEntry<K,V> p) {
        if (p != null) {
            SplayEntry<K,V> r =  p.right;
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
    private void rotateRight(SplayEntry<K,V> p) {
        if (p != null) {
            SplayEntry<K,V> l = p.left;
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

    final void splay(SplayEntry<K,V> entry){
        SplayEntry<K,V> parent,grandfather;
        while (entry.parent != root){
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
        if (entry == leftOf(entry.parent))
            rotateRight(entry);
        else rotateLeft(entry);
    }

    private static class SplayEntry<K,V> extends AbstractNavigableMap.NavigableEntry<K,V> {

        SplayEntry<K,V> left, right, parent;

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
