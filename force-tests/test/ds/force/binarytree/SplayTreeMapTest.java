package ds.force.binarytree;

import junit.framework.TestCase;

public class SplayTreeMapTest extends TestCase {

    public void testPut(){
        SplayTreeMap<Integer,Integer> splayTreeMap = new SplayTreeMap<>();
        for (int i = 1; i < 10; i++) {
            splayTreeMap.put(i,i);
        }
        splayTreeMap.get(1);
        splayTreeMap.get(6);
    }
}
