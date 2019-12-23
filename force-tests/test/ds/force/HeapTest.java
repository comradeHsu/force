package ds.force;

import ds.force.heap.Heap;
import junit.framework.TestCase;

import java.util.PriorityQueue;

public class HeapTest extends TestCase {

    public void testMaxTopHeap(){
        Heap<Integer> heap = new Heap<>();
        heap.insert(6);
        heap.insert(26);
        assertEquals(new Integer(26),heap.peek());
        assertEquals(2,heap.size());
        heap.insert(30);
        heap.insert(45);
        heap.insert(41);
        assertEquals(new Integer(45),heap.peek());
        assertEquals(5,heap.size());
        heap.remove();
        assertEquals(new Integer(41),heap.peek());
    }

    public void testPriorityQueue(){
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        heap.add(6);
        heap.add(26);
        heap.add(30);
        heap.add(45);
        heap.add(41);
        heap.add(4);
        System.out.println(heap.remove());
        System.out.println(heap.remove());
        System.out.println(heap.remove());
        System.out.println(heap.remove());
        System.out.println(heap.remove());
    }

    public void testMinTopHeap(){
        Heap<Integer> heap = new Heap<>(false);
        heap.insert(6);
        heap.insert(26);
        assertEquals(new Integer(6),heap.peek());
        assertEquals(2,heap.size());
        heap.insert(30);
        heap.insert(45);
        heap.insert(41);
        assertEquals(new Integer(6),heap.peek());
        assertEquals(5,heap.size());
        heap.remove();
        assertEquals(new Integer(26),heap.peek());
        heap.remove();
        assertEquals(new Integer(30),heap.peek());
    }

    public void testClone(){
        Integer[] array = new Integer[]{6,26,52,36,89,75};
        Heap<Integer> init = new Heap<>(array,null,false);
        Heap<Integer> heap = (Heap<Integer>) init.clone();
        assertEquals(new Integer(6),heap.peek());
        assertEquals(6,heap.size());
        heap.remove();
        assertEquals(new Integer(26),heap.peek());
        heap.remove();
        assertEquals(new Integer(36),heap.peek());
    }
}
