package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import org.junit.Assert;
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

    @Test
    public void testList() {
        Room room1 = new Room(new Position(1, 1), new Position(1, 0), new Position(2, 1), new Position(2, 0));
        Room room2 = new Room(new Position(1, 1), new Position(1, 0), new Position(2, 1), new Position(2, 0));
        Room.addExistRooms(room1);
        Assert.assertTrue(Room.overlap(room2));
    }

    public static void main(String[] args) {
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        generator.finalWorldFrame = world;
        generator.seed = 1234;
        ter.initialize(world.length, world[0].length);
//        generator.drawLHall(world, new Position(3, 3), MapGenerator.Direction.RIGHT, MapGenerator.Direction.UP, 5, 5);
//        System.out.println(generator);
//        ter.renderFrame(world);
        Room room1 = new Room(new Position(0, 3), new Position(0, 0), new Position(3, 3), new Position(3, 0));
        generator.drawRoom(world, room1);
        ter.renderFrame(world);
    }
}
