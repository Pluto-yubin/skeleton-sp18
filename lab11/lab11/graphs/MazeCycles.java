package lab11.graphs;


import edu.princeton.cs.algs4.Stack;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private int[] pathTo;
    private boolean cycleFind = false;
    public MazeCycles(Maze m) {
        super(m);
        maze = m;
        pathTo = new int[m.V()];
    }

    @Override
    public void solve() {
        findCycles(0);
    }

    // Helper methods go here
    public void findCycles(int start) {
        Stack<Integer> stack = new Stack<>();
        stack.push(start);
        int i = 0;
        while (!stack.isEmpty()) {
            int v = stack.pop();
            marked[v] = true;
            announce();
            for (int adj : maze.adj(v)) {
                if (!marked[adj]) {
                    pathTo[adj] = v;
                    stack.push(adj);
                } else if (adj != pathTo[v]) {
                    edgeTo[adj] = v;
                    announce();
                    int cur = v;
                    while (cur != adj) {
                        edgeTo[cur] = pathTo[cur];
                        announce();
                        cur = pathTo[cur];
                    }
                    return;
                }
            }
        }
    }
}

