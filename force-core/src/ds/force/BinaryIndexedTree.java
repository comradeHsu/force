package ds.force;

public class BinaryIndexedTree {

    transient int[] dataTable;

    transient int[] treeArray;

    private int size;

    int lowBit(int m){
        return m & (-m);
    }

    public void set(int index, int value){
        dataTable[index] += value;
        while(index <= size){
            treeArray[index] += value;
            index += lowBit(index);
        }
    }

    public int getSum(int m){
        int ans = 0;
        while(m > 0){
            ans += treeArray[m];
            m -= lowBit(m);
        }
        return ans;
    }
}
