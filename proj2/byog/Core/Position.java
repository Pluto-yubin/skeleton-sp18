package byog.Core;

import java.util.Objects;
import java.util.Random;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 17:08
 */
public class Position {
    int x;
    int y;
    boolean close;
    // 从起点位置向x轴延伸长度，所以真实长度为xDistance + 1
    // 这样定义是因为drawTile函数从start开始占据像素点，这里的start对应(x, y)
    int xDistance;
    // y轴延伸长度
    int yDistance;

    // 延伸的方向
    MapGenerator.Direction direction;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(int x, int y, boolean close, int xDistance, int yDistance, MapGenerator.Direction direction) {
        this.x = x;
        this.y = y;
        this.close = close;
        this.xDistance = xDistance;
        this.yDistance = yDistance;
        this.direction = direction;
    }

    /**
     * @param random
     * @param room
     * @param length
     * @param height
     * @param firstDir  走廊第一个方向，为上下左右中的一个
     * @param secondDir
     * @return
     */
    public static Position createHallsInRoom(Random random, Room room, int length, int height, MapGenerator.Direction firstDir, MapGenerator.Direction secondDir) {
        Position hallway = null;
        int rux = room.rightUp.x, ruy = room.rightUp.y, lux = room.leftUp.x, luy = room.leftUp.y;
        int rdx = room.rightDown.x, rdy = room.rightDown.y, ldx = room.leftDown.x, ldy = room.leftDown.y;
        int xDirSign = 1, yDirSign = 1;
        if (firstDir == MapGenerator.Direction.DOWN) {
            yDirSign = -1;
        } else if (firstDir == MapGenerator.Direction.LEFT) {
            xDirSign = -1;
        }
        switch (firstDir) {
            case UP: if (ruy >= height - 3) { return null; }
                hallway = new Position(RandomUtils.uniform(random, lux + 1, rux - 1), luy);
                if (secondDir == MapGenerator.Direction.LEFT) {
                    xDirSign = -1;
                }
                break;
            case DOWN: if (rdy <= 2) { return null; }
                hallway = new Position(RandomUtils.uniform(random, lux + 1, rux - 1), ldy);
                if (secondDir == MapGenerator.Direction.LEFT) {
                    xDirSign = -1;
                }
                break;
            case LEFT: if (ldx <= 2) { return null; }
                hallway = new Position(ldx, RandomUtils.uniform(random, ldy + 1, luy - 1));
                if (secondDir == MapGenerator.Direction.DOWN) {
                    yDirSign = -1;
                }
                break;
            case RIGHT: if (rdx >= length - 3) { return null; }
                hallway = new Position(rdx, RandomUtils.uniform(random, rdy + 1, ruy - 1));
                if (secondDir == MapGenerator.Direction.DOWN) {
                    yDirSign = -1;
                }
                break;
        }

        int hallwayVerticalLen = RandomUtils.uniform(random, 3, 10);
        hallway.yDistance = getDistance(hallway.y, hallwayVerticalLen, height, yDirSign) * yDirSign;
        int hallwayHorizonLen = RandomUtils.uniform(random, 3, 10);
        hallway.xDistance = getDistance(hallway.x, hallwayHorizonLen, length, xDirSign) * xDirSign;
        if (RandomUtils.uniform(random) < 0.5) {
            if (isVerticalDirection(firstDir)) {
                hallway.xDistance = 0;
            } else {
                hallway.yDistance = 0;
            }
        }
        int x = hallway.x + hallway.xDistance, y = hallway.y + hallway.yDistance;
        if (x <= 2 || x >= length - 3 || y <= 2 || y >= height - 3) {
            hallway.close = true;
        }
        hallway.direction = firstDir;
        return hallway;
    }

    public static int getDistance(int hallway, int hallLen, int totalLen, int dirSign) {
        int checkBoundary = hallway + hallLen * dirSign;
        int distance;
        if (checkBoundary <= totalLen - 1 && checkBoundary >= 0) {
            distance = hallLen;
        } else {
            if (checkBoundary < 0) {
                distance = hallway - 1;
            } else {
                distance = totalLen - hallway - 1;
            }
        }
        return distance;
    }

    public static Position modifyXY(Position position, int x, int y) {
        Position position1 = new Position(position.x + x, position.y + y);
        position1.xDistance = position.xDistance;
        position1.yDistance = position.yDistance;
        position1.direction = position.direction;
        position1.close = position.close;
        return position1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public boolean needClose() {
        return close;
    }

    public static boolean isVerticalDirection(MapGenerator.Direction direction) {
        return direction == MapGenerator.Direction.UP || direction == MapGenerator.Direction.DOWN;
    }

    /**
     * Give a start point of hallway, return the end point of it
     * @param position
     * @return
     */
    public static Position getHallEndPos(Position position) {
        if (position.xDistance == 0 || position.yDistance == 0) {
            return Position.modifyXY(position, position.xDistance, position.yDistance);
        }
        int xSign = -1, ySign = -1;
        if (position.xDistance < 0) {
            xSign = 1;
        }
        if (position.yDistance < 0) {
            ySign = 1;
        }
        return Position.modifyXY(position, position.xDistance + xSign, position.yDistance + ySign);
    }

}
