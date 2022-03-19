import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    private static Queue<Long> queue;
    private static List<Long> marked;
    private static Map<Long, Long> edgeTo;
    private static Map<Long, Double> disTo;
    private static GraphDB gDB;
    private static long target;
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        gDB = g;
        long s = g.closest(stlon, stlat);
        target = g.closest(destlon, destlat);
        marked = new ArrayList<>();
        edgeTo = new HashMap<>();
        disTo = new HashMap<>();
        for (long node : g.vertices()) {
            disTo.put(node, Double.POSITIVE_INFINITY);
        }

        queue = new PriorityQueue<>(Comparator.comparingDouble(o -> disTo.get(o) + h(o)));
        queue.add(s);
        disTo.put(s, .0);

        while (!queue.isEmpty()) {
            long node = queue.poll();
            if (node == target) {
                break;
            }
            marked.add(node);
            for (long w : g.adjacent(node)) {
                relax(node, w);
            }
        }
        return generatePath(s);
    }

    private static List<Long> generatePath(long src) {
        List<Long> path = new LinkedList<>();
        long node = target;
        path.add(target);
        while (node != src) {
            if (edgeTo.get(node) == null) {
                return new LinkedList<>();
            }
            node = edgeTo.get(node);
            path.add(0, node);
        }
        return path;
    }

    private static double h(long w) {
        return gDB.distance(w, target);
    }

    private static void relax(long v, long w) {
        if (marked.contains(w)) {
            return;
        }

        double newDis = disTo.get(v) + gDB.distance(v, w);
        if (newDis < disTo.get(w)) {
            disTo.put(w, newDis);
            edgeTo.put(w, v);
            queue.add(w);
        }
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     * Between -15 and 15 degrees the direction should be “Continue straight”.
     * Beyond -15 and 15 degrees but between -30 and 30 degrees the direction should be “Slight left/right”.
     * Beyond -30 and 30 degrees but between -100 and 100 degrees the direction should be “Turn left/right”.
     * Beyond -100 and 100 degrees the direction should be “Sharp left/right”.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> list = new LinkedList<>();
        for (int i = 0; i < route.size() - 1; i++) {
            long from = route.get(i);
            long to = route.get(i + 1);
            double bearing = g.bearing(from, to);
            NavigationDirection direction = new NavigationDirection();
            direction.distance = g.distance(from, to);
            String name = g.getWayName(from, to);
            direction.way = name;
            if (Math.abs(bearing) <= 15.0) {
                direction.direction = NavigationDirection.STRAIGHT;
            } else if (bearing < -15 && bearing >= -30) {
                direction.direction = NavigationDirection.SLIGHT_LEFT;
            } else if (bearing > 15 && bearing <= 30) {
                direction.direction = NavigationDirection.SLIGHT_RIGHT;
            } else if (bearing < -30 && bearing >= -100) {
                direction.direction = NavigationDirection.LEFT;
            } else if (bearing > 30 && bearing <= 100) {
                direction.direction = NavigationDirection.RIGHT;
            } else if (bearing < -100) {
                direction.direction = NavigationDirection.SHARP_LEFT;
            } else if (bearing > 100) {
                direction.direction = NavigationDirection.SLIGHT_RIGHT;
            }
            list.add(direction);
        }
        return list;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
