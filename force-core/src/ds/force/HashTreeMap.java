package ds.force;

import ds.force.primitive.IntArrayList;

import java.util.*;
import java.util.function.Consumer;

public class HashTreeMap<K extends Number,V> implements Map<K,V> {

    private static final IntArrayList primes = IntArrayList.of(7,11,13,17,19,23,29,31,37,41);

    transient Entry<K,V> root;

    private int size;

    /**
     * Holds cached entrySet(). Note that AbstractMap fields are used
     * for keySet() and values().
     */
    transient Set<Map.Entry<K,V>> entrySet;

    transient final IntUnaryFunction<K> modFunction;

    transient Set<K> keySet;

    transient Collection<V> values;

    public HashTreeMap() {
        this.root = new Entry<>();
        this.modFunction = null;
    }

    public HashTreeMap(Map<? extends K, ? extends V> m) {
        this();
        putAll(m);
    }

    private static class Entry<K extends Number, V> implements Map.Entry<K,V> {

        K key;

        V value;

        Entry<K, V>[] slots;

        boolean deleted;

        Entry() {

        }

        Entry(K key, V value){
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

        public Entry<K, V>[] getSlots(){
            return slots;
        }

        public boolean alive(){
            return !deleted;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
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
        return getEntry((K) key) != null;
    }

    /**
     *
     * @param key
     * @return
     */
    private Entry<K,V> getEntry(K key) {
        Entry<K,V> entry = this.root;
        int index = 0;
        while (entry != null && entry.slots != null){
            int remainder = remainder(key,primes.get(index));
            Entry<K,V> node = entry.slots[remainder];
            if (node != null && (node.key == key || key.equals(node.key))
                    && !node.deleted) return node;
            entry = node;
            index++;
        }
        return null;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    private int remainder(K key, int prime){
        if (modFunction != null) {
            return modFunction.applyAsInt(key,prime);
        }
        return (int)(key.longValue() % prime);
    }

    @Override
    public V get(Object key) {
        Entry<K,V> entry = getEntry((K) key);
        if (entry != null)
            return entry.value;
        return null;
    }

    @Override
    public V put(K key, V value) {
        Entry<K,V> entry = this.root;
        int index = 0;
        Entry<K,V> target = null;
        while(entry.slots != null){
            int remainder = remainder(key,primes.get(index));
            Entry<K,V> node = entry.slots[remainder];
            if (node == null) {
                entry.slots[remainder] = new Entry<>(key,value);
                return null;
            } else if ((node.key == key || key.equals(node.key))
                    && !node.deleted) {
                return node.setValue(value);
            } else if (node.deleted) target = node;
            entry = node;
            index++;
        }
        if (target != null){
            target.deleted = false;
            size++;
            return target.setValue(value);
        }
        if (index == primes.size()) extendPrimes();
        int prime = primes.get(index);
        entry.slots = new Entry[prime];
        entry.slots[remainder(key,prime)] = new Entry<>(key,value);
        size++;
        return null;
    }

    /**
     * <p>Sieve of Eratosthenes algorithm</p>
     */
    private void extendPrimes(){
        int lastPrime = primes.get(primes.size()-1);
        int size = lastPrime + (lastPrime >>> 1);
        BitSet bitSet = new BitSet(size);
        int half = size >>> 1;
        for(int i = 2; i <= half;i++) {
            if(bitSet.get(i)) {
                continue;
            }
            int count = size / i;
            for( int j = 2; j <= count; ++j) {
                bitSet.set(i * j);
            }
        }
        for (int x = lastPrime; x <= size; x++){
            if(!bitSet.get(x) && x > lastPrime) {
                primes.add(x);
            }
        }
    }

    @Override
    public V remove(Object key) {
        Entry<K,V> entry = getEntry((K) key);
        if (entry != null) {
            entry.deleted = true;
            return entry.value;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(Map.Entry<? extends K, ? extends V> entry : m.entrySet()){
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.root.slots = null;
        this.size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    final class KeySet extends AbstractSet<K> {
        public final int size()                 { return size; }
        public final void clear()               { HashTreeMap.this.clear(); }
        public final Iterator<K> iterator()     { return new KeyIterator(); }
        public final boolean contains(Object o) { return containsKey(o); }
        public final boolean remove(Object key) {
            return HashTreeMap.this.remove(key) != null;
        }
        public final Spliterator<K> spliterator() {
            return new KeySpliterator<>(HashTreeMap.this, 0, -1, 0);
        }
        public final void forEach(Consumer<? super K> action) {
            Entry<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = root.getSlots()) != null) {
                Deque<Entry<K,V>> stack = new ArrayDeque<>(size);
                for (Entry<K,V> entry : tab){
                    if (entry != null)
                        stack.push(entry);
                }
                while (!stack.isEmpty()){
                    Entry<K,V> e = stack.pop();
                    if (e.alive()) action.accept(e.key);
                    if (e.getSlots() == null) {
                        continue;
                    }
                    for (Entry<K,V> entry : e.getSlots()){
                        if (entry != null)
                            stack.push(entry);
                    }
                }
            }
        }
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

    final class Values extends AbstractCollection<V> {
        public final int size()                 { return size; }
        public final void clear()               { HashTreeMap.this.clear(); }
        public final Iterator<V> iterator()     { return new ValueIterator(); }
        public final boolean contains(Object o) { return containsValue(o); }
        public final Spliterator<V> spliterator() {
            return new ValueSpliterator<>(HashTreeMap.this, 0, -1, 0);
        }
        public final void forEach(Consumer<? super V> action) {
            Entry<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = root.getSlots()) != null) {
                Deque<Entry<K,V>> stack = new ArrayDeque<>(size);
                for (Entry<K,V> entry : tab){
                    if (entry != null)
                        stack.push(entry);
                }
                while (!stack.isEmpty()){
                    Entry<K,V> e = stack.pop();
                    if (e.alive()) action.accept(e.value);
                    if (e.getSlots() == null) {
                        continue;
                    }
                    for (Entry<K,V> entry : e.getSlots()){
                        if (entry != null)
                            stack.push(entry);
                    }
                }
            }
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K,V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size()                 { return size; }
        public final void clear()               { HashTreeMap.this.clear(); }
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();
            Entry<K,V> candidate = getEntry((K)key);
            return candidate != null && candidate.equals(e);
        }
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();
                return HashTreeMap.this.remove(key) != null;
            }
            return false;
        }
        public final Spliterator<Map.Entry<K,V>> spliterator() {
            return new EntrySpliterator<>(HashTreeMap.this, 0, -1, 0);
        }
        public final void forEach(Consumer<? super Map.Entry<K,V>> action) {
            Entry<K,V>[] tab;
            if (action == null)
                throw new NullPointerException();
            if (size > 0 && (tab = root.getSlots()) != null) {
                Deque<Entry<K,V>> stack = new ArrayDeque<>(size);
                for (Entry<K,V> entry : tab){
                    if (entry != null)
                        stack.push(entry);
                }
                while (!stack.isEmpty()){
                    Entry<K,V> e = stack.pop();
                    if (e.alive()) action.accept(e);
                    if (e.getSlots() == null) {
                        continue;
                    }
                    for (Entry<K,V> entry : e.getSlots()){
                        if (entry != null)
                            stack.push(entry);
                    }
                }
            }
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class HashIterator {
        Entry<K,V> current;     // current entry
        ArrayDeque<Entry<K,V>> stack;

        HashIterator() {
            Entry<K,V>[] t = root.slots;
            current = null;
            stack = new ArrayDeque<>(size);
            if (t != null && size > 0) { // advance to first entry
                for (Entry<K,V> entry : t){
                    if (entry != null)
                        stack.push(entry);
                }
            }
        }

        public final boolean hasNext() {
            return stack.isEmpty() || stack.peek().alive();
        }

        final Entry<K,V> nextNode() {
            Entry<K,V> e = stack.pop();
            if (e.getSlots() != null) {
                for (Entry<K,V> entry : e.getSlots()){
                    if (entry != null)
                        stack.push(entry);
                }
            }
            return e;
        }

        final Entry<K,V> nextAliveNode() {
            Entry<K,V> e = nextNode();
            while (!e.alive()) {
                e = nextNode();
            }
            current = e;
            return e;
        }

        public final void remove() {
            Entry<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            current = null;
            K key = p.key;
            HashTreeMap.this.remove(key);
        }
    }

    final class KeyIterator extends HashIterator
            implements Iterator<K> {
        public final K next() { return nextAliveNode().key; }
    }

    final class ValueIterator extends HashIterator
            implements Iterator<V> {
        public final V next() { return nextAliveNode().value; }
    }

    final class EntryIterator extends HashIterator
            implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextAliveNode(); }
    }

    /* ------------------------------------------------------------ */
    // spliterators

    static class HashTreeMapSpliterator<K extends Number,V> {
        final HashTreeMap<K,V> map;
        Entry<K,V> current;          // current node
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate

        HashTreeMapSpliterator(HashTreeMap<K,V> m, int origin,
                           int fence, int est) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
        }

        final int getFence() { // initialize fence and size on first use
            int hi;
            if ((hi = fence) < 0) {
                HashTreeMap<K,V> m = map;
                est = m.size;
                Entry<K,V>[] tab = m.root.getSlots();
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            return hi;
        }

        public final long estimateSize() {
            getFence(); // force init
            return (long) est;
        }
    }

    static final class KeySpliterator<K extends Number,V>
            extends HashTreeMapSpliterator<K,V>
            implements Spliterator<K> {
        KeySpliterator(HashTreeMap<K,V> m, int origin, int fence, int est) {
            super(m, origin, fence, est);
        }

        public KeySpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                    new KeySpliterator<>(map, lo, index = mid, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super K> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashTreeMap<K,V> m = map;
            Entry<K,V>[] tab = m.root.getSlots();
            if ((hi = fence) < 0) {
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
            if (tab != null && tab.length >= hi &&
                    (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Entry<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.key);
//                        p = p.next;
                    }
                } while (p != null || i < hi);
            }
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Entry<K,V>[] tab = map.root.getSlots();
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        K k = current.key;
//                        current = current.next;
                        action.accept(k);
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT;
        }
    }

    static final class ValueSpliterator<K extends Number,V>
            extends HashTreeMapSpliterator<K,V>
            implements Spliterator<V> {
        ValueSpliterator(HashTreeMap<K,V> m, int origin, int fence, int est) {
            super(m, origin, fence, est);
        }

        public ValueSpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                    new ValueSpliterator<>(map, lo, index = mid, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashTreeMap<K,V> m = map;
            Entry<K,V>[] tab = m.root.getSlots();
            if ((hi = fence) < 0) {
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
            if (tab != null && tab.length >= hi &&
                    (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Entry<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p.value);
//                        p = p.next;
                    }
                } while (p != null || i < hi);
            }
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Entry<K,V>[] tab = map.root.getSlots();
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        V v = current.value;
//                        current = current.next;
                        action.accept(v);
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0);
        }
    }

    static final class EntrySpliterator<K extends Number,V>
            extends HashTreeMapSpliterator<K,V>
            implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(HashTreeMap<K,V> m, int origin, int fence, int est) {
            super(m, origin, fence, est);
        }

        public EntrySpliterator<K,V> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || current != null) ? null :
                    new EntrySpliterator<>(map, lo, index = mid, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K,V>> action) {
            int i, hi, mc;
            if (action == null)
                throw new NullPointerException();
            HashTreeMap<K,V> m = map;
            Entry<K,V>[] tab = m.root.getSlots();
            if ((hi = fence) < 0) {
                hi = fence = (tab == null) ? 0 : tab.length;
            }
            else
            if (tab != null && tab.length >= hi &&
                    (i = index) >= 0 && (i < (index = hi) || current != null)) {
                Entry<K,V> p = current;
                current = null;
                do {
                    if (p == null)
                        p = tab[i++];
                    else {
                        action.accept(p);
//                        p = p.next;
                    }
                } while (p != null || i < hi);
            }
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K,V>> action) {
            int hi;
            if (action == null)
                throw new NullPointerException();
            Entry<K,V>[] tab = map.root.getSlots();
            if (tab != null && tab.length >= (hi = getFence()) && index >= 0) {
                while (current != null || index < hi) {
                    if (current == null)
                        current = tab[index++];
                    else {
                        Entry<K,V> e = current;
//                        current = current.next;
                        action.accept(e);
                        return true;
                    }
                }
            }
            return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size ? Spliterator.SIZED : 0) |
                    Spliterator.DISTINCT;
        }
    }
}
