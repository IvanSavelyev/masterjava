package ru.javaops.masterjava.matrix;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply2(int[][] matrixA, int[][] matrixB, ExecutorService executorService) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;

        class ColumnMultiplyResult {
            private final int col;
            private final int[] columnC;

            public ColumnMultiplyResult(int col, int[] columnC) {
                this.col = col;
                this.columnC = columnC;
            }
        }

        final CompletionService<ColumnMultiplyResult> completionService = new ExecutorCompletionService<>(executorService);

        for (int j = 0; j < matrixSize; j++) {
            final int col = j;
            final int[] columnB = new int[matrixSize];
            for (int k = 0; k < matrixSize; k++) {
                columnB[k] = matrixB[k][col];
            }
            completionService.submit(() -> {
                final int[] columnC = new int[matrixSize];

                for (int row = 0; row < matrixSize; row++) {
                    final int[] rowA = matrixA[row];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += rowA[k] * columnB[k];
                    }
                    columnC[row] = sum;
                }
                return new ColumnMultiplyResult(col, columnC);
            });
        }
        final int[][] matrixC = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            ColumnMultiplyResult result = completionService.take().get();
            for (int k = 0; k < matrixSize; k++) {
                matrixC[k][result.col] = result.columnC[k];
            }
        }
        return matrixC;
    }

    public static int[][] concurrentMultiply3(int[][] matrixA, int[][] matrixB, ExecutorService executorService) throws InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixResult = new int[matrixSize][matrixSize];
        final int threadCount = Runtime.getRuntime().availableProcessors();
        final int maxIndex = matrixSize * matrixSize;
        final int cellsInThread = maxIndex / threadCount;
        final int[][] matrixBTranspose = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBTranspose[i][j] = matrixB[j][i];
            }
        }

        Set<Callable<Void>> threads = new HashSet<>();

        int fromIndex = 0;
        for (int i = 0; i < threadCount; i++) {
            final int toIndex = i == threadCount ? maxIndex : fromIndex + cellsInThread;
            final int firstIndexFinal = fromIndex;
            threads.add(() -> {
                for (int j = firstIndexFinal; j < toIndex; j++) {
                    final int row = j / matrixSize;
                    final int col = j % matrixSize;

                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += matrixA[row][k] * matrixBTranspose[col][k];
                    }
                    matrixResult[row][col] = sum;
                }
                return null;
            });
            fromIndex = toIndex;
        }
        executorService.invokeAll(threads);
        return matrixResult;
    }

    public static int[][] concurrentMultiply4(int[][] matrixA, int[][] matrixB, ExecutorService executorService) throws Exception {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][];

        List<Callable<Void>> tasks = new ArrayList<>(matrixSize);
        for (int j = 0; j < matrixSize; j++) {
            final int row = j;
            final int[] rowA = matrixA[row];
            tasks.add(() -> {
                final int[] rowC = new int[matrixSize];
                for (int idx = 0; idx < matrixSize; idx++) {
                    final int elA = rowA[idx];
                    final int[] rowB = matrixB[idx];
                    for (int col = 0; col < matrixSize; col++) {
                        rowC[col] += elA * rowB[col];
                    }
                }
                matrixC[row] = rowC;
                return null;
            });
        }
        executorService.invokeAll(tasks);
        return matrixC;
    }

    public static int[][] concurrentMultiplyStreams(int[][] matrixA, int[][] matrixB) throws ExecutionException, InterruptedException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        new ForkJoinPool(Runtime.getRuntime().availableProcessors() - 1).submit(
                () -> IntStream.range(0, matrixSize)
                    .parallel()
                    .forEach(row -> {
                        final int[] rowA = matrixA[row];
                        final int[] rowC = matrixC[row];

                        for (int idx = 0; idx < matrixSize; idx++) {
                            final int elA = rowA[idx];
                            final int[] rowB = matrixB[idx];
                            for (int col = 0; col < matrixSize; col++) {
                                rowC[col] += elA * rowB[col];
                            }
                        }
                    })).get();
        return matrixC;
    }


    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, int threadNumber) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        int[][] C = new int[matrixSize][matrixSize];
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
        LinkedList<Future<int[][]>> list = new LinkedList<>();

        int part = matrixSize / threadNumber;
        if (part < 1) {
            part = 1;
        }
        for (int i = 0; i < matrixSize; i += part) {
            final int finalI = i;
            final int finalPart = part;
            list.add(executor.submit(() -> {
                for (int m = finalI; m < finalI + finalPart; m++) {
                    for (int k = 0; k < matrixSize; k++) {
                        for (int j = 0; j < matrixSize; j++) {
                            C[m][j] += matrixA[m][k] * matrixB[k][j];
                        }
                    }
                }
                return C;
            }));
        }

        // now retrieve the result
        int start = 0;
        int CF[][];
        for (Future<int[][]> future : list) {
            CF = future.get();
            for (int i = start; i < start + part; i += 1) {
                C[i] = CF[i];
            }
            start += part;
        }
        executor.shutdown();
        return C;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int[] thatColumn = new int[matrixSize];

        try {
            for (int j = 0; ; j++) {
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][j];
                }

                for (int i = 0; i < matrixSize; i++) {
                    int[] thisRow = matrixA[i];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = sum;
                }
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        return matrixC;
    }

    public static int[][] singleThreadMultiply2(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int row = 0; row < matrixSize; row++) {
            final int[] rowA = matrixA[row];
            final int[] rowC = matrixC[row];
            for (int idx = 0; idx < matrixSize; idx++) {
                final int elA = rowA[idx];
                final int[] rowB = matrixB[idx];
                for (int col = 0; col < matrixSize; col++) {
                    rowC[col] += elA * rowB[col];
                }
            }
            matrixC[row] = rowC;
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
