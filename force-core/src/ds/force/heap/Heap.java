package ds.force.heap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class Heap<E> implements Cloneable, Serializable {

    private transient Object[] table;
    /**
     * The size of the Heap (the number of elements it contains).
     *
     * @serial
     */
    private transient int size;

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering.
     */
    private final Comparator<? super E> comparator;
    /**
     * is big top heap? if true this heap is a big top heap
     * else a small top heap
     */
    private transient boolean isMaxTop = true;
    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    /**
     * 分配的数组的最大大小
     * 有些虚拟机会在数组中保留一些头部信息。
     * 尝试分配较大的数组可能会导致OutOfMemory错误：请求的数组大小超过了虚拟机限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    /**
     * Constructs an empty list with an initial capacity of eleven.
     */
    public Heap(){
        this(DEFAULT_INITIAL_CAPACITY);
    }
    /**
     * Constructs an empty big top heap or small top heap.
     *
     * @param  maxTop  the type of heap
     */
    public Heap(boolean maxTop){
        this(DEFAULT_INITIAL_CAPACITY,null,maxTop);
    }

    public Heap(int initialCapacity){
        this(initialCapacity,null,true);
    }

    public Heap(Comparator<? super E> comparator) {
        this(DEFAULT_INITIAL_CAPACITY, comparator,true);
    }

    public Heap(int initialCapacity, boolean isMaxTop){
        this(initialCapacity,null,true);
    }

    public Heap(Object[] array, Comparator<? super E> comparator,boolean isMaxTop){
        this.table = array;
        this.size = array.length;
        this.comparator = comparator;
        this.isMaxTop = isMaxTop;
        heapify();
    }

    public Heap(int initialCapacity,
                         Comparator<? super E> comparator,boolean isMaxTop) {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.table = new Object[initialCapacity];
        this.comparator = comparator;
        this.isMaxTop = isMaxTop;
    }

    /**
     * insert element to this heap
     * @param e element
     * @throws NullPointerException when element is null
     * @return true
     */
    public boolean insert(E e){
        int i = size;
        if (i >= table.length)
            grow(i + 1);
        size = i + 1;
        if (i == 0)
            table[0] = e;
        else
            shiftUp(i,e);
        return true;
    }

    /**
     * remove and return the top's element of heap
     * @throws NullPointerException when heap is empty
     * @return
     */
    @SuppressWarnings("unchecked")
    public E remove(){
        if (size == 0){
            throw new NullPointerException();
        }
        E result = (E)table[0];
        --size;
        E lastElement = (E)table[size];
        table[0] = lastElement;
        shiftDown(0,lastElement);
        table[size] = 0;
        return result;
    }

    /**
     * return the top of heap
     * @return
     */
    @SuppressWarnings("unchecked")
    public E peek(){
        return (E)table[0];
    }

    /**
     * remove element at the index of heap
     * @param index
     * @throws IllegalArgumentException illegal index
     * @return old element at index
     */
    @SuppressWarnings("unchecked")
    public E removeAtIndex(int index){
        if (index >= size || index < 0){
            throw new IllegalArgumentException("illegal index");
        }
        int s = --size;
        E result = (E)table[index];
        if (s == index) // removed last element
            table[index] = 0;
        else {
            E moved = (E)table[s];
            table[s] = 0;
            table[index] = moved;
            shiftDown(index, moved);
            if (table[index] == moved) {
                shiftUp(index, moved);
            }
        }
        return result;
    }

    /**
     * 堆化
     */
    @SuppressWarnings("unchecked")
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--)
            shiftDown(i, (E)table[i]);
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     *
     * To simplify and speed up coercions and comparisons. the
     * Comparable and Comparator versions are separated into different
     * methods that are otherwise identical. (Similarly for siftDown.)
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private void shiftUp(int k, E x) {
        if (comparator != null)
            shiftUpUsingComparator(k, x);
        else
            shiftUpComparable(k, x);
    }

    @SuppressWarnings("unchecked")
    private void shiftUpUsingComparator(int index, E e) {
        while(index > 0){
            int parentIndex = (index - 1) >>> 1;
            Object parent = table[parentIndex];
            if (isMaxTop){
                if (comparator.compare(e,(E)parent) <= 0)
                    break;
                table[index] = parent;
                index = parentIndex;
            } else {
                if (comparator.compare(e,(E)parent) >= 0)
                    break;
                table[index] = parent;
                index = parentIndex;
            }
        }
        table[index] = e;
    }

    /**
     * 节点的上浮
     * @param index
     * @param e
     */
    @SuppressWarnings("unchecked")
    private void shiftUpComparable(int index,E e) {
        Comparable<? super E> key = (Comparable<? super E>) e;
        while(index > 0){
            int parentIndex = (index - 1) >>> 1;
            Object parent = table[parentIndex];
            if (isMaxTop){
                if (key.compareTo((E)parent) <= 0)
                    break;
                table[index] = parent;
                index = parentIndex;
            } else {
                if (key.compareTo((E)parent) >= 0)
                    break;
                table[index] = parent;
                index = parentIndex;
            }
        }
        table[index] = e;
    }
    /**
     * 节点的下沉
     * @param index
     * @param e
     */
//    @Deprecated
//    private void shiftDown(int index,int e){
//        int child = (index << 1) + 1;
//        if (child >= size){
//            return;
//        }
//        int rightIndex = child+1;
//        if (maxTop) {
//            if (rightIndex < size && table[child] < table[rightIndex] && e < table[rightIndex]){
//                table[index] = table[rightIndex];
//                table[rightIndex] = e;
//                shiftDown(child+1,e);
//                return;
//            }
//            if (e >= table[child]) return;
//            table[index] = table[child];
//            table[child] = e;
//            shiftDown(child,e);
//        } else {
//            if (rightIndex < size && table[rightIndex] < table[child] && e > table[child]){
//                table[index] = table[rightIndex];
//                table[rightIndex] = e;
//                shiftDown(child+1,e);
//                return;
//            }
//            if (e <= table[child]) return;
//            table[index] = table[child];
//            table[child] = e;
//            shiftDown(child,e);
//        }
//    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     *
     * @param index the position to fill
     * @param x the item to insert
     */
    private void shiftDown(int index, E x) {
        if (comparator != null)
            shiftDownUsingComparator(index, x);
        else
            shiftDownComparable(index, x);
    }

    @SuppressWarnings("unchecked")
    private void shiftDownUsingComparator(int index, E e) {
        int half = size >>> 1;
        while (index < half) {
            int child = (index << 1) + 1;
            Object c = (E)table[child];
            int right = child + 1;
            if (isMaxTop){
                if (right < size && comparator.compare((E)c,(E)table[right]) < 0)
                    c = table[child = right];
                if (comparator.compare(e,(E)c) >= 0)
                    break;
            } else {
                if (right < size && comparator.compare((E)c,(E)table[right]) > 0)
                    c = table[child = right];
                if (comparator.compare(e,(E)c) <= 0)
                    break;
            }
            table[index] = c;
            index = child;
        }
        table[index] = e;
    }

    /**
     * 节点的下沉
     * @param index
     * @param e
     */
    @SuppressWarnings("unchecked")
    private void shiftDownComparable(int index, E e) {
        Comparable<? super E> key = (Comparable<? super E>)e;
        //half对应的元素总是第一个没有子节点的元素
        int half = size >>> 1;
        while (index < half) {
            int child = (index << 1) + 1;
            Object c = table[child];
            int right = child + 1;
            if (isMaxTop){
                if (right < size && ((Comparable<? super E>) c).compareTo((E)table[right]) < 0)
                    c = table[child = right];
                if (key.compareTo((E)c) >= 0)
                    break;
            } else {
                if (right < size && ((Comparable<? super E>) c).compareTo((E)table[right]) > 0)
                    c = table[child = right];
                if (key.compareTo((E)c) <= 0)
                    break;
            }
            table[index] = c;
            index = child;
        }
        table[index] = e;
    }

    private void grow(int minCapacity) {
        int oldCapacity = table.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        table = Arrays.copyOf(table, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    public int size(){
        return size;
    }

    /**
     * Returns a shallow copy of this <tt>Heap</tt> instance.  (The
     * elements themselves are not copied.)
     *
     * @return a clone of this <tt>Heap</tt> instance
     */
    public Object clone() {
        try {
            Heap<?> v = (Heap<?>) super.clone();
            v.table = Arrays.copyOf(table, size);
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
}

