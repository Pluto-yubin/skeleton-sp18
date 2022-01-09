package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import org.junit.Test;

import java.util.Arrays;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 21:39
 */
public class MapVisualTest {
    static MapGenerator generator = new MapGenerator();
    static TERenderer ter = new TERenderer();
    static TETile[][] world = new TETile[10][10];

    public static void main(String[] args) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        generator.finalWorldFrame = world;
        generator.seed = 1234;
        ter.initialize(world.length, world[0].length);
        generator.drawLHall(world, new Position(3, 3), MapGenerator.Direction.RIGHT, MapGenerator.Direction.DOWN, 5, 5);
        System.out.println(generator);
        ter.renderFrame(world);
    }
}
