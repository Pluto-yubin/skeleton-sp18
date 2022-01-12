package byog.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @auther Zhang Yubin
 * @date 2022/1/12 15:37
 */
public class Room {
    Position leftUp, leftDown, rightUp, rightDown;
    private static List<Room> list = new ArrayList<>();

    public Room(Position leftUp, Position leftDown, Position rightUp, Position rightDown) {
        this.leftUp = leftUp;
        this.leftDown = leftDown;
        this.rightUp = rightUp;
        this.rightDown = rightDown;
    }

    public static boolean overlap(Room room) {
        return list.contains(room);
    }

    public static void addExistRooms(Room room) {
        list.add(room);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(leftUp, room.leftUp) && Objects.equals(leftDown, room.leftDown) && Objects.equals(rightUp, room.rightUp) && Objects.equals(rightDown, room.rightDown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leftUp, leftDown, rightUp, rightDown);
    }
}
