import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    //  longitude (a.k.a. x coordinates)
    private double lrlon;
    private double ullon;
    private double ullat;
    private double lrlat;
    private double w;
    private double h;
    private double raster_ul_lon = Double.MAX_VALUE;
    private double raster_ul_lat = Double.MIN_VALUE;
    private double raster_lr_lon = -Double.MAX_VALUE;
    private double raster_lr_lat = Double.MAX_VALUE;
    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        initVariable(params);
        Map<String, Object> results = new HashMap<>();
        int depth = getDepth();
        results.put("depth", depth);
        results.put("render_grid", getTilesName(depth));
        appendRaster(results);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("query_success", true);
        if (!inFields(MapServer.ROOT_LRLON, MapServer.ROOT_ULLON, MapServer.ROOT_ULLAT, MapServer.ROOT_LRLAT, false) || ullon > lrlon || ullat < lrlat) {
            results.put("query_success", false);
        }
        System.out.println(results);
        return results;
    }

    /**
     * format: d?_x?_y?
     * @param results
     */
    private void appendRaster(Map<String, Object> results) {
        String[][] maps = (String[][]) results.get("render_grid");
        int depth = (int) results.get("depth");
        String first = maps[0][0];
        String last = maps[maps.length - 1][maps[0].length - 1];
        String[] ss1 = first.split("\\.")[0].split("_");
        String[] ss2 = last.split("\\.")[0].split("_");
        int x0 = Integer.parseInt(ss1[1].substring(1));
        int y0 = Integer.parseInt(ss1[2].substring(1));
        int x1 = Integer.parseInt(ss2[1].substring(1));
        int y1 = Integer.parseInt(ss2[2].substring(1));
        raster_ul_lon = getLrLon(depth, x0 - 1);
        raster_ul_lat = getUllat(depth, y0);
        raster_lr_lon = getLrLon(depth, x1);
        raster_lr_lat = getUllat(depth, y1 + 1);
    }

    /**
     * init the variables according to request params
     * @param params
     */
    private void initVariable(Map<String, Double> params) {
        ullat = params.get("ullat");
        ullon = params.get("ullon");
        lrlat = params.get("lrlat");
        lrlon = params.get("lrlon");
        w = params.get("w");
        h = params.get("h");
    }
    /**
     * get the LonDPP provided by params
     * @return
     */
    private double getLonDPP() {
        return (lrlon - ullon) / w;
    }

    private double getLonDPP(double lrl, double ull, double ww) {
        return (lrl - ull) / ww;
    }

    private int getDepth() {
        return getDepth(MapServer.ROOT_LRLON, MapServer.ROOT_ULLON, 0);
    }
    /**
     * Get the required depth of map according to params
     * @param lrl
     * @param ull
     * @param depth default should be zero
     * @return The maximum of return value is 7
     */
    private int getDepth(double lrl, double ull, int depth) {
        double lonDPP = getLonDPP(lrl, ull, MapServer.TILE_SIZE);
        if (depth >= 7 || lonDPP < getLonDPP()) {
            return depth;
        }
        lrl = getLrLon(depth + 1, 0);
        return getDepth(lrl, ull, depth + 1);
    }

    /**
     * Get the right longitude of d(depth)_xi_y0
     * @param depth
     * @return
     */
    private double getLrLon(int depth, int i) {
        int divide = (int) Math.pow(2, depth);
        double divideLon = (MapServer.ROOT_LRLON - MapServer.ROOT_ULLON) / divide;
        return MapServer.ROOT_ULLON + divideLon * (i + 1);
    }

    /**
     * get the upper latitude (a.k.a y coordinate) of picture d(depth)_x0_yi
     * @param depth
     * @param i
     * @return
     */
    private double getUllat(int depth, int i) {
        int divide = (int) Math.pow(2, depth);
        double divideLat = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / divide;
        return MapServer.ROOT_ULLAT - divideLat * i;
    }

    /**
     * get all the name of image including the field of user provides
     * @param depth
     * @return
     */
    private String[][] getTilesName(int depth) {
        int N = (int) Math.pow(2, depth);
        String d = "d" + depth;
        List<List<String>> lists = new LinkedList<>();
        // 因为地图从左往右遍历，所以在这里i表示上下，即为y轴，j表示左右，即为x轴
        for (int i = 0; i < N; i++) {
            double ulLat = getUllat(depth, i);
            double llLat = getUllat(depth, i + 1);
            // ignore all the regions that above the query box
            if (llLat > ullat) {
                continue;
            }
            // ignore all the regions that below the query box
            if (ulLat < lrlat) {
                break;
            }
            List<String> names = new LinkedList<>();
            for (int j = 0; j < N; j++) {
                double lrLon = getLrLon(depth, j);
                double llLon = getLrLon(depth, j - 1);
                // ignore all the regions that on the right side of the query box
                if (llLon > lrLon) {
                    break;
                }
                if (inFields(lrLon, llLon, ulLat, llLat, true)) {
                    names.add(d + "_x" + j + "_y" + i + ".png");
                }
            }
            if (!names.isEmpty()) {
                lists.add(names);
            }
        }
        return listToStringArray(lists);
    }

    private String[][] listToStringArray(List<List<String>> lists) {
        String[][] res = new String[lists.size()][lists.get(0).size()];
        for (int i = 0; i < lists.size(); i++) {
            for (int j = 0; j < lists.get(i).size(); j++) {
                res[i][j] = lists.get(i).get(j);
            }
        }
        return res;
    }

    private boolean inFields(double lrLon, double llLon, double ulLat, double llLat, boolean modify) {
        if (lonInField(llLon, lrLon) && latInFieds(llLat, ulLat)) {
//            raster_ul_lon = Math.min(raster_ul_lon, llLon);
//            raster_ul_lat = Math.max(raster_ul_lat, ulLat);
//            raster_lr_lat = Math.min(raster_lr_lat, llLat);
//            raster_lr_lon = Math.max(raster_lr_lon, lrLon);
            return true;
        }
        return false;
    }

    /**
     * In y coordinate----------->   y2
     *                                |         y3
     *                                |          |
     *                                |          |
     *                                y1         |
     *                                           y4
     * y3 is ullat, y4 is lrlat
     * @param y1
     * @param y2
     * @return
     */
    private boolean latInFieds(double y1, double y2) {
        if (y1 < ullat && y1 > lrlat || y2 < ullat && y2 > lrlat) {
            return true;
        }
        // check if the region user provided in the region of whole map, vice verse
        if (y1 < lrlat && y2 > ullat || y1 > lrlat && y2 < ullat) {
            return true;
        }
        return false;
    }

    /**
     * In x coordinate---------->   x1_____________x2
     *                                      x3______________x4
     *                                              x1______________x2
     * x3 is ullon, x4 is lrlon
     * @param x1
     * @param x2
     * @return
     */
    private boolean lonInField(double x1, double x2) {
        if (x2 > ullon && x2 < lrlon || x1 > ullon && x1 < lrlon) {
            return true;
        }

        if (x1 < ullon && x2 > lrlon || x1 > ullon && x2 < lrlon) {
            return true;
        }
        return false;
    }
}
