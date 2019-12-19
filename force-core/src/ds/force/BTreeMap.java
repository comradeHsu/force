package ds.force;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

public class BTreeMap<K,V> implements NavigableMap<K,V>, Cloneable, java.io.Serializable {
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

    public BTreeMap(int degree){
        this.comparator = null;
        this.degree = degree;
        this.root = new BTreeNode<>(degree,null);
    }

    public BTreeMap(int degree, Comparator<? super K> comparator){
        this.comparator = comparator;
        this.degree = degree;
        this.root = new BTreeNode<>(degree,null);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this map has least a mapping entry
     * @return {@code true or false}
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the
     *         specified key
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey(Object key) {
        Entry<K,V> target;
        K k = (K) key;
        target = getEntry(k);
        return target != null;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the
     * specified value.  More formally, returns {@code true} if and only if
     * this map contains at least one mapping to a value {@code v} such
     * that {@code (value==null ? v==null : value.equals(v))}.  This
     * operation will probably require time linear in the map size for
     * most implementations.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if a mapping to {@code value} exists;
     *         {@code false} otherwise
     */
    @Override
    public boolean containsValue(Object value) {
        for (Entry<K,V> e : entrySet())
            if (valEquals(value, e.getValue()))
                return true;
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    @Override
    public V get(Object key) {
        Entry<K,V> target;
        @SuppressWarnings("unchecked")
        K k = (K) key;
        target = getEntry(k);
        return target == null ? null : target.getValue();
    }

    @SuppressWarnings("unchecked")
    private NodeEntry<K,V> getEntry(K key){
        if (comparator != null)
            return getEntry(key,comparator::compare);
        else
            return getEntry(key,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
    }

    private NodeEntry<K,V> getEntry(K key,final ToIntBiFunction<K,K> compare){
        BTreeNode<K,V> node = this.root;
        while (node != null){
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                NodeEntry<K,V> entry = node.keys.get(i);
                int cmp = compare.applyAsInt(key, entry.key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else return entry;
            }
            node = node.isLeaf() ? null : node.childes.get(index);
        }
        return null;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     *
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
       if (comparator != null)
           return putUsingFunction(key, value, comparator::compare);
       return putUsingFunction(key, value, (k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    // see put
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

    /**
     * when the BTreeNode's keys only t-1, if we want delete entry in this node or child's
     * node, we should merge the key's two child's node
     * @param node the current BTreeNode
     * @param keyPoint the key's index for will be merge
     */
    final void merge(BTreeNode<K,V> node,int keyPoint){
        NodeEntry<K,V> k = node.keys.get(keyPoint);
        BTreeNode<K,V> left = node.childes.get(keyPoint);
        BTreeNode<K,V> right = node.childes.get(keyPoint+1);
        node.keys.remove(keyPoint);
        left.keys.add(k);
        left.keys.addAll(right.keys);
        if (!left.isLeaf()) {
            left.childes.addAll(right.childes);
            for (BTreeNode<K,V> c : left.childes)
                c.parent = left;
        }
        node.childes.remove(keyPoint+1);
        if (node.parent == null && node.keys.size() == 0){
            this.root = left;
            left.parent = null;
        }
    }

    /**
     * Removes the mapping for this key from this TreeMap if present.
     *
     * @param  key key for which mapping should be removed
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *         and this map uses natural ordering, or its comparator
     *         does not permit null keys
     */
    @Override
    public V remove(Object key) {
        if (comparator != null)
            return remove(key,comparator::compare);
        return remove(key,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
    }

    private BTreeNode<K, V> borrowOrMerge(BTreeNode<K, V> node, int least, int nodePoint) {
        List<BTreeNode<K, V>> childes = node.parent.childes;
        BTreeNode<K, V> preNode = null, nextNode = null;
        if (nodePoint != 0 && (preNode = childes.get(nodePoint - 1)).keys.size() > least) {
            NodeEntry<K, V> point = node.parent.keys.get(nodePoint - 1);
            int lastKeyIndex = preNode.keys.size() - 1;
            node.parent.keys.set(nodePoint - 1, preNode.keys.get(lastKeyIndex));
            node.keys.add(0, point);
            preNode.keys.remove(lastKeyIndex);
            if (!node.isLeaf()) {
                node.childes.add(0, preNode.childes.get(lastKeyIndex + 1));
                preNode.childes.remove(lastKeyIndex + 1);
                node.childes.get(0).parent = node;
            }
        } else if (nodePoint != childes.size() - 1 &&
                (nextNode = childes.get(nodePoint + 1)) != null && nextNode.keys.size() > least) {
            NodeEntry<K, V> point = node.parent.keys.get(nodePoint);
            node.parent.keys.set(nodePoint, nextNode.keys.get(0));
            nextNode.keys.remove(0);
            node.keys.add(point);
            if (!node.isLeaf()) {
                node.childes.add(nextNode.childes.get(0));
                nextNode.childes.remove(0);
                node.childes.get(node.childes.size() - 1).parent = node;
            }
        } else {
            int point = nodePoint != 0 ? nodePoint - 1 : nodePoint;
            merge(node.parent, point);
            node = childes.get(point);
        }
        return node;
    }

    /**
     * When deleting the entry, the target entry is found in the node.
     * At this time, we select the next node to traverse
     * @param node target entry's node
     * @param index the key's index
     * @param least degree-1
     * @return the next node to traverse
     */
    private BTreeNode<K,V> selectNextNode(BTreeNode<K,V> node, int index,int least){
        BTreeNode<K,V> preNode = null, nextNode = null;
        if ((preNode = node.childes.get(index)).keys.size() > least){
            node = preNode;
        } else if ((nextNode = node.childes.get(index+1)).keys.size() > least){
            node = nextNode;
        } else {
            merge(node,index);
            node = node.childes.get(index);
        }
        return node;
    }
    // see remove
    private V remove(Object key, final ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> target = null, replaceNode = null;
        K k = (K) key;
        V value = null;
        int keyPoint = 0;
        BTreeNode<K,V> node = this.root;
        int least = degree - 1;
        loop:while(node != null){
            int index = 0;
            if (node != root && node.keys.size() == degree-1){
                int nodePoint = node.parent.childes.indexOf(node);
                node = borrowOrMerge(node, least, nodePoint);
            }
            for (int i = index; i < node.keys.size(); i++) {
                NodeEntry<K,V> entry = node.keys.get(i);
                if (entry == null) break;
                int cmp = compare.applyAsInt(k, entry.key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else {
                    target = node;
                    value = entry.value;
                    keyPoint = i;
                    if (target.isLeaf()) break loop;
                    node = selectNextNode(node,i,least);
                    continue loop;
                }
            }
            replaceNode = node;
            node = node.childes.size() == 0 ? null : node.childes.get(index);
        }
        if (target != null){
            if (target.isLeaf()){
                target.keys.remove(keyPoint);
            } else {
                int replaceIndex = compare.applyAsInt(replaceNode.keys.get(0).key, target.keys.get(keyPoint).key) > 0 ?
                        0 : replaceNode.keys.size()-1;
                target.keys.set(keyPoint,replaceNode.keys.get(replaceIndex));
                replaceNode.keys.remove(replaceIndex);
            }
            size--;
        }
        return value;
    }

    final NodeEntry<K,V> predecessor(Entry<K,V> entry){
        return lowerEntry(entry.getKey());
    }

    final NodeEntry<K,V> successor(Entry<K,V> entry){
        return higherEntry(entry.getKey());
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(Entry<? extends K, ? extends V> entry : m.entrySet()){
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.root.clear();
        this.size = 0;
    }

    // Views

    /**
     * Fields initialized to contain an instance of the entry set view
     * the first time this view is requested.  Views are stateless, so
     * there's no reason to create more than one.
     */
    private transient EntrySet entrySet;
    private transient KeySet<K> navigableKeySet;
    private transient NavigableMap<K,V> descendingMap;
    transient Collection<V> values;

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
    public Set<Entry<K, V>> entrySet() {
        EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the least key greater than the specified
     * key; if no such entry exists (i.e., the greatest key in the Tree is less
     * than the specified key), returns {@code null}.
     */
    final NodeEntry<K,V> getCeilingEntry(K key,ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> node = root;
        while (!node.isLeaf()) {
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                NodeEntry<K,V> entry = node.keys.get(i);
                int cmp = compare.applyAsInt(key, entry.key);
                if (cmp <= 0) break;
                else index++;
            }
            node = node.childes.get(index);
        }
        BTreeNode<K,V> parent = node.parent;
        int keyPoint = keyPoint(node,key, cmp -> cmp <= 0);
        while (parent != null && keyPoint == node.keys.size()){
            node = parent;
            keyPoint = keyPoint(parent,key,cmp -> cmp <= 0);
            parent = parent.parent;
        }
        return keyPoint == node.keys.size() || isEmpty() ? null : node.keys.get(keyPoint);
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the greatest key less than the specified
     * key; if no such entry exists, returns {@code null}.
     */
    final NodeEntry<K,V> getFloorEntry(K key,ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> node = root, preNode = root;
        while (node != null) {
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                NodeEntry<K,V> entry = node.keys.get(i);
                int cmp = compare.applyAsInt(key, entry.key);
                if (cmp < 0) break;
                else if (cmp > 0)index++;
                else return entry;
            }
            preNode = node;
            node = node.isLeaf() ? null : node.childes.get(index);
        }
        BTreeNode<K,V> parent = preNode.parent;
        int keyPoint = keyPoint(preNode,key);
        while (parent != null && keyPoint == 0){
            preNode = parent;
            keyPoint = keyPoint(parent,key);
            parent = parent.parent;
        }
        return keyPoint == 0 || isEmpty() ? null : preNode.keys.get(keyPoint-1);
    }

    /**
     * Gets the entry for the least key greater than the specified
     * key; if no such entry exists, returns the entry for the least
     * key greater than the specified key; if no such entry exists
     * returns {@code null}.
     */
    final NodeEntry<K,V> getHigherEntry(K key,ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> node = root;
        while (!node.isLeaf()) {
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                NodeEntry<K,V> entry = node.keys.get(i);
                int cmp = compare.applyAsInt(key, entry.key);
                if (cmp < 0) break;
                else index++;
            }
            node = node.childes.get(index);
        }
        BTreeNode<K,V> parent = node.parent;
        int keyPoint = keyPoint(node,key, cmp -> cmp < 0);
        while (parent != null && keyPoint == node.keys.size()){
            node = parent;
            keyPoint = keyPoint(parent,key,cmp -> cmp < 0);
            parent = parent.parent;
        }
        return keyPoint == node.keys.size() || isEmpty() ? null : node.keys.get(keyPoint);
    }

    /**
     * Returns the entry for the greatest key less than the specified key; if
     * no such entry exists (i.e., the least key in the Tree is greater than
     * the specified key), returns {@code null}.
     */
    final NodeEntry<K,V> getLowerEntry(K key,ToIntBiFunction<K,K> compare) {
        BTreeNode<K,V> node = root;
        while (!node.isLeaf()) {
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                NodeEntry<K,V> entry = node.keys.get(i);
                int cmp = compare.applyAsInt(key, entry.key);
                if (cmp <= 0) break;
                else index++;
            }
            node = node.childes.get(index);
        }
        BTreeNode<K,V> parent = node.parent;
        int keyPoint = keyPoint(node,key);
        while (parent != null && keyPoint == 0){
            node = parent;
            keyPoint = keyPoint(parent,key);
            parent = parent.parent;
        }
        return keyPoint == 0 || isEmpty() ? null : node.keys.get(keyPoint-1);
    }

    @Override
    public NodeEntry<K, V> lowerEntry(K key) {
        if (comparator != null)
            return getLowerEntry(key,comparator::compare);
        return getLowerEntry(key, (k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    @Override
    public K lowerKey(K key) {
        NodeEntry<K,V> entry = lowerEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NodeEntry<K, V> floorEntry(K key) {
        if (comparator != null)
            return getFloorEntry(key,comparator::compare);
        return getFloorEntry(key, (k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    @Override
    public K floorKey(K key) {
        NodeEntry<K,V> entry = floorEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NodeEntry<K, V> ceilingEntry(K key) {
        if (comparator != null)
            return getCeilingEntry(key,comparator::compare);
        return getCeilingEntry(key, (k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    @Override
    public K ceilingKey(K key) {
        NodeEntry<K,V> entry = ceilingEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NodeEntry<K, V> higherEntry(K key) {
        if (comparator != null)
            return getHigherEntry(key,comparator::compare);
        return getHigherEntry(key, (k, k2) -> ((Comparable<? super K>)k).compareTo(k2));
    }

    @Override
    public K higherKey(K key) {
        NodeEntry<K,V> entry = higherEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NodeEntry<K, V> firstEntry() {
        if (this.isEmpty()) return null;
        BTreeNode<K,V> node = root;
        while (!node.isLeaf()){
            node = node.childes.get(0);
        }
        return node.keys.get(0);
    }

    @Override
    public NodeEntry<K, V> lastEntry() {
        if (this.isEmpty()) return null;
        BTreeNode<K,V> node = root;
        while (!node.isLeaf()){
            node = node.childes.get(node.childes.size()-1);
        }
        return node.keys.get(node.keys.size()-1);
    }

    @Override
    public Entry<K, V> pollFirstEntry() {
        NodeEntry<K,V> entry = firstEntry();
        if (entry != null) remove(entry.key);
        return entry;
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        NodeEntry<K,V> entry = lastEntry();
        if (entry != null) remove(entry.key);
        return entry;
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        NavigableMap<K, V> km = descendingMap;
        return (km != null) ? km :
                (descendingMap = new DescendingSubMap<>(this,
                        true, null, true,
                        true, null, true));
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        KeySet<K> nks = navigableKeySet;
        return (nks != null) ? nks : (navigableKeySet = new KeySet<>(this));
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        return new AscendingSubMap<>(this,
                false, fromKey, fromInclusive,
                false, toKey,   toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                true,  null,  true,
                false, toKey, inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                false, fromKey, inclusive,
                true,  null,    true);
    }

    @Override
    public Comparator<? super K> comparator() {
        return comparator;
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return headMap(toKey,false);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public K firstKey() {
        NodeEntry<K,V> entry = firstEntry();
        return entry == null ? null : entry.key;
    }

    @Override
    public K lastKey() {
        NodeEntry<K,V> entry = lastEntry();
        return entry == null ? null : entry.key;
    }

    class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            V p = get(entry.getKey());
            return p != null && valEquals(p,value);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            V p = get(entry.getKey());
            if (p != null && valEquals(p, value)) {
                BTreeMap.this.remove(entry.getKey());
                return true;
            }
            return false;
        }

        public int size() {
            return BTreeMap.this.size();
        }

        public void clear() {
            BTreeMap.this.clear();
        }

        public Spliterator<Map.Entry<K,V>> spliterator() {
            throw new UnsupportedOperationException();
//            return new EntrySpliterator<K,V>(BTreeMap.this, null, null, 0, -1, 0);
        }
    }

    int keyPoint(BTreeNode<K,V> node, K key){
        if (comparator != null)
            return keyPointUsingComparator(node,key, cmp -> cmp <= 0);
        return keyPointComparable(node,key, cmp -> cmp <= 0);
    }

    int keyPoint(BTreeNode<K,V> node, K key, IntPredicate predicate){
        if (comparator != null)
            return keyPointUsingComparator(node,key, predicate);
        return keyPointComparable(node,key, predicate);
    }

    private int keyPointComparable(BTreeNode<K,V> node, K key, IntPredicate predicate){
        int index = 0;
        @SuppressWarnings("unchecked")
        Comparable<? super K> k = (Comparable<? super K>) key;
        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i) == null) break;
            int cmp = k.compareTo(node.keys.get(i).key);
            if (predicate.test(cmp)) break;
            else index++;
        }
        return index;
    }

    private int keyPointUsingComparator(BTreeNode<K,V> node, K key, IntPredicate predicate){
        int index = 0;
        for (int i = 0; i < node.keys.size(); i++) {
            if (node.keys.get(i) == null) break;
            int cmp = comparator.compare(key, node.keys.get(i).key);
            if (predicate.test(cmp)) break;
            else index++;
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

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (Objects.equals(o1, o2));
    }

    class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        public int size() {
            return BTreeMap.this.size();
        }

        public boolean contains(Object o) {
            return BTreeMap.this.containsValue(o);
        }

        public boolean remove(Object o) {
            for (Entry<K,V> e = firstEntry(); e != null; e = successor(e)) {
                if (valEquals(e.getValue(), o)) {
                    BTreeMap.this.remove(e.getKey());
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            BTreeMap.this.clear();
        }

        public Spliterator<V> spliterator() {
            throw new UnsupportedOperationException();
//            return new ValueSpliterator<K,V>(TreeMap.this, null, null, 0, -1, 0);
        }
    }

    /*
     * Unlike Values and EntrySet, the KeySet class is static,
     * delegating to a NavigableMap to allow use by SubMaps, which
     * outweighs the ugliness of needing type-tests for the following
     * Iterator methods that are defined appropriately in main versus
     * submap classes.
     */

    Iterator<K> keyIterator() {
        return new KeyIterator();
    }

    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator();
    }

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> m;
        KeySet(NavigableMap<E,?> map) { m = map; }

        public Iterator<E> iterator() {
            if (m instanceof BTreeMap)
                return ((BTreeMap<E,?>)m).keyIterator();
            else
                return ((BTreeMap.NavigableSubMap<E,?>)m).keyIterator();
        }

        public Iterator<E> descendingIterator() {
            if (m instanceof TreeMap)
                return ((BTreeMap<E,?>)m).descendingKeyIterator();
            else
                return ((BTreeMap.NavigableSubMap<E,?>)m).descendingKeyIterator();
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

        public Spliterator<E> spliterator() {
            throw new UnsupportedOperationException();
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class BTreeIterator {
        NodeEntry<K,V> current;     // current entry
        Deque<Object> stack;
        BTreeIterator() {
            BTreeNode<K,V> t = root;
            current = null;
            stack = new ArrayDeque<>();
            if (t != null && size > 0) { // advance to first entry
                stack.push(t);
            }
        }

        public final boolean hasNext() {
            return !stack.isEmpty();
        }

        @SuppressWarnings("unchecked")
        final NodeEntry<K,V> nextEntry() {
            Object e = stack.pop();
            if (e instanceof NodeEntry){
                NodeEntry<K,V> entry = (NodeEntry<K, V>) e;
                current = entry;
            } else {
                BTreeNode<K,V> node = (BTreeNode<K, V>) e;
                while (!node.isLeaf()){
                    for (int i = node.keys.size() - 1; i > -1; i--) {
                        stack.push(node.childes.get(i+1));
                        stack.push(node.keys.get(i));
                    }
                    node = node.childes.get(0);
                }
                for (int i = node.keys.size() - 1; i > -1; i--){
                    stack.push(node.keys.get(i));
                }
                current = (NodeEntry<K, V>) stack.pop();
            }
            return current;
        }

        public final void remove() {
            NodeEntry<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            current = null;
            K key = p.key;
            BTreeMap.this.remove(key);
        }
    }

    final class KeyIterator extends BTreeIterator
            implements Iterator<K> {
        public final K next() { return nextEntry().key; }
    }

    final class ValueIterator extends BTreeIterator
            implements Iterator<V> {
        public final V next() { return nextEntry().value; }
    }

    final class EntryIterator extends BTreeIterator
            implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextEntry(); }
    }

    final class DescendingKeyIterator extends BTreeIterator
            implements Iterator<K> {
        @SuppressWarnings("unchecked")
        public final K next() {
            Object e = stack.pop();
            if (e instanceof NodeEntry){
                current = (NodeEntry<K, V>) e;
            } else {
                BTreeNode<K,V> node = (BTreeNode<K, V>) e;
                while (!node.isLeaf()){
                    for (int i = 0; i < node.keys.size(); i++) {
                        stack.push(node.childes.get(i));
                        stack.push(node.keys.get(i));
                    }
                    node = node.childes.get(node.keys.size());
                }
                for (int i = 0; i < node.keys.size(); i++){
                    stack.push(node.keys.get(i));
                }
                current = (NodeEntry<K, V>) stack.pop();
            }
            return current.key;
        }
    }

    /**
     * Dummy value serving as unmatchable fence key for unbounded
     * SubMapIterators
     */
    private static final Object UNBOUNDED = new Object();

    /**
     * @serial include
     */
    abstract static class NavigableSubMap<K,V> extends AbstractMap<K,V>
            implements NavigableMap<K,V>, java.io.Serializable {
        private static final long serialVersionUID = -2102997345730753016L;
        /**
         * The backing map.
         */
        final BTreeMap<K,V> m;

        /**
         * Endpoints are represented as triples (fromStart, lo,
         * loInclusive) and (toEnd, hi, hiInclusive). If fromStart is
         * true, then the low (absolute) bound is the start of the
         * backing map, and the other values are ignored. Otherwise,
         * if loInclusive is true, lo is the inclusive bound, else lo
         * is the exclusive bound. Similarly for the upper bound.
         */
        final K lo, hi;
        final boolean fromStart, toEnd;
        final boolean loInclusive, hiInclusive;

        NavigableSubMap(BTreeMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            if (!fromStart && !toEnd) {
                if (m.compare(lo, hi) > 0)
                    throw new IllegalArgumentException("fromKey > toKey");
            } else {
                if (!fromStart) // type check
                    m.compare(lo, lo);
                if (!toEnd)
                    m.compare(hi, hi);
            }

            this.m = m;
            this.fromStart = fromStart;
            this.lo = lo;
            this.loInclusive = loInclusive;
            this.toEnd = toEnd;
            this.hi = hi;
            this.hiInclusive = hiInclusive;
        }

        // internal utilities

        final boolean tooLow(Object key) {
            if (!fromStart) {
                int c = m.compare((K)key, lo);
                if (c < 0 || (c == 0 && !loInclusive))
                    return true;
            }
            return false;
        }

        final boolean tooHigh(Object key) {
            if (!toEnd) {
                int c = m.compare((K)key, hi);
                if (c > 0 || (c == 0 && !hiInclusive))
                    return true;
            }
            return false;
        }

        final boolean inRange(Object key) {
            return !tooLow(key) && !tooHigh(key);
        }

        final boolean inClosedRange(Object key) {
            return (fromStart || m.compare((K)key, lo) >= 0)
                    && (toEnd || m.compare(hi, (K)key) >= 0);
        }

        final boolean inRange(Object key, boolean inclusive) {
            return inclusive ? inRange(key) : inClosedRange(key);
        }

        /*
         * Absolute versions of relation operations.
         * Subclasses map to these using like-named "sub"
         * versions that invert senses for descending maps
         */

        final NodeEntry<K,V> absLowest() {
            NodeEntry<K,V> e =
                    (fromStart ?  m.firstEntry() :
                            (loInclusive ? m.ceilingEntry(lo) :
                                    m.higherEntry(lo)));
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final NodeEntry<K,V> absHighest() {
            NodeEntry<K,V> e =
                    (toEnd ?  m.lastEntry() :
                            (hiInclusive ?  m.floorEntry(hi) :
                                    m.lowerEntry(hi)));
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final NodeEntry<K,V> absCeiling(K key) {
            if (tooLow(key))
                return absLowest();
            NodeEntry<K,V> e = m.ceilingEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final NodeEntry<K,V> absHigher(K key) {
            if (tooLow(key))
                return absLowest();
            NodeEntry<K,V> e = m.higherEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final NodeEntry<K,V> absFloor(K key) {
            if (tooHigh(key))
                return absHighest();
            NodeEntry<K,V> e = m.floorEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final NodeEntry<K,V> absLower(K key) {
            if (tooHigh(key))
                return absHighest();
            NodeEntry<K,V> e = m.lowerEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        /** Returns the absolute high fence for ascending traversal */
        final NodeEntry<K,V> absHighFence() {
            return (toEnd ? null : (hiInclusive ?
                    m.higherEntry(hi) :
                    m.ceilingEntry(hi)));
        }

        /** Return the absolute low fence for descending traversal  */
        final NodeEntry<K,V> absLowFence() {
            return (fromStart ? null : (loInclusive ?
                    m.lowerEntry(lo) :
                    m.floorEntry(lo)));
        }

        // Abstract methods defined in ascending vs descending classes
        // These relay to the appropriate absolute versions

        abstract NodeEntry<K,V> subLowest();
        abstract NodeEntry<K,V> subHighest();
        abstract NodeEntry<K,V> subCeiling(K key);
        abstract NodeEntry<K,V> subHigher(K key);
        abstract NodeEntry<K,V> subFloor(K key);
        abstract NodeEntry<K,V> subLower(K key);

        /** Returns ascending iterator from the perspective of this submap */
        abstract Iterator<K> keyIterator();

        abstract Spliterator<K> keySpliterator();

        /** Returns descending iterator from the perspective of this submap */
        abstract Iterator<K> descendingKeyIterator();

        // public methods

        public boolean isEmpty() {
            return (fromStart && toEnd) ? m.isEmpty() : entrySet().isEmpty();
        }

        public int size() {
            return (fromStart && toEnd) ? m.size() : entrySet().size();
        }

        public final boolean containsKey(Object key) {
            return inRange(key) && m.containsKey(key);
        }

        public final V put(K key, V value) {
            if (!inRange(key))
                throw new IllegalArgumentException("key out of range");
            return m.put(key, value);
        }

        public final V get(Object key) {
            return !inRange(key) ? null :  m.get(key);
        }

        public final V remove(Object key) {
            return !inRange(key) ? null : m.remove(key);
        }

        public final Map.Entry<K,V> ceilingEntry(K key) {
            return exportEntry(subCeiling(key));
        }

        public final K ceilingKey(K key) {
            return keyOrNull(subCeiling(key));
        }

        public final Map.Entry<K,V> higherEntry(K key) {
            return exportEntry(subHigher(key));
        }

        public final K higherKey(K key) {
            return keyOrNull(subHigher(key));
        }

        public final Map.Entry<K,V> floorEntry(K key) {
            return exportEntry(subFloor(key));
        }

        public final K floorKey(K key) {
            return keyOrNull(subFloor(key));
        }

        public final Map.Entry<K,V> lowerEntry(K key) {
            return exportEntry(subLower(key));
        }

        public final K lowerKey(K key) {
            return keyOrNull(subLower(key));
        }

        public final K firstKey() {
            return key(subLowest());
        }

        public final K lastKey() {
            return key(subHighest());
        }

        public final Map.Entry<K,V> firstEntry() {
            return exportEntry(subLowest());
        }

        public final Map.Entry<K,V> lastEntry() {
            return exportEntry(subHighest());
        }

        public final Map.Entry<K,V> pollFirstEntry() {
            NodeEntry<K,V> e = subLowest();
            Map.Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.remove(e.key);
            return result;
        }

        public final Map.Entry<K,V> pollLastEntry() {
            NodeEntry<K,V> e = subHighest();
            Map.Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.remove(e.key);
            return result;
        }

        // Views
        transient NavigableMap<K,V> descendingMapView;
        transient EntrySetView entrySetView;
        transient KeySet<K> navigableKeySetView;

        public final NavigableSet<K> navigableKeySet() {
            KeySet<K> nksv = navigableKeySetView;
            return (nksv != null) ? nksv :
                    (navigableKeySetView = new BTreeMap.KeySet<>(this));
        }

        public final Set<K> keySet() {
            return navigableKeySet();
        }

        public NavigableSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        public final SortedMap<K,V> subMap(K fromKey, K toKey) {
            return subMap(fromKey, true, toKey, false);
        }

        public final SortedMap<K,V> headMap(K toKey) {
            return headMap(toKey, false);
        }

        public final SortedMap<K,V> tailMap(K fromKey) {
            return tailMap(fromKey, true);
        }

        // View classes

        abstract class EntrySetView extends AbstractSet<Map.Entry<K,V>> {
            private transient int size = -1;

            public int size() {
                if (fromStart && toEnd)
                    return m.size();
                if (size == -1) {
                    size = 0;
                    Iterator<?> i = iterator();
                    while (i.hasNext()) {
                        size++;
                        i.next();
                    }
                }
                return size;
            }

            public boolean isEmpty() {
                NodeEntry<K,V> n = absLowest();
                return n == null || tooHigh(n.key);
            }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                NodeEntry<?,?> node = m.getEntry((K)key);
                return node != null &&
                        valEquals(node.getValue(), entry.getValue());
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                NodeEntry<K,V> node = m.getEntry((K)key);
                if (node!=null && valEquals(node.getValue(),
                        entry.getValue())) {
                    m.remove(node.key);
                    return true;
                }
                return false;
            }
        }

        /**
         * Iterators for SubMaps
         */
        abstract class SubMapIterator<T> implements Iterator<T> {
            NodeEntry<K,V> lastReturned;
            NodeEntry<K,V> next;
            final Object fenceKey;

            SubMapIterator(NodeEntry<K,V> first,
                           NodeEntry<K,V> fence) {
                lastReturned = null;
                next = first;
                fenceKey = fence == null ? UNBOUNDED : fence.key;
            }

            public final boolean hasNext() {
                return next != null && next.key != fenceKey;
            }

            final NodeEntry<K,V> nextEntry() {
                NodeEntry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                next = m.successor(e);
                lastReturned = e;
                return e;
            }

            final NodeEntry<K,V> prevEntry() {
                NodeEntry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                next = m.predecessor(e);
                lastReturned = e;
                return e;
            }

            final void removeAscending() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                // deleted entries are replaced by their successors
                next = lastReturned;
                m.remove(lastReturned.key);
                lastReturned = null;
            }

            final void removeDescending() {
                if (lastReturned == null)
                    throw new IllegalStateException();
                m.remove(lastReturned.key);
                lastReturned = null;
            }

        }

        final class SubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
            SubMapEntryIterator(NodeEntry<K,V> first,
                                NodeEntry<K,V> fence) {
                super(first, fence);
            }
            public Map.Entry<K,V> next() {
                return nextEntry();
            }
            public void remove() {
                removeAscending();
            }
        }

        final class DescendingSubMapEntryIterator extends SubMapIterator<Map.Entry<K,V>> {
            DescendingSubMapEntryIterator(NodeEntry<K,V> last,
                                          NodeEntry<K,V> fence) {
                super(last, fence);
            }

            public Map.Entry<K,V> next() {
                return prevEntry();
            }
            public void remove() {
                removeDescending();
            }
        }

        // Implement minimal Spliterator as KeySpliterator backup
        final class SubMapKeyIterator extends SubMapIterator<K>
                implements Spliterator<K> {
            SubMapKeyIterator(NodeEntry<K,V> first,
                              NodeEntry<K,V> fence) {
                super(first, fence);
            }
            public K next() {
                return nextEntry().key;
            }
            public void remove() {
                removeAscending();
            }
            public Spliterator<K> trySplit() {
                return null;
            }
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED |
                        Spliterator.SORTED;
            }
            public final Comparator<? super K>  getComparator() {
                return NavigableSubMap.this.comparator();
            }
        }

        final class DescendingSubMapKeyIterator extends SubMapIterator<K>
                implements Spliterator<K> {
            DescendingSubMapKeyIterator(NodeEntry<K,V> last,
                                        NodeEntry<K,V> fence) {
                super(last, fence);
            }
            public K next() {
                return prevEntry().key;
            }
            public void remove() {
                removeDescending();
            }
            public Spliterator<K> trySplit() {
                return null;
            }
            public void forEachRemaining(Consumer<? super K> action) {
                while (hasNext())
                    action.accept(next());
            }
            public boolean tryAdvance(Consumer<? super K> action) {
                if (hasNext()) {
                    action.accept(next());
                    return true;
                }
                return false;
            }
            public long estimateSize() {
                return Long.MAX_VALUE;
            }
            public int characteristics() {
                return Spliterator.DISTINCT | Spliterator.ORDERED;
            }
        }
    }

    /**
     * @serial include
     */
    static final class AscendingSubMap<K,V> extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866124060L;

        AscendingSubMap(BTreeMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey,   boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                    false, fromKey, fromInclusive,
                    false, toKey,   toInclusive);
        }

        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new AscendingSubMap<>(m,
                    fromStart, lo,    loInclusive,
                    false,     toKey, inclusive);
        }

        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new AscendingSubMap<>(m,
                    false, fromKey, inclusive,
                    toEnd, hi,      hiInclusive);
        }

        public NavigableMap<K,V> descendingMap() {
            NavigableMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                    (descendingMapView =
                            new DescendingSubMap<>(m,
                                    fromStart, lo, loInclusive,
                                    toEnd,     hi, hiInclusive));
        }

        Iterator<K> keyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        Spliterator<K> keySpliterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        Iterator<K> descendingKeyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        final class AscendingEntrySetView extends EntrySetView {
            public Iterator<Map.Entry<K,V>> iterator() {
                return new SubMapEntryIterator(absLowest(), absHighFence());
            }
        }

        public Set<Map.Entry<K,V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new AscendingEntrySetView());
        }

        NodeEntry<K,V> subLowest()       { return absLowest(); }
        NodeEntry<K,V> subHighest()      { return absHighest(); }
        NodeEntry<K,V> subCeiling(K key) { return absCeiling(key); }
        NodeEntry<K,V> subHigher(K key)  { return absHigher(key); }
        NodeEntry<K,V> subFloor(K key)   { return absFloor(key); }
        NodeEntry<K,V> subLower(K key)   { return absLower(key); }
    }

    /**
     * @serial include
     */
    static final class DescendingSubMap<K,V>  extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866120460L;
        DescendingSubMap(BTreeMap<K,V> m,
                         boolean fromStart, K lo, boolean loInclusive,
                         boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        private final Comparator<? super K> reverseComparator =
                Collections.reverseOrder(m.comparator);

        public Comparator<? super K> comparator() {
            return reverseComparator;
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey,   boolean toInclusive) {
            if (!inRange(fromKey, fromInclusive))
                throw new IllegalArgumentException("fromKey out of range");
            if (!inRange(toKey, toInclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                    false, toKey,   toInclusive,
                    false, fromKey, fromInclusive);
        }

        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
            if (!inRange(toKey, inclusive))
                throw new IllegalArgumentException("toKey out of range");
            return new DescendingSubMap<>(m,
                    false, toKey, inclusive,
                    toEnd, hi,    hiInclusive);
        }

        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
            if (!inRange(fromKey, inclusive))
                throw new IllegalArgumentException("fromKey out of range");
            return new DescendingSubMap<>(m,
                    fromStart, lo, loInclusive,
                    false, fromKey, inclusive);
        }

        public NavigableMap<K,V> descendingMap() {
            NavigableMap<K,V> mv = descendingMapView;
            return (mv != null) ? mv :
                    (descendingMapView =
                            new AscendingSubMap<>(m,
                                    fromStart, lo, loInclusive,
                                    toEnd,     hi, hiInclusive));
        }

        Iterator<K> keyIterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        Spliterator<K> keySpliterator() {
            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
        }

        Iterator<K> descendingKeyIterator() {
            return new SubMapKeyIterator(absLowest(), absHighFence());
        }

        final class DescendingEntrySetView extends EntrySetView {
            public Iterator<Map.Entry<K,V>> iterator() {
                return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
            }
        }

        public Set<Map.Entry<K,V>> entrySet() {
            EntrySetView es = entrySetView;
            return (es != null) ? es : (entrySetView = new DescendingEntrySetView());
        }

        NodeEntry<K,V> subLowest()       { return absHighest(); }
        NodeEntry<K,V> subHighest()      { return absLowest(); }
        NodeEntry<K,V> subCeiling(K key) { return absFloor(key); }
        NodeEntry<K,V> subHigher(K key)  { return absLower(key); }
        NodeEntry<K,V> subFloor(K key)   { return absCeiling(key); }
        NodeEntry<K,V> subLower(K key)   { return absHigher(key); }
    }

    int compare(K key, K entryKey){
        return 0;
    }

    /**
     * Return SimpleImmutableEntry for entry, or null if null
     */
    static <K,V> Map.Entry<K,V> exportEntry(NodeEntry<K,V> e) {
        return (e == null) ? null :
                new AbstractMap.SimpleImmutableEntry<>(e);
    }

    /**
     * Return key for entry, or null if null
     */
    static <K,V> K keyOrNull(NodeEntry<K,V> e) {
        return (e == null) ? null : e.key;
    }

    /**
     * Returns the key corresponding to the specified Entry.
     * @throws NoSuchElementException if the Entry is null
     */
    static <K> K key(NodeEntry<K,?> e) {
        if (e==null)
            throw new NoSuchElementException();
        return e.key;
    }

}
