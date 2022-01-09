package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Random;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 17:02
 */
public class MapGenerator {
    TETile[][] finalWorldFrame;
    int seed;
    private static Random RANDOM;
    // Define the picture of the wall
    private static final TETile WALL = Tileset.WALL;
    // Define the picture of the floor
    private static final TETile FLOOR = Tileset.FLOOR;

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public MapGenerator(TETile[][] finalWorldFrame, int seed) {
        this.finalWorldFrame = finalWorldFrame;
        RANDOM = new Random(seed);
    }
    public MapGenerator() { }

    public TETile[][] generateMap() {
        if (finalWorldFrame == null) {
            return null;
        }
        // Initiate map
        Arrays.fill(finalWorldFrame, Tileset.NOTHING);


        return finalWorldFrame;
    }

    /**
     * draw a vertical hallway in chose direction and fix distance
     * @param world
     * @param startPos
     * @param dir
     * @param distance
     * @return May return the endPosition, I haven't know what to be returned
     */
    private Position drawVerticalHallway(TETile[][] world, Position startPos, Direction dir, int distance) {
        int direction = 1;
        if (dir == Direction.DOWN) {
            direction = -1;
        }
        for (int i = 0; i < distance; i++) {
            if (startPos.y + i * direction >= world.length || startPos.y + i * direction < 0) {
                break;
            }
            world[startPos.x][startPos.y + i * direction] = WALL;
        }
        if (dir == Direction.DOWN) {
            return new Position(startPos.x, Math.max(0, startPos.y - (distance - 1)));
        }
        return new Position(startPos.x, Math.min(world.length, startPos.y + (distance - 1)));
    }

    /**
     *
     * @param world
     * @param startPos
     * @param dir
     * @param distance
     * @return
     */
    private Position drawHorizonHallway(TETile[][] world, Position startPos, Direction dir, int distance) {
        int direction = 1;
        if (dir == Direction.LEFT) {
            direction = -1;
        }
        for (int i = 0; i < distance; i++) {
            if (startPos.x + i * direction >= world.length || startPos.x + i * direction < 0) {
                break;
            }
            world[startPos.x + i * direction][startPos.y] = WALL;
        }
        if (dir == Direction.LEFT) {
            return new Position(Math.max(0, startPos.x - (distance - 1)), startPos.y);
        }
        return new Position(Math.min(startPos.x + (distance - 1), world[0].length), startPos.y);
    }

    /**
     *
     * @param world
     * @param start
     * @param end
     * @param dir
     */
    private Position drawCorner(TETile[][] world, Position start, Position end, Direction dir) {
        int direction = 0, distance = 0;
        if (dir == Direction.RIGHT || dir == Direction.UP) {
            direction = 1;
        } else {
            direction = -1;
        }

        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            distance = Math.abs(start.x - end.x);
            for (int i = 0; i <= distance; i++) {
                world[start.x + i * direction][start.y] = FLOOR;
            }

        } else {
            distance = Math.abs(start.y - end.y);
            for (int i = 0; i <= distance; i++) {
                world[start.x][start.y + i * direction] = FLOOR;
            }
        }
        return new Position(start.x + (distance - 1) * direction, start.y);
    }

    public void drawLHall(TETile[][] world, Position start, Direction horizon, Direction vertical, int horDis, int velDis) {
        int horizonDir = 1;
        int verticalDir = 1;
        if (horizon == Direction.RIGHT) {
            horizonDir = -1;
        }
        if (vertical == Direction.DOWN) {
            verticalDir = -1;
        }
        Position floorStart = new Position(start.x, start.y + verticalDir);
        Position end = drawHorizonHallway(world, start, horizon, horDis);
        Position floorEnd = new Position(end.x, floorStart.y);


        floorStart = drawCorner(world, floorStart, floorEnd, horizon);

        start = end;
        end = drawVerticalHallway(world, end, vertical, velDis);
        floorEnd = new Position(floorStart.x, end.y);
        drawCorner(world, floorStart, floorEnd, vertical);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = finalWorldFrame.length - 1; i >= 0; i--) {
            for (int j = 0; j < finalWorldFrame[0].length; j++) {
                if (finalWorldFrame[i][j].equals(Tileset.NOTHING)) {
                   sb.append(' ');
                } else if (finalWorldFrame[i][j].equals(Tileset.FLOOR)) {
                    sb.append('.');
                } else if (finalWorldFrame[i][j].equals(Tileset.WALL)) {
                    sb.append('#');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
