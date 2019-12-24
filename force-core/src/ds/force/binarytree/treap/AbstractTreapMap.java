package ds.force.binarytree.treap;

import ds.force.AbstractNavigableMap;

import java.util.AbstractCollection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;

public abstract class AbstractTreapMap<K,V> extends AbstractNavigableMap<K,V> {

    public AbstractTreapMap(){
        super();
    }

    public AbstractTreapMap(Comparator<? super K> comparator){
        super(comparator);
    }

    /**
     * Returns the first Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    protected final AbstractEntry<K,V> getFirstEntry() {
        return getFirstEntry(getRoot());
    }

    protected final AbstractEntry<K,V> getFirstEntry(AbstractEntry<K,V> entry) {
        AbstractEntry<K,V> p = entry;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    /**
     * Returns the last Entry in the TreeMap (according to the TreeMap's
     * key-sort function).  Returns null if the TreeMap is empty.
     */
    protected final AbstractEntry<K,V> getLastEntry() {
        return getLastEntry(getRoot());
    }

    protected final AbstractEntry<K,V> getLastEntry(AbstractEntry<K,V> entry) {
        AbstractEntry<K,V> p = entry;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    protected abstract AbstractEntry<K,V> getRoot();

    private void deleteEntry(Map.Entry<K,V> entry){
        remove(entry.getKey());
    }

    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with {@code null} o1 properly.
     */
    static final boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

    static abstract class AbstractEntry<K,V> extends AbstractNavigableMap.NavigableEntry<K,V> {

        int priority;

        AbstractEntry<K,V> left,right;

        AbstractEntry(K key, V value, int priority){
            this.key = key;
            this.value = value;
            this.priority = priority;
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

    class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator(getFirstEntry());
        }

        public int size() {
            return AbstractTreapMap.this.size();
        }

        public boolean contains(Object o) {
            return AbstractTreapMap.this.containsValue(o);
        }

        public boolean remove(Object o) {
            for (NavigableEntry<K,V> e = getFirstEntry(); e != null; e = successor(e)) {
                if (valEquals(e.getValue(), o)) {
                    deleteEntry(e);
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            AbstractTreapMap.this.clear();
        }
    }

    class EntrySet extends AbstractNavigableMap.EntrySet {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator(getFirstEntry());
        }

        @Override
        public Spliterator<Entry> spliterator() {
            throw new UnsupportedOperationException();
        }

    }

    protected Iterator<K> keyIterator() {
        return new KeyIterator(getFirstEntry());
    }

    protected Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(getLastEntry());
    }

    /**
     * Base class for TreeMap Iterators
     */
    abstract class TreapEntryIterator<T> implements Iterator<T> {
        AbstractEntry<K,V> next;
        AbstractEntry<K,V> lastReturned;

        TreapEntryIterator(AbstractEntry<K,V> first) {
            lastReturned = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final AbstractEntry<K,V> nextEntry() {
            AbstractEntry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = (AbstractEntry<K, V>) successor(e);
            lastReturned = e;
            return e;
        }

        final AbstractEntry<K,V> prevEntry() {
            AbstractEntry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = (AbstractEntry<K, V>) predecessor(e);
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
        EntryIterator(AbstractEntry<K,V> first) {
            super(first);
        }
        public AbstractEntry<K,V> next() {
            return nextEntry();
        }
    }

    final class ValueIterator extends TreapEntryIterator<V> {
        ValueIterator(AbstractEntry<K,V> first) {
            super(first);
        }
        public V next() {
            return nextEntry().value;
        }
    }

    final class KeyIterator extends TreapEntryIterator<K> {
        KeyIterator(AbstractEntry<K,V> first) {
            super(first);
        }
        public K next() {
            return nextEntry().key;
        }
    }

    final class DescendingKeyIterator extends TreapEntryIterator<K> {
        DescendingKeyIterator(AbstractEntry<K,V> first) {
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
}
