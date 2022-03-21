import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class Boggle {
    
    // File path of dictionary file
    static String dictPath = "words.txt";
    private static Trie trie = new Trie();
    private static char[][] board;
    private static boolean[][] marked;
    private static Queue<String> queue;
    private static List<String> blockList = new ArrayList<>();
    private static List<String> validList = new LinkedList<>();
    private static int size;
    private static class Node {
        char c;
        int x;
        int y;

        Node(char c, int x, int y) {
            this.c = c;
            this.x = x;
            this.y = y;
        }

    }
    /**
     * Solves a Boggle puzzle.
     * 
     * @param k The maximum number of words to return.
     * @param boardFilePath The file path to Boggle board file.
     * @return a list of words found in given Boggle board.
     *         The Strings are sorted in descending order of length.
     *         If multiple words have the same length,
     *         have them in ascending alphabetical order.
     */
    public static List<String> solve(int k, String boardFilePath) {
        queue = new PriorityQueue<>((o1, o2) -> {
            if (o1.length() == o2.length()) {
                return o1.compareTo(o1);
            }
            return o1.length() - o2.length();
        });
        size = k;
        readWords();
        In in = new In(boardFilePath);
        String[] lines = in.readAllLines();
        initBoard(lines);
        marked = new boolean[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                findPath(String.valueOf(board[i][j]), i, j);
            }
        }
        while (!queue.isEmpty()) {
            validList.add(0, queue.poll());
        }
        return validList;
    }

    private static void findPath(String s, int i, int j) {
        if (blockList.contains(s)) {
            return;
        }
        marked[i][j] = true;
        switch (trie.contains(s)) {
            case Trie.BREAK:
                blockList.add(s);
                break;
            case Trie.CONTINUE:
                for (Node c : adjacent(i, j)) {
                    findPath(s + c.c, c.x, c.y);
                }
                break;
            case Trie.OK:
                if (!queue.contains(s)) {
                    queue.add(s);
                }
                if (queue.size() > size) {
                    queue.poll();
                }
                for (Node c : adjacent(i, j)) {
                    findPath(s + c.c, c.x, c.y);
                }
                break;
            default:
                System.out.println("I am trying to pass style checker");
                break;
        }
        marked[i][j] = false;
    }
    private static List<Node> adjacent(int x, int y) {
        List<Node> res = new LinkedList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (indexIsValid(i + x, j + y) && !(i == 0 && j == 0)) {
                    if (!marked[i + x][j + y]) {
                        res.add(new Node(board[i + x][j + y], i + x, j + y));
                    }
                }
            }
        }
        return res;
    }

    private static boolean indexIsValid(int i, int j) {
        return i >= 0 && j >= 0 && i < board.length && j < board[i].length;
    }

    private static void initBoard(String[] lines) {
        board = new char[lines.length][lines[0].length()];
        for (int i = 0; i < lines.length; i++) {
            for (int j = 0; j < lines[0].length(); j++) {
                board[i][j] = lines[i].charAt(j);
            }
        }
    }

    private static void readWords() {
        In in = new In(dictPath);
        while (in.hasNextLine()) {
            String line = in.readLine();
            trie.put(line);
        }
    }

    public static void main(String[] args) {
        List<String> solve = Boggle.solve(7, "exampleBoard.txt");
        System.out.println(solve);
    }
}
