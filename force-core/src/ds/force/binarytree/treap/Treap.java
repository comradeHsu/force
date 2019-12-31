package ds.force.binarytree.treap;

public interface Treap<E> {

    boolean insert(E e);

    E remove(E e);

    Treap<E> split(E e);

    void merge(Treap<E> treap);

    boolean contains(E e);
}
