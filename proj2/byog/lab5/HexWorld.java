package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;


/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 50;
    private static final int SIDE_LENGTH = 3;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void addHexagon(TETile[][] tiles, int s) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                tiles[i][j] = Tileset.NOTHING;
            }
        }
        drawHexagonUpper(tiles, s, getStartX(s),  HEIGHT - SIDE_LENGTH - 1, randomTile());
        drawHexagonLower(tiles, s, getStartX(s), SIDE_LENGTH, randomTile());
    }

    private static void drawHexagonUpper(TETile[][] tiles, int s, int x, int y, TETile tile) {
        int size = s + (s - 1) * 2;
        if (x + size >= WIDTH || y <= HEIGHT / 2 - SIDE_LENGTH || y - s + 1 < 0 || x < 0) {
            return;
        }
        getUpperTriangle(tiles, s, x, y, tile);
        getLowerTriangle(tiles, s, x, y + 1, tile);
        drawHexagonUpper(tiles, s, x - SIDE_LENGTH * 2 + 1, y - SIDE_LENGTH, randomTile());
        drawHexagonUpper(tiles, s, x + SIDE_LENGTH * 2 - 1, y - SIDE_LENGTH, randomTile());
    }

    private static void drawHexagonLower(TETile[][] tiles, int s, int x, int y, TETile tile) {
        int size = s + (s - 1) * 2;
        if (x + size >= WIDTH || y >= HEIGHT / 2 || y - s + 1 < 0 || x < 0) {
            return;
        }
        getUpperTriangle(tiles, s, x, y, tile);
        getLowerTriangle(tiles, s, x, y + 1, tile);
        drawHexagonLower(tiles, s, x - SIDE_LENGTH * 2 + 1, y + SIDE_LENGTH, randomTile());
        drawHexagonLower(tiles, s, x + SIDE_LENGTH * 2 - 1, y + SIDE_LENGTH, randomTile());
    }

    private static int getStartX(int s) {
        int size = s + (s - 1) * 2;
        return WIDTH / 2 - size / 2;
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(10);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.LOCKED_DOOR;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.WATER;
            case 5: return Tileset.SAND;
            case 6: return Tileset.PLAYER;
            case 7: return Tileset.GRASS;
            case 8: return Tileset.FLOOR;
            case 9: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    private static void getUpperTriangle(TETile[][] tiles, int s, int x, int y, TETile tile) {
        int size = s + (s - 1) * 2;
        while (size >= s) {
            for (int i = x; i < x + size; i++) {
                tiles[i][y] = tile;
            }
            size -= 2;
            y -= 1;
            x += 1;
        }
    }

    private static void getLowerTriangle(TETile[][] tiles, int s, int x, int y, TETile tile) {
        int size = s + (s - 1) * 2;
        while (size >= s) {
            for (int i = x; i < x + size; i++) {
                tiles[i][y] = tile;
            }
            size -= 2;
            y += 1;
            x += 1;
        }
    }

    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        addHexagon(hexTiles, SIDE_LENGTH);
        hexTiles[0][0] = Tileset.FLOWER;
//        hexTiles[29][0] = Tileset.FLOWER;
        ter.renderFrame(hexTiles);
        while (true) {
            if (StdDraw.isMousePressed()) {
                int x = (int) StdDraw.mouseX();
                int y = (int) StdDraw.mouseY();
                if (hexTiles[x][y] == Tileset.NOTHING) {
                    Font smallFont = new Font("arial", Font.PLAIN, 20);
                    StdDraw.setFont(smallFont);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.textLeft(0, HEIGHT - 1, "There is Nothing");
                    StdDraw.show();
                }
            }
        }
    }
}
