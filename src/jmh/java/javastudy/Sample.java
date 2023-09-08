package javastudy;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime) // 벤치마크 대상 메서드를 실행하는 데 걸린 평균 시간 측정
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 벤치마크 결과를 밀리초 단위로 출력
@Fork(value = 2,jvmArgs = {"-Xms4G","-Xmx4G"}) // 4Gb의 힙공간을 제공한 환경에서 두 번 벤치마크를 수행해 결과의 신뢰성 확보
public class Sample {
    @State(Scope.Thread)
    public static class MyState {
        public long value = 500000000;

        @TearDown(Level.Invocation)
        public void tearDown() {
            System.gc();
        }
    }


    @Benchmark
    public long iterativeSum(MyState myState){
        long result = 0;
        for(long i = 1L; i<=myState.value; i++){
            result+=i;
        }
        return result;
    }

    @Benchmark
    public long sequentialSum(MyState myState){
        return Stream.iterate(1L, i-> i+1)
                .limit(myState.value)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelSum(MyState myState) {
        return Stream.iterate(1L, i -> i + 1)
                .limit(myState.value)
                .parallel() // 스트림을 병렬 스트림으로 변환
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long rangeSum(MyState myState){
        return LongStream.rangeClosed(1, myState.value).reduce(0L, Long::sum);
    }
    @Benchmark
    public long parallelRangeSum(MyState myState){
        return LongStream.rangeClosed(1, myState.value)
                .parallel()
                .reduce(0L, Long::sum);
    }

}
