package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;


public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static Player player;
    private static boolean gameOver = false;
    private static int index = 0;
    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // N543SWWWWAA
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        if (input.charAt(index) == 'Q' || input.charAt(index) == 'q') {
            return null;
        }
        if (input.charAt(index) == 'L' || input.charAt(index) == 'l') {
            // reload the game
        } else if (input.charAt(index) == 'N' || input.charAt(index) == 'n') {
            index += 1;
        }
        while (input.charAt(index) >= ' ' && input.charAt(index) <= '9') {
            index += 1;
        }
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        MapGenerator mapGenerator = new MapGenerator();
        String seed = input.substring(1, index - 1);
        mapGenerator.finalWorldFrame = finalWorldFrame;
        mapGenerator.seed = Long.parseLong(seed);
        TETile[][] world = mapGenerator.generateMap();
        if (player == null) {
            throw new RuntimeException("Player initiated fail");
        }
        ter.initialize(WIDTH, HEIGHT);
        FrameDrawer.drawFrontCover(world);
        while (!gameOver && index < input.length()) {
            ter.renderFrame(world);
            StdDraw.pause(100);
            char step = input.charAt(index);
            controlPlayer(world, step);
            index += 1;
            if (StdDraw.isMousePressed()) {
                int x = (int) StdDraw.mouseX();
                int y = (int) StdDraw.mouseY();
                Position click = new Position(x, y);
                FrameDrawer.drawInfo(world, click, ter);
            }
        }
        return world;
    }

    public void controlPlayer(TETile[][] world,char step) {
        step = Character.toLowerCase(step);
        switch (step) {
            case 'w':
                Game.player.move(world, MapGenerator.Direction.UP);
                break;
            case 's':
                Game.player.move(world, MapGenerator.Direction.DOWN);
                break;
            case 'a':
                Game.player.move(world, MapGenerator.Direction.LEFT);
                break;
            case 'd':
                Game.player.move(world, MapGenerator.Direction.RIGHT);
        }
    }
}
