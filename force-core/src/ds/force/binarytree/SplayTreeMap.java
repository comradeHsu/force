package ds.force.binarytree;

import ds.force.AbstractNavigableMap;
import ds.force.BTreeMap;

import java.util.*;
import java.util.function.ToIntBiFunction;

public class SplayTreeMap<K,V> extends AbstractNavigableMap<K,V> {

    transient SplayEntry<K,V> root;

    @Override
    public int size() {
        return root == null ? 0 : root.size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean containsKey(Object key) {
        SplayEntry<K,V> entry = getEntry((K)key);
        return entry != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K,V> e : entrySet())
            if (Objects.equals(value, e.getValue()))
                return true;
        return false;
    }

    @Override
    public V get(Object key) {
        SplayEntry<K,V> entry = getEntry((K)key);
        V value = null;
        if (entry != null){
            splay(entry);
            value = entry.value;
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
//        if (key == null)
//            throw new NullPointerException();
        SplayEntry<K,V> t = root;
        if (t == null){
            root = new SplayEntry<>(key, value);
            return null;
        }
        SplayEntry<K,V> parent;
        int cmp;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        do {
            parent = t;
            cmp = compare.applyAsInt(key,t.key);
            if (cmp < 0)
                t =  t.left;
            else if (cmp > 0)
                t =  t.right;
            else {
                splay(t);
                return t.setValue(value);
            }
        } while (t != null);
        SplayEntry<K,V> e = new SplayEntry<>(key,value,parent);
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        splay(e);
        return null;
    }

    @Override
    public V remove(Object key) {
        SplayEntry<K,V> entry = getEntry((K)key);
        if (entry == null) return null;
        splay(entry);
        SplayEntry<K,V> preEntry = predecessor(root);
        if (preEntry == null){
            V value = this.root.value;
            this.root = root.right;
            if (root != null) root.parent = null;
            return value;
        }
        SplayEntry<K,V> parent,grandfather;
        while (preEntry.parent != root.left && preEntry != root.left){
            parent = preEntry.parent;
            grandfather = parent.parent;
            if (parent == leftOf(grandfather) && preEntry == leftOf(parent)){
                rotateRight(parent);
                rotateRight(preEntry);
            }
            else if (parent == rightOf(grandfather) && preEntry == rightOf(parent)){
                rotateLeft(parent);
                rotateLeft(preEntry);
            }
            else if (parent == rightOf(grandfather) && preEntry == leftOf(parent)){
                rotateRight(preEntry);
                rotateLeft(preEntry);
            }
            else {
                rotateLeft(preEntry);
                rotateRight(preEntry);
            }
        }
        if (preEntry != root.left) {
            if (preEntry == leftOf(preEntry.parent))
                rotateRight(preEntry);
            else if (preEntry == rightOf(preEntry.parent))
                rotateLeft(preEntry);
        }
        preEntry.right = root.right;
        if (root.right != null) {
            root.right.parent = preEntry;
        }
        updateSize(preEntry);
        V value = this.root.value;
        this.root = preEntry;
        preEntry.parent = null;
        return value;
    }

    @Override
    protected SplayEntry<K, V> getEntry(K key) {
        if (key == null)
            throw new NullPointerException();
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        SplayEntry<K,V> node = root;
        int cmp;
        do {
            cmp = compare.applyAsInt(key,node.key);
            if (cmp > 0){
                node = node.right;
            } else if (cmp < 0){
                node = node.left;
            } else {
                return node;
            }
        } while (node != null);
        return null;
    }

    @Override
    protected SplayEntry<K, V> successor(NavigableEntry<K, V> t) {
        SplayEntry<K,V> entry = (SplayEntry<K, V>) t;
        if (entry == null)
            return null;
        else if (entry.right != null) {
            SplayEntry<K,V> p = entry.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            SplayEntry<K,V> p = entry.parent;
            SplayEntry<K,V> ch = entry;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    @Override
    protected SplayEntry<K, V> predecessor(NavigableEntry<K, V> t) {
        SplayEntry<K,V> entry = (SplayEntry<K, V>) t;
        if (entry == null)
            return null;
        else if (entry.left != null) {
            SplayEntry<K,V> p = entry.left;
            while (p.right != null)
                p = p.right;
            return p;
        } else {
            SplayEntry<K,V> p = entry.parent;
            SplayEntry<K,V> ch = entry;
            while (p != null && ch == p.left) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    @Override
    protected NavigableEntry<K, V> getCeilingEntry(K key) {
        SplayEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else if (cmp > 0) {
                if (p.right != null) {
                    p = p.right;
                } else {
                    SplayEntry<K,V> parent = p.parent;
                    SplayEntry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;
        }
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getFloorEntry(K key) {
        SplayEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else if (cmp < 0) {
                if (p.left != null) {
                    p = p.left;
                } else {
                    SplayEntry<K,V> parent = p.parent;
                    SplayEntry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            } else
                return p;

        }
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getHigherEntry(K key) {
        SplayEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp < 0) {
                if (p.left != null)
                    p = p.left;
                else
                    return p;
            } else {
                if (p.right != null) {
                    p = p.right;
                } else {
                    SplayEntry<K,V> parent = p.parent;
                    SplayEntry<K,V> ch = p;
                    while (parent != null && ch == parent.right) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    @Override
    protected NavigableEntry<K, V> getLowerEntry(K key) {
        SplayEntry<K,V> p = root;
        ToIntBiFunction<K,K> compare = toIntBiFunction();
        while (p != null) {
            int cmp = compare.applyAsInt(key, p.key);
            if (cmp > 0) {
                if (p.right != null)
                    p = p.right;
                else
                    return p;
            } else {
                if (p.left != null) {
                    p = p.left;
                } else {
                    SplayEntry<K,V> parent = p.parent;
                    SplayEntry<K,V> ch = p;
                    while (parent != null && ch == parent.left) {
                        ch = parent;
                        parent = parent.parent;
                    }
                    return parent;
                }
            }
        }
        return null;
    }

    @Override
    public final SplayEntry<K, V> firstEntry() {
        SplayEntry<K,V> p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }

    @Override
    public final SplayEntry<K, V> lastEntry() {
        SplayEntry<K,V> p = root;
        if (p != null)
            while (p.right != null)
                p = p.right;
        return p;
    }

    @Override
    protected Iterator<K> keyIterator() {
        return new KeyIterator(firstEntry());
    }

    @Override
    protected Iterator<K> descendingKeyIterator() {
        return new DescendingKeyIterator(lastEntry());
    }

    @Override
    public void clear() {
        this.root = null;
    }

    /** From CLR */
    private void rotateLeft(SplayEntry<K,V> p) {
        if (p != null) {
            SplayEntry<K,V> parent =  p.parent;
            parent.right = p.left;
            if (p.left != null)
                p.left.parent = parent;
            p.parent = parent.parent;
            if (parent.parent == null)
                root = p;
            else if (parent.parent.left == parent)
                parent.parent.left = p;
            else
                parent.parent.right = p;
            p.left = parent;
            parent.parent = p;
            updateSize(parent);
            updateSize(p);
        }
    }

    /** From CLR */
    private void rotateRight(SplayEntry<K,V> p) {
        if (p != null) {
            SplayEntry<K,V> parent =  p.parent;
            parent.left = p.right;
            if (p.right != null) p.right.parent = parent;
            p.parent = parent.parent;
            if (p.parent == null)
                root = p;
            else if (parent.parent.right == parent)
                parent.parent.right = p;
            else parent.parent.left = p;
            p.right = parent;
            parent.parent = p;
            updateSize(parent);
            updateSize(p);
        }
    }

    private void updateSize(SplayEntry<K,V> entry){
        entry.size = sizeOf(entry.left)+sizeOf(entry.right)+1;
    }

    final void splay(SplayEntry<K,V> entry){
        SplayEntry<K,V> parent,grandfather;
        while (entry.parent != root && entry != root){
            parent = entry.parent;
            grandfather = parent.parent;
            if (parent == leftOf(grandfather) && entry == leftOf(parent)){
                rotateRight(parent);
                rotateRight(entry);
            }
            else if (parent == rightOf(grandfather) && entry == rightOf(parent)){
                rotateLeft(parent);
                rotateLeft(entry);
            }
            else if (parent == rightOf(grandfather) && entry == leftOf(parent)){
                rotateRight(entry);
                rotateLeft(entry);
            }
            else {
                rotateLeft(entry);
                rotateRight(entry);
            }
        }
        if (entry == root) return;
        if (entry == leftOf(entry.parent))
            rotateRight(entry);
        else if (entry == rightOf(entry.parent))
            rotateLeft(entry);
    }

    private static class SplayEntry<K,V> extends AbstractNavigableMap.NavigableEntry<K,V> {

        int size;

        SplayEntry<K,V> left, right, parent;

        SplayEntry(K key, V value){
            this.key = key;
            this.value = value;
            this.size = 1;
        }

        SplayEntry(K key, V value, SplayEntry<K,V> parent){
            this.key = key;
            this.value = value;
            this.parent = parent;
            this.size = 1;
        }

    }

    @Override
    public K firstKey() {
        SplayEntry<K,V> entry = firstEntry();
        return entry == null ? null : entry.key;
    }

    @Override
    public K lastKey() {
        SplayEntry<K,V> entry = lastEntry();
        return entry == null ? null : entry.key;
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

    class Values extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return new ValueIterator(firstEntry());
        }

        public int size() {
            return SplayTreeMap.this.size();
        }

        public boolean contains(Object o) {
            return SplayTreeMap.this.containsValue(o);
        }

        public boolean remove(Object o) {
            for (NavigableEntry<K,V> e = firstEntry(); e != null; e = successor(e)) {
                if (Objects.equals(e.getValue(), o)) {
                    SplayTreeMap.this.remove(e.getKey());
                    return true;
                }
            }
            return false;
        }

        public void clear() {
            SplayTreeMap.this.clear();
        }

        public Spliterator<V> spliterator() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        AbstractNavigableMap.EntrySet es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }

    class EntrySet extends AbstractNavigableMap.EntrySet {
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator(firstEntry());
        }

        public Spliterator<Map.Entry<K,V>> spliterator() {
            throw new UnsupportedOperationException();
        }
    }

    static final <K,V> SplayEntry<K, V> leftOf(SplayEntry<K,V> entry){
        return entry.left;
    }

    static final <K,V> SplayEntry<K, V> rightOf(SplayEntry<K,V> entry){
        return entry.right;
    }

    static final <K,V> int sizeOf(SplayEntry<K,V> entry){
        return entry == null ? 0 : entry.size;
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class SplayIterator {
        SplayEntry<K,V> current;     // current entry
        SplayEntry<K,V> next;

        SplayIterator(SplayEntry<K,V> first) {
            current = null;
            next = first;
        }

        public final boolean hasNext() {
            return next != null;
        }

        @SuppressWarnings("unchecked")
        final SplayEntry<K,V> nextEntry() {
            SplayEntry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = successor(e);
            current = e;
            return e;
        }

        final SplayEntry<K,V> prevEntry() {
            SplayEntry<K,V> e = next;
            if (e == null)
                throw new NoSuchElementException();
            next = predecessor(e);
            current = e;
            return e;
        }

        public final void remove() {
            SplayEntry<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            current = null;
            K key = p.key;
            SplayTreeMap.this.remove(key);
        }
    }

    final class KeyIterator extends SplayIterator
            implements Iterator<K> {
        KeyIterator(SplayEntry<K, V> first) {
            super(first);
        }

        public final K next() { return nextEntry().key; }
    }

    final class ValueIterator extends SplayIterator
            implements Iterator<V> {
        ValueIterator(SplayEntry<K, V> first) {
            super(first);
        }

        public final V next() { return nextEntry().value; }
    }

    final class EntryIterator extends SplayIterator
            implements Iterator<Map.Entry<K,V>> {
        EntryIterator(SplayEntry<K, V> first) {
            super(first);
        }

        public final Map.Entry<K,V> next() { return nextEntry(); }
    }

    final class DescendingKeyIterator extends SplayIterator
            implements Iterator<K> {
        DescendingKeyIterator(SplayEntry<K, V> first) {
            super(first);
        }

        public K next() {
            return prevEntry().key;
        }
    }

}
