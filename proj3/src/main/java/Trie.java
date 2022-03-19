import java.util.*;

/**
 * @auther Zhang Yubin
 * @date 2022/3/20 0:19
 */
public class Trie {
    private Node root;
    // key is cleaned name, value is normal name
    private Map<String, String> nameMap = new HashMap<>();
    public Trie() {
        root = new Node();
    }

    private static class Node {
        char c;
        boolean isKey;
        Hashtable<Character, Node> next;

        public Node(char c, boolean isKey) {
            this.c = c;
            this.isKey = isKey;
            next = new Hashtable<>();
        }

        public Node() {
            next = new Hashtable<>();
        }
    }

    public void put(String dirtyName) {
        if (dirtyName.equals("")) {
            return;
        }

        String s = GraphDB.cleanString(dirtyName);
        nameMap.put(s, dirtyName);
        if (s.equals("thelt")) {
            System.out.println(dirtyName + "!!");
        }
        Node node = root;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (node.next.containsKey(c)) {
                node = node.next.get(c);
            } else {
                Node newNode = new Node(c, i == s.length() - 1);
                node.next.put(c, newNode);
                node = node.next.get(c);
            }
        }
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
            System.out.println(prefix + node.c + " : " + nameMap.get(prefix + node.c));
            res.add(nameMap.get(prefix + node.c));
            return;
        }
        for (char c : node.next.keySet()) {
            preHelper(prefix + node.c, node.next.get(c), res);
        }
    }
}
