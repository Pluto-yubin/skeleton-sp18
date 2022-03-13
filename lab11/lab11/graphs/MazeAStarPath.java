package lab11.graphs;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;
    private int targetX, targetY;
    private Queue<Node>  queue;
    private class Node {
        int v;
        int priority;
        Node(int v, int priority) {
            this.v = v;
            this.priority = priority + h(v);
        }
    }
    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        return Math.abs(targetX - maze.toX(v)) + Math.abs(targetY - maze.toY(v));
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /** Performs an A star search from vertex s. */
    private void astar(int source) {
        queue = new PriorityQueue<>(maze.V(), Comparator.comparingInt(o -> o.priority));
        queue.add(new Node(source, 0));
        for (int i = 0; i < maze.V(); i++) {
            distTo[i] = maze.V();
        }
        marked[source] = true;
        distTo[source] = 0;

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.v == maze.xyTo1D(targetX, targetY)) {
                return;
            }
            for (int v : maze.adj(node.v)) {
                relax(v, node.v);
            }
        }
    }

    private void relax(int i, int v) {
        if (marked[i]) {
            return;
        }
        marked[i] = true;
        if (distTo[i] > distTo[v] + 1) {
            distTo[i] = distTo[v] + 1;
            edgeTo[i] = v;
            announce();
            queue.add(new Node(i, distTo[i]));
        }
    }

    @Override
    public void solve() {
        astar(s);
    }

}

