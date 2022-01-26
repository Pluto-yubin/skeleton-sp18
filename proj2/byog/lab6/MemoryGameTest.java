package byog.lab6;

import byog.Core.Game;
import org.junit.Test;

/**
 * @auther Zhang Yubin
 * @date 2022/1/26 0:59
 */
class MemoryGameTest {
    @Test
    public static void main(String[] args) {
        MemoryGame game = new MemoryGame(30, 30);
        game.solicitNCharsInput(5);
    }
}