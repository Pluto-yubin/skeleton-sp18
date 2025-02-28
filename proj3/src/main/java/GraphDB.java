import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    private Map<Long, Node> nodeMap;
    private Map<Long, List<Edge>> adj;
    private Trie trie;
    /**
     * add node to the graph
      * @param node
     */
    public void addNode(Node node) {
        nodeMap.put(node.id, node);
        String name = node.extraInfo.getOrDefault("name", "");
        trie.put(name);
    }

    public List<String> getLocationsByPrefix(String prefix) {
        return trie.matchPre(prefix);
    }

    public List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> res = new LinkedList<>();
        for (long nodes : nodeMap.keySet()) {
            Node node = nodeMap.get(nodes);
            if (locationName.equals(node.extraInfo.get("name"))) {
                Map<String, Object> map = new HashMap<>();
                map.put("lat", lat(nodes));
                map.put("lon", lon(nodes));
                map.put("name", node.extraInfo.get("name"));
                map.put("id", node.id);
                res.add(map);
            }
        }
        return res;
    }

    static class Node {
        long id;
        double lat;
        double lon;
        Map<String, String> extraInfo;

        Node(String id, String lat, String lon) {
            this.id = Long.valueOf(id);
            this.lat = Double.valueOf(lat);
            this.lon = Double.valueOf(lon);
            extraInfo = new HashMap<>();
        }
    }

    static class Edge {
        long id;
        long from;
        long to;
        Map<String, String> extraInfo;
        boolean isValid = false;

        Edge(long id, long from, long to, Map<String, String> extraInfo) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.extraInfo = extraInfo;
        }

        Edge(String id) {
            this.id = Long.valueOf(id);
            extraInfo = new HashMap<>();
        }

    }

    public void addEdge(Edge edge) {
        if (adj.get(edge.from) == null) {
            adj.put(edge.from, new LinkedList<>());
        }
        if (adj.get(edge.to) == null) {
            adj.put(edge.to, new LinkedList<>());
        }
        adj.get(edge.from).add(edge);
    }

    public String getWayName(long from, long to) {
        List<Edge> list = adj.get(from);
        for (Edge edge : list) {
            if (edge.to == to) {
                return edge.extraInfo.getOrDefault("name", Router.NavigationDirection.UNKNOWN_ROAD);
            }
        }
        return Router.NavigationDirection.UNKNOWN_ROAD;
    }

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        nodeMap = new HashMap<>();
        adj = new HashMap<>();
        trie = new Trie();
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Iterator<Long> iterator = nodeMap.keySet().iterator();
        List<Long> deleteList = new LinkedList<>();
        while (iterator.hasNext()) {
            long key = iterator.next();
            if (adj.get(key) == null) {
                deleteList.add(key);
            }
        }
        for (long key : deleteList) {
            nodeMap.remove(key);
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return nodeMap.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        Set<Long> set = new HashSet<>();
        List<Edge> list = adj.get(v);
        for (Edge e : list) {
            set.add(e.to);
        }
        return set;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double min = Double.MAX_VALUE;
        long destination = 0;
        for (long key : nodeMap.keySet()) {
            double dlon = lon(key);
            double dlat = lat(key);
            double dis = distance(lon, lat, dlon, dlat);
            if (min > dis) {
                min = dis;
                destination = key;
            }
        }
        return destination;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        if (nodeMap.get(v) == null) {
            System.out.println(nodeMap.get(v));
        }
        return nodeMap.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        if (nodeMap.get(v) == null) {
            System.out.println(nodeMap.get(v));
        }
        return nodeMap.get(v).lat;
    }
}
