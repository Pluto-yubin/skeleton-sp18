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
    long seed;
    private Random RANDOM;

    private static Direction RIGHT = Direction.RIGHT;
    private static Direction LEFT = Direction.LEFT;
    private static Direction UP = Direction.UP;
    private static Direction DOWN = Direction.DOWN;

    public MapGenerator(TETile[][] finalWorldFrame, int seed) {
        this.finalWorldFrame = finalWorldFrame;
        RANDOM = new Random(seed);
    }

    public MapGenerator() {
    }

    /**
     * 在对不同方向进行填充的时候需要进行位移
     * 例如当左上方向画走廊时候，往上的startPoint需要在左边的endPoint上右移，右上则需要左移
     * @param x
     * @return
     */
    private static int getDirOffset(int x) {
        return x >= 0 ? -1 : 1;
    }

    public TETile[][] generateMap() {
        if (finalWorldFrame == null) {
            return null;
        }
        if (RANDOM == null) {
            RANDOM = new Random(seed);
        }
        // Initiate map
        for (int i = 0; i < finalWorldFrame.length; i++) {
            for (int j = 0; j < finalWorldFrame[0].length; j++) {
                finalWorldFrame[i][j] = Tileset.NOTHING;
            }
        }

        int roomLength = RandomUtils.uniform(RANDOM, 3, 10);
        int roomHeight = RandomUtils.uniform(RANDOM, 3, 10);
        int min = Math.min(finalWorldFrame.length, finalWorldFrame[0].length);
        int max = Math.max(roomLength, roomHeight);
        int random = RandomUtils.uniform(RANDOM, 0, min - max);
        Room room = new Room(
                new Position(random, roomHeight + random),
                new Position(random, random),
                new Position(roomLength + random, roomHeight + random),
                new Position(roomLength + random, random)
        );
        generateMapRecur(finalWorldFrame, room, null);
        return finalWorldFrame;
    }

    /**
     * hall为room所连接的那条hall
     * @param world
     * @param room
     * @param hall
     */
    private void generateMapRecur(TETile[][] world, Room room, Position hall) {
        if (!drawRoom(finalWorldFrame, room)) {
            if (hall != null) {
                Position temp = Position.getHallEndPos(hall);
                world[temp.x][temp.y] = WALL;
            }
            return;
        }
        draw(world, room, RIGHT, DOWN);

        draw(world, room, UP, LEFT);

    }

    /**
     * extract by idea, amazing
     * @param world
     * @param room
     * @param right
     * @param down
     */
    private void draw(TETile[][] world, Room room, Direction right, Direction down) {
        Position hallway;
        hallway = Position.createHalls(RANDOM, room, world, right);
        if (drawHallway(world, hallway)) {
            Room room1 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room1, hallway);
        }

        hallway = Position.createHalls(RANDOM, room, world, down);
        if (drawHallway(world, hallway)) {
            Room room2 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room2, hallway);
        }
    }

    /**
     *
     * @param world
     * @param hallway
     */
    private boolean drawHallway(TETile[][] world, Position hallway) {
        if (hallway == null || hallway.x < 0 || hallway.y < 0) {
            return false;
        }
        boolean res = drawLHall(world, hallway);
        if (res) {
            world[hallway.x][hallway.y] = FLOOR;
        }
        return res;
    }

    /**
     * 只能画水平或垂直的直线
     *
     * @param world
     * @param start
     * @param end
     */
    private void drawTile(TETile[][] world, Position start, Position end, TETile tileType) {
        int len = world.length, height = world[0].length;
        if (start.x != end.x && start.y != end.y) {
            throw new RuntimeException("你这叫我怎么画？");
        }

        int left = Math.min(start.x, end.x), right = Math.max(start.x, end.x);
        int up = Math.max(start.y, end.y), down = Math.min(start.y, end.y);
        for (int i = Math.max(left, 0); i <= Math.min(right, len) && up == down; i++) {
            if (world[i][up] == NOTHING) {
                world[i][up] = tileType;
            }
        }
        for (int i = Math.max(down, 0); i <= Math.min(up, height - 1) && left == right; i++) {
            if (world[left][i] == NOTHING) {
                world[left][i] = tileType;
            }
        }
    }

    /**
     * draw L style hallway
     * @param world
     * @param start
     */
    public boolean drawLHall(TETile[][] world, Position start) {
        int x = start.xDistance, y = start.yDistance;
        if (checkObstacleInHall(world, start)) {
            return false;
        }
        if (!Position.isVerticalDirection(start.direction)) {
            if (y != 0) {   // hall为L形时hall的end端得为wall
                world[start.x + x][start.y] = WALL;
            }
            drawHorizonHall(world, start);
            if (y == 0) {
                return true;
            }
            Position ver = Position.modifyXY(start, x + getDirOffset(x), getDirOffset(y));
            drawVerticalHall(world, ver);
            int dy = start.y + y / Math.abs(y);
            int dx = start.x + x + getDirOffset(x);
            world[dx][dy] = FLOOR;
        } else {
            if (x != 0) {
                world[start.x][start.y + y] = WALL;
            }
            drawVerticalHall(world, start);
            if (x == 0) {
                return true;
            }
            drawHorizonHall(world, Position.modifyXY(start, getDirOffset(x), getDirOffset(y) + y));
            int dy = start.y + y + getDirOffset(y);
            int dx = start.x + x / Math.abs(x);
            world[dx][dy] = FLOOR;
        }
        return true;
    }

    /**
     * Return true if there are some obstacles in the way of hallway
     * @param world
     * @param start
     * @return
     */
    private boolean checkObstacleInHall(TETile[][] world, Position start) {
        int x = start.xDistance, y = start.yDistance;
        switch (start.direction) {
            case UP:
                return checkHallVertical(world, start, Position.modifyXY(start, 0, y));
            case DOWN:
                return checkHallVertical(world, Position.modifyXY(start, 0, y), start);
            case LEFT:
                return checkHallHorizon(world, Position.modifyXY(start, x, 0), start);
            case RIGHT:
                return checkHallHorizon(world, start, Position.modifyXY(start, x, 0));
            default:
                return false;
        }
    }

    private boolean checkHallVertical(TETile[][] world, Position start, Position end) {
        int sx = start.xDistance, sy = start.yDistance;
        int ex = end.xDistance, ey = end.yDistance;
        if (checkVer(world, start, end)) {
            return true;
        }
        if (checkVer(world, Position.modifyXY(start, 1, 0), Position.modifyXY(end, 1, 0))) {
            return true;
        }
        if (checkVer(world, Position.modifyXY(start, -1, 0), Position.modifyXY(end, -1, 0))) {
            return true;
        }
        Direction direction = Room.getGenerateDirection(start);
        if (direction != start.direction) {
            if (start.direction == DOWN) {
                Position temp = new Position(end.x, end.y + ey + 1, false, ex, 0, direction);
                return checkObstacleInHall(world, temp);
            }
            Position temp = new Position(start.x, start.y + sy - 1, false, sx, 0, direction);
            return checkObstacleInHall(world, temp);
        }
        return false;
    }

    private boolean checkHallHorizon(TETile[][] world, Position start, Position end) {
        int sx = start.xDistance, sy = start.yDistance;
        int ex = end.xDistance, ey = end.yDistance;
        Position upIndex = Position.modifyXY(start, 0, 1);
        Position downIndex = Position.modifyXY(start, 0, -1);
        if (checkHor(world, start, end)) {
            return true;
        } else if (checkHor(world, downIndex, downIndex)) {
            return true;
        } else if (checkHor(world, upIndex, upIndex)) {
            return true;
        }
        Direction direction = Room.getGenerateDirection(start);
        Position temp;
        if (direction != start.direction) {
            temp = new Position(end.x + ex + 1, end.y, false, 0, ey, direction);
            if (start.direction == LEFT) {
                return checkObstacleInHall(world, temp);
            }
            temp = new Position(start.x + sx - 1, start.y, false, 0, sy, direction);
            return checkObstacleInHall(world, temp);
        }
        return false;
    }

    private boolean checkHor(TETile[][] world, Position start, Position end) {
        int count = 0;
        for (int i = Math.max(0, start.x); i < Math.min(world.length - 1, end.x); i++) {
            if (world[i][start.y] != NOTHING) {
                count += 1;
            }
        }
        return count > 1;
    }

    private boolean checkVer(TETile[][] world, Position start, Position end) {
        int count = 0;
        for (int i = Math.max(start.y, 0); i < Math.min(world[0].length - 1, end.y); i++) {
            if (world[start.x][i] != NOTHING) {
                count += 1;
            }
        }
        return count > 1;
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
        // delete room.leftDown.x < 0 || room.rightUp.x >= world.length
        // || room.leftDown.y < 0 || room.rightUp.y > world[0].length
        if (room == null) {
            return false;
        }
        if (Room.overlap(room) || hasObstacleInRoom(world, room)) {
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

    private boolean hasObstacleInRoom(TETile[][] world, Room room) {
        int count = 0;
        for (int i = room.leftDown.x; i < room.rightDown.x; i++) {
            for (int j = room.leftDown.y; j < room.leftUp.y; j++) {
                if (world[i][j] != NOTHING) {
                    count += 1;
                }
            }
        }
        return count > 3;
    }

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
