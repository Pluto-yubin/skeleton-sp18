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
    private Random RANDOM;
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
            world[hallway.x][hallway.y + 1] = FLOOR;
        }
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
    private Position drawCorner(TETile[][] world, Position start, Position end, Direction dir, TETile tileType) {
        int direction = 0, distance = 0;
        if (dir == Direction.RIGHT || dir == Direction.UP) {
            direction = 1;
        } else {
            direction = -1;
        }

        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            distance = Math.abs(start.x - end.x);
            for (int i = 0; i <= distance; i++) {
                TETile tile = world[start.x + i * direction][start.y];
                if (tile.equals(Tileset.NOTHING)) {
                    world[start.x + i * direction][start.y] = tileType;
                }
            }

        } else {
            distance = Math.abs(start.y - end.y);
            for (int i = 0; i <= distance; i++) {
                TETile tile = world[start.x][start.y+ i * direction];
                if (tile.equals(Tileset.NOTHING)) {
                    world[start.x][start.y + i * direction] = Tileset.FLOOR;
                }
            }
        }
        return new Position(start.x + (distance - 1) * direction, start.y);
    }

    /**
     * Drwa the hallway like belows
     *      ####
     *      ...#
     *      ##.#
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
        int horizonDir = 1;
        int verticalDir = 1;
        if (horizon == Direction.RIGHT) {
            horizonDir = -1;
        }
        if (vertical == Direction.DOWN) {
            verticalDir = -1;
        }

        //Draw the horizon hallway
        Position floorStart = new Position(start.x, start.y + verticalDir);
        Position innerWallStart = new Position(start.x, floorStart.y +  verticalDir);
        // The end of horizon wall
        Position wallEnd = drawCorner(world, start, new Position(start.x + horDis, start.y), horizon, Tileset.WALL);
//        Position wallEnd = drawHorizonHallway(world, start, horizon, horDis);
        Position floorEnd = new Position(wallEnd.x, floorStart.y);
        floorStart = drawCorner(world, floorStart, floorEnd, horizon, Tileset.FLOOR);
        Position innerWallEnd = drawHorizonHallway(world, innerWallStart, horizon, horDis - 2);

        start = wallEnd;
        wallEnd = drawVerticalHallway(world, wallEnd, vertical, velDis);
        floorEnd = new Position(floorStart.x, wallEnd.y);
        drawCorner(world, floorStart, floorEnd, vertical, Tileset.FLOOR);
        drawVerticalHallway(world, innerWallEnd, vertical, velDis - 2);
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
        for (int i = 0; i <= room.rightUp.x - room.leftUp.x; i++) {
            if (world[i + room.leftUp.x][room.leftUp.y].equals(Tileset.NOTHING)) {
                world[i + room.leftUp.x][room.leftUp.y] = Tileset.WALL;
            }
            if (world[i + room.leftDown.x][room.leftDown.y].equals(Tileset.NOTHING)) {
                world[i + room.leftDown.x][room.leftDown.y] = Tileset.WALL;
            }
        }

        for (int i = 0; i <= room.leftUp.y - room.leftDown.y; i++) {
            if (world[room.leftDown.x][room.leftDown.y + i].equals(Tileset.NOTHING)) {
                world[room.leftDown.x][room.leftDown.y + i] = Tileset.WALL;
            }
            if (world[i + room.rightDown.x][room.rightDown.y + i].equals(Tileset.NOTHING)) {
                world[room.rightDown.x][room.rightDown.y + i] = Tileset.WALL;
            }
        }
        int temp1 = room.leftDown.y, temp2 = room.rightDown.y;
        while (room.leftDown.y < room.rightUp.y) {
            drawCorner(world, room.leftDown, room.rightDown, Direction.RIGHT, FLOOR);
            room.leftDown.y += 1;
            room.rightDown.y += 1;
        }
        room.leftDown.y = temp1;
        room.rightDown.y = temp2;
    }


}
