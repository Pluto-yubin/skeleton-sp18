package byog.Core;

import byog.TileEngine.TETile;
import org.junit.Assert;
import org.junit.Test;

/**
 * @auther Zhang Yubin
 * @date 2022/1/22 23:35
 */
public class TestWorldGen {
    @Test
    public void testSameInputNoMovement() {
        Game game = new Game();
        TETile[][] tiles = game.playWithInputString("n5197880843569031643s");
        TETile[][] tiles1 = game.playWithInputString("n5197880843569031643s");
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles1[0].length; j++) {
                Assert.assertTrue("i: " + i + " j: " + j, tiles[i][j].equals(tiles1[i][j]));
            }
        }

    }
}
