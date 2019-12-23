package ds.force;

import ds.force.IntArrayList;
import junit.framework.TestCase;

import java.util.*;

public class BTreeMapTest extends TestCase {

    public String btreeToString(BTreeMap.BTreeNode node){
        if (node == null) return "";
        BTreeMap.BTreeNode treeNode = node;
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
        IntArrayList ints = new IntArrayList(new int[]{46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37});
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
        assertEquals("-->3-->19-->27-->14|20-->37-->48-->46-->34",btreeToString(map.root));
        map.remove(14);
        assertEquals("-->3|19-->27-->20-->37-->48-->46-->34",btreeToString(map.root));
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
        Random random = new Random();
//        List<Integer> list = new ArrayList<>();
//        for (int i = 0; i < 25; i++) {
//            int key = random.nextInt(50);
//            list.add(key);
//        }
//        list.forEach(k -> map.put(k,k));
        for (int i = 0; i < 10; i++) {
            int key = random.nextInt(50);
            map.remove(key);
        }
    }
}
