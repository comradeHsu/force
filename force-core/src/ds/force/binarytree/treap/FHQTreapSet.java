package ds.force.binarytree.treap;

import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.SortedSet;

public class FHQTreapSet<E> extends AbstractTreapSet<E> {

    /**
     * Constructs a set backed by the specified navigable map.
     */
    FHQTreapSet(NavigableMap<E,Object> m) {
        this.m = m;
    }

    public FHQTreapSet() {
        this(new TreapMap<>());
    }

    public FHQTreapSet(Comparator<? super E> comparator) {
        this(new TreapMap<>(comparator));
    }

    public FHQTreapSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    public FHQTreapSet(SortedSet<E> s) {
        this(s.comparator());
        addAll(s);
    }
}
