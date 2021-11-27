package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int nworkers;
    /**
     * @param nworkers
     *      The number of workers that perform the sum.
     */
    public MultiThreadedSumMatrix(final int nworkers) {
        this.nworkers = nworkers;
    }

    private final class Worker extends Thread {

        private final int start;
        private final int size;
        private final double[][] matrix;
        private double sum;

        Worker(final int start, final int size, final double[][] matrix) {
            this.start = start;
            this.size = size;
            this.matrix = matrix;
        }

        public void run() {
            System.out.println("Starting worker, start: " + this.start + ", size: " + this.size);
//            this.sum = list.stream()
//                            .skip(start)
//                            .limit(size)
//                            .reduce(0d, (a, b) -> a + b).doubleValue();
            this.sum = IntStream.iterate(start, v -> v + 1)
                                .limit(size)
                                .mapToDouble(i -> matrix[i / matrix[0].length][i % matrix[0].length])
                                .sum();
        }

        public double getResult() {
            return this.sum;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final int length = matrix.length * matrix[0].length;
        final int partSize = length / this.nworkers;
        final int delta = length % this.nworkers;
        return IntStream.iterate(0, threadNum -> threadNum + 1)
                        .limit(this.nworkers)
                        .mapToObj(threadNum -> new Worker(partSize * threadNum, threadNum == this.nworkers - 1 
                                                                                ? partSize + delta // The last thread adds the remaining values.
                                                                                : partSize, matrix))
                        .peek(Thread::start)
                        .peek(MultiThreadedSumMatrix::join)
                        .mapToDouble(Worker::getResult)
                        .sum();
    }

    private static void join(final Thread thread) {
        boolean joined = false;
        while (!joined) {
            try {
                thread.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Double> matrixToList(final double[][] matrix) {
        final List<Double> out = new ArrayList<>();
        for (final double[] array : matrix) {
            for (final double d : array) {
                out.add((Double) d);
            }
        }
        return out;
    }
}
