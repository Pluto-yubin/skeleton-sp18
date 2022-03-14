package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.*;

/**
 * @auther Zhang Yubin
 * @date 2022/3/14 14:47
 */
public class Solver {
    private WorldState words;
    private MinPQ<Node> pq;
    private Node goal = null;
    private Map<WorldState, Integer> cache;
    private class Node {
        WorldState word;
        int moveCounts;
        Node prev;
        int priority;
        Node(WorldState word, int moveCounts, Node prev) {
            this.word = word;
            this.moveCounts = moveCounts;
            this.prev = prev;
            priority = cache.getOrDefault(word, word.estimatedDistanceToGoal())+ moveCounts;
            cache.put(word, word.estimatedDistanceToGoal());
        }
    }
    public Solver(WorldState initial) {
        pq = new MinPQ<>(Comparator.comparingInt(o -> o.priority));
        cache = new HashMap<>();
        pq.insert(new Node(initial, 0, null));
        while (!pq.isEmpty()) {
            Node node = pq.delMin();
            if (node.word.isGoal()) {
                goal = node;
                break;
            }
            for (WorldState v : node.word.neighbors()) {
                if (node.prev != null && v.equals(node.prev.word)) {
                    continue;
                }
                pq.insert(new Node(v, node.moveCounts + 1, node));
            }
        }
    }

    public int moves() {
        return goal.moveCounts;
    }

    public Iterable<WorldState> solution() {
        List<WorldState> list = new LinkedList<>();
        Node temp = goal;
        while (temp != null) {
            list.add(temp.word);
            temp = temp.prev;
        }
        Collections.reverse(list);
        return list;
    }
}
