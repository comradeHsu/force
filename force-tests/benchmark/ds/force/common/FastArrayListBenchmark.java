package ds.force.common;

import ds.force.FastArrayList;
import ds.force.IntArrayList;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(2)
@Warmup(iterations = 2)
@Measurement(iterations = 4)
public class FastArrayListBenchmark {

    @Param({"1000", "10000", "50000", "100000"})
    private int n;

    private FastArrayList array;

    private List<Integer> list;

    @Setup(Level.Trial)
    public void init() {
        array = new FastArrayList();
        list = new ArrayList<>(0);
        for (int i = 0; i < n; i++) {
            array.add(i);
            list.add(i);
        }
    }
}
