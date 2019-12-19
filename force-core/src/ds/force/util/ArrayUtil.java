package ds.force.util;

public class ArrayUtil {

    public static void addAll(Object[] src, int index, Object[] c){
        int numNew = c.length;
        int numMoved = src.length - index - c.length;
        if (numMoved > 0)
            System.arraycopy(src, index, src, index + numNew - 1,
                    numMoved);

        System.arraycopy(c, 0, src, index, numNew);
    }

    public static void add(Object[] src, int index, Object c){
        System.arraycopy(src, index, src, index + 1,
                src.length - index -1);
        src[index] = c;
    }

    public static void remove(Object[] src, int index){
        int numMoved = src.length - index - 1;
        if (numMoved > 0)
            System.arraycopy(src, index+1, src, index,
                    numMoved);
        src[src.length-1] = null;
    }
}
