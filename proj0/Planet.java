import examples.StdDraw;

public class Planet {
    // Its current position
    public double xxPos;
    public double yyPos;
    // Its current velocity in the direction
    public double xxVel;
    public double yyVel;
    // Its mass
    public double mass;
    // The name of the file that corresponds to the image that depicts the planet
    public String imgFileName;

    private static final double G = 6.67e-11;

    public Planet(double xxPos, double yyPos, double xxVel, double yyVel, double mass, String imgFileName) {
        this.xxPos = xxPos;
        this.yyPos = yyPos;
        this.xxVel = xxVel;
        this.yyVel = yyVel;
        this.mass = mass;
        this.imgFileName = imgFileName;
    }

    public Planet(Planet p) {
        this.xxPos = p.xxPos;
        this.yyPos = p.yyPos;
        this.xxVel = p.xxVel;
        this.yyVel = p.yyVel;
        this.mass = p.mass;
        this.imgFileName = p.imgFileName;
    }

    /**
     * Calculate the distance between two planets
     * @param planet
     * @return
     */
    public double calcDistance(Planet planet) {
        return Math.sqrt(Math.pow(xxPos - planet.xxPos, 2) + Math.pow(yyPos - planet.yyPos, 2));
    }

    /**
     * Calculate the force exerted in the planet
     * @param planet
     * @return
     */
    public double calcForceExertedBy(Planet planet) {
        return G * mass * planet.mass / Math.pow(calcDistance(planet), 2);
    }

    /**
     * Use to calculate the force exerted in X direction
     * @param planet
     * @return
     */
    public double calcForceExertedByX(Planet planet) {
        double res =  calcForceExertedBy(planet) * (xxPos - planet.xxPos) / calcDistance(planet);
        return res > 0 ? res : -res;
    }

    /**
     * Use to calculate the force exerted in Y direction
     * @param planet
     * @return
     */
    public double calcForceExertedByY(Planet planet) {
        double res =  calcForceExertedBy(planet) * (yyPos - planet.yyPos) / calcDistance(planet);
        return res > 0 ? res : -res;
    }

    /**
     * Calculate all the force exerted on the planet in X direction
     * @param planets
     * @return
     */
    public double calcNetForceExertedByX(Planet[] planets) {
        double res = 0;
        for (Planet planet : planets) {
            if (planet.equals(this)) {
                continue;
            }
            if (planet.xxPos > xxPos) {
                res -= calcForceExertedByX(planet);
            } else {
                res += calcForceExertedByX(planet);
            }
        }
        return res > 0 ? res : -res;
    }

    /**
     * Calculate all the force exerted on the planet in Y direction
     * @param planets
     * @return
     */
    public double calcNetForceExertedByY(Planet[] planets) {
        double res = 0;
        for (Planet planet : planets) {
            if (planet.equals(this)) {
                continue;
            }
            if (planet.yyPos > yyPos) {
                res -= calcForceExertedByY(planet);
            } else {
                res += calcForceExertedByY(planet);
            }
        }
        return res > 0 ? res : -res;
    }

    /**
     * Calculate the position and velocity in X, Y directions
     * @param seconds
     * @param xForce
     * @param yForce
     */
    public void update(double seconds, double xForce, double yForce) {
        xxVel += seconds * xForce / mass;
        yyVel += seconds * yForce / mass;
        xxPos += seconds * xxVel;
        yyPos += seconds * yyVel;
    }

    /**
     * Drwa the picture of planet
     */
    public void draw() {
        StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
    }
}

