package ds.force.binarytree;

import ds.force.primitive.IntArrayList;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;

public class SplayTreeMapTest extends TestCase {

    public static SplayTreeMap<Integer,Integer> init(){
        SplayTreeMap<Integer,Integer> map = new SplayTreeMap<>();
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        return map;
    }

    public void testPut(){
        SplayTreeMap<Integer,Integer> splayTreeMap = new SplayTreeMap<>();
        for (int i = 1; i < 10; i++) {
            splayTreeMap.put(i,i);
        }
        splayTreeMap.get(1);
        splayTreeMap.get(6);
    }

    public void testRemove(){
        SplayTreeMap<Integer,Integer> splayTreeMap = new SplayTreeMap<>();
        for (int i = 1; i < 10; i++) {
            splayTreeMap.put(i,i);
        }
        splayTreeMap.get(1);
        splayTreeMap.get(6);
        splayTreeMap.remove(1);
        splayTreeMap.remove(3);
    }

    public void testIterator(){
        SplayTreeMap<Integer,Integer> map = new SplayTreeMap<>();
        IntArrayList list = IntArrayList.of(860,130,104,871,501,479,593,445,690,903,205,663,113,844,745,988,338,409,256,
                680,400,631,90,566,680,169,445,903,871,5);
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i),list.get(i));
        }
        int[] array = list.stream().distinct().sorted().toArray();
        int index = 0;
        for (Map.Entry<Integer,Integer> entry : map.entrySet()){
            assertEquals(Integer.valueOf(array[index]),entry.getKey());
            index++;
        }
    }

    public void testGet() {
        Map<Integer, Integer> map = new SplayTreeMap<>();
        map.put(3, 3);
        assertNull(map.get(10));
        map.put(2, 2);
        assertEquals(Integer.valueOf(3), map.get(3));
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            map.put(i, i);
        }
        for (int i = 0; i < 50; i++) {
            int key = random.nextInt(48);
            assertEquals(Integer.valueOf(key), map.get(key));
        }
    }

    public void testFloorEntry(){
        NavigableMap<Integer,Integer> map = new SplayTreeMap<>();
        assertNull(map.floorEntry(2));
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        assertEquals(Integer.valueOf(3),map.floorEntry(3).getKey());
        assertEquals(Integer.valueOf(5),map.floorEntry(5).getKey());
        assertEquals(Integer.valueOf(12),map.floorEntry(13).getKey());
        assertEquals(Integer.valueOf(27),map.floorEntry(27).getKey());
        assertEquals(Integer.valueOf(27),map.floorEntry(31).getKey());
        assertEquals(Integer.valueOf(48),map.floorEntry(188).getKey());
        assertEquals(Integer.valueOf(21),map.floorEntry(21).getKey());
    }

    public void testHigherEntry(){
        NavigableMap<Integer,Integer> map = new SplayTreeMap<>();
        assertNull(map.higherEntry(2));
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        assertNull(map.higherEntry(48));
        assertEquals(Integer.valueOf(7),map.higherEntry(5).getKey());
        assertEquals(Integer.valueOf(14),map.higherEntry(13).getKey());
        assertEquals(Integer.valueOf(34),map.higherEntry(27).getKey());
        assertEquals(Integer.valueOf(34),map.higherEntry(31).getKey());
        assertEquals(Integer.valueOf(3),map.higherEntry(1).getKey());
        assertEquals(Integer.valueOf(27),map.higherEntry(21).getKey());
    }

    public void testCeilingEntry(){
        NavigableMap<Integer,Integer> map = new SplayTreeMap<>();
        assertNull(map.ceilingEntry(2));
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        assertNull(map.ceilingEntry(49));
        assertEquals(Integer.valueOf(5),map.ceilingEntry(5).getKey());
        assertEquals(Integer.valueOf(14),map.ceilingEntry(13).getKey());
        assertEquals(Integer.valueOf(27),map.ceilingEntry(27).getKey());
        assertEquals(Integer.valueOf(34),map.ceilingEntry(31).getKey());
        assertEquals(Integer.valueOf(3),map.ceilingEntry(3).getKey());
        assertEquals(Integer.valueOf(21),map.ceilingEntry(21).getKey());
        assertEquals(Integer.valueOf(48),map.ceilingEntry(48).getKey());
    }

    public void testLowerEntry(){
        NavigableMap<Integer,Integer> map = init();
        assertEquals(null,map.lowerEntry(3));
        assertEquals(Integer.valueOf(3),map.lowerEntry(5).getKey());
        assertEquals(Integer.valueOf(12),map.lowerEntry(13).getKey());
        assertEquals(Integer.valueOf(21),map.lowerEntry(27).getKey());
        assertEquals(Integer.valueOf(27),map.lowerEntry(31).getKey());
        assertEquals(Integer.valueOf(48),map.lowerEntry(188).getKey());
    }

    public void testBatchEntry(){
        NavigableMap<Integer,Integer> map = new SplayTreeMap<>();
        for (int i = 0; i < 50; i++) {
            map.put(i*2,i);
        }
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer key = random.nextInt(94)+2;
            if (key >= 97) System.out.println(key);
            if (key < 0) System.out.println(key);
            try {
                if ((key & 1) == 1) {
                    assertEquals(Integer.valueOf(key + 1), map.ceilingEntry(key).getKey());
                    assertEquals(Integer.valueOf(key + 1), map.higherEntry(key).getKey());
                    assertEquals(Integer.valueOf(key - 1), map.lowerEntry(key).getKey());
                    assertEquals(Integer.valueOf(key - 1), map.floorEntry(key).getKey());
                } else {
                    assertEquals(key, map.ceilingEntry(key).getKey());
                    assertEquals(Integer.valueOf(key + 2), map.higherEntry(key).getKey());
                    assertEquals(Integer.valueOf(key - 2), map.lowerEntry(key).getKey());
                    assertEquals(key, map.floorEntry(key).getKey());
                }
            }catch (NullPointerException e){
                e.printStackTrace();
                System.out.println(key);
            }
        }
    }

    public void testDescendingMap(){
        SplayTreeMap<Integer,Integer> map = init();
        NavigableMap<Integer,Integer> des = map.descendingMap();
        Iterator<Map.Entry<Integer,Integer>> iterable = des.entrySet().iterator();
        while (iterable.hasNext()){
            Map.Entry<Integer,Integer> entry = iterable.next();
            if (entry.getKey() == 3 || entry.getKey() == 48 || entry.getKey() == 34) iterable.remove();
        }
        assertFalse(des.containsKey(3));
        assertFalse(des.containsKey(34));
        assertFalse(des.containsKey(48));
        Iterator<Map.Entry<Integer,Integer>> iter = des.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry<Integer,Integer> entry = iter.next();
            iter.remove();
        }
    }

    public void testSplit(){
        SplayTreeMap<Integer,Integer> map = init();
        assertEquals(Integer.valueOf(3),map.firstKey());
        SplayTreeMap<Integer,Integer> zero = map.split(3);
        assertEquals(1,zero.size());
        assertEquals(Integer.valueOf(3),zero.firstKey());
    }

    public void testRanking(){
        SplayTreeMap<Integer,Integer> map = init();
        assertEquals(Integer.valueOf(5),map.get(2));
        assertEquals(Integer.valueOf(48),map.get(map.size()));
    }

    public void testGetSequence(){
        SplayTreeMap<Integer,Integer> map = init();
        assertEquals(0,map.getSequence(3));
        assertEquals(17,map.getSequence(48));
        assertEquals(18,map.getSequence(49));
        assertEquals(0,map.getSequence(2));
    }
}
