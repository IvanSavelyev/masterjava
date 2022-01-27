package ru.javaops.masterjava.matrix;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10)
@Measurement(iterations = 10)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(5)
@Fork(10)
@Timeout(time = 5, timeUnit = TimeUnit.MINUTES)
public class MatrixBenchmark {

    @Param({"100", "1000"})
    private int matrixSize;

    private static final int THREAD_NUMBER = 10;
    private final static ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);

    private static int[][] matrixA;
    private static int[][] matrixB;

    @Setup
    public void setUp() {
        matrixA = MatrixUtil.create(matrixSize);
        matrixB = MatrixUtil.create(matrixSize);
    }

    @Benchmark
    public int[][] singleThreadMultiply() throws Exception {
        return MatrixUtil.singleThreadMultiply(matrixA, matrixB);
    }

    @Benchmark
    public int[][] singleThreadMultiply2() throws Exception {
        return MatrixUtil.singleThreadMultiply2(matrixA, matrixB);
    }

    @Benchmark
    public int[][] concurrentMultiply() throws Exception {
        return MatrixUtil.concurrentMultiply(matrixA, matrixB, THREAD_NUMBER);
    }

    @Benchmark
    public int[][] concurrentMultiply2() throws Exception {
        return MatrixUtil.concurrentMultiply2(matrixA, matrixB, executorService);
    }

    @Benchmark
    public int[][] concurrentMultiply3() throws Exception {
        return MatrixUtil.concurrentMultiply3(matrixA, matrixB, executorService);
    }

    @Benchmark
    public int[][] concurrentMultiply4() throws Exception {
        return MatrixUtil.concurrentMultiply4(matrixA, matrixB, executorService);
    }

    @Benchmark
    public int[][] concurrentMultiplyStreams() throws Exception {
        return MatrixUtil.concurrentMultiplyStreams(matrixA, matrixB);
    }

    @TearDown
    public void tearDown() {
        executorService.shutdown();
    }
}
