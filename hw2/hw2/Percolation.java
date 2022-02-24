package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static WeightedQuickUnionUF unionUF;
    private static boolean[][] grids;
    private static int ROOT = 0;
    private static int TAIL = 0;
    private int count;
    public Percolation(int N) {
        if (N < 0) {
            throw new IllegalArgumentException("N should be positive");
        }
        grids = new boolean[N][N];
        unionUF = new WeightedQuickUnionUF(N * N + 2);
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
        grids[row][col] = true;
        count += 1;
        int index = xyTo1D(row, col);
        if (index < grids.length) {
            unionUF.union(index, ROOT);
        } else if (index >= grids.length * (grids.length - 1)) {
            unionUF.union(index, TAIL);
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
        return unionUF.connected(ROOT, index);
    }

    public int numberOfOpenSites() {
        return count;
    }

    public boolean percolates() {
        return unionUF.connected(ROOT, TAIL);
    }

    public static void main(String[] args) {
        Percolation percolation = new Percolation(5);
        percolation.open(3, 4);
        percolation.open(2, 4);
        System.out.println(unionUF.connected(14, 19));
        percolation.open(2, 2);
        percolation.open(2, 3);
        System.out.println(unionUF.connected(12, 19));
        percolation.open(0, 2);
        percolation.open(1, 2);
        System.out.println(percolation.isFull(0, 2));
        System.out.println(percolation.isFull(1, 2));
        System.out.println(percolation.isFull(3, 4));
        System.out.println(unionUF.connected(4, 12));
        System.out.println(percolation.isFull(0, 0));
    }
}
