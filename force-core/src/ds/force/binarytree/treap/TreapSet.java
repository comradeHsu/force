package ds.force.binarytree.treap;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;

public class TreapSet<E> extends AbstractTreapSet<E> {

    /**
     * Constructs a set backed by the specified navigable map.
     */
    TreapSet(NavigableMap<E,Object> m) {
        this.m = m;
    }

    public TreapSet() {
        this(new TreapMap<>());
    }

    public TreapSet(Comparator<? super E> comparator) {
        this(new TreapMap<>(comparator));
    }

    public TreapSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public TreapSet(SortedSet<E> s) {
        this(s.comparator());
        addAll(s);
    }

    public TreapSet<E> split(E element) {
        return new TreapSet<>(((TreapMap<E,Object>)m).split(element));
    }

    public int getSequence(E element){
        return ((TreapMap<E,Object>)m).getSequence(element);
    }

    public E get(int ranking){
        Map.Entry<E,Object> entry = ((TreapMap<E,Object>)m).getEntry(ranking);
        return entry == null ? null : entry.getKey();
    }
}
