package ds.force;

import ds.force.primitive.IntArrayList;
import junit.framework.TestCase;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;

public class BTreeMapTest extends TestCase {

    public static <K,V> String btreeToString(BTreeMap.BTreeNode<K,V> node){
        if (node == null) return "";
        BTreeMap.BTreeNode<K,V> treeNode = node;
        StringBuilder bts = new StringBuilder();
        for(BTreeMap.BTreeNode bTreeNode : treeNode.childes){
            if (bTreeNode != null){
                bts.append(btreeToString(bTreeNode));
            }
        }
        bts.append("-->");
        for (BTreeMap.NodeEntry key :treeNode.keys){
            if (key != null) bts.append(key.key.toString()+"|");
        }
        return bts.subSequence(0,bts.length()-1).toString();
    }

    public void testPut(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>();
        map.put(3,3);
        map.put(2,2);
        map.put(4,4);
        map.put(5,5);
        for (int i = 6; i < 20; i++) {
            map.put(i,i);
        }
    }

    public void testRemove(){
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        BTreeMap<Integer,Integer> map = new BTreeMap<>();
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        System.out.println(btreeToString(map.root));
        assertEquals("-->3|5-->12|14-->19|20-->7|16-->27-->35|37-->34-->41-->46|48-->42-->21|40",btreeToString(map.root));
        map.remove(7);
        assertEquals("-->3-->12|14-->19|20-->5|16-->27-->35|37-->34-->41-->46|48-->42-->21|40",btreeToString(map.root));
        map.remove(35);
        assertEquals("-->3-->12|14-->5-->19|20-->27-->37-->21|34-->41-->46|48-->42-->16|40",btreeToString(map.root));
        map.remove(12);
        assertEquals("-->3-->14-->19|20-->5|16-->27-->37-->34-->41-->46|48-->42-->21|40",btreeToString(map.root));
        map.remove(5);
        assertEquals("-->3|14-->19|20-->16-->27-->37-->34-->41-->46|48-->42-->21|40",btreeToString(map.root));
        map.remove(40);
        assertEquals("-->3|14-->19|20-->16-->27-->37|41-->46|48-->34|42-->21",btreeToString(map.root));
        map.remove(42);
        assertEquals("-->3|14-->19|20-->16-->27-->37-->46|48-->34|41-->21",btreeToString(map.root));
        map.remove(16);
        assertEquals("-->3-->19|20-->27-->14|21-->37-->46|48-->41-->34",btreeToString(map.root));
        map.remove(41);
        assertEquals("-->3-->19|20-->14-->27-->37-->48-->34|46-->21",btreeToString(map.root));
        map.remove(21);
        assertEquals("-->3-->19|20-->14-->34|37-->48-->46-->27",btreeToString(map.root));
        map.remove(14);
        assertEquals("-->3-->20-->34|37-->48-->19|27|46",btreeToString(map.root));
        map.remove(20);
        assertEquals("-->3-->27-->37-->48-->19|34|46",btreeToString(map.root));
        map.remove(3);
        assertEquals("-->19|27-->37-->48-->34|46",btreeToString(map.root));
        map.remove(37);
        assertEquals("-->19-->34-->48-->27|46",btreeToString(map.root));
        map.remove(48);
        assertEquals("-->19-->34|46-->27",btreeToString(map.root));
        map.remove(27);
        assertEquals("-->19-->46-->34",btreeToString(map.root));
        map.remove(46);
        assertEquals("-->19|34",btreeToString(map.root));
        map.remove(34);
        assertEquals("-->19",btreeToString(map.root));
        map.remove(19);
        assertEquals("--",btreeToString(map.root));
    }

    public void testRemoves(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(3);
        Random random = new Random();
        for (int i = 0; i < 3000; i++) {
            int key = random.nextInt(1000);
            map.put(key,key);
        }
        parentEq(map);
        for (int i = 0; i < 30000; i++) {
            int key = random.nextInt(1000);
            map.remove(key);
            parentEq(map);
        }
    }

    public void testRemoveFixedData(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(3);
        Random random = new Random();
        IntArrayList list = IntArrayList.of(860,130,104,871,501,479,593,445,690,903,205,663,113,844,745,988,338,409,256,
                680,400,631,90,566,680,169,445,903,871,5);
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i),list.get(i));
        }
        parentEq(map);
        map.remove(412);
        parentEq(map);
        map.remove(559);
        parentEq(map);
        map.remove(48);
        parentEq(map);
        map.remove(496);
        parentEq(map);
        map.remove(939);
        parentEq(map);
        for (int i = 0; i < 30; i++) {
            int key = random.nextInt(1000);
            map.remove(key);
            System.out.println(key);
            parentEq(map);
        }
    }

    public void testIterator(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(3);
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

    public void testGet(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(10);
        map.put(3,3);
        assertNull(map.get(10));
        map.put(2,2);
        assertEquals(Integer.valueOf(3),map.get(3));
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            map.put(i,i);
        }
        for (int i = 0; i < 50; i++) {
            int key = random.nextInt(20);
            assertEquals(Integer.valueOf(key),map.get(key));
        }
    }

    public void testLowerEntry(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        assertEquals(null,map.lowerEntry(3));
        assertEquals(Integer.valueOf(3),map.lowerEntry(5).getKey());
        assertEquals(Integer.valueOf(12),map.lowerEntry(13).getKey());
        assertEquals(Integer.valueOf(21),map.lowerEntry(27).getKey());
        assertEquals(Integer.valueOf(27),map.lowerEntry(31).getKey());
        assertEquals(Integer.valueOf(48),map.lowerEntry(188).getKey());
    }

    public void testFloorEntry(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
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
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
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
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
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

    public void testBatchEntry(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
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
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
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

    public void testExtendFunc(){
        BTreeMap<Integer,Integer> map = new BTreeMap<>(2);
        assertEquals(Integer.valueOf(1),map.getOrDefault(5,1));
    }

    public void parentEq(BTreeMap map){
        Deque<BTreeMap.BTreeNode> stack = new ArrayDeque<>();
        stack.push(map.root);
        while (!stack.isEmpty()){
            BTreeMap.BTreeNode node = stack.pop();
            List<BTreeMap.BTreeNode> childes = node.childes;
            for (BTreeMap.BTreeNode treeNode: childes) {
                assert treeNode.parent == node;
                stack.push(treeNode);
            }
        }
    }
}
