import java.util.*;

/**
 * @auther Zhang Yubin
 * @date 2022/3/20 0:19
 */
public class Trie {
    private Node root;
    public Trie() {
        root = new Node();
    }

    private static class Node {
        char c;
        boolean isKey;
        List<String> name;
        Hashtable<Character, Node> next;

        public Node(char c) {
            this.c = c;
            this.isKey = false;
            next = new Hashtable<>();
            name = new LinkedList<>();
        }

        Node() {
            next = new Hashtable<>();
            name = new LinkedList<>();
        }
    }

    public void put(String dirtyName) {
        if (dirtyName.equals("")) {
            return;
        }

        String s = GraphDB.cleanString(dirtyName);
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (node.next.containsKey(c)) {
                node = node.next.get(c);
            } else {
                Node newNode = new Node(c);
                node.next.put(c, newNode);
                node = node.next.get(c);
            }
        }
        node.isKey = true;
        node.name.add(dirtyName);
    }

    public List<String> matchPre(String prefix) {
        Node node = root;
        List<String> res = new LinkedList<>();
        for (int i = 0; i < prefix.length(); i++) {
            node = node.next.get(prefix.charAt(i));
            if (node == null || node.isKey) {
                return res;
            }
        }

        for (char c : node.next.keySet()) {
            preHelper(prefix, node.next.get(c), res);
        }
        return res;
    }

    private void preHelper(String prefix, Node node, List<String> res) {
        if (node.isKey) {
            res.addAll(node.name);
            return;
        }
        for (char c : node.next.keySet()) {
            preHelper(prefix + node.c, node.next.get(c), res);
        }
    }
}
