package ds.force;

public class MonotonicStack<E extends Comparable> implements Stack<E> {

    transient Object[] elements;

    transient boolean isIncrease;

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public E push(E item) {
        return null;
    }

    @Override
    public E pop() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }
}
