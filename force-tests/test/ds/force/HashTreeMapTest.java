package ds.force;

import ds.force.primitive.IntArrayList;
import junit.framework.TestCase;

import java.util.BitSet;
import java.util.Map;
import java.util.Random;

public class HashTreeMapTest extends TestCase {

    public void testExtendPrimes(){
        BitSet set = new BitSet(2);
        set.set(6);
        set.set(12000);
        IntArrayList primes = IntArrayList.of(5,7,11,13,17,19,23,29,31,37,41,43,47);
        int lastPrime = primes.get(primes.size()-1);
        int size = lastPrime + (lastPrime >>> 1);
        BitSet bitSet = new BitSet(size);
        int half = size >>> 1;
        for(int i = 2; i <= half;i++) {
            if(bitSet.get(i)) {
                continue;
            }
            int count = size / i;
            for( int j = 2; j <= count; ++j) {
                bitSet.set(i * j);
            }
        }
        for (int x = lastPrime; x <= size; x++){
            if(!bitSet.get(x) && x > lastPrime) {
                primes.add(x);
            }
        }
    }

    public void testBalance(){
        Map<Integer,Integer> map = new HashTreeMap<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Integer lon = Math.abs(random.nextInt());
            map.put(lon,lon);
        }
        System.out.println(map.size());
    }
}
