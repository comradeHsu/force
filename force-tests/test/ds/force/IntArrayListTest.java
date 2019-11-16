package ds.force;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

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

    public void testAddAll(){
        IntArrayList list = new IntArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        IntArrayList other = new IntArrayList();
        other.add(4);
        other.add(5);
        other.add(6);
        list.addAll(other.toArray());
        assertEquals(4,list.get(3));
    }

    public void testRemoveAll(){
        IntArrayList list = new IntArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        int[] array = new int[]{2,4,5};
        list.removeAll(new IntArrayList(array));
        assertEquals(3,list.get(1));
    }

    public void testClear(){
        IntArrayList list = new IntArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.clear();
        assertEquals(0,list.size());
    }

    public void testModifiedException(){
        IntArrayList list = new IntArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 3){
                list.remove(3);
            }
            System.out.println(list.get(i));
        }
        List<Integer> li = new ArrayList<>();
        li.add(1);
        li.add(2);
        li.add(3);
        li.add(5);
        li.add(5);
        li.add(6);
        for (int i = 0; i < li.size(); i++) {
            if (li.get(i) == 3){
                li.remove(li.get(i));
            }
            System.out.println(list.get(i));
        }
    }
}
