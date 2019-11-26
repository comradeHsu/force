package ds.force;

public interface Stack<E> {

    int size();

    boolean isEmpty();

    E push(E item);

    E pop();

    E peek();

    void clear();
}
