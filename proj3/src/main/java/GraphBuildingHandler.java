import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 *  Parses OSM XML files using an XML SAX parser. Used to construct the graph of roads for
 *  pathfinding, under some constraints.
 *  See OSM documentation on
 *  <a href="http://wiki.openstreetmap.org/wiki/Key:highway">the highway tag</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Way">the way XML element</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Node">the node XML element</a>,
 *  and the java
 *  <a href="https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html">SAX parser tutorial</a>.
 *
 *  You may find the CSCourseGraphDB and CSCourseGraphDBHandler examples useful.
 *
 *  The idea here is that some external library is going to walk through the XML
 *  file, and your override method tells Java what to do every time it gets to the next
 *  element in the file. This is a very common but strange-when-you-first-see it pattern.
 *  It is similar to the Visitor pattern we discussed for graphs.
 *
 *  @author Alan Yao, Maurice Lee
 */
public class GraphBuildingHandler extends DefaultHandler {
    /**
     * Only allow for non-service roads; this prevents going on pedestrian streets as much as
     * possible. Note that in Berkeley, many of the campus roads are tagged as motor vehicle
     * roads, but in practice we walk all over them with such impunity that we forget cars can
     * actually drive on them.
     */
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));
    private String activeState = "";
    private final GraphDB g;
    private GraphDB.Node lastNode;
    private GraphDB.Edge lastEdge;
    private Queue<Long> curWay = new LinkedList<>();
    /**
     * Create a new GraphBuildingHandler.
     * @param g The graph to populate with the XML data.
     */
    public GraphBuildingHandler(GraphDB g) {
        this.g = g;
    }

    /**
     * Called at the beginning of an element. Typically, you will want to handle each element in
     * here, and you may want to track the parent element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available. This tells us which element we're looking at.
     * @param attributes The attributes attached to the element. If there are no attributes, it
     *                   shall be an empty Attributes object.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see Attributes
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        /* Some example code on how you might begin to parse XML files. */
        if (qName.equals("node")) {
            /* We encountered a new <node...> tag. */
            activeState = "node";
            GraphDB.Node node = new GraphDB.Node(attributes.getValue("id"),
                    attributes.getValue("lat"),
                    attributes.getValue("lon"));
            lastNode = node;
        } else if (qName.equals("way")) {
            /* We encountered a new <way...> tag. */
            activeState = "way";
            GraphDB.Edge edge = new GraphDB.Edge(attributes.getValue("id"));
            lastEdge = edge;
        } else if (activeState.equals("way") && qName.equals("nd")) {
            long nodeId = Long.parseLong(attributes.getValue("ref"));
            curWay.add(nodeId);
            /* While looking at a way, we found a <nd...> tag. */
        } else if (activeState.equals("way") && qName.equals("tag")) {
            /* While looking at a way, we found a <tag...> tag. */
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("maxspeed")) {
                lastEdge.extraInfo.put(k, v);
            } else if (k.equals("highway")) {
                lastEdge.extraInfo.put(k, v);
                if (ALLOWED_HIGHWAY_TYPES.contains(v)) {
                    lastEdge.isValid = true;
                }
            } else if (k.equals("name")) {
                lastEdge.extraInfo.put(k, v);
            }
        } else if (activeState.equals("node") && qName.equals("tag") && attributes.getValue("k")
                .equals("name")) {
            /* While looking at a node, we found a <tag...> with k="name". */
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            lastNode.extraInfo.put(k, v);
        }
    }

    /**
     * Receive notification of the end of an element. You may want to take specific terminating
     * actions here, like finalizing vertices or edges found.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available.
     * @throws SAXException  Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way")) {
            if (lastEdge.isValid) {
                lastEdge.from = curWay.poll();
                while (!curWay.isEmpty()) {
                    lastEdge.to = curWay.poll();
                    g.addEdge(lastEdge);
                    g.addEdge(new GraphDB.Edge(lastEdge.id, lastEdge.to, lastEdge.from, lastEdge.extraInfo));
                    lastEdge = new GraphDB.Edge(lastEdge.id, lastEdge.to, lastEdge.to, lastEdge.extraInfo);
                }
            }
            curWay = new LinkedList<>();
        } else if (activeState.equals("way") && qName.equals("nd")) {
        } else if (qName.equals("node")) {
            g.addNode(lastNode);
        }
    }

}
