package ds.force;

import junit.framework.TestCase;

import java.util.Comparator;
import java.util.TreeMap;

public class FHQTreapSetTest extends TestCase {

    public void testAdd(){
        TreeMap<Integer,String> map = new TreeMap<>((o1, o2) -> {
            if (o1.equals(o2 * 2)) return 0;
            return o1 - o2;
        });
        map.put(1,"1");
        map.put(2,"2");
        System.out.println(map.get(1));
        FHQTreapSet<Integer> set = new FHQTreapSet<>();
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        assertFalse(set.add(2));
    }
}
