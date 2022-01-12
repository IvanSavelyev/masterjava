package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, int threadNumber) throws InterruptedException, ExecutionException {
//        final int matrixSize = matrixA.length;
//        int[][] matrixC = new int[matrixSize][matrixSize];
//        LinkedList<Future<int[][]>> taskFutures = new LinkedList<>();
//        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
//
//        int part = matrixSize / threadNumber;
//        if (part < 1) {
//            part = 1;
//        }
//        for (int j = 0; j < matrixSize; j += part) {
//            final int finalJ = j;
//            final int finalPart = part;
//            int finalJ1 = j;
//            taskFutures.add(executor.submit(() -> {
//                for (int i = finalJ; i < finalJ + finalPart; i++) {
//                    for (int k = 0; k < matrixSize; k++) {
//                        for (int m = 0; m < matrixSize; m++) {
//                            matrixC[i][finalJ1] = matrixA[i][k] * matrixB[k][m];
//                        }
//                    }
//                }
//                return matrixC;
////                for (int k = 0; k < matrixSize; k++) {
////                    thatColumn[k] = matrixB[k][finalJ];
////                }
////                for (int i = 0; i < matrixSize; i++) {
////                    final int[] thisRow = matrixA[i];
////                    int sum = 0;
////                    for (int k = 0; k < matrixSize; k++) {
////                        sum += thisRow[k] * thatColumn[k];
////                    }
////                    matrixC[i][finalJ] = sum;
////                }
//            }));
//        }
//        int[][] C = new int[matrixSize][matrixSize];
//        int[][] CR;
//        int start = 0;
//        for (Future<int[][]> taskFuture : taskFutures) {
//            CR = taskFuture.get();
//            for (int i = start; i < part + start; i += 1) {
//                C[i] = CR[i];
//            }
//            start += part;
//        }
//        executor.shutdown();
//        return C;
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
