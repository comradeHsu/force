package ds.force;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.EmptyStackException;

public class MonotonicStackTest extends TestCase {

    public void testIncreaseStack(){
        MonotonicStack<Integer> stack = new MonotonicStack<>();
        stack.push(20);
        stack.push(15);
        assertEquals(2,stack.size());
        stack.push(26);
        assertEquals(1,stack.size());
        assertEquals(Integer.valueOf(26),stack.peek());
        stack.push(10);
        stack.push(19);
        assertEquals(2,stack.size());
        assertEquals(Integer.valueOf(19),stack.peek());
    }

    public void testDecreaseStack(){
        MonotonicStack<Integer> stack = new MonotonicStack<>(false);
        stack.push(20);
        stack.push(15);
        assertEquals(1,stack.size());
        stack.push(26);
        assertEquals(2,stack.size());
        assertEquals(Integer.valueOf(26),stack.peek());
        stack.push(10);
        stack.push(19);
        assertEquals(2,stack.size());
        assertEquals(Integer.valueOf(19),stack.peek());
        stack.push(15);
        assertEquals(2,stack.size());
        assertEquals(Integer.valueOf(15),stack.peek());
    }

    public void testPush(){
        MonotonicStack<BigDecimal> stack = new MonotonicStack<>(false);
        stack.push(BigDecimal.valueOf(26));
        stack.push(BigDecimal.valueOf(15));
        stack.push(BigDecimal.valueOf(28));
        stack.push(BigDecimal.valueOf(28));
        assertEquals(3,stack.size());
        stack.push(BigDecimal.valueOf(2));
        assertEquals(1,stack.size());
    }

    public void testPop(){
        MonotonicStack<Integer> stack = new MonotonicStack<>();
        stack.push(20);
        stack.push(26);
        stack.push(20);
        stack.push(15);
        stack.push(13);
        stack.push(15);
        assertEquals(4,stack.size());
        assertEquals(Integer.valueOf(15),stack.pop());
        assertEquals(Integer.valueOf(15),stack.pop());
        assertEquals(Integer.valueOf(20),stack.pop());
        assertEquals(Integer.valueOf(26),stack.pop());
    }

    public void testEmptyStack(){
        MonotonicStack<Integer> stack = new MonotonicStack<>();
        try {
            stack.pop();
            fail();
        } catch (EmptyStackException e){

        }

    }
}
