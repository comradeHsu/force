package ds.force.binarytree.treap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

public class TreapMap<K,V> extends AbstractTreapMap<K,V> {

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
    protected NavigableEntry<K, V> getCeilingEntry(K key, ToIntBiFunction<K, K> compare) {
        AbstractEntry<K,V> p = root;
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else if (cmp > 0) {
                if (p.right != null) {
                    p = p.right;
                } else {
                    Entry<K,V> parent = ((Entry<K,V>)p).parent;
                    AbstractEntry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;
        }
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getFloorEntry(K key, ToIntBiFunction<K, K> compare) {
        AbstractEntry<K,V> p = root;
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else if (cmp < 0) {
                if (p.left != null) {
                    p = p.left;
                } else {
                    Entry<K,V> parent = ((Entry<K,V>)p).parent;
                    AbstractEntry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;

        }
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getHigherEntry(K key, ToIntBiFunction<K, K> compare) {
        AbstractEntry<K,V> p = root;
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else {
                if (p.right != null) {
                    p = p.right;
                } else {
                    Entry<K,V> parent = ((Entry<K,V>)p).parent;
                    AbstractEntry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getLowerEntry(K key, ToIntBiFunction<K, K> compare) {
        AbstractEntry<K,V> p = root;
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else {
                if (p.left != null) {
                    p = p.left;
                } else {
                    Entry<K,V> parent = ((Entry<K,V>)p).parent;
                    AbstractEntry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
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
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key){
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    @Override
    protected AbstractEntry<K, V> getRoot() {
        return this.root;
    }

    @Override
    protected AbstractEntry<K, V> successor(NavigableEntry<K, V> entry) {
        return successor(entry.key,compare -> compare <= 0);
    }

    @Override
    protected AbstractEntry<K, V> predecessor(NavigableEntry<K, V> entry) {
       return predecessor(entry.key,compare -> compare >= 0);
    }

    @SuppressWarnings("unchecked")
    final AbstractEntry<K,V> successor(K key,IntPredicate predicate){
        if (comparator != null) return successor(key, predicate,comparator::compare);
        return successor(key,predicate,(k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    @SuppressWarnings("unchecked")
    private AbstractEntry<K,V> successor(K key, IntPredicate predicate,ToIntBiFunction<K,K> compare){
        AbstractEntry<K,V> entry = this.root;
        AbstractEntry<K,V> target = null;
        while (entry != null){
            if (predicate.test(compare.applyAsInt(key,entry.key))){
                entry = entry.left;
            } else {
                if (target == null ||
                        compare.applyAsInt(target.key,entry.key) > 0) target = entry;
                entry = entry.right;
            }
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    final AbstractEntry<K,V> predecessor(K key,IntPredicate predicate){
        if (comparator != null) return predecessorUsingComparator(key, predicate);
        return predecessorComparable(key,predicate);
    }

    @SuppressWarnings("unchecked")
    private AbstractEntry<K,V> predecessorComparable(K key, IntPredicate predicate){
        AbstractEntry<K,V> entry = this.root;
        Comparable<? super K> k = (Comparable<? super K>) key;
        AbstractEntry<K,V> target = null;
        while (entry != null){
            if (predicate.test(k.compareTo(entry.key))){
                entry = entry.right;
            } else {
                if (target == null ||
                        ((Comparable<? super K>)target.key).compareTo(entry.key) < 0) target = entry;
                entry = entry.left;
            }
        }
        return target;
    }

    @SuppressWarnings("unchecked")
    private AbstractEntry<K,V> predecessorUsingComparator(K key,IntPredicate predicate){
        AbstractEntry<K,V> entry = this.root;
        AbstractEntry<K,V> target = null;
        while (entry != null){
            if (predicate.test(comparator.compare(key, entry.key))){
                entry = entry.right;
            } else {
                if (target == null ||
                        ((Comparable<? super K>)target.key).compareTo(entry.key) < 0) target = entry;
                entry = entry.left;
            }
        }
        return target;
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

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

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

    public TreapMap<K,V> split(K key){
        return null;
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
    public AbstractEntry<K, V> lowerEntry(K key) {
        return predecessor(key,compare -> compare >= 0);
    }

    @Override
    public K lowerKey(K key) {
        AbstractEntry<K,V> entry = lowerEntry(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K, V> floorEntry(K key) {
        return predecessor(key,compare -> compare > 0);
    }

    @Override
    public K floorKey(K key) {
        AbstractEntry<K,V> entry = floorEntry(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K, V> ceilingEntry(K key) {
        return successor(key,compare -> compare < 0);
    }

    @Override
    public K ceilingKey(K key) {
        AbstractEntry<K,V> entry = ceilingEntry(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K, V> firstEntry() {
        return getFirstEntry();
    }

    @Override
    public AbstractEntry<K, V> lastEntry() {
        return getLastEntry();
    }

    @Override
    public K firstKey() {
        AbstractEntry<K,V> entry = getFirstEntry();
        return entry == null ? null : entry.key;
    }

    @Override
    public K lastKey() {
        AbstractEntry<K,V> entry = getLastEntry();
        return entry == null ? null : entry.key;
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
