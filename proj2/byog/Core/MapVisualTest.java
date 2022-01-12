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
    MapGenerator generator = new MapGenerator();
    static TERenderer ter = new TERenderer();
    static TETile[][] world = new TETile[50][50];

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
//        generator.drawLHall(world, new Position(3, 3), MapGenerator.Direction.RIGHT, MapGenerator.Direction.UP, 5, 5);
//        System.out.println(generator);
//        ter.renderFrame(world);
        Room room1 = new Room(new Position(0, 3), new Position(0, 0), new Position(3, 3), new Position(3, 0));
        world = generator.generateMap();
//        generator.drawRoom(world, room1);
//        generator.drawLHall(world, new Position(4, 5), MapGenerator.Direction.LEFT, MapGenerator.Direction.UP, 5, 5);
        ter.renderFrame(world);
    }
}
