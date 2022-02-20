package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static boolean gameOver = false;
    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        MapGenerator mapGenerator = new MapGenerator();
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        ter.initialize(WIDTH, HEIGHT);
        FrameDrawer.drawFrontCover(world);
        while (true) {
            while (!StdDraw.hasNextKeyTyped()) {
                if (StdDraw.isMousePressed()) {
                    int x = (int) StdDraw.mouseX();
                    int y = (int) StdDraw.mouseY();
                    Position click = new Position(x, y);
                    FrameDrawer.drawInfo(world, click, ter);
                }
                continue;
            }
            char input = StdDraw.nextKeyTyped();
            input = Character.toLowerCase(input);
            if (input == 'l') {
                mapGenerator = loadMap();
                if (mapGenerator == null) {
                    return;
                }
                world = mapGenerator.generateMap(world);
            } else if (input == 'n') {
                String seed = "";
                FrameDrawer.drawText(world, seed);
                while (!StdDraw.hasNextKeyTyped()) {
                    continue;
                }
                input = StdDraw.nextKeyTyped();
                while (input >= '0' && input <= '9') {
                    seed += input;
                    System.out.println(seed);
                    FrameDrawer.drawText(world, seed);
                    while (!StdDraw.hasNextKeyTyped()) {
                        continue;
                    }
                    input = StdDraw.nextKeyTyped();
                }
                mapGenerator.seed = Long.parseLong(seed);
                world = mapGenerator.generateMap(world);
                if (mapGenerator.player == null) {
                    throw new RuntimeException("Player initiated fail");
                }
            }
            Font font = new Font("Monaco", Font.BOLD, 16 - 2);
            StdDraw.setFont(font);
            mapGenerator.controlPlayer(world, input);
            ter.renderFrame(world);
            if (input == ':') {
                input = StdDraw.nextKeyTyped();
                if (input == 'q') {
                    return;
                } else if (input == 'l') {
                    saveGame(mapGenerator);
                    return;
                }
            }
        }
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
        int index = 0;
        MapGenerator mapGenerator = new MapGenerator();
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        ter.initialize(WIDTH, HEIGHT);
        if (input.charAt(index) == 'Q' || input.charAt(index) == 'q') {
            return null;
        }
        if (input.charAt(index) == 'L' || input.charAt(index) == 'l') {
            // reload the game
            mapGenerator = loadMap();
            if (mapGenerator == null) {
                return null;
            }
            world = mapGenerator.generateMap(world);
        } else if (input.charAt(index) == 'N' || input.charAt(index) == 'n') {
            index += 1;
            while (input.charAt(index) >= '0' && input.charAt(index) <= '9') {
                index += 1;
            }
            String seed = input.substring(1, index - 1);
            mapGenerator.seed = Long.parseLong(seed);
            world = mapGenerator.generateMap(world);
            if (mapGenerator.player == null) {
                throw new RuntimeException("Player initiated fail");
            }
            FrameDrawer.drawFrontCover(world);
            StdDraw.pause(1000);
        }
        while (!gameOver && index < input.length()) {
            ter.renderFrame(world);
            StdDraw.pause(100);
            char step = input.charAt(index);
            if (Character.toLowerCase(input.charAt(index)) == 'q') {
                break;
            } else if (Character.toLowerCase(input.charAt(index)) == 'l') {
                saveGame(mapGenerator);
            }
            mapGenerator.controlPlayer(world, step);
            index += 1;
            if (StdDraw.isMousePressed()) {
                int x = (int) StdDraw.mouseX();
                int y = (int) StdDraw.mouseY();
                Position click = new Position(x, y);
                FrameDrawer.drawInfo(world, click, ter);
            }
            if (index < input.length() && input.charAt(index) == ':') {
                index += 1;
            }
        }
        StdDraw.pause(10000);
        return world;
    }

    private MapGenerator loadMap() {
        File f = new File("./mapGenerator.ser");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                MapGenerator mapGenerator = (MapGenerator) os.readObject();
                os.close();
                return mapGenerator;
            } catch (FileNotFoundException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void saveGame(MapGenerator mapGenerator) {

        File f = new File("./mapGenerator.ser");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(mapGenerator);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
