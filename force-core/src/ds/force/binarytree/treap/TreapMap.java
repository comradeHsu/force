package ds.force.binarytree.treap;

import ds.force.binarytree.SplayTreeMap;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

public class TreapMap<K,V> extends AbstractTreapMap<K,V> {

    transient Random random;

    transient Entry<K,V> root;

    public TreapMap(){
        super();
        this.random = new Random();
    }

    public TreapMap(Comparator<? super K> comparator) {
        super(comparator);
        this.random = new Random();
    }

    @Override
    protected NavigableEntry<K, V> getCeilingEntry(K key) {
        AbstractEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
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
    protected NavigableEntry<K, V> getFloorEntry(K key) {
        AbstractEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
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
    protected NavigableEntry<K, V> getHigherEntry(K key) {
        AbstractEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
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
    protected NavigableEntry<K, V> getLowerEntry(K key) {
        AbstractEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
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
        return root == null ? 0 : root.size;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public boolean containsKey(Object key) {
        Entry<K,V> entry = getEntry(key);
        return entry != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Map.Entry<K,V> e : entrySet())
            if (valEquals(value, e.getValue()))
                return true;
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
        if (key == null)
            throw new NullPointerException();
        Entry<K,V> node = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        @SuppressWarnings("unchecked")
        K k = (K) key;
        int cmp;
        do {
            cmp = compare.applyAsInt(k,node.key);
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
        updateSizeForTree(parent);
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
            Entry<K,V> parent = node.parent;
            if (node == root){
                root = null;
            } else if (node == node.parent.left){
                node.parent.left = node.parent =null;
            } else {
                node.parent.right = node.parent =null;
            }
            updateSizeForTree(parent);
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
        updateSizeForTree(swap.parent);
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
            updateSize(p);
            updateSize(r);
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
            updateSize(p);
            updateSize(l);
        }
    }

    public TreapMap<K,V> split(K key){
        Entry<K,V> newNode = null, splitPoint = null;
        Entry<K,V> node = this.root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        Deque<Entry<K,V>> stack = new ArrayDeque<>();
        while (node != null){
            stack.push(node);
            if (compare.applyAsInt(node.key,key) <= 0){
                node = (Entry<K, V>) node.right;
            } else {
                node = (Entry<K, V>) node.left;
            }
        }
        while (!stack.isEmpty()){
            Entry<K,V> current = stack.pop();
            if (compare.applyAsInt(current.key,key) <= 0){
                current.right = splitPoint;
                splitPoint = current;
            } else {
                current.left = newNode;
                newNode = current;
            }
            updateSize(current);
        }
        TreapMap<K,V> result = new TreapMap<>();
        if (newNode == root) {
            result.root = splitPoint;
        } else {
            result.root = newNode;
        }
        return result;
    }

    public int getSequence(K key){
        int sequence = 0;
        AbstractEntry<K,V> entry = this.root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        while (entry != null){
            int cmp = compare.applyAsInt(key,entry.key);
            if (cmp < 0){
                entry = entry.left;
            }
            else if (cmp > 0){
                entry = entry.right;
                sequence = sequence + sizeOf(entry.left)+1;
            } else {
                break;
            }
        }
        return sequence;
    }

    public V get(int ranking){
        AbstractEntry<K,V> node = this.root;
        while (node != null){
            if (ranking == sizeOf(node.left) + 1) {
                return node.value;
            } else if (ranking <= sizeOf(node.left)) {
                node = node.left;
            } else {
                ranking -= sizeOf(node.left) + 1;
                node = node.right;
            }
        }
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

    @Override
    public Set<K> keySet() {
        return navigableKeySet();
    }

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
    @SuppressWarnings("unchecked")
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
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

        int size;

        Entry<K,V> parent;

        Entry(K key, V value, int priority){
            super(key, value, priority);
            this.size = 1;
        }

        Entry(K key, V value, int priority,Entry<K,V> parent){
            super(key, value, priority);
            this.parent = parent;
            this.size = 1;
        }
    }

    private void updateSize(Entry<K,V> entry){
        entry.size = sizeOf(entry.left)+sizeOf(entry.right)+1;
    }

    private void updateSizeForTree(Entry<K,V> entry){
        while (entry != null) {
            entry.size = sizeOf(entry.left) + sizeOf(entry.right) + 1;
            entry = entry.parent;
        }
    }

    static final <K,V> int sizeOf(AbstractEntry<K,V> entry){
        return entry == null ? 0 : ((Entry<K,V>)entry).size;
    }
}
