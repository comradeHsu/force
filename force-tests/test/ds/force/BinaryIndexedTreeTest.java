package ds.force;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class BinaryIndexedTreeTest extends TestCase {

    private BinaryOperator<Integer> plus = (integer, integer2) -> {
        if (integer == null) return integer2;
        if (integer2 == null) return integer;
        return integer + integer2;
    };

    private BinaryOperator<Integer> sub =  (integer, integer2) -> {
        if (integer == null) return -integer2;
        if (integer2 == null) return integer;
        return integer - integer2;
    };

    public void testLowBit(){
        BinaryIndexedTree<Integer> tree = new BinaryIndexedTree<>(plus,sub);
        assertEquals(1,tree.lowBit(2));
        assertEquals(8,tree.lowBit(7));
        System.out.println("123456".substring(0,6));
    }

    public void testSum(){
        BinaryIndexedTree<Integer> tree = new BinaryIndexedTree<>(new Integer[]{1,2,3,4,5,6},plus,sub);
        assertEquals(Integer.valueOf(21),tree.getSum(6));
        assertEquals(Integer.valueOf(15),tree.getSum(5));
        assertEquals(Integer.valueOf(10),tree.getSum(4));
        assertEquals(Integer.valueOf(1),tree.getSum(1));
    }

    public void testIntervalSum(){
        BinaryIndexedTree<Integer> tree = new BinaryIndexedTree<>(new Integer[]{1,2,3,4,5,6},plus,sub);
        assertEquals(Integer.valueOf(21),tree.getIntervalSum(0,6));
        assertEquals(Integer.valueOf(20),tree.getIntervalSum(1,6));
        assertEquals(Integer.valueOf(6),tree.getIntervalSum(5,6));
        assertEquals(Integer.valueOf(7),tree.getIntervalSum(2,4));
        assertEquals(Integer.valueOf(1),tree.getIntervalSum(0,1));
    }

    public void testSubOperation(){
        assertEquals(21,new BigDecimal(31).subtract(new BigDecimal(10)).intValue());
    }

    public void testInsert(){
        BinaryIndexedTree<Integer> tree = new BinaryIndexedTree<>(new Integer[]{1,2,3,4,5,6},plus,sub);
        tree.insert(7);
        assertEquals(Integer.valueOf(28),tree.getIntervalSum(0,7));
        tree.insert(8);
        assertEquals(Integer.valueOf(36),tree.getIntervalSum(0,8));
        tree.insert(8);
        assertEquals(Integer.valueOf(44),tree.getIntervalSum(0,9));
        tree.insert(6);
        assertEquals(Integer.valueOf(14),tree.getIntervalSum(8,10));
    }

    public void testInsertAtIndex(){
        BinaryIndexedTree<Integer> tree = new BinaryIndexedTree<>(new Integer[]{1,6},plus,sub);
        tree.insert(1,2);
        assertEquals(Integer.valueOf(9),tree.getIntervalSum(0,3));
        tree.insert(2,3);
        assertEquals(Integer.valueOf(12),tree.getIntervalSum(0,4));
        tree.insert(3,5);
        assertEquals(Integer.valueOf(17),tree.getIntervalSum(0,5));
        tree.insert(3,4);
        assertEquals(Integer.valueOf(21),tree.getIntervalSum(0,6));
        assertEquals(tree.getSum(6),tree.getIntervalSum(0,6));
    }

    public void testRemove(){
        BinaryIndexedTree<Integer> tree = new BinaryIndexedTree<>(new Integer[]{1,2,3,4,5,6},plus,sub);
        tree.remove(1);
        assertEquals(Integer.valueOf(8),tree.getIntervalSum(0,3));
        tree.remove(0);
        assertEquals(Integer.valueOf(18),tree.getIntervalSum(0,4));
        tree.remove(3);
        assertEquals(Integer.valueOf(12),tree.getIntervalSum(0,3));
        tree.remove(1);
        assertEquals(Integer.valueOf(8),tree.getIntervalSum(0,2));
    }
}
