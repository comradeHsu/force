package ds.force.primitive;

import junit.framework.TestCase;

public class CharObjectMapTest extends TestCase {

    public void testGetAndSize(){
        CharObjectMap<Integer> map = new CharObjectMap<>();
        map.put('1',1);
        map.put('2',2);
        map.put('中',6);
        assertEquals(Integer.valueOf(6),map.get('中'));
        assertEquals(Integer.valueOf(1),map.get('1'));
        assertEquals(3,map.size());
    }

    public void testPut(){
        CharObjectMap<Integer> map = new CharObjectMap<>();
        map.put('1',1);
        map.put('中',6);
        assertEquals(Integer.valueOf(6),map.get('中'));
        assertNull(map.get('2'));
        assertEquals(2,map.size());
        map.put('中',12);
        assertEquals(Integer.valueOf(12),map.get('中'));
        assertEquals(2,map.size());
    }

    public void testRemove(){
        CharObjectMap<Integer> map = new CharObjectMap<>();
        map.put('1',1);
        map.put('中',6);
        assertEquals(Integer.valueOf(6),map.get('中'));
        assertNull(map.get('2'));
        assertEquals(2,map.size());
        map.put('中',12);
        map.remove('1');
        assertEquals(Integer.valueOf(12),map.get('中'));
        assertEquals(1,map.size());
        assertNull(map.get('1'));
    }
}
