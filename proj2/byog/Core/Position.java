package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

/**
 * @auther Zhang Yubin
 * @date 2022/1/9 17:08
 */
public class Position implements Serializable {
    private static final MapGenerator.Direction RIGHT = MapGenerator.Direction.RIGHT;
    private static final MapGenerator.Direction LEFT = MapGenerator.Direction.LEFT;
    private static final MapGenerator.Direction UP = MapGenerator.Direction.UP;
    private static final MapGenerator.Direction DOWN = MapGenerator.Direction.DOWN;
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

    public Position(int x, int y, boolean close, int xd, int yd, MapGenerator.Direction direction) {
        this.x = x;
        this.y = y;
        this.close = close;
        this.xDistance = xd;
        this.yDistance = yd;
        this.direction = direction;
    }
    /**
     * get horizon direction randomly
     * @return
     */
    private static MapGenerator.Direction getHorDir(Random random) {
        if (RandomUtils.uniform(random) < 0.5) {
            return RIGHT;
        }
        return LEFT;
    }

    private static MapGenerator.Direction getVelDir(Random random) {
        if (RandomUtils.uniform(random) < 0.5) {
            return UP;
        }
        return DOWN;
    }
    /**
     * @param ran
     * @param r
     * @param d  走廊第一个方向，为上下左右中的一个
     * @return
     */
    public static Position createHalls(Random ran, Room r, TETile[][] w, MapGenerator.Direction d) {
        Position hallway;
        int height = w[0].length, length = w.length;
        int rux = r.rightUp.x, ruy = r.rightUp.y, lux = r.leftUp.x, luy = r.leftUp.y;
        int rdx = r.rightDown.x, rdy = r.rightDown.y;
        int ldx = r.leftDown.x, ldy = r.leftDown.y;
        int xDirSign = 1, yDirSign = 1;
        if (d == DOWN) {
            yDirSign = -1;
        } else if (d == LEFT) {
            xDirSign = -1;
        }
        switch (d) {
            case UP:
                if (ruy >= height - 3) {
                    return null;
                }
                hallway = new Position(RandomUtils.uniform(ran, lux + 1, rux - 1), luy);
                if (getHorDir(ran) == LEFT) {
                    xDirSign = -1;
                }
                break;
            case DOWN:
                if (rdy <= 2) {
                    return null;
                }
                hallway = new Position(RandomUtils.uniform(ran, lux + 1, rux - 1), ldy);
                if (getHorDir(ran) == LEFT) {
                    xDirSign = -1;
                }
                break;
            case LEFT:
                if (ldx <= 2) {
                    return null;
                }
                hallway = new Position(ldx, RandomUtils.uniform(ran, ldy + 1, luy - 1));
                if (getVelDir(ran) == DOWN) {
                    yDirSign = -1;
                }
                break;
            case RIGHT:
                if (rdx >= length - 3) {
                    return null;
                }
                hallway = new Position(rdx, RandomUtils.uniform(ran, rdy + 1, ruy - 1));
                if (getVelDir(ran) == DOWN) {
                    yDirSign = -1;
                }
                break;
            default:
                return null;
        }

        int hallwayVerticalLen = RandomUtils.uniform(ran, 3, 10);
        hallway.yDistance = getDistance(hallway.y, hallwayVerticalLen, height, yDirSign) * yDirSign;
        int hallwayHorizonLen = RandomUtils.uniform(ran, 3, 10);
        hallway.xDistance = getDistance(hallway.x, hallwayHorizonLen, length, xDirSign) * xDirSign;
        if (RandomUtils.uniform(ran) < 0.5) {
            if (isVerticalDirection(d)) {
                hallway.xDistance = 0;
            } else {
                hallway.yDistance = 0;
            }
        }
        int x = hallway.x + hallway.xDistance, y = hallway.y + hallway.yDistance;
        if (x <= 2 || x >= length - 3 || y <= 2 || y >= height - 3) {
            hallway.close = true;
        }
        hallway.direction = d;
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
        return direction == UP || direction == DOWN;
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
