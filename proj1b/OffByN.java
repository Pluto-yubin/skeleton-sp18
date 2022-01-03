/**
 * @auther Zhang Yubin
 * @date 2022/1/3 20:51
 */
public class OffByN implements CharacterComparator {
    private int offset = 0;
    public OffByN(int N) {
        offset = N;
    }
    @Override
    public boolean equalChars(char x, char y) {
        return Math.abs(x - y) == offset;
    }
}
