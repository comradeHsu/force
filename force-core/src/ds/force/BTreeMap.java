package ds.force;

import java.util.AbstractCollection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.IntPredicate;
import java.util.function.ToIntBiFunction;

public class BTreeMap<K,V> extends AbstractNavigableMap<K,V> {
    /**
     * default minmum degree
     */
    private static final int DEFAULT_DEGREE = 2;
    /**
     * minmum degree,min t-1,max 2t-1
     */
    private int degree;

    transient BTreeNode<K,V> root;

    private int size;

    public BTreeMap(){
        super();
        this.degree = DEFAULT_DEGREE;
        this.root = new BTreeNode<>(degree,null);
    }

    public BTreeMap(int degree){
        super();
        this.degree = degree;
        this.root = new BTreeNode<>(degree,null);
    }

    public BTreeMap(int degree, Comparator<? super K> comparator){
        super(comparator);
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
        target = getEntry(k);
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
        target = getEntry(k);
        return target == null ? null : target.getValue();
    }

    protected NodeEntry<K,V> getEntry(K key){
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

    @Override
    protected final NodeEntry<K,V> predecessor(NavigableEntry<K,V> entry){
        return lowerEntry(entry.getKey());
    }

    @Override
    protected final NodeEntry<K,V> successor(NavigableEntry<K, V> entry){
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
        AbstractNavigableMap.EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the least key greater than the specified
     * key; if no such entry exists (i.e., the greatest key in the Tree is less
     * than the specified key), returns {@code null}.
     */
    protected final NodeEntry<K,V> getCeilingEntry(K key,ToIntBiFunction<K,K> compare) {
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
    protected final NodeEntry<K,V> getFloorEntry(K key, ToIntBiFunction<K, K> compare) {
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
    protected final NodeEntry<K,V> getHigherEntry(K key,ToIntBiFunction<K,K> compare) {
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
    protected final NodeEntry<K,V> getLowerEntry(K key,ToIntBiFunction<K,K> compare) {
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
    public K firstKey() {
        NodeEntry<K,V> entry = firstEntry();
        return entry == null ? null : entry.key;
    }

    @Override
    public K lastKey() {
        NodeEntry<K,V> entry = lastEntry();
        return entry == null ? null : entry.key;
    }

    class EntrySet extends AbstractNavigableMap.EntrySet {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
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

    protected static class NodeEntry<K,V> extends AbstractNavigableMap.NavigableEntry<K,V> {

        NodeEntry(K key, V value) {
            this.key = key;
            this.value = value;
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
            for (NodeEntry<K,V> e = firstEntry(); e != null; e = successor(e)) {
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
    @Override
    Iterator<K> keyIterator() {
        return new KeyIterator();
    }

    @Override
    Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator();
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

//    /**
//     * @serial include
//     */
//    static final class AscendingSubMap<K,V> extends NavigableSubMap<K,V> {
//        private static final long serialVersionUID = 912986545866124060L;
//
//        AscendingSubMap(AbstractNavigableMap<K,V> m,
//                        boolean fromStart, K lo, boolean loInclusive,
//                        boolean toEnd,     K hi, boolean hiInclusive) {
//            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
//        }
//
//        public Comparator<? super K> comparator() {
//            return m.comparator();
//        }
//
//        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
//                                        K toKey, boolean toInclusive) {
//            if (!inRange(fromKey, fromInclusive))
//                throw new IllegalArgumentException("fromKey out of range");
//            if (!inRange(toKey, toInclusive))
//                throw new IllegalArgumentException("toKey out of range");
//            return new AscendingSubMap<>(m,
//                    false, fromKey, fromInclusive,
//                    false, toKey,   toInclusive);
//        }
//
//        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
//            if (!inRange(toKey, inclusive))
//                throw new IllegalArgumentException("toKey out of range");
//            return new AscendingSubMap<>(m,
//                    fromStart, lo,    loInclusive,
//                    false,     toKey, inclusive);
//        }
//
//        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
//            if (!inRange(fromKey, inclusive))
//                throw new IllegalArgumentException("fromKey out of range");
//            return new AscendingSubMap<>(m,
//                    false, fromKey, inclusive,
//                    toEnd, hi,      hiInclusive);
//        }
//
//        public NavigableMap<K,V> descendingMap() {
//            NavigableMap<K,V> mv = descendingMapView;
//            return (mv != null) ? mv :
//                    (descendingMapView =
//                            new DescendingSubMap<>(m,
//                                    fromStart, lo, loInclusive,
//                                    toEnd,     hi, hiInclusive));
//        }
//
//        Iterator<K> keyIterator() {
//            return new SubMapKeyIterator(absLowest(), absHighFence());
//        }
//
//        Spliterator<K> keySpliterator() {
//            return new SubMapKeyIterator(absLowest(), absHighFence());
//        }
//
//        Iterator<K> descendingKeyIterator() {
//            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
//        }
//
//        final class AscendingEntrySetView extends EntrySetView {
//            public Iterator<Map.Entry<K,V>> iterator() {
//                return new SubMapEntryIterator(absLowest(), absHighFence());
//            }
//        }
//
//        public Set<Map.Entry<K,V>> entrySet() {
//            EntrySetView es = entrySetView;
//            return (es != null) ? es : (entrySetView = new AscendingEntrySetView());
//        }
//
//        NavigableEntry<K,V> subLowest()       { return absLowest(); }
//        NavigableEntry<K,V> subHighest()      { return absHighest(); }
//        NavigableEntry<K,V> subCeiling(K key) { return absCeiling(key); }
//        NavigableEntry<K,V> subHigher(K key)  { return absHigher(key); }
//        NavigableEntry<K,V> subFloor(K key)   { return absFloor(key); }
//        NavigableEntry<K,V> subLower(K key)   { return absLower(key); }
//    }
//
//    /**
//     * @serial include
//     */
//    static final class DescendingSubMap<K,V>  extends NavigableSubMap<K,V> {
//        private static final long serialVersionUID = 912986545866120460L;
//        DescendingSubMap(AbstractNavigableMap<K,V> m,
//                         boolean fromStart, K lo, boolean loInclusive,
//                         boolean toEnd,     K hi, boolean hiInclusive) {
//            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
//        }
//
//        private final Comparator<? super K> reverseComparator =
//                Collections.reverseOrder(m.comparator);
//
//        public Comparator<? super K> comparator() {
//            return reverseComparator;
//        }
//
//        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
//                                        K toKey,   boolean toInclusive) {
//            if (!inRange(fromKey, fromInclusive))
//                throw new IllegalArgumentException("fromKey out of range");
//            if (!inRange(toKey, toInclusive))
//                throw new IllegalArgumentException("toKey out of range");
//            return new DescendingSubMap<>(m,
//                    false, toKey,   toInclusive,
//                    false, fromKey, fromInclusive);
//        }
//
//        public NavigableMap<K,V> headMap(K toKey, boolean inclusive) {
//            if (!inRange(toKey, inclusive))
//                throw new IllegalArgumentException("toKey out of range");
//            return new DescendingSubMap<>(m,
//                    false, toKey, inclusive,
//                    toEnd, hi,    hiInclusive);
//        }
//
//        public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
//            if (!inRange(fromKey, inclusive))
//                throw new IllegalArgumentException("fromKey out of range");
//            return new DescendingSubMap<>(m,
//                    fromStart, lo, loInclusive,
//                    false, fromKey, inclusive);
//        }
//
//        public NavigableMap<K,V> descendingMap() {
//            NavigableMap<K,V> mv = descendingMapView;
//            return (mv != null) ? mv :
//                    (descendingMapView =
//                            new AscendingSubMap<>(m,
//                                    fromStart, lo, loInclusive,
//                                    toEnd,     hi, hiInclusive));
//        }
//
//        Iterator<K> keyIterator() {
//            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
//        }
//
//        Spliterator<K> keySpliterator() {
//            return new DescendingSubMapKeyIterator(absHighest(), absLowFence());
//        }
//
//        Iterator<K> descendingKeyIterator() {
//            return new SubMapKeyIterator(absLowest(), absHighFence());
//        }
//
//        final class DescendingEntrySetView extends EntrySetView {
//            public Iterator<Map.Entry<K,V>> iterator() {
//                return new DescendingSubMapEntryIterator(absHighest(), absLowFence());
//            }
//        }
//
//        public Set<Map.Entry<K,V>> entrySet() {
//            EntrySetView es = entrySetView;
//            return (es != null) ? es : (entrySetView = new DescendingEntrySetView());
//        }
//
//        NavigableEntry<K,V> subLowest()       { return absHighest(); }
//        NavigableEntry<K,V> subHighest()      { return absLowest(); }
//        NavigableEntry<K,V> subCeiling(K key) { return absFloor(key); }
//        NavigableEntry<K,V> subHigher(K key)  { return absLower(key); }
//        NavigableEntry<K,V> subFloor(K key)   { return absCeiling(key); }
//        NavigableEntry<K,V> subLower(K key)   { return absHigher(key); }
//    }

}
