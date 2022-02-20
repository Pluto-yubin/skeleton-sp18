package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;

/**
 * @auther Zhang Yubin
 * @date 2022/1/30 15:18
 */
public class FrameDrawer {

    public static void drawInfo(TETile[][] world, Position position, TERenderer ter) {
        int x = position.x;
        int y = position.y;
        StdDraw.clear(StdDraw.BLACK);
        ter.renderFrame(world);
        StdDraw.setPenColor(Color.WHITE);
        TETile tile = world[x][y];
        int height = world[0].length - 1;
        if (tile == Tileset.NOTHING) {
            StdDraw.textLeft(0, height, "This is Nothing");
        } else if (tile == Tileset.WALL) {
            StdDraw.textLeft(0, height, "This is Wall");
        } else if (tile == Tileset.LOCKED_DOOR) {
            StdDraw.textLeft(0, height, "locked door");
        } else if (tile == Tileset.PLAYER) {
            StdDraw.textLeft(0, height, "You");
        }
        StdDraw.show();
    }

    public static void drawFrontCover(TETile[][] world) {
        int midX = world.length / 2;
        double midY = world[0].length * 0.75;
        initDrawer();
        StdDraw.text(midX, midY, "CS61B: THE GAME");
        Font font1 = new Font("Arial", Font.PLAIN, 16);
        StdDraw.setFont(font1);
        midY = midY / 2;
        StdDraw.text(midX, midY, "New Game (N)");
        StdDraw.text(midX, midY - 1, "Load Game (L)");
        StdDraw.text(midX, midY - 2, "Quit (Q)");
        StdDraw.show();
    }

    public static void drawText(TETile[][] world, String text) {
        initDrawer();
        int midX = world.length / 2;
        double midY = world[0].length * 0.75;
        StdDraw.text(midX, midY, "Seed: " + text);
        StdDraw.text(midX, midY / 2, "Press any direction button to start the game");
        StdDraw.show();
    }

    private static void initDrawer() {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Arial", Font.PLAIN, 32);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
    }

}
