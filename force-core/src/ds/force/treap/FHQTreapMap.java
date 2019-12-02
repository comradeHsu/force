package ds.force.treap;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.TreeMap;

public class FHQTreapMap<K,V> implements NavigableMap<K,V> {

    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super K> comparator;

    transient Random random = new Random();

    transient Entry<K,V> root;

    public FHQTreapMap() {
        this.comparator = null;
    }

    public FHQTreapMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int size() {
        return sizeOf(this.root);
    }

    private static <K,V> int sizeOf(Entry<K,V> node){
        return node == null ? 0 : node.size;
    }

    @Override
    public boolean isEmpty() {
        return this.root == null;
    }

    @Override
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e))
            if (valEquals(value, e.value))
                return true;
        return false;
    }

    public Entry<K,V> splitToLeft(K key){
        return comparator == null ? splitToLeftComparable(key) : splitToLeftUsingComparator(key);
    }

    @SuppressWarnings("unchecked")
    private Entry<K,V> splitToLeftComparable(K key){
        Entry<K,V> node = this.root;
        Entry<K,V> newNode = null, splitPoint = null;
        Deque<Entry<K,V>> stack = new ArrayDeque<>();
        while (node != null){
            stack.push(node);
            if (((Comparable<? super K>)node.key).compareTo(key) <= 0){
                node = node.right;
            } else {
                node = node.left;
            }
        }
        while (!stack.isEmpty()){
            Entry<K,V> current = stack.pop();
            if (((Comparable<? super K>)current.key).compareTo(key) <= 0){
                current.right = splitPoint;
                splitPoint = current;
            } else {
                current.left = newNode;
                newNode = current;
            }
            update(current);
        }
        return newNode == root ? splitPoint : newNode;
    }

    private Entry<K,V> splitToLeftUsingComparator(K key){
        Entry<K,V> node = this.root;
        Entry<K,V> newNode = null, splitPoint = null;
        Deque<Entry<K,V>> stack = new ArrayDeque<>();
        while (node != null){
            stack.push(node);
            if (comparator.compare(node.key,key) < 0){
                node = node.right;
            } else {
                node = node.left;
            }
        }
        while (!stack.isEmpty()){
            Entry<K,V> current = stack.pop();
            if (comparator.compare(current.key,key) <= 0){
                current.right = splitPoint;
                splitPoint = current;
            } else {
                current.left = newNode;
                newNode = current;
            }
            update(current);
        }
        return newNode == root ? splitPoint : newNode;
    }

    /**
     *
     * @param key
     * @return
     */
    public Entry<K,V> splitToRight(K key){
        return comparator == null ? splitToRightComparable(key) : splitToRightUsingComparator(key);
    }

    @SuppressWarnings("unchecked")
    private Entry<K,V> splitToRightComparable(K key){
        Entry<K,V> node = this.root;
        Entry<K,V> newNode = null, splitPoint = null;
        Deque<Entry<K,V>> stack = new ArrayDeque<>();
        while (node != null){
            stack.push(node);
            if (((Comparable<? super K>)node.key).compareTo(key) < 0){
                node = node.right;
            } else {
                node = node.left;
            }
        }
        while (!stack.isEmpty()){
            Entry<K,V> current = stack.pop();
            if (((Comparable<? super K>)current.key).compareTo(key) < 0){
                current.right = splitPoint;
                splitPoint = current;
            } else {
                current.left = newNode;
                newNode = current;
            }
            update(current);
        }
        return newNode == root ? splitPoint : newNode;
    }

    private Entry<K,V> splitToRightUsingComparator(K key){
        Entry<K,V> node = this.root;
        Entry<K,V> newNode = null, splitPoint = null;
        Deque<Entry<K,V>> stack = new ArrayDeque<>();
        while (node != null){
            stack.push(node);
            if (comparator.compare(node.key,key) < 0){
                node = node.right;
            } else {
                node = node.left;
            }
        }
        while (!stack.isEmpty()){
            Entry<K,V> current = stack.pop();
            if (comparator.compare(current.key,key) < 0){
                current.right = splitPoint;
                splitPoint = current;
            } else {
                current.left = newNode;
                newNode = current;
            }
            update(current);
        }
        return newNode == root ? splitPoint : newNode;
    }

    private void update(Entry<K,V> entry){
        entry.size = sizeOf(entry.left) + sizeOf(entry.right) + 1;
    }

    private void merge(Entry<K,V> a, Entry<K,V> b){
        if (a == null || b == null) return;
        Entry<K,V> small, big;
        if (compareTo(a.key,b.key) <= 0){
            small = a;big = b;
        } else{
            small = b;big = a;
        }
        Deque<Entry<K,V>> stack = new ArrayDeque<>();
        while(small != null && big != null){
            stack.push(small);
            stack.push(big);
            if (small.priority < big.priority){
                small = small.right;
            } else {
                big = big.left;
            }
        }
        Entry<K,V> child = small == null ? big : small;
        while (!stack.isEmpty()){
            Entry<K,V> bigNode = stack.pop();
            Entry<K,V> smallNode = stack.pop();
            if (smallNode.priority < bigNode.priority){
                smallNode.right = child;
                update(smallNode);
                child = smallNode;
            } else {
                bigNode.left = child;
                update(bigNode);
                child = bigNode;
            }
        }
        this.root = child;
    }

    @SuppressWarnings("unchecked")
    private int compareTo(K a, K b){
        if (comparator == null){
            return ((Comparable<? super K>)a).compareTo(b);
        } else {
            return comparator.compare(a,b);
        }
    }

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    @Override
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }

    @Override
    public V put(K key, V value) {
        Entry<K,V> t = root;
        if (t == null){
            compareTo(key, key); // type (and possibly null) check
            root = new Entry<>(key, value, random.nextInt());
            return null;
        }
        V oldValue = null;
        Entry<K,V> splittingResult = splitToLeft(key);
        Entry<K,V> itemNode = splitToRight(key);
        if (itemNode == null)
            itemNode = new Entry<>(key, value, random.nextInt());
        else
            oldValue = itemNode.setValue(value);
        merge(this.root,itemNode);
        merge(this.root,splittingResult);
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Entry<K,V> splittingNode = splitToLeft(k);
        Entry<K,V> itemNode = splitToRight(k);
        merge(this.root,splittingNode);
        return itemNode == null ? null : itemNode.value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    /**
     * Returns this map's entry for the given key, or {@code null} if the map
     * does not contain an entry for the key.
     *
     * @return this map's entry for the given key, or {@code null} if the map
     *         does not contain an entry for the key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            int cmp = k.compareTo(p.key);
            if (cmp < 0)
                p = p.left;
            else if (cmp > 0)
                p = p.right;
            else
                return p;
        }
        return null;
    }

    /**
     * Version of getEntry using comparator. Split off from getEntry
     * for performance. (This is not worth doing for most methods,
     * that are less dependent on comparator performance, but is
     * worthwhile here.)
     */
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }

    @Override
    public void clear() {
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

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    private static class Entry<K,V> implements Map.Entry<K,V>{

        K key;

        V value;

        int priority;

        Entry<K,V> left,right;

        int size;

        Entry(K key, V value, int priority) {
            this.key = key;
            this.value = value;
            this.priority = priority;
            this.size = 1;
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

    private void deleteEntry(Entry<K,V> entry){
        remove(entry.getKey());
    }

    class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator(getFirstEntry());
        }

        public int size() {
            return FHQTreapMap.this.size();
        }

        public boolean contains(Object o) {
            return FHQTreapMap.this.containsValue(o);
        }

        public boolean remove(Object o) {
            for (Entry<K,V> e = getFirstEntry(); e != null; e = successor(e)) {
                if (valEquals(e.getValue(), o)) {
                    deleteEntry(e);
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            FHQTreapMap.this.clear();
        }
    }

    class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator(getFirstEntry());
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            Entry<K,V> p = getEntry(entry.getKey());
            return p != null && valEquals(p.getValue(), value);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            Entry<K,V> p = getEntry(entry.getKey());
            if (p != null && valEquals(p.getValue(), value)) {
                deleteEntry(p);
                return true;
            }
            return false;
        }

        public int size() {
            return FHQTreapMap.this.size();
        }

        public void clear() {
            FHQTreapMap.this.clear();
        }

    }

    Iterator<K> keyIterator() {
        return new KeyIterator(getFirstEntry());
    }

    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(getLastEntry());
    }

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> m;
        KeySet(NavigableMap<E,?> map) { m = map; }

        public Iterator<E> iterator() {
            return ((FHQTreapMap<E,?>)m).keyIterator();
        }

        public Iterator<E> descendingIterator() {
            return ((FHQTreapMap<E,?>)m).descendingKeyIterator();
        }

        public int size() { return m.size(); }
        public boolean isEmpty() { return m.isEmpty(); }
        public boolean contains(Object o) { return m.containsKey(o); }
        public void clear() { m.clear(); }
        public E lower(E e) { return m.lowerKey(e); }
        public E floor(E e) { return m.floorKey(e); }
        public E ceiling(E e) { return m.ceilingKey(e); }
        public E higher(E e) { return m.higherKey(e); }
        public E first() { return m.firstKey(); }
        public E last() { return m.lastKey(); }
        public Comparator<? super E> comparator() { return m.comparator(); }
        public E pollFirst() {
            Map.Entry<E,?> e = m.pollFirstEntry();
            return (e == null) ? null : e.getKey();
        }
        public E pollLast() {
            Map.Entry<E,?> e = m.pollLastEntry();
            return (e == null) ? null : e.getKey();
        }
        public boolean remove(Object o) {
            int oldSize = size();
            m.remove(o);
            return size() != oldSize;
        }
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive,
                                      E toElement,   boolean toInclusive) {
            return new KeySet<>(m.subMap(fromElement, fromInclusive,
                    toElement,   toInclusive));
        }
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new KeySet<>(m.headMap(toElement, inclusive));
        }
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new KeySet<>(m.tailMap(fromElement, inclusive));
        }
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }
        public NavigableSet<E> descendingSet() {
            return new KeySet<>(m.descendingMap());
        }
    }

    /**
     * Base class for TreeMap Iterators
     */
    abstract class TreapEntryIterator<T> implements Iterator<T> {
        Entry<K,V> next;
        Entry<K,V> lastReturned;

        TreapEntryIterator(Entry<K,V> first) {
            lastReturned = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Entry<K,V> nextEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = successor(e);
            lastReturned = e;
            return e;
        }

        final Entry<K,V> prevEntry() {
            Entry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = predecessor(e);
            lastReturned = e;
            return e;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            // deleted entries are replaced by their successors
            if (lastReturned.left != null && lastReturned.right != null)
                next = lastReturned;
            deleteEntry(lastReturned);
            lastReturned = null;
        }
    }

    final class EntryIterator extends TreapEntryIterator<Map.Entry<K,V>> {
        EntryIterator(Entry<K,V> first) {
            super(first);
        }
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    final class ValueIterator extends TreapEntryIterator<V> {
        ValueIterator(Entry<K,V> first) {
            super(first);
        }
        public V next() {
            return nextEntry().value;
        }
    }

    final class KeyIterator extends TreapEntryIterator<K> {
        KeyIterator(Entry<K,V> first) {
            super(first);
        }
        public K next() {
            return nextEntry().key;
        }
    }

    final class DescendingKeyIterator extends TreapEntryIterator<K> {
        DescendingKeyIterator(Entry<K,V> first) {
            super(first);
        }
        public K next() {
            return prevEntry().key;
        }
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            deleteEntry(lastReturned);
            lastReturned = null;
        }
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
        KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
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

    /**
     * Returns the first Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getFirstEntry() {
        return getFirstEntry(root);
    }

    final Entry<K,V> getFirstEntry(Entry<K,V> entry) {
        Entry<K,V> p = entry;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    /**
     * Returns the last Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    final Entry<K,V> getLastEntry() {
        Entry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     */
    final Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
        Entry<K,V> splitting = splitToLeft(t.key);
        Entry<K,V> target = getFirstEntry(splitting);
        merge(this.root,splitting);
        return target;
    }

    /**
     * Returns the predecessor of the specified Entry, or null if no such.
     */
    final Entry<K,V> predecessor(Entry<K,V> t) {
        if (t == null)
            return null;
        Entry<K,V> splitting = splitToRight(t.key);
        Entry<K,V> target = getLastEntry();
        merge(this.root,splitting);
        return target;
    }

}
