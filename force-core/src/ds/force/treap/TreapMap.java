package ds.force.treap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

public class TreapMap<K,V> extends AbstractTreapMap<K,V> implements NavigableMap<K,V> {

    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super K> comparator;

    transient Random random;

    transient Entry<K,V> root;

    public TreapMap(){
        this.comparator = null;
        this.random = new Random();
    }

    public TreapMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.random = new Random();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public V get(Object key){
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    @Override
    protected AbstractEntry<K, V> getFirstEntry() {
        return null;
    }

    @Override
    protected AbstractEntry<K, V> getLastEntry() {
        return null;
    }

    @Override
    protected AbstractEntry<K, V> successor(AbstractEntry<K, V> entry) {
        return null;
    }

    @Override
    protected AbstractEntry<K, V> predecessor(AbstractEntry<K, V> entry) {
        return null;
    }

    /**
     * 搜索节点
     * @param key
     * @return
     */
    @Override
    protected Entry<K,V> getEntry(Object key){
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        Entry<K,V> node = root;
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        int cmp;
        do {
            cmp = k.compareTo(node.key);
            if (cmp > 0){
                node = (Entry<K, V>) node.right;
            } else if (cmp < 0){
                node = (Entry<K, V>) node.left;
            } else {
                return node;
            }
        } while (node != null);
        return null;
    }

    private Entry<K,V> getEntryUsingComparator(Object key){
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = (Entry<K, V>) p.left;
                else if (cmp > 0)
                    p = (Entry<K, V>) p.right;
                else
                    return p;
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        Entry<K,V> t = root;
        if (t == null){
            root = new Entry<>(key, value, random.nextInt());
            return null;
        }
        Entry<K,V> parent;
        int cmp;
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0)
                    t = (Entry<K, V>) t.left;
                else if (cmp > 0)
                    t = (Entry<K, V>) t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = (Entry<K, V>) t.left;
                else if (cmp > 0)
                    t = (Entry<K, V>) t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        Entry<K,V> e = new Entry<>(key,value,random.nextInt(),parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        shiftUp(e);
        return null;
    }

    @Override
    public V remove(Object key) {
        Entry<K,V> node = getEntry(key);
        if (node == null){
            return null;
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
            return node.getValue();
        }
        Entry<K,V> swap = (Entry<K, V>) (node.left == null ? node.right : node.left);
        swap.parent = node.parent;
        if (node == root){
            root = swap;
        }else if (node == node.parent.left){
            node.parent.left = swap;
        } else {
            node.parent.right = swap;
        }
        node.left = node.right = node.parent = null;
        return node.getValue();
    }

    private void shiftUp(Entry<K,V> node) {
        while(node != root){
            Entry<K,V> parent = node.parent;
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
    private void rotateLeft(Entry<K,V> p) {
        if (p != null) {
            Entry<K,V> r = (Entry<K, V>) p.right;
            p.right = r.left;
            if (r.left != null)
                ((Entry<K, V>)r.left).parent = p;
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
    private void rotateRight(Entry<K,V> p) {
        if (p != null) {
            Entry<K,V> l = (Entry<K, V>) p.left;
            p.left = l.right;
            if (l.right != null) ((Entry<K, V>)l.right).parent = p;
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

    @Override
    public void clear(){
        this.root = null;
    }

    /**
     * Fields initialized to contain an instance of the entry set view
     * the first time this view is requested.  Views are stateless, so
     * there's no reason to create more than one.
     */
    private transient EntrySet entrySet;
    private transient KeySet<K> navigableKeySet;
    private transient NavigableMap<K,V> descendingMap;

    @Override
    public Set<K> keySet() {
        return navigableKeySet();
    }

    transient Collection<V> values;

    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    @Override
    public Entry<K, V> lowerEntry(K key) {
        return null;
    }

    @Override
    public K lowerKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> floorEntry(K key) {
        return null;
    }

    @Override
    public K floorKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> ceilingEntry(K key) {
        return null;
    }

    @Override
    public K ceilingKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> higherEntry(K key) {
        return null;
    }

    @Override
    public K higherKey(K key) {
        return null;
    }

    @Override
    public Entry<K, V> firstEntry() {
        return null;
    }

    @Override
    public Entry<K, V> lastEntry() {
        return null;
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        return null;
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        return null;
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return null;
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return null;
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return null;
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return null;
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return null;
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return null;
    }

    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return null;
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return null;
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return null;
    }

    @Override
    public K firstKey() {
        return null;
    }

    @Override
    public K lastKey() {
        return null;
    }

    private static class Entry<K,V> extends AbstractTreapMap.AbstractEntry<K,V> {

        Entry<K,V> parent;

        Entry(K key, V value, int priority){
            super(key, value, priority);
        }

        Entry(K key, V value, int priority,Entry<K,V> parent){
            super(key, value, priority);
            this.parent = parent;
        }
    }
}
