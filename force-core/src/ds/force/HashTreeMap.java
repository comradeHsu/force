package ds.force;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class  HashTreeMap<K extends Number,V> implements Map<K,V> {

    private static final IntArrayList primes = IntArrayList.of(7,11,13,17,19,23,29,31,37,41);

    transient Entry<K,V> root;

    transient final IntUnaryFunction<K> modFunction;

    public HashTreeMap() {
        this.root = new Entry<>();
        this.modFunction = null;
    }

    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    private int remainder(K key, int prime){
        if (modFunction != null) {
            return modFunction.applyAsInt(key,prime);
        }
        return (int)(key.longValue() % prime);
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {

        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return null;
    }

    private static class Entry<K extends Number, V> implements Map.Entry<K,V> {

        K key;

        V value;

        Entry<K, V> slots;

        boolean deleted;

        Entry() {

        }

        Entry(K key, V value, int prime){

        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
