package ds.force.binarytree;

import ds.force.AbstractNavigableSet;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

public class SplayTreeSet<E> extends AbstractNavigableSet<E> {

    /**
     * Constructs a set backed by the specified navigable map.
     */
    SplayTreeSet(NavigableMap<E,Object> m) {
        this.m = m;
    }

    public SplayTreeSet() {
        this(new SplayTreeMap<>());
    }

    public SplayTreeSet(Comparator<? super E> comparator) {
        this(new SplayTreeMap<>(comparator));
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return new SplayTreeSet<>(m.subMap(fromElement, fromInclusive,
                toElement,   toInclusive));
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        return new SplayTreeSet<>(m.headMap(toElement, inclusive));
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new SplayTreeSet<>(m.tailMap(fromElement, inclusive));
    }

    public SplayTreeSet<E> split(E element) {
        return new SplayTreeSet<>(((SplayTreeMap<E,Object>)m).split(element));
    }

    public int getSequence(E element){
        return ((SplayTreeMap<E,Object>)m).getSequence(element);
    }

    public E get(int ranking){
        Map.Entry<E,Object> entry = ((SplayTreeMap<E,Object>)m).getEntry(ranking);
        return entry == null ? null : entry.getKey();
    }
}
