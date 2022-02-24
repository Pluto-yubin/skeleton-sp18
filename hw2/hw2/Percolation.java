package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.Arrays;

public class Percolation {
    private  WeightedQuickUnionUF unionUF;
    private WeightedQuickUnionUF unionUFHelp;
    private boolean[][] grids;
    private int ROOT = 0;
    private int TAIL = 0;
    private int count;
    public Percolation(int N) {
        if (N < 0) {
            throw new IllegalArgumentException("N should be positive");
        }
        grids = new boolean[N][N];
        unionUF = new WeightedQuickUnionUF(N * N + 2);
        unionUFHelp = new WeightedQuickUnionUF(N * N + 1);
        ROOT = N * N;
        TAIL = N * N + 1;
    }

    private int xyTo1D(int row, int col) {
        return row * grids.length + col;
    }

    public void open(int row, int col) {
        if (col < 0 || row < 0 || col >= grids.length || row >= grids.length) {
            throw new ArrayIndexOutOfBoundsException("Just like what you see");
        }
        if (isOpen(row, col)) {
            return;
        }
        grids[row][col] = true;
        count += 1;
        int index = xyTo1D(row, col);
        if (index < grids.length) {
            unionUF.union(ROOT, index);
            unionUFHelp.union(ROOT, index);
        }
        if (index >= grids.length * (grids.length - 1)) {
            unionUF.union(TAIL, index);
        }
        connectTo(row - 1, col, index);
        connectTo(row + 1, col, index);
        connectTo(row, col - 1, index);
        connectTo(row, col + 1, index);
    }

    private void connectTo(int row, int col, int index) {
        if (col < 0 || row < 0 || col >= grids.length || row >= grids.length) {
            return;
        }
        if (isOpen(row, col)) {
            unionUF.union(xyTo1D(row, col), index);
            unionUFHelp.union(xyTo1D(row, col), index);
        }
    }
    public boolean isOpen(int row, int col) {
        if (col < 0 || row < 0 || col >= grids.length || row >= grids.length) {
            throw new ArrayIndexOutOfBoundsException("Just like what you see");
        }
        return grids[row][col];
    }

    public boolean isFull(int row, int col) {
        if (col < 0 || row < 0 || col >= grids.length || row >= grids.length) {
            throw new ArrayIndexOutOfBoundsException("Just like what you see");
        }
        int index = xyTo1D(row, col);
        return unionUFHelp.connected(ROOT, index);
    }

    public int numberOfOpenSites() {
        return count;
    }

    public boolean percolates() {
        return unionUF.connected(ROOT, TAIL);
    }

    public void clear() {
        int N = grids.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                grids[i][j] = false;
            }
        }
        unionUF = new WeightedQuickUnionUF(N * N + 2);
        unionUFHelp = new WeightedQuickUnionUF(N * N + 1);
        count = 0;
    }

    public static void main(String[] args) {
        Percolation percolation = new Percolation(3);
        percolation.open(0, 2);
        percolation.open(1, 2);
        percolation.open(2, 2);
        percolation.open(2, 0);
        System.out.println(percolation.isFull(2, 0));
        System.out.println(percolation.percolates());
    }
}
