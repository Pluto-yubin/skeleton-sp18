public class NBody {
    /**
     * Read the radius of university from the file
     * @param filePath
     * @return
     */
    public static double readRadius(String filePath) {
        double radius = 0;
        In in = new In(filePath);
        in.readInt();
        radius = in.readDouble();
        return radius;
    }

    public static Planet[] readPlanets(String filePath) {
        In in = new In(filePath);
        int num = in.readInt();
        Planet[] planets = new Planet[num];
        in.readDouble();
        for (int i = 0; i < num; i++) {
            double xxPos = in.readDouble();
            double yyPos = in.readDouble();
            double xxVel = in.readDouble();
            double yyVel = in.readDouble();
            double mass = in.readDouble();
            String planetName = in.readString();
            planets[i] = new Planet(xxPos, yyPos, xxVel, yyVel, mass, planetName);
        }
        return planets;
    }
}
