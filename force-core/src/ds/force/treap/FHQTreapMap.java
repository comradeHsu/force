package ds.force.treap;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

public class FHQTreapMap<K,V> extends AbstractTreapMap<K,V> implements NavigableMap<K,V> {

    /**
     * The comparator used to maintain order in this tree map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super K> comparator;

    transient Random random;

    transient Entry<K,V> root;

    public FHQTreapMap() {
        this.comparator = null;
        this.random = new Random();
    }

    public FHQTreapMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
        this.random = new Random();
    }

    @Override
    public int size() {
        return sizeOf(this.root);
    }

    private static <K,V> int sizeOf(AbstractEntry<K,V> node){
        return node == null ? 0 : ((Entry<K,V>)node).size;
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
        for (AbstractEntry<K,V> e = getFirstEntry(); e != null; e = successor(e))
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
                node = (Entry<K, V>) node.right;
            } else {
                node = (Entry<K, V>) node.left;
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
                node = (Entry<K, V>) node.right;
            } else {
                node = (Entry<K, V>) node.left;
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
                node = (Entry<K, V>) node.right;
            } else {
                node = (Entry<K, V>) node.left;
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
                node = (Entry<K, V>) node.right;
            } else {
                node = (Entry<K, V>) node.left;
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
        if (a == null || b == null){
            this.root = a == null ? b : a;
            return;
        }
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
                small = (Entry<K, V>) small.right;
            } else {
                big = (Entry<K, V>) big.left;
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

    @Override
    public V get(Object key) {
        AbstractEntry<K,V> p = getEntry(key);
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
    public void putAll(Map<? extends K, ? extends V> map) {
        super.putAll(map);
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

    public int getSequence(K key){
        Entry<K,V> splittingNode = splitToLeft(key);
        int sequence = sizeOf(this.root);
        merge(this.root,splittingNode);
        return sequence;
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
    protected final AbstractEntry<K, V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        AbstractEntry<K,V> p = root;
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
    protected final AbstractEntry<K, V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            AbstractEntry<K,V> p = root;
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

    private static class Entry<K,V> extends AbstractEntry<K,V>{

        int size;

        Entry(K key, V value, int priority) {
            super(key, value, priority);
            this.size = 1;
        }
    }

    private void deleteEntry(Entry<K,V> entry){
        remove(entry.getKey());
    }

    @Override
    public AbstractEntry<K,V> lowerEntry(K key) {
        return predecessor(key);
    }

    @Override
    public K lowerKey(K key) {
        AbstractEntry<K,V> entry = predecessor(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K,V> floorEntry(K key) {
        if (key == null)
            return null;
        Entry<K,V> splitting = splitToLeft(key);
        AbstractEntry<K,V> target = getLastEntry();
        merge(this.root,splitting);
        return target;
    }

    @Override
    public K floorKey(K key) {
        AbstractEntry<K,V> entry = floorEntry(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K,V> ceilingEntry(K key) {
        if (key == null)
            return null;
        Entry<K,V> splitting = splitToRight(key);
        AbstractEntry<K,V> target = getFirstEntry(splitting);
        merge(this.root,splitting);
        return target;
    }

    @Override
    public K ceilingKey(K key) {
        AbstractEntry<K,V> entry = ceilingEntry(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K,V> higherEntry(K key) {
        if (key == null)
            return null;
        Entry<K,V> splitting = splitToLeft(key);
        AbstractEntry<K,V> target = getFirstEntry(splitting);
        merge(this.root,splitting);
        return target;
    }

    @Override
    public K higherKey(K key) {
        AbstractEntry<K,V> entry = higherEntry(key);
        return entry == null ? null : entry.key;
    }

    @Override
    public AbstractEntry<K,V> firstEntry() {
        return getFirstEntry();
    }

    @Override
    public AbstractEntry<K,V> lastEntry() {
        return getLastEntry();
    }

    @Override
    public AbstractEntry<K,V> pollFirstEntry() {
        AbstractEntry<K,V> entry = getFirstEntry();
        remove(entry.key);
        return entry;
    }

    @Override
    public AbstractEntry<K,V> pollLastEntry() {
        AbstractEntry<K,V> entry = getLastEntry();
        remove(entry.key);
        return entry;
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        throw new UnsupportedOperationException();
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

    @Override
    protected AbstractEntry<K, V> getRoot() {
        return this.root;
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     */
    @Override
    protected final AbstractEntry<K,V> successor(AbstractEntry<K,V> t) {
        if (t == null)
            return null;
        Entry<K,V> splitting = splitToLeft(t.key);
        AbstractEntry<K,V> target = getFirstEntry(splitting);
        merge(this.root,splitting);
        return target;
    }

    /**
     * Returns the predecessor of the specified Entry, or null if no such.
     */

    final AbstractEntry<K,V> predecessor(K key) {
        if (key == null)
            return null;
        Entry<K,V> splitting = splitToRight(key);
        AbstractEntry<K,V> target = getLastEntry();
        merge(this.root,splitting);
        return target;
    }

    @Override
    protected final AbstractEntry<K,V> predecessor(AbstractEntry<K,V> t) {
        if (t == null)
            return null;
        Entry<K,V> splitting = splitToRight(t.key);
        AbstractEntry<K,V> target = getLastEntry();
        merge(this.root,splitting);
        return target;
    }

}
