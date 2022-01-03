/**
 * @auther Zhang Yubin
 * @date 2022/1/3 19:58
 */
public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> res = new ArrayDeque<>();
        for (Character c : word.toCharArray()) {
            res.addLast(c);
        }
        return res;
    }

    public boolean isPalindrome(String word) {
        int l = 0, h = word.length() - 1;
        while (l < h) {
            if (word.charAt(l) != word.charAt(h)) {
                return false;
            }
            l += 1;
            h -= 1;
        }
        return true;
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        int l = 0, h = word.length() - 1;
        while (l < h) {
            if (cc.equalChars(word.charAt(l), word.charAt(h))) {
                return false;
            }
            l += 1;
            h -= 1;
        }
        return true;
    }
}
