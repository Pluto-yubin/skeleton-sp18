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
    // 从起点位置向x轴延伸长度，所以真实长度为xDistance + 1，这样定义是因为drawTile函数从start开始占据像素点，这里的start对应(x, y)
    int xDistance;
    // y轴延伸长度
    int yDistance;

    // 延伸的方向
    MapGenerator.Direction direction;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }


    /**
     *
     * @param random
     * @param room
     * @param length
     * @param height
     * @param firstDir 走廊第一个方向，为上下左右中的一个
     * @param secondDir
     * @return
     */
    public static Position createHallsInRoom(Random random, Room room, int length, int height, MapGenerator.Direction firstDir, MapGenerator.Direction secondDir) {
        Position hallway = null;
        int xDirSign = 1, yDirSign = 1;

        switch (firstDir) {
            case UP: hallway = new Position(RandomUtils.uniform(random, room.leftUp.x + 1, room.rightUp.x - 1), room.leftUp.y);
                if (secondDir == MapGenerator.Direction.LEFT) {
                    xDirSign = -1;
                }
                break;
            case DOWN: hallway = new Position(RandomUtils.uniform(random, room.leftUp.x + 1, room.rightUp.x - 1), room.leftDown.y);
                if (secondDir == MapGenerator.Direction.LEFT) {
                    xDirSign = -1;
                }
                break;
            case LEFT: hallway = new Position(room.leftDown.x, RandomUtils.uniform(random, room.leftDown.y + 1, room.leftUp.y - 1));
                if (secondDir == MapGenerator.Direction.DOWN) {
                    yDirSign = -1;
                }
                break;
            case RIGHT: hallway = new Position(room.rightDown.x, RandomUtils.uniform(random, room.rightDown.y + 1, room.rightUp.y - 1));
                if (secondDir == MapGenerator.Direction.DOWN) {
                    yDirSign = -1;
                }
                break;
        }

        int hallwayVerticalLen = RandomUtils.uniform(random, 1, 10);
        hallway.yDistance = getDistance(hallway.y, hallwayVerticalLen, height, yDirSign) * yDirSign;
        int hallwayHorizonLen = RandomUtils.uniform(random, 1, 10);
        hallway.xDistance = getDistance(hallway.x, hallwayHorizonLen, length, xDirSign) * xDirSign;

        hallway.direction = firstDir;
        return hallway;
    }

    public static int getDistance(int hallway, int hallLen, int totalLen, int dirSign) {
        int checkBoundary = hallway + hallLen * dirSign;
        int distance;
        if (checkBoundary <= totalLen - 1 || checkBoundary >= 0) {
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
        position1.close = position.close;
        return position1;
    }

    public static Position modifyClose(Position position) {
        Position position1 = new Position(position.x, position.y);
        position1.xDistance = position.xDistance;
        position1.yDistance = position.yDistance;
        position1.close = !position.close;
        return position1;
    }

    public boolean needClose() {
        return close;
    }
}
