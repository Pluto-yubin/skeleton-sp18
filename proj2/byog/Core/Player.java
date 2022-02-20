package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

/**
 * @auther Zhang Yubin
 * @date 2022/2/8 14:36
 */
public class Player implements Serializable {
    private Position position;

    public Player(int x, int y) {
        position = new Position(x, y);
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public TETile nextW(TETile[][] world)  {
        if (position.y + 1 >= world[0].length) {
            return null;
        }
        return world[position.x][position.y + 1];
    }
    public TETile nextS(TETile[][] world) {
        if (position.y - 1 < 0) {
            return null;
        }
        return world[position.x][position.y - 1];
    }
    public TETile nextA(TETile[][] world) {
        if (position.x - 1 < 0) {
            return null;
        }
        return world[position.x - 1][position.y];
    }
    public TETile nextD(TETile[][] world) {
        if (position.x + 1 >= world.length) {
            return null;
        }
        return world[position.x + 1][position.y];
    }

    public void move(TETile[][] world, MapGenerator.Direction dir) {
        world[position.x][position.y] = Tileset.FLOOR;
        switch (dir) {
            case UP:
                if (isFloor(nextW(world))) {
                    position.y += 1;
                }
                break;
            case DOWN:
                if (isFloor(nextS(world))) {
                    position.y -= 1;
                }
                break;
            case LEFT:
                if (isFloor(nextA(world))) {
                    position.x -= 1;
                }
                break;
            case RIGHT:
                if (isFloor(nextD(world))) {
                    position.x += 1;
                }
                break;
            default:
                return;
        }
        world[position.x][position.y] = Tileset.PLAYER;
    }

    private boolean isFloor(TETile tile) {
        return tile == Tileset.FLOOR;
    }
}
