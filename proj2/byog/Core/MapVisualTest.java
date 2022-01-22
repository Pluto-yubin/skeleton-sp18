package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import org.junit.Assert;
import org.junit.Test;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 21:39
 */
public class MapVisualTest {
    MapGenerator generator = new MapGenerator();
    static TERenderer ter = new TERenderer();
    static TETile[][] world = new TETile[80][30];

    @Test
    public void testList() {
        Room room1 = new Room(new Position(1, 1), new Position(1, 0), new Position(2, 1), new Position(2, 0));
        Room room2 = new Room(new Position(1, 1), new Position(1, 0), new Position(2, 1), new Position(2, 0));
        Room.addExistRooms(room1);
        Assert.assertTrue(Room.overlap(room2));
    }

    public static void main(String[] args) {
        MapGenerator generator = new MapGenerator(world, 1234);
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        ter.initialize(world.length, world[0].length);
        world = generator.generateMap();
        ter.renderFrame(world);
    }
}
