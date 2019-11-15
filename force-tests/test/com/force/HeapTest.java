package com.force;

import junit.framework.TestCase;
import org.junit.Test;

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
}
