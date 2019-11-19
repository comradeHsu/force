package ds.force;

import java.util.Arrays;

public class BinaryIndexedTree {

    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Shared empty array instance used for empty instances.
     */
    private static final int[] EMPTY_ELEMENTDATA = {};

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    transient int[] dataTable;

    transient int[] treeArray;

    private int size;

    public BinaryIndexedTree(){
        this(DEFAULT_CAPACITY);
    }

    public BinaryIndexedTree(int initCapacity){
        if (initCapacity > 0) {
            this.dataTable = new int[initCapacity];
            this.treeArray = new int[initCapacity];
        } else if (initCapacity == 0) {
            this.dataTable = EMPTY_ELEMENTDATA;
            this.treeArray = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initCapacity);
        }
    }

    /**
     * @throws NullPointerException if the specified array is null
     * @param array
     */
    public BinaryIndexedTree(int[] array){
        this.dataTable = array;
        this.size = array.length;
        this.treeArray = new int[size];
        for (int i = 0; i < array.length; i++){
            int index = i;
            while(index < size){
                treeArray[index] += dataTable[i];
                index += lowBit(index);
            }
        }
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (dataTable == EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }

    private void ensureExplicitCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - dataTable.length > 0)
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
        int oldCapacity = dataTable.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        this.dataTable = Arrays.copyOf(dataTable, newCapacity);
        this.treeArray = Arrays.copyOf(treeArray, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }

    int lowBit(int index){
        int sequence = index + 1;
        return sequence & (-sequence);
    }

    public int get(int index){
        rangeCheckForInsert(index);
        return dataTable[index];
    }

    public void set(int index, int value){
        int oldValue = dataTable[index];
        dataTable[index] = value;
        int diff = oldValue - value;
        while(index < size){
            treeArray[index] -= diff;
            index += lowBit(index);
        }
    }

    public void add(int index, int value){
        dataTable[index] += value;
        while(index < size){
            treeArray[index] += value;
            index += lowBit(index);
        }
    }

    public boolean insert(int value){
        ensureCapacityInternal(size + 1);
        dataTable[size] = value;
        int index = size;
        int newSum = getSum(size) + value;
        size = size + 1;
        treeArray[index] = newSum - getSum(size - lowBit(index));
        return true;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by insert and addAll.
     */
    private void rangeCheckForInsert(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    public void insert(int index, int value){
        rangeCheckForInsert(index);
        ensureCapacityInternal(size + 1);
        System.arraycopy(dataTable, index, dataTable, index + 1,
                size - index);
        System.arraycopy(treeArray, index, treeArray, index + 1,
                size - index);
        dataTable[index] = value;
        size++;
        for (int i = index; i < size; i++){
            treeArray[i] = 0;
            int newSum = getSum(i) + dataTable[i];
            treeArray[i] = newSum - getSum(i - lowBit(i) + 1);
        }
    }

    public int remove(int index){
        rangeCheck(index);
        int oldValue = dataTable[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(dataTable, index + 1, dataTable, index,
                    numMoved);
        }
        size--;
        for (int i = index; i < size; i++){
            treeArray[i] = 0;
            int newSum = getSum(i) + dataTable[i];
            treeArray[i] = newSum - getSum(i - lowBit(i) + 1);
        }
        treeArray[size] = 0;
        dataTable[size] = 0;
        return oldValue;
    }

    public int getSum(int index){
        int sum = 0;
        int tail = index - 1;
        while(tail >= 0){
            sum += treeArray[tail];
            tail -= lowBit(tail);
        }
        return sum;
    }

    public int getIntervalSum(int start, int end){
        int sum = 0;
        int redundant = 0;
        int head = start - 1,tail = end - 1;
        while(head >= 0){
            redundant += treeArray[head];
            head -= lowBit(head);
        }
        while(tail >= 0){
            sum += treeArray[tail];
            tail -= lowBit(tail);
        }
        return sum - redundant;
    }

    public int size(){
        return size;
    }
}
