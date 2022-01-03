/**
 * @auther Zhang Yubin
 * @date 2022/1/3 20:21
 */
public class OffByOne implements CharacterComparator {
    @Override
    public boolean equalChars(char x, char y) {
        return Math.abs(x - y) == 1;
    }

}
