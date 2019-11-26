package ds.force;

import java.util.Arrays;
import java.util.EmptyStackException;

public class MonotonicStack<E extends Comparable> implements Stack<E> {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Shared empty stack instance used for empty instances.
     */
    private static final Object[] EMPTY_ELEMENTS = {};

    /**
     * The array buffer into which the elements of the MonotonicStack are stored.
     * The capacity of the MonotonicStack is the length of this array buffer. Any
     * empty MonotonicStack with elements == EMPTY_ELEMENTS
     * will be expanded to DEFAULT_CAPACITY when the first element is pushed.
     */
    transient Object[] elements;

    transient boolean isIncrease;

    private int size;

    public MonotonicStack(){
        this(DEFAULT_CAPACITY,true);
    }

    public MonotonicStack(int initCapacity){
        this(initCapacity,true);
    }

    public MonotonicStack(boolean isIncrease){
        this(DEFAULT_CAPACITY,isIncrease);
    }

    public MonotonicStack(int initCapacity, boolean isIncrease){
        if (initCapacity > 0) {
            this.elements = new Object[initCapacity];
        } else if (initCapacity == 0) {
            this.elements = EMPTY_ELEMENTS;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initCapacity);
        }
        this.isIncrease = isIncrease;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (elements == EMPTY_ELEMENTS) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }

    private void ensureExplicitCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elements.length > 0)
            grow(minCapacity);
    }

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elements.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elements = Arrays.copyOf(elements, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E push(E item) {
        if (isEmpty() || comparePeekEle(item)){
            ensureCapacityInternal(size + 1);
            elements[size++] = item;
        } else {
            while (!isEmpty() && !comparePeekEle(item)){
                pop();
            }
            elements[size++] = item;
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private boolean comparePeekEle(E item){
        if (isIncrease)
            return peek().compareTo(item) >= 0;
        else
            return peek().compareTo(item) <= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E pop() {
        E item = peek();
        elements[--size] = null;
        return item;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        if (size == 0)
            throw new EmptyStackException();
        return (E) elements[size-1];
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++)
            elements[i] = null;
        size = 0;
    }
}
