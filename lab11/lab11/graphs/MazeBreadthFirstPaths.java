package lab11.graphs;

import java.util.LinkedList;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;

    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        announce();

        while (!queue.isEmpty()) {
            int v = queue.poll();
            marked[v] = true;
            announce();

            if (v == t) {
                return;
            }
            for (int adj : maze.adj(v)) {
                if (!marked[adj]) {
                    edgeTo[adj] = v;
                    distTo[adj] = distTo[v] + 1;
                    queue.add(adj);
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

