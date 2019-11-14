package com.force;

import java.util.Arrays;
import java.util.PriorityQueue;

public class Heap {

    transient int[] table;

    transient int size;
    /**
     * 是否是大顶堆
     */
    transient boolean maxTop = true;

    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    /**
     * 分配的数组的最大大小
     * 有些虚拟机会在数组中保留一些头部信息。
     * 尝试分配较大的数组可能会导致OutOfMemory错误：请求的数组大小超过了虚拟机限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public Heap(){
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public Heap(int initialCapacity){
        table = new int[initialCapacity];
    }

    public Heap(int initialCapacity, boolean maxTop){
        table = new int[initialCapacity];
        this.maxTop = maxTop;
    }

    public Heap(int[] array, boolean maxTop){
        this.table = array;
        this.size = array.length;
        this.maxTop = maxTop;
        heapify();
    }

    /**
     * 向堆中插入一个元素
     * @param e
     * @return
     */
    public boolean insert(int e){
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
     * 删除堆的顶点并返回
     * @return
     */
    public int remove(){
        if (size == 0){
            throw new NullPointerException();
        }
        int result = table[0];
        --size;
        int lastElement = table[size];
        table[0] = lastElement;
        shiftDownLoop(0,lastElement);
        table[size] = 0;
        return result;
    }

    public int peek(){
        return table[0];
    }

    public int removeAtIndex(int index){
        if (index >= size){
            throw new IllegalArgumentException("index too big");
        }
        int s = --size;
        int result = table[index];
        if (s == index) // removed last element
            table[index] = 0;
        else {
            int moved = table[s];
            table[s] = 0;
            table[index] = moved;
            shiftDownLoop(index, moved);
            if (table[index] == moved) {
                shiftUp(index, moved);
            }
        }
        return result;
    }

    /**
     * 堆化
     */
    private void heapify() {
        for (int i = (size >>> 1) - 1; i >= 0; i--)
            shiftDownLoop(i, table[i]);
    }

    /**
     * 节点的上浮
     * @param index
     * @param e
     */
    private void shiftUp(int index,int e) {
        while(index > 0){
            int parentIndex = (index - 1) >>> 1;
            int parent = table[parentIndex];
            if (maxTop){
                if (e <= parent)
                    break;
                table[index] = parent;
                index = parentIndex;
            } else {
                if (e >= parent)
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
    @Deprecated
    private void shiftDown(int index,int e){
        int child = (index << 1) + 1;
        if (child >= size){
            return;
        }
        int rightIndex = child+1;
        if (maxTop) {
            if (rightIndex < size && table[child] < table[rightIndex] && e < table[rightIndex]){
                table[index] = table[rightIndex];
                table[rightIndex] = e;
                shiftDown(child+1,e);
                return;
            }
            if (e >= table[child]) return;
            table[index] = table[child];
            table[child] = e;
            shiftDown(child,e);
        } else {
            if (rightIndex < size && table[rightIndex] < table[child] && e > table[child]){
                table[index] = table[rightIndex];
                table[rightIndex] = e;
                shiftDown(child+1,e);
                return;
            }
            if (e <= table[child]) return;
            table[index] = table[child];
            table[child] = e;
            shiftDown(child,e);
        }
    }

    /**
     * 节点的下沉
     * @param index
     * @param e
     */
    private void shiftDownLoop(int index, int e) {
        //half对应的元素总是第一个没有子节点的元素
        int half = size >>> 1;
        while (index < half) {
            int child = (index << 1) + 1;
            int c = table[child];
            int right = child + 1;
            if (maxTop){
                if (right < size && c < table[right])
                    c = table[child = right];
                if (e >= c)
                    break;
            } else {
                if (right < size && c > table[right])
                    c = table[child = right];
                if (e <= c)
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
}

