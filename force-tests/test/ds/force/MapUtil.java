package ds.force;

import ds.force.primitive.IntArrayList;

import java.util.Map;

public class MapUtil {

    public static <T extends Map> T init(Class<T> cla){
        T map = null;
        try {
            map = cla.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        IntArrayList ints = IntArrayList.of(46,5,42,41,41,7,21,40,48,34,41,16,14,3,19,27,35,34,27,12,7,41,20,41,37);
        for (int i = 0; i < ints.size(); i++) {
            map.put(ints.get(i),ints.get(i));
        }
        return map;
    }
}
