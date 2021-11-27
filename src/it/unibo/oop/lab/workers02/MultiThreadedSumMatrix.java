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
        private final List<Double> list;
        private double sum;

        Worker(final int start, final int size, final List<Double> list) {
            this.start = start;
            this.size = size;
            this.list = list;
        }

        public void run() {
            System.out.println("Starting worker, start: " + this.start + ", size: " + this.size);
            this.sum = list.stream()
                            .skip(start)
                            .limit(size)
                            .reduce(0d, (a, b) -> a + b).doubleValue();
        }

        public double getResult() {
            return this.sum;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        final List<Double> list = matrixToList(matrix);
        final int partSize = list.size() / this.nworkers;
        final int delta = list.size() % this.nworkers;
        return IntStream.iterate(0, threadNum -> threadNum + 1)
                        .limit(this.nworkers)
                        .mapToObj(threadNum -> new Worker(partSize * threadNum, threadNum == this.nworkers - 1 
                                                                                ? partSize + delta // The last thread adds the remaining values.
                                                                                : partSize, list))
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

    private List<Double> matrixToList(final double[][] matrix) {
        final List<Double> out = new ArrayList<>();
        for (final double[] array : matrix) {
            for (final double d : array) {
                out.add((Double) d);
            }
        }
        return out;
    }
}
