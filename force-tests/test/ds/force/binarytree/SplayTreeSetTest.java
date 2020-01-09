package ds.force.binarytree;

import ds.force.primitive.IntArrayList;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;

public class SplayTreeSetTest extends TestCase {

    public static SplayTreeSet<Integer> init(){
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            set.add(ints.get(i));
        }
        return set;
    }

    public void testPut(){
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        for (int i = 1; i < 10; i++) {
            set.add(i);
        }
        assertTrue(set.contains(1));
        assertTrue(set.contains(6));
    }

    public void testRemove(){
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        for (int i = 1; i < 10; i++) {
            set.add(i);
        }
        set.contains(1);
        set.contains(6);
        set.remove(1);
        set.remove(3);
    }

    public void testIterator(){
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        IntArrayList list = IntArrayList.of(860,130,104,871,501,479,593,445,690,903,205,663,113,844,745,988,338,409,256,
                680,400,631,90,566,680,169,445,903,871,5);
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i));
        }
        int[] array = list.stream().distinct().sorted().toArray();
        int index = 0;
        for (Integer entry: set){
            assertEquals(Integer.valueOf(array[index]),entry);
            index++;
        }
    }

    public void testGet() {
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        set.add(3);
        assertFalse(set.contains(10));
        set.add(2);
        assertEquals(true, set.contains(3));
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            set.add(i);
        }
        for (int i = 0; i < 50; i++) {
            int key = random.nextInt(48);
            assertEquals(true, set.contains(key));
        }
    }

    public void testFloor(){
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        assertNull(set.floor(2));
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            set.add(ints.get(i));
        }
        assertEquals(Integer.valueOf(3),set.floor(3));
        assertEquals(Integer.valueOf(5),set.floor(5));
        assertEquals(Integer.valueOf(12),set.floor(13));
        assertEquals(Integer.valueOf(27),set.floor(27));
        assertEquals(Integer.valueOf(27),set.floor(31));
        assertEquals(Integer.valueOf(48),set.floor(188));
        assertEquals(Integer.valueOf(21),set.floor(21));
    }

    public void testHigher(){
        SplayTreeSet<Integer> set = new SplayTreeSet<>();
        assertNull(set.higher(2));
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            set.add(ints.get(i));
        }
        assertNull(set.higher(48));
        assertEquals(Integer.valueOf(7),set.higher(5));
        assertEquals(Integer.valueOf(14),set.higher(13));
        assertEquals(Integer.valueOf(34),set.higher(27));
        assertEquals(Integer.valueOf(34),set.higher(31));
        assertEquals(Integer.valueOf(3),set.higher(1));
        assertEquals(Integer.valueOf(27),set.higher(21));
    }

    public void testCeiling(){
        NavigableSet<Integer> set = new SplayTreeSet<>();
        assertNull(set.ceiling(2));
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            set.add(ints.get(i));
        }
        assertNull(set.ceiling(49));
        assertEquals(Integer.valueOf(5),set.ceiling(5));
        assertEquals(Integer.valueOf(14),set.ceiling(13));
        assertEquals(Integer.valueOf(27),set.ceiling(27));
        assertEquals(Integer.valueOf(34),set.ceiling(31));
        assertEquals(Integer.valueOf(3),set.ceiling(3));
        assertEquals(Integer.valueOf(21),set.ceiling(21));
        assertEquals(Integer.valueOf(48),set.ceiling(48));
    }

    public void testLower(){
        NavigableSet<Integer> set = init();
        assertEquals(null,set.lower(3));
        assertEquals(Integer.valueOf(3),set.lower(5));
        assertEquals(Integer.valueOf(12),set.lower(13));
        assertEquals(Integer.valueOf(21),set.lower(27));
        assertEquals(Integer.valueOf(27),set.lower(31));
        assertEquals(Integer.valueOf(48),set.lower(188));
    }

    public void testBatch(){
        NavigableSet<Integer> set = new SplayTreeSet<>();
        for (int i = 0; i < 50; i++) {
            set.add(i*2);
        }
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer key = random.nextInt(94)+2;
            if (key >= 97) System.out.println(key);
            if (key < 0) System.out.println(key);
            try {
                if ((key & 1) == 1) {
                    assertEquals(Integer.valueOf(key + 1), set.ceiling(key));
                    assertEquals(Integer.valueOf(key + 1), set.higher(key));
                    assertEquals(Integer.valueOf(key - 1), set.lower(key));
                    assertEquals(Integer.valueOf(key - 1), set.floor(key));
                } else {
                    assertEquals(key, set.ceiling(key));
                    assertEquals(Integer.valueOf(key + 2), set.higher(key));
                    assertEquals(Integer.valueOf(key - 2), set.lower(key));
                    assertEquals(key, set.floor(key));
                }
            }catch (NullPointerException e){
                e.printStackTrace();
                System.out.println(key);
            }
        }
    }

    public void testDescendingSet(){
        NavigableSet<Integer> set = init();
        NavigableSet<Integer> des = set.descendingSet();
        Iterator<Integer> iterable = des.iterator();
        while (iterable.hasNext()){
            Integer entry = iterable.next();
            if (entry == 3 || entry == 48 || entry == 34) iterable.remove();
        }
        assertFalse(des.contains(3));
        assertFalse(des.contains(34));
        assertFalse(des.contains(48));
        Iterator<Integer> iter = des.iterator();
        while (iter.hasNext()){
            Integer entry = iter.next();
            iter.remove();
        }
    }

    public void testSplit(){
        SplayTreeSet<Integer> set = init();
        assertEquals(Integer.valueOf(3),set.first());
        SplayTreeSet<Integer> zero = set.split(3);
        assertEquals(1,zero.size());
        assertEquals(Integer.valueOf(3),zero.first());
    }

    public void testRanking(){
        SplayTreeSet<Integer> set = init();
        assertEquals(Integer.valueOf(5),set.get(2));
        assertEquals(Integer.valueOf(48),set.get(set.size()));
    }

    public void testGetSequence(){
        SplayTreeSet<Integer> set = init();
        assertEquals(0,set.getSequence(3));
        assertEquals(17,set.getSequence(48));
        assertEquals(18,set.getSequence(49));
        assertEquals(0,set.getSequence(2));
    }
}
