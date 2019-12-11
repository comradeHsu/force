package ds.force.common;

import ds.force.HashTreeMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 1,time = 1)
@Measurement(iterations = 2,time = 1,timeUnit = TimeUnit.SECONDS)
public class HashTreeMapBenchmark {

    @Param({"10000", "50000", "10000"})
    private int n;

    private HashTreeMap<Long,Long> treeMap;

    private HashMap<Long,Long> hashMap;

    Random random = new Random();

    List<Long> longs = new ArrayList<>(500000);

    @Setup(Level.Trial)
    public void init() {
        treeMap = new HashTreeMap<>();
        hashMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            Long c = Math.abs(random.nextLong());
            treeMap.put(c,c);
            hashMap.put(c,c);
        }
    }

    @Benchmark
    public void treeMapPut() {
        for (int i = 0; i < n; i++) {
            Long c = Math.abs(random.nextLong());
            treeMap.put(c,c);
        }
    }

    @Benchmark
    public void hashMapPut() {
        for (int i = 0; i < n; i++) {
            Long c = Math.abs(random.nextLong());
            hashMap.put(c,c);
        }
    }

    @Benchmark
    public void treeMapGet() {
        for (int i = 0; i < n; i++) {
            Long c = Math.abs(random.nextLong());
            treeMap.get(c);
        }
    }

    @Benchmark
    public void hashMapGet() {
        for (int i = 0; i < n; i++) {
            Long c = Math.abs(random.nextLong());
            hashMap.get(c);
        }
    }

    @TearDown(Level.Trial) // 结束方法，在全部Benchmark运行之后进行
    public void destroy() {
        System.out.println("treemap:size="+hashMap.size());
        System.out.println("hashmap:size="+hashMap.size());
        treeMap.clear();
        hashMap.clear();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(HashTreeMapBenchmark.class.getSimpleName())
                .jvmArgs("-Xms2018M","-Xmx2048M").build();
        new Runner(options).run();
    }
}
