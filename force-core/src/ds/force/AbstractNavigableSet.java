package ds.force;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedSet;

public abstract class AbstractNavigableSet<E> extends AbstractSet<E> implements NavigableSet<E> {

    /**
     * The backing map.
     */
    protected transient NavigableMap<E,Object> m;

    // Dummy value to associate with an Object in the backing Map
    protected static final Object PRESENT = new Object();


    @Override
    public boolean add(E e) {
        return m.put(e,PRESENT) == null;
    }

    @Override
    public boolean contains(Object o) {
        return m.containsKey(o);
    }

    @Override
    public boolean remove(Object o) {
        return m.remove(o)==PRESENT;
    }

    /**
     * Removes all of the elements from this set.
     * The set will be empty after this call returns.
     */
    public void clear() {
        m.clear();
    }

    @Override
    public E lower(E e) {
        return m.lowerKey(e);
    }

    @Override
    public E floor(E e) {
        return m.floorKey(e);
    }

    @Override
    public E ceiling(E e) {
        return m.ceilingKey(e);
    }

    @Override
    public E higher(E e) {
        return m.higherKey(e);
    }

    @Override
    public E pollFirst() {
        Map.Entry<E,?> e = m.pollFirstEntry();
        return (e == null) ? null : e.getKey();
    }

    @Override
    public E pollLast() {
        Map.Entry<E,?> e = m.pollLastEntry();
        return (e == null) ? null : e.getKey();
    }

    @Override
    public Iterator<E> iterator() {
        return m.navigableKeySet().iterator();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return m.descendingKeySet();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return m.descendingKeySet().iterator();
    }

    @Override
    public Comparator<? super E> comparator() {
        return m.comparator();
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        return m.firstKey();
    }

    @Override
    public E last() {
        return m.lastKey();
    }

    @Override
    public int size() {
        return m.size();
    }
}
