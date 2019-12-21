package ds.force;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ToIntBiFunction;

public abstract class AbstractNavigableMap<K,V> implements NavigableMap<K,V> {

    /**
     * The comparator used to maintain order in this map, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    protected final Comparator<? super K> comparator;

    public AbstractNavigableMap(){
        this.comparator = null;
    }

    public AbstractNavigableMap(Comparator<? super K> comparator){
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    protected ToIntBiFunction<K,K> toIntBiFunction(){
        if (comparator != null) return comparator::compare;
        return (k1,k2) -> ((Comparable<? super K>)k1).compareTo(k2);
    }

    protected abstract NavigableEntry<K,V> getEntry(K key);

    protected abstract NavigableEntry<K,V> successor(NavigableEntry<K, V> entry);

    protected abstract NavigableEntry<K,V> predecessor(NavigableEntry<K,V> entry);
    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the least key greater than the specified
     * key; if no such entry exists (i.e., the greatest key in the Tree is less
     * than the specified key), returns {@code null}.
     */
    protected abstract NavigableEntry<K,V> getCeilingEntry(K key);

    /**
     * Gets the entry corresponding to the specified key; if no such entry
     * exists, returns the entry for the greatest key less than the specified
     * key; if no such entry exists, returns {@code null}.
     */
    protected abstract NavigableEntry<K,V> getFloorEntry(K key);

    /**
     * Gets the entry for the least key greater than the specified
     * key; if no such entry exists, returns the entry for the least
     * key greater than the specified key; if no such entry exists
     * returns {@code null}.
     */
    protected abstract NavigableEntry<K,V> getHigherEntry(K key);

    /**
     * Returns the entry for the greatest key less than the specified key; if
     * no such entry exists (i.e., the least key in the Tree is greater than
     * the specified key), returns {@code null}.
     */
    protected abstract NavigableEntry<K,V> getLowerEntry(K key);

    protected abstract static class NavigableEntry<K,V> implements Map.Entry<K,V> {

        public K key;

        public V value;

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


    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Entry<? extends K, ? extends V> entry : map.entrySet()){
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public NavigableEntry<K, V> lowerEntry(K key) {
        return getLowerEntry(key);
    }

    @Override
    public K lowerKey(K key) {
        NavigableEntry<K,V> entry = lowerEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NavigableEntry<K, V> floorEntry(K key) {
        return getFloorEntry(key);
    }

    @Override
    public K floorKey(K key) {
        NavigableEntry<K,V> entry = floorEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NavigableEntry<K, V> ceilingEntry(K key) {
        return getCeilingEntry(key);
    }

    @Override
    public K ceilingKey(K key) {
        NavigableEntry<K,V> entry = ceilingEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public NavigableEntry<K, V> higherEntry(K key) {
        return getHigherEntry(key);
    }

    @Override
    public K higherKey(K key) {
        NavigableEntry<K,V> entry = higherEntry(key);
        return entry != null ? entry.key : null;
    }

    @Override
    public abstract NavigableEntry<K, V> firstEntry();

    @Override
    public abstract NavigableEntry<K, V> lastEntry();

    @Override
    public Entry<K, V> pollFirstEntry() {
        Entry<K,V> entry = firstEntry();
        if (entry != null) remove(entry.getKey());
        return entry;
    }

    @Override
    public Entry<K, V> pollLastEntry() {
        Entry<K,V> entry = lastEntry();
        if (entry != null) remove(entry.getKey());
        return entry;
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
    public NavigableMap<K, V> descendingMap() {
        NavigableMap<K, V> km = descendingMap;
        return (km != null) ? km :
                (descendingMap = new DescendingSubMap<>(this,
                        true, null, true,
                        true, null, true));
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

    int compare(K key, K entryKey){
       if (comparator != null) return comparator.compare(key,entryKey);
       return ((Comparable<? super K>)key).compareTo(entryKey);
    }

    /**
     * Return SimpleImmutableEntry for entry, or null if null
     */
    static <K,V> Map.Entry<K,V> exportEntry(NavigableEntry<K,V> e) {
        return (e == null) ? null :
                new AbstractMap.SimpleImmutableEntry<>(e);
    }

    /**
     * Return key for entry, or null if null
     */
    static <K,V> K keyOrNull(NavigableEntry<K,V> e) {
        return (e == null) ? null : e.key;
    }

    /**
     * Returns the key corresponding to the specified Entry.
     * @throws NoSuchElementException if the Entry is null
     */
    static <K> K key(NavigableEntry<K,?> e) {
        if (e==null)
            throw new NoSuchElementException();
        return e.key;
    }

    // Views

    /**
     * Fields initialized to contain an instance of the entry set view
     * the first time this view is requested.  Views are stateless, so
     * there's no reason to create more than one.
     */
    protected transient EntrySet entrySet;
    protected transient KeySet<K> navigableKeySet;
    protected transient NavigableMap<K,V> descendingMap;
    protected transient Collection<V> values;

    abstract class EntrySet<E> extends AbstractSet<Map.Entry<K,V>> {
        public abstract Iterator<Map.Entry<K,V>> iterator();

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            V p = get(entry.getKey());
            return p != null && Objects.equals(p,value);
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object value = entry.getValue();
            V p = get(entry.getKey());
            if (p != null && Objects.equals(p, value)) {
                AbstractNavigableMap.this.remove(entry.getKey());
                return true;
            }
            return false;
        }

        public int size() {
            return AbstractNavigableMap.this.size();
        }

        public void clear() {
            AbstractNavigableMap.this.clear();
        }

        public abstract Spliterator<Map.Entry<K,V>> spliterator();
    }

    /*
     * Unlike Values and EntrySet, the KeySet class is static,
     * delegating to a NavigableMap to allow use by SubMaps, which
     * outweighs the ugliness of needing type-tests for the following
     * Iterator methods that are defined appropriately in main versus
     * submap classes.
     */

    protected abstract Iterator<K> keyIterator();

    protected abstract Iterator<K> descendingKeyIterator();

    static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> m;
        KeySet(NavigableMap<E,?> map) { m = map; }

        public Iterator<E> iterator() {
            if (m instanceof AbstractNavigableMap)
                return ((AbstractNavigableMap<E,?>)m).keyIterator();
            else
                return ((AbstractNavigableMap.NavigableSubMap<E,?>)m).keyIterator();
        }

        public Iterator<E> descendingIterator() {
            if (m instanceof AbstractNavigableMap)
                return ((AbstractNavigableMap<E,?>)m).descendingKeyIterator();
            else
                return ((AbstractNavigableMap.NavigableSubMap<E,?>)m).descendingKeyIterator();
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
        // wait implement
        public Spliterator<E> spliterator() {
            throw new UnsupportedOperationException();
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
        final AbstractNavigableMap<K,V> m;

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

        NavigableSubMap(AbstractNavigableMap<K,V> m,
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

        final NavigableEntry<K,V> absLowest() {
            NavigableEntry<K,V> e =
                    (fromStart ?  m.firstEntry() :
                            (loInclusive ? m.ceilingEntry(lo) :
                                    m.higherEntry(lo)));
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final NavigableEntry<K,V> absHighest() {
            NavigableEntry<K,V> e =
                    (toEnd ?  m.lastEntry() :
                            (hiInclusive ?  m.floorEntry(hi) :
                                    m.lowerEntry(hi)));
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final NavigableEntry<K,V> absCeiling(K key) {
            if (tooLow(key))
                return absLowest();
            NavigableEntry<K,V> e = m.ceilingEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final NavigableEntry<K,V> absHigher(K key) {
            if (tooLow(key))
                return absLowest();
            NavigableEntry<K,V> e = m.higherEntry(key);
            return (e == null || tooHigh(e.key)) ? null : e;
        }

        final NavigableEntry<K,V> absFloor(K key) {
            if (tooHigh(key))
                return absHighest();
            NavigableEntry<K,V> e = m.floorEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        final NavigableEntry<K,V> absLower(K key) {
            if (tooHigh(key))
                return absHighest();
            NavigableEntry<K,V> e = m.lowerEntry(key);
            return (e == null || tooLow(e.key)) ? null : e;
        }

        /** Returns the absolute high fence for ascending traversal */
        final NavigableEntry<K,V> absHighFence() {
            return (toEnd ? null : (hiInclusive ?
                    m.higherEntry(hi) :
                    m.ceilingEntry(hi)));
        }

        /** Return the absolute low fence for descending traversal  */
        final NavigableEntry<K,V> absLowFence() {
            return (fromStart ? null : (loInclusive ?
                    m.lowerEntry(lo) :
                    m.floorEntry(lo)));
        }

        // Abstract methods defined in ascending vs descending classes
        // These relay to the appropriate absolute versions

        abstract NavigableEntry<K,V> subLowest();
        abstract NavigableEntry<K,V> subHighest();
        abstract NavigableEntry<K,V> subCeiling(K key);
        abstract NavigableEntry<K,V> subHigher(K key);
        abstract NavigableEntry<K,V> subFloor(K key);
        abstract NavigableEntry<K,V> subLower(K key);

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
            NavigableEntry<K,V> e = subLowest();
            Map.Entry<K,V> result = exportEntry(e);
            if (e != null)
                m.remove(e.key);
            return result;
        }

        public final Map.Entry<K,V> pollLastEntry() {
            NavigableEntry<K,V> e = subHighest();
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
                    (navigableKeySetView = new KeySet<>(this));
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
                NavigableEntry<K,V> n = absLowest();
                return n == null || tooHigh(n.key);
            }

            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                NavigableEntry<?,?> node = m.getEntry((K)key);
                return node != null &&
                        Objects.equals(node.getValue(), entry.getValue());
            }

            public boolean remove(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
                Object key = entry.getKey();
                if (!inRange(key))
                    return false;
                NavigableEntry<K,V> node = m.getEntry((K)key);
                if (node!=null && Objects.equals(node.getValue(),
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
            NavigableEntry<K,V> lastReturned;
            NavigableEntry<K,V> next;
            final Object fenceKey;

            SubMapIterator(NavigableEntry<K,V> first,
                           NavigableEntry<K,V> fence) {
                lastReturned = null;
                next = first;
                fenceKey = fence == null ? UNBOUNDED : fence.key;
            }

            public final boolean hasNext() {
                return next != null && next.key != fenceKey;
            }

            final NavigableEntry<K,V> nextEntry() {
                NavigableEntry<K,V> e = next;
                if (e == null || e.key == fenceKey)
                    throw new NoSuchElementException();
                next = m.successor(e);
                lastReturned = e;
                return e;
            }

            final NavigableEntry<K,V> prevEntry() {
                NavigableEntry<K,V> e = next;
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
            SubMapEntryIterator(NavigableEntry<K,V> first,
                                NavigableEntry<K,V> fence) {
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
            DescendingSubMapEntryIterator(NavigableEntry<K,V> last,
                                          NavigableEntry<K,V> fence) {
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
            SubMapKeyIterator(NavigableEntry<K,V> first,
                              NavigableEntry<K,V> fence) {
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
            DescendingSubMapKeyIterator(NavigableEntry<K,V> last,
                                        NavigableEntry<K,V> fence) {
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

        AscendingSubMap(AbstractNavigableMap<K,V> m,
                        boolean fromStart, K lo, boolean loInclusive,
                        boolean toEnd,     K hi, boolean hiInclusive) {
            super(m, fromStart, lo, loInclusive, toEnd, hi, hiInclusive);
        }

        public Comparator<? super K> comparator() {
            return m.comparator();
        }

        public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                        K toKey, boolean toInclusive) {
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

        NavigableEntry<K,V> subLowest()       { return absLowest(); }
        NavigableEntry<K,V> subHighest()      { return absHighest(); }
        NavigableEntry<K,V> subCeiling(K key) { return absCeiling(key); }
        NavigableEntry<K,V> subHigher(K key)  { return absHigher(key); }
        NavigableEntry<K,V> subFloor(K key)   { return absFloor(key); }
        NavigableEntry<K,V> subLower(K key)   { return absLower(key); }
    }

    /**
     * @serial include
     */
    static final class DescendingSubMap<K,V>  extends NavigableSubMap<K,V> {
        private static final long serialVersionUID = 912986545866120460L;
        DescendingSubMap(AbstractNavigableMap<K,V> m,
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

        NavigableEntry<K,V> subLowest()       { return absHighest(); }
        NavigableEntry<K,V> subHighest()      { return absLowest(); }
        NavigableEntry<K,V> subCeiling(K key) { return absFloor(key); }
        NavigableEntry<K,V> subHigher(K key)  { return absLower(key); }
        NavigableEntry<K,V> subFloor(K key)   { return absCeiling(key); }
        NavigableEntry<K,V> subLower(K key)   { return absHigher(key); }
    }
}
