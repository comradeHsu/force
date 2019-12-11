package ds.force;

import java.util.Map;

public class BTreeMap<K,V> {
    /**
     * default minmum degree
     */
    private static final int DEFAULT_DEGREE = 2;
    /**
     * minmum degree,min t-1,max 2t-1
     */
    private int degree;

    private static class NodeEntry<K,V> implements Map.Entry<K,V> {

        K key;

        V value;

        @Override
        public K getKey() {
            return null;
        }

        @Override
        public V getValue() {
            return null;
        }

        @Override
        public V setValue(V value) {
            return null;
        }
    }

    private static class BTreeNode {

        NodeEntry[] keys;

        BTreeNode[] childes;

    }
}
