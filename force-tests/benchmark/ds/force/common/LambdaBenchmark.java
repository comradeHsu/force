package ds.force.common;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.ToIntBiFunction;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2,time = 5)
@Measurement(iterations = 10,time = 1)
public class LambdaBenchmark {

    Random random = new Random();

    ToIntBiFunction<Integer,Integer> toIntBiFunction = (integer, integer2) -> integer - integer2;

    @Benchmark
    public void classCompare() {
        int key = random.nextInt();
        compare(key,10,toIntBiFunction);
    }

    @Benchmark
    public void lambdaCompare() {
        int key = random.nextInt();
        compare(key, 10, (integer, integer2) -> integer - integer2);
    }

    @Benchmark
    public void innerClassCompare() {
        int key = random.nextInt();
        compare(key, 10, new ToIntBiFunction<Integer, Integer>() {
            @Override
            public int applyAsInt(Integer integer, Integer integer2) {
                return integer - integer2;
            }
        });
    }

    public int compare(Integer a,Integer b,ToIntBiFunction<Integer,Integer> toIntBiFunction){
        return toIntBiFunction.applyAsInt(a,b);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder().include(LambdaBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }
}
