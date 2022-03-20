/**
 * @auther Zhang Yubin
 * @date 2022/3/20 16:32
 */
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    public Picture picture() {
        return picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public double energy(int col, int row) {
        if (col < 0 || row < 0 || col >= width() || row >= height()) {
            return Double.MAX_VALUE;
        }
        Color left = picture.get(getOffsetCol(col, -1), row);
        Color right = picture.get(getOffsetCol(col, 1), row);
        Color up = picture.get(col, getOffsetRow(row, 1));
        Color down = picture.get(col, getOffsetRow(row, -1));
        return getDeltaSquare(left, right) + getDeltaSquare(up, down);
    }

    private double getDeltaSquare(Color c1, Color c2) {
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        double b = c1.getBlue() - c2.getBlue();
        return r * r + g * g + b * b;
    }

    private int getOffsetCol(int x, int offset) {
        return (x + offset + width()) % width();
    }

    private int getOffsetRow(int y, int offset) {
        return (y + offset + height()) % height();
    }

    public int[] findHorizontalSeam() {
        double[][] table = getEnergyTable(width(), height(), true);
        return getPath(width(), height(), table);
    }

    // get the table contains energy accumulate from top to down
    // by M(i,j)=e(i,j)+min(M(i−1,j−1),M(i,j−1),M(i+1,j−1))
    // if horizon, x means width and y means heigth
    // so if is horizon, i represents col and j represents row
    private double[][] getEnergyTable(int x, int y, boolean isHorizon) {
        double[][] table = new double[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                if (i == 0) {
                    if (!isHorizon) {
                        table[i][j] = energy(j, i);
                    } else {
                        table[i][j] = energy(i, j);
                    }
                    continue;
                } else if (j == 0) {
                    table[i][j] = Math.min(table[i - 1][j], table[i - 1][j + 1]);
                } else if (j == y - 1) {
                    table[i][j] = Math.min(table[i - 1][j], table[i - 1][j - 1]);
                } else {
                    table[i][j] = getMin(table[i - 1][j - 1], table[i - 1][j], table[i - 1][j + 1]);
                }

                if (isHorizon) {
                    table[i][j] += energy(i, j);
                } else {
                    table[i][j] += energy(j, i);
                }
            }
        }
        return table;
    }
    public int[] findVerticalSeam() {
        double[][] table = getEnergyTable(height(), width(), false);
        return getPath(height(), width(), table);
    }

    private int[] getPath(int x, int y, double[][] table) {
        int[] path = new int[x];
        int pathIdx = 0;
        for (int i = 0; i < y; i++) {
            if (table[x - 1][i] < table[x - 1][pathIdx]) {
                pathIdx = i;
            }
        }
        path[x - 1] = pathIdx;
        for (int i = x - 2; i >= 0; i--) {
            path[i] = getMinIndex(table, i, pathIdx);
            pathIdx = path[i];
        }
        return path;
    }


    // i --> row, j --> col,
    // get the col index of min energy value of table[i][j], table[i][j-1] and table[i][j+1]
    private int getMinIndex(double[][] table, int i, int j) {
        if (j == 0) {
            return table[i][j] < table[i][j + 1] ? j : j + 1;
        } else if (j == table[i].length - 1) {
            return table[i][j] < table[j][j - 1] ? j : j - 1;
        }
        if (table[i][j] < table[i][j - 1]) {
            return table[i][j] < table[i][j + 1] ? j : j + 1;
        } else {
            return table[i][j - 1] < table[i][j + 1] ? j - 1 : j + 1;
        }
    }

    private double getMin(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam.length != width() || !isValidSeam(seam)) {
            throw new IllegalArgumentException();
        }
        SeamRemover.removeHorizontalSeam(picture, seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (seam.length != height() || !isValidSeam(seam)) {
            throw new IllegalArgumentException();
        }
        SeamRemover.removeVerticalSeam(picture, seam);
    }

    private boolean isValidSeam(int[] seam) {
        for (int i = 0, j = 1; j < seam.length; i++, j++) {
            if (Math.abs(seam[i] - seam[j]) > 1) {
                return false;
            }
        }
        return true;
    }

}
