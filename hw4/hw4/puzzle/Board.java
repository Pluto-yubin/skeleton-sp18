package hw4.puzzle;
import edu.princeton.cs.algs4.Queue;


public class Board implements WorldState {
    private int[][] tiles;
    private static final int BLANK = 0;
    public Board(int[][] tiles) {
        int[][] temp = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                temp[i][j] = tiles[i][j];
            }
        }
        this.tiles = temp;
    }

    public int tileAt(int i, int j) {
        if (i >= size() || i < 0 || j >= size() || j < 0) {
            throw new ArrayIndexOutOfBoundsException("i or j should be between 0 with " + size());
        }
        return tiles[i][j];
    }

    /**
     * @author http://joshh.ug/neighbors.html
     * @return
     */
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == BLANK) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = BLANK;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = BLANK;
                }
            }
        }
        return neighbors;
    }

    public int size() {
        return tiles.length;
    }

    public int hamming() {
        int dis = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tileAt(i, j) == BLANK) {
                    continue;
                }
                if (tileAt(i, j) != xyTo1D(i, j)) {
                    dis += 1;
                }
            }
        }
        return dis;
    }

    private int xyTo1D(int x, int y) {
        return x * size() + y + 1;
    }

    private int toX(int v) {
        return (v - 1) / size();
    }

    private int toY(int v) {
        return (v - 1) % size();
    }

    public int manhattan() {
        int dis = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                int index = tileAt(i, j);
                if (index != BLANK && !inCorrectIndex(i, j, index)) {
                    dis += Math.abs(toX(index) - i) + Math.abs(toY(index) - j);
                }
            }
        }
        return dis;
    }

    private boolean inCorrectIndex(int i, int j, int index) {
        return toX(index) == i && toY(index) == j;
    }

    public int estimatedDistanceToGoal() {
        return hamming();
    }

    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (!(y instanceof Board)) {
            return false;
        }
        if (y.hashCode() == hashCode()) {
            return true;
        }
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tileAt(i, j) != ((Board) y).tileAt(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /** Returns the string representation of the board.
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

}
