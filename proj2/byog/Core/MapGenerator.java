package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 17:02
 */
public class MapGenerator {
    // Define the picture of the wall
    private static final TETile WALL = Tileset.WALL;
    // Define the picture of the floor
    private static final TETile FLOOR = Tileset.FLOOR;
    private static final TETile NOTHING = Tileset.NOTHING;
    TETile[][] finalWorldFrame;
    int seed;
    private Random RANDOM;

    public MapGenerator(TETile[][] finalWorldFrame, int seed) {
        this.finalWorldFrame = finalWorldFrame;
        RANDOM = new Random(seed);
    }

    public MapGenerator() {
    }

    /**
     * 在对不同方向进行填充的时候需要进行位移，例如当左上方向画走廊时候，往上的startPoint需要在左边的endPoint上右移，右上则需要左移
     *
     * @param x
     * @return
     */
    private static int getDirectionOffset(int x) {
        return x >= 0 ? -1 : 1;
    }

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

        int roomLength = RandomUtils.uniform(RANDOM, 3, 10);
        int roomHeight = RandomUtils.uniform(RANDOM, 3, 10);
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
        if (room == null || Room.overlap(room)) {
            return;
        }
        if (!drawRoom(finalWorldFrame, room)) {
            return;
        }
        Position hallway = Position.createHallsInRoom(RANDOM, room, world.length, world[0].length, Direction.RIGHT, Direction.DOWN);
        drawLHall(world, hallway);
        world[hallway.x][hallway.y] = FLOOR;

    }

    /**
     * 只能画水平或垂直的直线
     *
     * @param world
     * @param start
     * @param end
     */
    private void drawTile(TETile[][] world, Position start, Position end, TETile tileType) {
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
     * #####
     * ....#
     * ###.#
     * #.#
     * #.#
     *
     * @param world
     * @param start
     */
    public void drawLHall(TETile[][] world, Position start) {
        if (start.direction == Direction.LEFT || start.direction == Direction.RIGHT) {
            if (!start.needClose()) {
                drawHorizonHall(world, Position.modifyClose(start));
            } else {
                drawHorizonHall(world, start);
            }
            drawVerticalHall(world, Position.modifyXY(start, start.xDistance + getDirectionOffset(start.xDistance),
                    getDirectionOffset(start.yDistance)));
            int y = start.y + start.yDistance / Math.abs(start.yDistance);
            int x = start.x + start.xDistance + getDirectionOffset(start.xDistance);
            world[x][y] = FLOOR;
        } else {
            if (!start.needClose()) {
                drawVerticalHall(world, Position.modifyClose(start));
            } else {
                drawVerticalHall(world, start);
            }
            drawHorizonHall(world, Position.modifyXY(start, getDirectionOffset(start.xDistance),
                    getDirectionOffset(start.yDistance) + start.yDistance));
            int y = start.y + start.yDistance + getDirectionOffset(start.y);
            int x = start.x + start.xDistance / Math.abs(start.xDistance);
            world[x][y] = FLOOR;
        }

    }

    private void drawHorizonHall(TETile[][] world, Position start) {
        Position end = new Position(start.x + start.xDistance, start.y);
        drawTile(world, start, end, FLOOR);
        drawTile(world, new Position(start.x, start.y - 1), new Position(end.x, end.y - 1), WALL);
        drawTile(world, new Position(start.x, start.y + 1), new Position(end.x, end.y + 1), WALL);
        if (start.needClose()) {
            world[end.x][end.y] = WALL;
        }
    }

    private void drawVerticalHall(TETile[][] world, Position start) {
        Position end = new Position(start.x, start.y + start.yDistance);
        drawTile(world, start, end, FLOOR);
        drawTile(world, new Position(start.x - 1, start.y), new Position(end.x - 1, end.y), WALL);
        drawTile(world, new Position(start.x + 1, start.y), new Position(end.x + 1, end.y), WALL);
        if (start.needClose()) {
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

    public boolean drawRoom(TETile[][] world, Room room) {
        if (room.leftDown.x < 0 || room.rightUp.x >= world.length || room.leftDown.y < 0 || room.rightUp.y > world[0].length) {
            return false;
        }
        if (Room.overlap(room)) {
            return false;
        }
        // Draw the Wall
        drawTile(world, room.leftDown, room.leftUp, WALL);
        drawTile(world, room.rightDown, room.rightUp, WALL);
        drawTile(world, room.leftDown, room.rightDown, WALL);
        drawTile(world, room.leftUp, room.rightUp, WALL);
        // Draw the floor
        int temp1 = room.leftDown.y, temp2 = room.rightDown.y;
        while (room.leftDown.y < room.rightUp.y) {
            drawTile(world, room.leftDown, room.rightDown, FLOOR);
            room.leftDown.y += 1;
            room.rightDown.y += 1;
        }
        room.leftDown.y = temp1;
        room.rightDown.y = temp2;
        Room.addExistRooms(room);
        return true;
    }

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
