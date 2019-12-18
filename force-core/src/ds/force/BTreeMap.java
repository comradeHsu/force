package ds.force;

import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

public class BTreeMap<K,V> implements NavigableMap<K,V> {
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
        Entry<K,V> target;
        K k = (K) key;
        if (comparator != null)
            target = getEntry(k,comparator::compare);
        else target = getEntry(k,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
        return target != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K,V> e : entrySet())
            if (valEquals(value, e.getValue()))
                return true;
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        Entry<K,V> target;
        K k = (K) key;
        if (comparator != null)
            target = getEntry(k,comparator::compare);
        else target = getEntry(k,(k1, k2) -> ((Comparable<? super K>)k1).compareTo(k2));
        return target == null ? null : target.getValue();
    }

    private BTreeNode<K,V> getNode(K key,final ToIntBiFunction<K,K> compare){
        BTreeNode<K,V> node = this.root;
        do {
            int index = 0;
            for (int i = index; i < node.keys.size(); i++) {
                if (node.keys.get(i) == null) break;
                int cmp = compare.applyAsInt(key, node.keys.get(i).key);
                if (cmp < 0) break;
                else if (cmp > 0) index++;
                else return node;
            }
            node = node.childes.get(index);
        } while (!node.isLeaf());
        return null;
    }

    private Entry<K,V> getEntry(K key,final ToIntBiFunction<K,K> compare){
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
                    BTreeNode<K,V> preNode = null, nextNode = null;
                    if (target.isLeaf()) break loop;
                    if ((preNode = node.childes.get(i)).keys.size() > least){
                        node = preNode;
                    } else if ((nextNode = target.childes.get(i+1)).keys.size() > least){
                        node = nextNode;
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
                replaceNode.keys.remove(replaceIndex);
                size--;
            }
        }
        return value;
    }

    final NodeEntry<K,V> getPrecursorEntry(BTreeNode<K,V> precursorNode){
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

    final NodeEntry<K,V> getSuccessorEntry(BTreeNode<K,V> successorNode){
        NodeEntry<K,V> target = null;
        while (successorNode != null){
            target = successorNode.keys.get(0);
            successorNode = successorNode.childes.get(0);
        }
        return target;
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
//    private transient KeySet<K> navigableKeySet;
//    private transient NavigableMap<K,V> descendingMap;

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
        return comparator;
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

    int nodePoint(BTreeNode<K,V> node){
        List<BTreeNode<K,V>> childes = node.parent.childes;
        for (int i = 0; i < childes.size(); i++) {
            if (childes.get(i) == node) return i;
        }
        return -1;
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

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (Objects.equals(o1, o2));
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class BTreeIterator {
        NodeEntry<K,V> current;     // current entry
        NodeEntry<K,V> next;
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

}
