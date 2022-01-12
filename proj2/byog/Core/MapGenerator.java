package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 17:02
 */
public class MapGenerator {
    TETile[][] finalWorldFrame;
    int seed;
    private Random RANDOM;
    // Define the picture of the wall
    private static final TETile WALL = Tileset.WALL;
    // Define the picture of the floor
    private static final TETile FLOOR = Tileset.FLOOR;
    private static final TETile NOTHING = Tileset.NOTHING;

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
        for (int i = 0; i < finalWorldFrame.length; i++) {
            for (int j = 0; j < finalWorldFrame[0].length; j++) {
                finalWorldFrame[i][j] = Tileset.NOTHING;
            }
        }

        int roomLength = RandomUtils.uniform(RANDOM, 1, 10);
        int roomHeight = RandomUtils.uniform(RANDOM, 1, 10);
        Room room = new Room(
                new Position(0, roomHeight),
                new Position(0, 0),
                new Position(roomLength, roomHeight),
                new Position(roomLength, 0)
        );
        generateMapRecur(finalWorldFrame, room);
        return finalWorldFrame;
    }

    private void generateMapRecur(TETile[][] world, Room room) {
        if (Room.overlap(room)) {
            return;
        }
        drawRoom(finalWorldFrame, room);
//        return;
        int roomLength = room.rightDown.x - room.leftDown.x;
        int roomHeigth = room.rightUp.y - room.rightDown.y;
        double hallWayDire = RandomUtils.uniform(RANDOM);
        Position hallway = new Position(room.rightDown.x, RandomUtils.uniform(RANDOM, room.rightDown.y, room.rightUp.y));
        int hallwayHorizonLen = RandomUtils.uniform(RANDOM, 1, 10);
        int hallwayVerticalLen = RandomUtils.uniform(RANDOM, 1, 10);
        if (hallway.x + hallwayHorizonLen < world[0].length) {
            drawLHall(world, hallway, Direction.RIGHT, Direction.UP, hallwayHorizonLen, hallwayVerticalLen);
            world[hallway.x][hallway.y] = FLOOR;
        }
    }

    /**
     * 只能画水平或垂直的直线
     * @param world
     * @param start
     * @param end
     * @param dir
     */
    private void drawTile(TETile[][] world, Position start, Position end, Direction dir, TETile tileType) {
        if (start.x != end.x && start.y != end.y) {
            throw new RuntimeException("你这叫我怎么画？");
        }

        int left = Math.min(start.x, end.x), right = Math.max(start.x, end.x);
        int up = Math.max(start.y, end.y), down = Math.min(start.y, end.y);
        for (int i = Math.max(left, 0); i <= Math.min(right, world[0].length - 1) && up == down; i++) {
            if (world[i][up] == NOTHING) {
                world[i][up] = tileType;
            }
        }
        for (int i = Math.max(down, 0); i <= Math.min(up, world.length - 1) && left == right; i++) {
            if (world[left][i] == NOTHING) {
                world[left][i] = tileType;
            }
        }
    }

    /**
     * Drwa the hallway like belows
     *     #####
     *     ....#
     *     ###.#
     *       #.#
     *       #.#
     * @param world
     * @param start
     * @param horizon
     * @param vertical
     * @param horDis
     * @param velDis
     */
    public void drawLHall(TETile[][] world, Position start, Direction horizon, Direction vertical, int horDis, int velDis) {
        int dirSign = 1;
        if (horizon == Direction.LEFT || vertical == Direction.DOWN) {
            dirSign = -1;
        }
        drawHorizonHall(world, start, horizon, horDis, true);
        Position end = new Position(start.x + (horDis - 1) * dirSign, start.y - 1);
        if (start.x + (horDis - 1) * dirSign <= 0) {
            end = new Position(1, start.y - 1);
        } else if (start.x + (horDis - 1) * dirSign >= world[0].length - 1) {
            end = new Position(world[0].length - 2, start.y - 1);
        }
        drawVerticalHall(world, end, vertical, velDis, false);
        world[end.x][start.y + 1] = FLOOR;
    }

    private void drawHorizonHall(TETile[][] world, Position start, Direction direction, int distance, boolean close) {
        assert direction == Direction.LEFT || direction == Direction.RIGHT;
        int dirSign = 1;
        if (direction == Direction.LEFT) {
            dirSign = -1;
        }
        Position end = new Position(start.x + dirSign * distance, start.y);
        if (start.x + dirSign * distance < 0) {
            end = new Position(0, start.y);
        } else if (start.x + dirSign * distance > world[0].length) {
            end = new Position(world[0].length - 1, start.y);
        }
        drawTile(world, start, end, direction, FLOOR);
        drawTile(world, new Position(start.x, start.y - 1), new Position(end.x, end.y - 1), direction, WALL);
        drawTile(world, new Position(start.x, start.y + 1), new Position(end.x, end.y + 1), direction, WALL);
        if (close) {
            world[end.x][end.y] = WALL;
        }
    }

    private void drawVerticalHall(TETile[][] world, Position start, Direction direction, int distance, boolean close) {
        assert direction == Direction.UP || direction == Direction.DOWN;
        int dirSign = 1;
        if (direction == Direction.DOWN) {
            dirSign = -1;
        }
        Position end = new Position(start.x, start.y + dirSign * distance);
        if (start.y + dirSign * distance < 0) {
            end = new Position(start.x, 0);
        } else if (start.y + dirSign * distance >=world.length) {
            end = new Position(start.x, world.length - 1);
        }

        drawTile(world, start, end, direction, FLOOR);
        drawTile(world, new Position(start.x - 1, start.y ), new Position(end.x - 1, end.y), direction, WALL);
        drawTile(world, new Position(start.x + 1, start.y), new Position(end.x + 1, end.y), direction, WALL);
        if (close) {
            world[end.x][end.y] = WALL;
        }
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

    public void drawRoom(TETile[][] world, Room room) {
        // Draw the Wall
        drawTile(world, room.leftDown, room.leftUp, Direction.UP, WALL);
        drawTile(world, room.rightDown, room.rightUp, Direction.UP, WALL);
        drawTile(world, room.leftDown, room.rightDown, Direction.RIGHT, WALL);
        drawTile(world, room.leftUp, room.rightUp, Direction.RIGHT, WALL);
        // Draw the floor
        int temp1 = room.leftDown.y, temp2 = room.rightDown.y;
        while (room.leftDown.y < room.rightUp.y) {
            drawTile(world, room.leftDown, room.rightDown, Direction.RIGHT, FLOOR);
            room.leftDown.y += 1;
            room.rightDown.y += 1;
        }
        room.leftDown.y = temp1;
        room.rightDown.y = temp2;
    }
}
