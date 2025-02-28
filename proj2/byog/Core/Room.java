package byog.Core;

import byog.TileEngine.TETile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static byog.TileEngine.Tileset.WALL;

/**
 * @auther Zhang Yubin
 * @date 2022/1/12 15:37
 */
public class Room {
    Position leftUp, leftDown, rightUp, rightDown;
    private static List<Room> list;

    public Room(Position leftUp, Position leftDown, Position rightUp, Position rightDown) {
        this.leftUp = leftUp;
        this.leftDown = leftDown;
        this.rightUp = rightUp;
        this.rightDown = rightDown;
        list = new ArrayList<>();
    }

    public Room() { }

    public static boolean overlap(Room room) {
        if (list.contains(room)) {
            return true;
        }

        for (Room room1 : list) {
            if (checkRoomOverlap(room, room1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkRoomOverlap(Room room1, Room room2) {
        if (intOverlap(room1.rightDown.x, room1.leftDown.x, room2.rightDown.x, room2.leftDown.x)) {
            if (intOverlap(room1.leftUp.y, room1.leftDown.y, room2.leftUp.y, room2.leftDown.y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * x1 must be bigger than y1, x2 is the same
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private static boolean intOverlap(int x1, int y1, int x2, int y2) {
        if (x1 < x2 && x1 > y2 || x2 < x1 && x2 > y1) {
            return true;
        }
        return false;
    }

    public static void addExistRooms(Room room) {
        list.add(room);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        boolean b1 = Objects.equals(leftUp, room.leftUp) && Objects.equals(leftDown, room.leftDown);
        boolean b2 = Objects.equals(rightUp, room.rightUp);
        boolean b3 = Objects.equals(rightDown, room.rightDown);
        return b1 && b2 && b3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftUp, leftDown, rightUp, rightDown);
    }

    /**
     * 在走廊边缘创造房间
     * 以hall为中心，分别向左边和右边延伸一个随机数作为Room的leftDown和rightDown
     * 再随机生成一个heigth来确定leftUp和rightUp
     * @param hall
     * @return
     */
    public static Room createRoom(TETile[][] world, Position hall, Random random) {
        if (hall == null || hall.needClose() || hall.x < 0 || hall.y < 0) {
            return null;
        }
        int len = world.length, height = world[0].length;
        MapGenerator.Direction direction = getGenerateDirection(hall);
        hall = Position.getHallEndPos(hall);
        Room room = new Room();
        int left = RandomUtils.uniform(random, 3, 10);
        int right = RandomUtils.uniform(random, 3, 10);
        int up = RandomUtils.uniform(random, 3, 10);
        int down = RandomUtils.uniform(random, 3, 10);
        switch (direction) {
            case UP:
                room.leftDown = new Position(Math.max(hall.x - left, 0), hall.y);
                room.leftUp = new Position(room.leftDown.x, Math.min(hall.y + up, height - 1));
                room.rightDown = new Position(Math.min(len - 1, hall.x + right), room.leftDown.y);
                room.rightUp = new Position(room.rightDown.x, room.leftUp.y);
                break;
            case DOWN:
                room.leftUp = new Position(Math.max(hall.x - left, 0), hall.y);
                room.leftDown = new Position(room.leftUp.x, Math.max(hall.y - down, 0));
                room.rightUp = new Position(Math.min(len - 1, hall.x + right), room.leftUp.y);
                room.rightDown = new Position(room.rightUp.x, room.leftDown.y);
                break;
            case LEFT:
                room.rightDown = new Position(hall.x, Math.max(hall.y - down, 0));
                room.rightUp = new Position(room.rightDown.x, Math.min(height - 1, hall.y + up));
                room.leftDown = new Position(Math.max(0, hall.x - left), room.rightDown.y);
                room.leftUp = new Position(room.leftDown.x, room.rightUp.y);
                break;
            case RIGHT:
                room.leftDown = new Position(hall.x, Math.max(hall.y - down, 0));
                room.leftUp = new Position(room.leftDown.x, Math.min(height - 1, hall.y + up));
                room.rightDown = new Position(Math.min(len - 1, hall.x + right), room.leftDown.y);
                room.rightUp = new Position(room.rightDown.x, room.leftUp.y);
                break;
            default:
                return null;
        }

        if (Room.overlap(room)) {
            world[hall.x][hall.y] = WALL;
            return null;
        }
        return room;
    }

    public static MapGenerator.Direction getGenerateDirection(Position hallway) {
        MapGenerator.Direction direction = hallway.direction;
        if (direction == MapGenerator.Direction.LEFT || direction == MapGenerator.Direction.RIGHT) {
            if (hallway.yDistance > 0) {
                return MapGenerator.Direction.UP;
            } else if (hallway.yDistance < 0) {
                return MapGenerator.Direction.DOWN;
            } else {
                return hallway.direction;
            }
        } else {
            if (hallway.xDistance > 0) {
                return MapGenerator.Direction.RIGHT;
            } else if (hallway.xDistance < 0) {
                return MapGenerator.Direction.LEFT;
            } else {
                return hallway.direction;
            }
        }
    }
}
