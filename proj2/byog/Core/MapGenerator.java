package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Random;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 17:02
 */
public class MapGenerator implements Serializable {
    Player player;
    long seed;
    private Random RANDOM;

    private static Direction RIGHT = Direction.RIGHT;
    private static Direction LEFT = Direction.LEFT;
    private static Direction UP = Direction.UP;
    private static Direction DOWN = Direction.DOWN;

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

    public TETile[][] generateMap(TETile[][] world) {
        if (world == null) {
            return null;
        }
        RANDOM = new Random(seed);
        // Initiate map
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[0].length; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }

        int roomLength = RandomUtils.uniform(RANDOM, 3, 10);
        int roomHeight = RandomUtils.uniform(RANDOM, 3, 10);
        int min = Math.min(world.length, world[0].length);
        int max = Math.max(roomLength, roomHeight);
        int random = RandomUtils.uniform(RANDOM, 0, min - max);
        Room room = new Room(
                new Position(random, roomHeight + random),
                new Position(random, random),
                new Position(roomLength + random, roomHeight + random),
                new Position(roomLength + random, random)
        );
        generateMapRecur(world, room, null);
        TETile tile = world[room.rightUp.x][room.rightDown.y];
        if (tile == Tileset.WALL) {
            world[room.rightUp.x][room.rightDown.y] = Tileset.LOCKED_DOOR;
            if (player == null) {
                player = new Player(room.rightUp.x, room.rightDown.y + 1);
            }
            world[player.getX()][player.getY()] = Tileset.PLAYER;
        }
        return world;
    }

    /**
     * hall为room所连接的那条hall
     * @param world
     * @param room
     * @param hall
     */
    private void generateMapRecur(TETile[][] world, Room room, Position hall) {
        if (!drawRoom(world, room)) {
            if (hall != null) {
                Position temp = Position.getHallEndPos(hall);
                world[temp.x][temp.y] = Tileset.WALL;
            }
            return;
        }
        draw(world, room, RIGHT);
        draw(world, room, UP);
        draw(world, room, DOWN);
        draw(world, room, LEFT);
    }

    /**
     * @param world
     * @param room
     * @param dir
     */
    private void draw(TETile[][] world, Room room, Direction dir) {
        Position hallway;
        hallway = Position.createHalls(RANDOM, room, world, dir);
        if (drawHallway(world, hallway)) {
            Room room1 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room1, hallway);
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
            world[hallway.x][hallway.y] = Tileset.FLOOR;
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
            if (world[i][up] == Tileset.NOTHING) {
                world[i][up] = tileType;
            }
        }
        for (int i = Math.max(down, 0); i <= Math.min(up, height - 1) && left == right; i++) {
            if (world[left][i] == Tileset.NOTHING) {
                world[left][i] = tileType;
            }
        }
    }

    /**
     * draw L shape hallway
     * @param world
     * @param start
     */
    public boolean drawLHall(TETile[][] world, Position start) {
        int x = start.xDistance, y = start.yDistance;
        if (checkObstacleInHall(world, start)) {
            return false;
        }
        if (!Position.isVerticalDirection(start.direction)) {
            drawHorizonHall(world, start);
            if (y != 0) {   // hall为L形时hall的end端得为wall
                world[start.x + x][start.y] = Tileset.WALL;
            } else {
                return true;
            }
            Position ver = Position.modifyXY(start, x + getDirOffset(x), getDirOffset(y));
            drawVerticalHall(world, ver);
            int dy = start.y + y / Math.abs(y);
            int dx = start.x + x + getDirOffset(x);
            world[dx][dy] = Tileset.FLOOR;
        } else {
            drawVerticalHall(world, start);
            if (x != 0) {
                world[start.x][start.y + y] = Tileset.WALL;
            } else {
                return true;
            }
            drawHorizonHall(world, Position.modifyXY(start, getDirOffset(x), getDirOffset(y) + y));
            int dy = start.y + y + getDirOffset(y);
            int dx = start.x + x / Math.abs(x);
            world[dx][dy] = Tileset.FLOOR;
        }
        return true;
    }

    /**
     * Return true if there are some obstacles in the trace of hallway
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
            if (world[i][start.y] != Tileset.NOTHING) {
                count += 1;
            }
        }
        return count > 1;
    }

    private boolean checkVer(TETile[][] world, Position start, Position end) {
        int count = 0;
        for (int i = Math.max(start.y, 0); i < Math.min(world[0].length - 1, end.y); i++) {
            if (world[start.x][i] != Tileset.NOTHING) {
                count += 1;
            }
        }
        return count > 1;
    }

    private void drawHorizonHall(TETile[][] world, Position start) {
        int x = start.x, y = start.y;
        Position end = new Position(start.x + start.xDistance, start.y);
        drawTile(world, start, end, Tileset.FLOOR);
        drawTile(world, new Position(x, y - 1), new Position(end.x, end.y - 1), Tileset.WALL);
        drawTile(world, new Position(x, y + 1), new Position(end.x, end.y + 1), Tileset.WALL);
        if (start.needClose()) {
            world[end.x][end.y] = Tileset.WALL;
        }
    }

    private void drawVerticalHall(TETile[][] world, Position start) {
        int x = start.x, y = start.y;
        Position end = new Position(start.x, start.y + start.yDistance);
        drawTile(world, start, end, Tileset.FLOOR);
        drawTile(world, new Position(x - 1, y), new Position(end.x - 1, end.y), Tileset.WALL);
        drawTile(world, new Position(x + 1, y), new Position(end.x + 1, end.y), Tileset.WALL);
        if (start.needClose()) {
            world[end.x][end.y] = Tileset.WALL;
        }
    }


    public boolean drawRoom(TETile[][] world, Room room) {
        if (room == null) {
            return false;
        }
        if (Room.overlap(room) || hasObstacleInRoom(world, room)) {
            return false;
        }
        // Draw the Wall
        drawTile(world, room.leftDown, room.leftUp, Tileset.WALL);
        drawTile(world, room.rightDown, room.rightUp, Tileset.WALL);
        drawTile(world, room.leftDown, room.rightDown, Tileset.WALL);
        drawTile(world, room.leftUp, room.rightUp, Tileset.WALL);
        // Draw the floor
        int temp1 = room.leftDown.y, temp2 = room.rightDown.y;
        while (room.leftDown.y < room.rightUp.y) {
            drawTile(world, room.leftDown, room.rightDown, Tileset.FLOOR);
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
                if (world[i][j] != Tileset.NOTHING) {
                    count += 1;
                }
            }
        }
        return count > 3;
    }

    public void controlPlayer(TETile[][] world, char step) {
        step = Character.toLowerCase(step);
        switch (step) {
            case 'w':
                player.move(world, MapGenerator.Direction.UP);
                break;
            case 's':
                player.move(world, MapGenerator.Direction.DOWN);
                break;
            case 'a':
                player.move(world, MapGenerator.Direction.LEFT);
                break;
            case 'd':
                player.move(world, MapGenerator.Direction.RIGHT);
                break;
            default:
                return;
        }
    }

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
