package ds.force.common;

import ds.force.IntArrayList;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All) // 吞吐量
@OutputTimeUnit(TimeUnit.SECONDS) // 结果所使用的时间单位
@State(Scope.Thread) // 每个测试线程分配一个实例
@Fork(2) // Fork进行的数目
@Warmup(iterations = 2) // 先预热4轮
@Measurement(iterations = 4) // 进行10轮测试
public class IntArrayListBenchmark {

    @Param({"1000", "10000", "50000", "100000"}) // 定义四个参数，之后会分别对这四个参数进行测试
    private int n;

    private IntArrayList array;

    private List<Integer> list;

    @Setup(Level.Trial) // 初始化方法，在全部Benchmark运行之前进行
    public void init() {
        array = new IntArrayList(0);
        list = new ArrayList<>(0);
        for (int i = 0; i < n; i++) {
            array.add(i);
            list.add(i);
        }
    }

    @Benchmark
    public void arrayTraverse() {
        for (int i = 0; i < n; i++) {
            array.get(i);
        }
    }

    @Benchmark
    public void listTraverse() {
        for (int i = 0; i < n; i++) {
            list.get(i);
        }
    }

    @TearDown(Level.Trial) // 结束方法，在全部Benchmark运行之后进行
    public void arrayRemove() {
        for (int i = 0; i < n; i++) {
            array.removeAtIndex(0);
            list.remove(0);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(IntArrayListBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}
