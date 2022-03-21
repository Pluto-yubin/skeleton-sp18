package creatures;

import huglife.Action;
import huglife.Creature;
import huglife.Direction;
import huglife.Occupant;
import huglife.HugLifeUtils;
import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * @auther Zhang Yubin
 * @date 2022/3/21 14:51
 */
public class Clorus extends Creature {
    private double energy;
    /** red color. */
    private int r;
    /** green color. */
    private int g;
    /** blue color. */
    private int b;

    private static final double MOVE_ENERGY_COST = 0.03;
    private static final double STAY_ENERGY_COST = 0.01;
    private double repEnergyRetained = 0.5;
    private double repEnergyGiven = 0.5;

    public Clorus() {
        this(1);
    }

    public Clorus(double energy) {
        super("clorus");
        r = 34;
        g = 0;
        b = 231;
        this.energy = energy;
    }

    public double energy() {
        return energy;
    }

    @Override
    public Color color() {
        return new Color(r, g, b);
    }

    public void move() {
        energy -= MOVE_ENERGY_COST;
    }

    public void stay() {
        energy -= STAY_ENERGY_COST;
    }

    public void attack(Creature creature) {
        energy += creature.energy();
    }

    public Clorus replicate() {
        double babyEnergy = energy * repEnergyGiven;
        energy *= repEnergyRetained;
        return new Clorus(babyEnergy);
    }

    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        List<Direction> empty = getNeighborsOfType(neighbors, "empty");
        List<Direction> prey = getNeighborsOfType(neighbors, "plip");
        if (empty.isEmpty()) {
            return new Action(Action.ActionType.STAY);
        } else if (!prey.isEmpty()) {
            return new Action(Action.ActionType.ATTACK, HugLifeUtils.randomEntry(prey));
        } else if (energy >= 1) {
            return new Action(Action.ActionType.REPLICATE, HugLifeUtils.randomEntry(empty));
        } else {
            return new Action(Action.ActionType.MOVE, HugLifeUtils.randomEntry(empty));
        }
    }
}
