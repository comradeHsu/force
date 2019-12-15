package ds.force;

import junit.framework.TestCase;

public class BTreeMapTest extends TestCase {

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
        BTreeMap<Integer,Integer> map = new BTreeMap<>();
        map.put(3,3);
        map.put(2,2);
        map.put(4,4);
        map.put(5,5);
        map.remove(2);
        map.remove(3);
    }
}
