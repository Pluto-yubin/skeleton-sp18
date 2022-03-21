import java.util.Hashtable;

/**
 * @auther Zhang Yubin
 * @date 2022/3/21 16:52
 */
public class Trie {
    private Node root;
    public static final int OK = 1;
    public static final int CONTINUE = 2;
    public static final int BREAK = 3;

    public Trie() {
        root = new Node();
    }

    private class Node {
        char c;
        boolean isKey;
        Hashtable<Character, Node> next;

        public Node(char c) {
            this.c = c;
            isKey = false;
            next = new Hashtable();
        }

        public Node() {
            next = new Hashtable<>();
        }
    }

    public void put(String s) {
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (node.next.containsKey(c)) {
                node = node.next.get(c);
            } else {
                node.next.put(c, new Node(c));
                node = node.next.get(c);
            }
        }
        node.isKey = true;
    }

    /**
     * OK means s is valid
     * CONTINUE means s in trie and you can continue to find deeper node to match valid s
     * BREAK means s has beyond the depth of trie
     * @param s
     * @return
     */
    public int contains(String s) {
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (node == null) {
                return BREAK;
            }
            node = node.next.get(c);
        }
        if (node == null) {
            return BREAK;
        } else if (node.isKey) {
            return OK;
        } else {
            return CONTINUE;
        }
    }
}
