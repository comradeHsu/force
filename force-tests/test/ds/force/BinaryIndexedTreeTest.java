package ds.force;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class BinaryIndexedTreeTest extends TestCase {

    public void testLowBit(){
        BinaryIndexedTree tree = new BinaryIndexedTree();
        assertEquals(1,tree.lowBit(2));
        assertEquals(8,tree.lowBit(7));
        System.out.println("123456".substring(0,6));
    }

    public void testSum(){
        BinaryIndexedTree tree = new BinaryIndexedTree(new int[]{1,2,3,4,5,6});
        assertEquals(21,tree.getSum(6));
        assertEquals(15,tree.getSum(5));
        assertEquals(10,tree.getSum(4));
        assertEquals(1,tree.getSum(1));
    }

    public void testIntervalSum(){
        BinaryIndexedTree tree = new BinaryIndexedTree(new int[]{1,2,3,4,5,6});
        assertEquals(21,tree.getIntervalSum(0,6));
        assertEquals(20,tree.getIntervalSum(1,6));
        assertEquals(6,tree.getIntervalSum(5,6));
        assertEquals(7,tree.getIntervalSum(2,4));
        assertEquals(1,tree.getIntervalSum(0,1));
    }

    public void testSubOperation(){
        assertEquals(21,new BigDecimal(31).subtract(new BigDecimal(10)).intValue());
    }

    public void testInsert(){
        BinaryIndexedTree tree = new BinaryIndexedTree(new int[]{1,2,3,4,5,6});
        tree.insert(7);
        assertEquals(28,tree.getIntervalSum(0,7));
        tree.insert(8);
        assertEquals(36,tree.getIntervalSum(0,8));
        tree.insert(8);
        assertEquals(44,tree.getIntervalSum(0,9));
        tree.insert(6);
        assertEquals(14,tree.getIntervalSum(8,10));
    }

    public void testInsertAtIndex(){
        BinaryIndexedTree tree = new BinaryIndexedTree(new int[]{1,6});
        tree.insert(1,2);
        assertEquals(9,tree.getIntervalSum(0,3));
        tree.insert(2,3);
        assertEquals(12,tree.getIntervalSum(0,4));
        tree.insert(3,5);
        assertEquals(17,tree.getIntervalSum(0,5));
        tree.insert(3,4);
        assertEquals(21,tree.getIntervalSum(0,6));
        assertEquals(tree.getSum(6),tree.getIntervalSum(0,6));
    }

    public void testRemove(){
        BinaryIndexedTree tree = new BinaryIndexedTree(new int[]{1,2,3,4,5,6});
        tree.remove(1);
        assertEquals(8,tree.getIntervalSum(0,3));
        tree.remove(0);
        assertEquals(18,tree.getIntervalSum(0,4));
        tree.remove(3);
        assertEquals(12,tree.getIntervalSum(0,3));
        tree.remove(1);
        assertEquals(8,tree.getIntervalSum(0,2));
    }
}
