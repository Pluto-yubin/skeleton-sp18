package byog.Core;

import byog.TileEngine.TETile;
import org.junit.Assert;
import org.junit.Test;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 16:27
 */
public class TestGame {
    @Test
    public void testMapGenerator() {
        MapGenerator mapGenerator = new MapGenerator();
        TETile[][] world = new TETile[1][1];
        Assert.assertNotNull(mapGenerator.generateMap(world));
    }
}
