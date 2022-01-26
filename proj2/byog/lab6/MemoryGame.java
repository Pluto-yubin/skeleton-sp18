package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.Timer;

public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40);
        game.rand = new Random(seed);
        game.startGame();
    }

    public MemoryGame(int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int random = rand.nextInt(26);
            sb.append(CHARACTERS[random]);
        }
        return sb.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        int midX= width / 2, midY = height / 2;
        StdDraw.clear();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.text(midX, midY, s);
        //TODO: If game is not over, display relevant game information at the top of the screen
        if (!gameOver) {
            Font smallFont = new Font("Monaco", Font.BOLD, 20);
            StdDraw.setFont(smallFont);
            StdDraw.textLeft(1, height - 1, "Round " + round);
            if (playerTurn) {
                StdDraw.text(midX, height - 1, "Type!");
            } else {
                StdDraw.text(midX, height - 1, "Watch!");
            }
            StdDraw.textRight(width - 1, height - 1, ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
            StdDraw.line(0, height - 2, width, height - 2);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(letters.substring(i, i+1));
            StdDraw.pause(1000);
            drawFrame(" ");
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder sb = new StringBuilder();
        drawFrame(" ");
        while (n > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                n -= 1;
                char key = StdDraw.nextKeyTyped();
                sb.append(key);
                drawFrame(sb.toString());
                StdDraw.pause(500);
            }
        }
        return sb.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        StdDraw.setPenColor(Color.white);
        //TODO: Establish Game loop
        while (!gameOver) {
            playerTurn = false;
            drawFrame("Round " + round);
            String randStr = generateRandomString(round);
            StdDraw.pause(1000);
            flashSequence(randStr);
            String input = solicitNCharsInput(round);
            playerTurn = true;
            if (!input.equals(randStr)) {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + round);
            }
            round += 1;
        }
    }

}
