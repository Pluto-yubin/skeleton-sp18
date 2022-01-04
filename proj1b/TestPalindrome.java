import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testIsPalindrome() {
        String word = "tenet";
        assertTrue(palindrome.isPalindrome(word));
        word = "hello";
        assertFalse(palindrome.isPalindrome(word));
        assertTrue(palindrome.isPalindrome("aba"));
        CharacterComparator cc1 = new OffByOne();
        assertFalse(palindrome.isPalindrome("aba", cc1));
        assertTrue(palindrome.isPalindrome("flake", cc1));
        CharacterComparator cc2 = new OffByN(3);
        assertTrue(palindrome.isPalindrome("abd", cc2));
        assertFalse(palindrome.isPalindrome("tenet", cc2));
    }


}
