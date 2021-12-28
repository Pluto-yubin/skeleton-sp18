import examples.StdDraw;

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

    /**
     * Read all the information of planets from file
     * @param filePath
     * @return
     */
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

    public static void main(String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String fileName = args[2];
        Planet[] planets = readPlanets(fileName);
        double radius = readRadius(fileName);

        StdDraw.enableDoubleBuffering();



        for (double t = 0; t < T; t += dt) {
            StdDraw.setScale(-radius, radius);
            StdDraw.picture(0, 0, "images/starfield.jpg");
            double[] xForces = new double[planets.length];
            double[] yForces = new double[planets.length];
            for (int i = 0; i < planets.length; i++) {
                xForces[i] = planets[i].calcNetForceExertedByX(planets);
                yForces[i] = planets[i].calcNetForceExertedByY(planets);
            }
            for (int i = 0; i < planets.length; i++) {
                planets[i].update(dt, xForces[i], yForces[i]);
            }
            for (Planet planet : planets) {
                planet.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            StdDraw.clear();
        }

    }
}
