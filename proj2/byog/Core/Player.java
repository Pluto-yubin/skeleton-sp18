package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

/**
 * @auther Zhang Yubin
 * @date 2022/2/8 14:36
 */
public class Player {
    private Position position;
    private TETile[][] world;

    public Player(int x, int y, TETile[][] world) {
        position = new Position(x, y);
        this.world = world;
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public TETile nextW() {
        if (position.y + 1 >= world[0].length) {
            return null;
        }
        return world[position.x][position.y + 1];
    }
    public TETile nextS() {
        if (position.y - 1 < 0) {
            return null;
        }
        return world[position.x][position.y - 1];
    }
    public TETile nextA() {
        if (position.x - 1 < 0) {
            return null;
        }
        return world[position.x - 1][position.y];
    }
    public TETile nextD() {
        if (position.x + 1 >= world.length) {
            return null;
        }
        return world[position.x + 1][position.y];
    }

    public void move(TETile[][] world, MapGenerator.Direction dir) {
        world[position.x][position.y] = Tileset.FLOOR;
        switch (dir) {
            case UP:
                if (isFloor(nextW())) {
                    position.y += 1;
                }
                break;
            case DOWN:
                if (isFloor(nextS())) {
                    position.y -= 1;
                }
                break;
            case LEFT:
                if (isFloor(nextA())) {
                    position.x -= 1;
                }
                break;
            case RIGHT:
                if (isFloor(nextD())) {
                    position.x += 1;
                }
        }
        world[position.x][position.y] = Tileset.PLAYER;
    }

    private boolean isFloor(TETile tile) {
        return tile == Tileset.FLOOR;
    }
}
