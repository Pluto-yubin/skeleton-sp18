package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 21:39
 */
public class MapVisualTest {
    static TERenderer ter = new TERenderer();
    static TETile[][] world = new TETile[80][30];


    public static void main(String[] args) {
        MapGenerator generator = new MapGenerator();
        generator.seed = Long.parseLong("5197880843569031643");
        generator.finalWorldFrame = world;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        ter.initialize(world.length, world[0].length);
        world = generator.generateMap();
        world = generator.generateMap();
        ter.renderFrame(world);
    }
}
