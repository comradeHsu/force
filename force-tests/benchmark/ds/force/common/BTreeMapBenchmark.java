package ds.force.common;

import ds.force.BTreeMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2,time = 5)
@Measurement(iterations = 4,time = 1)
public class BTreeMapBenchmark {

    @Param({"10000", "100000", "500000", "1000000"})
    private int n;

    private BTreeMap<Integer,Integer> bTreeMap;

    private TreeMap<Integer,Integer> treeMap;

    Random random = new Random();

    @Setup(Level.Trial)
    public void init() {
        bTreeMap = new BTreeMap<>(30);
        treeMap = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            int key = random.nextInt();
            bTreeMap.put(key,i);
            treeMap.put(key,i);
        }
    }

    @Benchmark
    public void bTreeMapGet() {
        int key = random.nextInt();
        bTreeMap.get(key);
    }

    @Benchmark
    public void treeMapGet() {
        int key = random.nextInt();
        treeMap.get(key);
    }

//    @Benchmark
//    public void bTreeMapPut() {
//        for (int i = 0; i < n; i++) {
//            int key = random.nextInt();
//            bTreeMap.put(key,i);
//        }
//    }
//
//    @Benchmark
//    public void treeMapPut() {
//        for (int i = 0; i < n; i++) {
//            int key = random.nextInt();
//            treeMap.put(key,i);
//        }
//    }

    @TearDown(Level.Trial) // 结束方法，在全部Benchmark运行之后进行
    public void allRemove() {
        bTreeMap.clear();
        treeMap.clear();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(BTreeMapBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}