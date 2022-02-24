package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private Percolation percolation;
    private double[] threshold;
    private int T;
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (T <= 0 || N <= 0) {
            throw new IllegalArgumentException("T and N should be positive");
        }
        this.T = T;
        threshold = new double[T];
        for (int i = 0; i < T; i++) {
            percolation = pf.make(N);
            while (!percolation.percolates()) {
                int x = StdRandom.uniform(N);
                int y = StdRandom.uniform(N);
                if (!percolation.isOpen(x, y)) {
                    percolation.open(x, y);
                }
            }
            threshold[i] = percolation.numberOfOpenSites() / Math.pow(N, 2);
        }
    }
    public double mean() {
        return StdStats.mean(threshold);
    }

    public double stddev() {
        return StdStats.stddev(threshold);
    }
    public double confidenceLow() {
        double u = mean();
        return u - getNumber();
    }
    public double confidenceHigh() {
        double u = mean();
        return u + getNumber();
    }

    private double getNumber() {
        double d = 1.96 * Math.sqrt(stddev()) / Math.sqrt(T);
        return d;
    }


    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(5, 30, new PercolationFactory());
        System.out.println(stats.confidenceLow());
        System.out.println(stats.confidenceHigh());
    }
}
