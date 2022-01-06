package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    public static void addHexagon(TETile[][] tiles, int s) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
        int size = s + (s - 1) * 2;
        getUpperTriangle(tiles, s);
        getLowerTriangle(tiles, s);
    }
    private static int getStartX(int s) {
        int size = s + (s - 1) * 2;
        return WIDTH / 2 - size / 2;
    }

    private static int getStartY(int s) {
        return HEIGHT / 2 - 1;
    }

    private static void getUpperTriangle(TETile[][] tiles, int s) {
        int x = getStartX(s);
        int y = getStartY(s);
        int size = s + (s - 1) * 2;
        while (size >= s) {
            for (int i = x; i < x + size; i++) {
                tiles[i][y] = Tileset.FLOWER;
            }
            size -= 2;
            y -= 1;
            x += 1;
        }
    }

    private static void getLowerTriangle(TETile[][] tiles, int s) {
        int x = getStartX(s);
        int y = getStartY(s) + 1;
        int size = s + (s - 1) * 2;
        while (size >= s) {
            for (int i = x; i < x + size; i++) {
                tiles[i][y] = Tileset.FLOWER;
            }
            size -= 2;
            y += 1;
            x += 1;
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        addHexagon(hexTiles, 3);
        ter.renderFrame(hexTiles);
    }
}
