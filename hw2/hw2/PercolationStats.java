package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private Percolation percolation;
    private int count = 0;
    private int size = 0;
    private double[] threshold;
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (T < 0 || N < 0) {
            throw new IllegalArgumentException("T and N should be positive");
        }
        percolation = pf.make(N);
        count = T;
        size = N;
    }
    public double mean() {
        return StdStats.mean(threshold);
    }

    public double stddev() {
        return StdStats.stddev(threshold);
    }
    public double confidenceLow() {
        getThreshold();
        double u = mean();
        return u - getNumber();
    }
    public double confidenceHigh() {
        getThreshold();
        double u = mean();
        return u + getNumber();
    }

    private double getNumber() {
        double d = 1.96 * Math.sqrt(stddev()) / Math.sqrt(count);
        return d;
    }

    private void getThreshold() {
        if (threshold != null) {
            return;
        }
        threshold = new double[count];
        for (int i = 0; i < count; i++) {
            PercolationStats stats = new PercolationStats(size, count, new PercolationFactory());
            this.percolation = stats.percolation;
            while (!percolation.percolates()) {
                int x = StdRandom.uniform(size);
                int y = StdRandom.uniform(size);
                if (!percolation.isOpen(x, y)) {
                    percolation.open(x, y);
                }
            }
            threshold[i] = percolation.numberOfOpenSites() / Math.pow(size, 2);
        }
    }
}
