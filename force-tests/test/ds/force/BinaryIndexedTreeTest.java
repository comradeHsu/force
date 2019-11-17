package ds.force;

import junit.framework.TestCase;

import java.math.BigDecimal;

public class BinaryIndexedTreeTest extends TestCase {

    public void testLowBit(){
        BinaryIndexedTree tree = new BinaryIndexedTree();
        assertEquals(1,tree.lowBit(3));
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
}
