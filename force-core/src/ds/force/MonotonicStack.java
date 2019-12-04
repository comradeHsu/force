package ds.force;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EmptyStackException;

/**
 * Monotonic stack is divided into monotonic increasing stack and monotonic decreasing stack.
 * Monotonic increasing stack is the stack in which the elements keep monotonic increasing.
 * Similarly monotonic decreasing stack is the stack in which the elements keep monotonic decreasing
 * @param <E> must be extends Comparable
 * @author comradeHsu
 * @see Stack
 */
public class MonotonicStack<E> implements Stack<E> {

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

    /**
     * The comparator used to maintain order in this stack, or
     * null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super E> comparator;

    transient boolean isIncrease;

    /**
     * The size of the Stack (the number of elements it contains).
     *
     * @serial
     */
    private int size;

    /**
     * Constructs an empty stack with an initial capacity of ten.
     */
    public MonotonicStack(){
        this(DEFAULT_CAPACITY,true,null);
    }

    /**
     * Constructs an empty stack with an initial capacity .
     */
    public MonotonicStack(int initCapacity){
        this(initCapacity,true,null);
    }

    /**
     * Constructs an empty stack with a initial capacity of ten and isIncrease param.
     * @see  MonotonicStack#isIncrease
     */
    public MonotonicStack(boolean isIncrease){
        this(DEFAULT_CAPACITY,isIncrease,null);
    }

    public MonotonicStack(boolean isIncrease, Comparator<? super E> comparator){
        this(DEFAULT_CAPACITY,isIncrease,comparator);
    }

    /**
     * Constructs an empty stack with a initial capacity and isIncrease param.
     * @see  MonotonicStack#isIncrease
     */
    public MonotonicStack(int initCapacity, boolean isIncrease,Comparator<? super E> comparator){
        if (initCapacity > 0) {
            this.elements = new Object[initCapacity];
        } else if (initCapacity == 0) {
            this.elements = EMPTY_ELEMENTS;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initCapacity);
        }
        this.isIncrease = isIncrease;
        this.comparator = comparator;
    }

    /**
     * return the size of stack
     *
     * @return the number of elements in this stack
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this stack contains no elements.
     *
     * @return <tt>true</tt> if this stack contains no elements
     */
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

    /**
     * Pushes an item onto the top of this stack.
     *
     * @param item the item to be pushed onto this stack.
     * @return the <code>item</code> argument.
     */
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
        if (comparator != null) return comparePeekEleUsingComparator(item);
        Comparable<? super E> c = (Comparable<? super E>) peek();
        if (isIncrease)
            return c.compareTo(item) >= 0;
        else
            return c.compareTo(item) <= 0;
    }

    private boolean comparePeekEleUsingComparator(E item){
        if (isIncrease)
            return comparator.compare(peek(),item) >= 0;
        else
            return comparator.compare(peek(),item) <= 0;
    }

    /**
     * Removes the object at the top of this stack and returns that
     * object as the value of this function.
     *
     * @return The object at the top of this stack
     * @throws EmptyStackException if this stack is empty
     */
    @Override
    @SuppressWarnings("unchecked")
    public E pop() {
        E item = peek();
        elements[--size] = null;
        return item;
    }

    /**
     * Looks at the object at the top of this stack without removing it
     * from the stack.
     *
     * @return the object at the top of this stack
     * @throws EmptyStackException if this stack is empty
     */
    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        if (size == 0)
            throw new EmptyStackException();
        return (E) elements[size-1];
    }

    /**
     * Removes all of the elements from this stack (optional operation).
     * The stack will be empty after this call returns.
     */
    @Override
    public void clear() {
        for (int i = 0; i < size; i++)
            elements[i] = null;
        size = 0;
    }
}
