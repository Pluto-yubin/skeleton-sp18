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
        int random = RandomUtils.uniform(RANDOM, 0, Math.min(finalWorldFrame.length, finalWorldFrame[0].length) - Math.max(roomLength, roomHeight));
        Room room = new Room(
                new Position(random, roomHeight + random),
                new Position(random, random),
                new Position(roomLength + random, roomHeight + random),
                new Position(roomLength + random, random)
        );
        generateMapRecur(finalWorldFrame, room);
        return finalWorldFrame;
    }

    private void generateMapRecur(TETile[][] world, Room room) {
        if (!drawRoom(finalWorldFrame, room)) {
            return;
        }
        Position hallway;
        hallway = Position.createHallsInRoom(RANDOM, room, world.length, world[0].length, Direction.RIGHT, getVelDirRandomly());
        if (drawHallway(world, hallway, false)) {
            Room room1 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room1);
        }

        hallway = Position.createHallsInRoom(RANDOM, room, world.length, world[0].length, Direction.DOWN, getHorDirRandomly());
        if (drawHallway(world, hallway, true)) {
            Room room2 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room2);
        }

        hallway = Position.createHallsInRoom(RANDOM, room, world.length, world[0].length, Direction.UP, getHorDirRandomly());
        if (drawHallway(world, hallway, true)) {
            Room room3 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room3);
        }

        hallway = Position.createHallsInRoom(RANDOM, room, world.length, world[0].length, Direction.LEFT, getVelDirRandomly());
        if (drawHallway(world, hallway, false)) {
            Room room4 = Room.createRoom(world, hallway, RANDOM);
            generateMapRecur(world, room4);
        }

    }

    /**
     * get horizon direction randomly
     * @return
     */
    private Direction getHorDirRandomly() {
        if (RandomUtils.uniform(RANDOM) < 0.5) {
            return Direction.RIGHT;
        }
        return Direction.LEFT;
    }

    private Direction getVelDirRandomly() {
        if (RandomUtils.uniform(RANDOM) < 0.5) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    /**
     *
     * @param world
     * @param hallway
     * @param isVertical
     */
    private boolean drawHallway(TETile[][] world, Position hallway, boolean isVertical) {
        if (hallway == null || hallway.x < 0 || hallway.y < 0) {
            return false;
        }
        boolean res = drawLHall(world, hallway);
        if(res) {
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
        if (start.x != end.x && start.y != end.y) {
            throw new RuntimeException("你这叫我怎么画？");
        }

        int left = Math.min(start.x, end.x), right = Math.max(start.x, end.x);
        int up = Math.max(start.y, end.y), down = Math.min(start.y, end.y);
        for (int i = Math.max(left, 0); i <= Math.min(right, world.length - 1) && up == down; i++) {
            if (world[i][up] == NOTHING) {
                world[i][up] = tileType;
            }
        }
        for (int i = Math.max(down, 0); i <= Math.min(up, world[0].length - 1) && left == right; i++) {
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
        if (checkObstacleInHall(world, start)) {
            return false;
        }
        if (!Position.isVerticalDirection(start.direction)) {
            if (start.yDistance != 0) {   // hall为L形时hall的end端得为wall
                world[start.x + start.xDistance][start.y] = WALL;
            }
            drawHorizonHall(world, start);
            if (start.yDistance == 0) {
                return true;
            }

            drawVerticalHall(world, Position.modifyXY(start, start.xDistance + getDirectionOffset(start.xDistance),
                    getDirectionOffset(start.yDistance)));
            int y = start.y + start.yDistance / Math.abs(start.yDistance);
            int x = start.x + start.xDistance + getDirectionOffset(start.xDistance);
            world[x][y] = FLOOR;
        } else {
            if (start.xDistance != 0) {
                world[start.x][start.y + start.yDistance] = WALL;
            }
            drawVerticalHall(world, start);
            if (start.xDistance == 0) {
                return true;
            }
            drawHorizonHall(world, Position.modifyXY(start, getDirectionOffset(start.xDistance),
                    getDirectionOffset(start.yDistance) + start.yDistance));
            int y = start.y + start.yDistance + getDirectionOffset(start.yDistance);
            int x = start.x + start.xDistance / Math.abs(start.xDistance);
            world[x][y] = FLOOR;
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
        switch (start.direction) {
            case UP:
                return checkHallVertical(world, start, Position.modifyXY(start, 0, start.yDistance));
            case DOWN:
                return checkHallVertical(world, Position.modifyXY(start, 0, start.yDistance), start);
            case LEFT:
                return checkHallHorizon(world, Position.modifyXY(start, start.xDistance, 0), start);
            case RIGHT:
                return checkHallHorizon(world, start, Position.modifyXY(start, start.xDistance, 0));
        }
        return false;
    }

    private boolean checkHallVertical(TETile[][] world, Position start, Position end) {
        if (checkVertical(world, start, end)) {
            return true;
        } else if (checkVertical(world, Position.modifyXY(start, 1, 0), Position.modifyXY(end, 1, 0))) {
            return true;
        } else if (checkVertical(world, Position.modifyXY(start, -1, 0), Position.modifyXY(end, -1, 0))) {
            return true;
        }
        Direction direction = Room.getGenerateDirection(start);
        if (direction != start.direction) {
            if (start.direction == Direction.DOWN) {
                return checkObstacleInHall(world, new Position(end.x, end.y + end.yDistance + 1, false, end.xDistance, 0, direction));
            }
            return checkObstacleInHall(world, new Position(start.x, start.y + start.yDistance - 1, false, start.xDistance, 0, direction));
        }
        return false;
    }

    private boolean checkHallHorizon(TETile[][] world, Position start, Position end) {
        if (checkHorizon(world, start, end)) {
            return true;
        } else if (checkHorizon(world, Position.modifyXY(start, 0, -1), Position.modifyXY(end, 0, -1))) {
            return true;
        } else if (checkHorizon(world, Position.modifyXY(start, 0, 1), Position.modifyXY(end, 0, 1))) {
            return true;
        }
        Direction direction = Room.getGenerateDirection(start);
        if (direction != start.direction) {
            if (start.direction == Direction.LEFT) {
                return checkObstacleInHall(world, new Position(end.x + end.xDistance + 1, end.y , false, 0, end.yDistance, direction));
            }
            return checkObstacleInHall(world, new Position(start.x + start.xDistance - 1, start.y , false, 0, start.yDistance, direction));

        }
        return false;
    }

    private boolean checkHorizon(TETile[][] world, Position start, Position end) {
        int count = 0;
        for (int i = Math.max(0, start.x); i < Math.min(world.length - 1, end.x); i++) {
            if (world[i][start.y] != NOTHING) {
                count += 1;
            }
        }
        return count > 1;
    }

    private boolean checkVertical(TETile[][] world, Position start, Position end) {
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
        if (room == null || room.leftDown.x < 0 || room.rightUp.x >= world.length || room.leftDown.y < 0 || room.rightUp.y > world[0].length) {
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
