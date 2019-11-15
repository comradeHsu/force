package ds.force;

import junit.framework.TestCase;

public class IntArrayListTest extends TestCase {

    public void testAdd(){
        IntArrayList list = new IntArrayList();
        list.add(1);
        assertEquals(1,list.get(0));
        list.add(2);
        assertEquals(2,list.get(1));
        list.add(3);
        assertEquals(3,list.get(2));
    }

    public void testRemove(){
        IntArrayList list = new IntArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.remove(2);
        assertEquals(3,list.get(1));
    }
}
