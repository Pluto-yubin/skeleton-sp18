package lab9tester;

import static org.junit.Assert.*;

import org.junit.Test;
import lab9.BSTMap;

/**
 * Tests by Brendan Hu, Spring 2015, revised for 2018 by Josh Hug
 */
public class TestBSTMap {

    @Test
    public void sanityGenericsTest() {
        try {
            BSTMap<String, String> a = new BSTMap<String, String>();
            BSTMap<String, Integer> b = new BSTMap<String, Integer>();
            BSTMap<Integer, String> c = new BSTMap<Integer, String>();
            BSTMap<Boolean, Integer> e = new BSTMap<Boolean, Integer>();
        } catch (Exception e) {
            fail();
        }
    }

    //assumes put/size/containsKey/get work
    @Test
    public void sanityClearTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1 + i);
            //make sure put is working via containsKey and get
            assertTrue(null != b.get("hi" + i));
            assertTrue(b.get("hi" + i).equals(1 + i));
            assertTrue(b.containsKey("hi" + i));
        }
        assertEquals(455, b.size());
        b.clear();
        assertEquals(0, b.size());
        for (int i = 0; i < 455; i++) {
            assertTrue(null == b.get("hi" + i) && !b.containsKey("hi" + i));
        }
    }

    // assumes put works
    @Test
    public void sanityContainsKeyTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertFalse(b.containsKey("waterYouDoingHere"));
        b.put("waterYouDoingHere", 0);
        assertTrue(b.containsKey("waterYouDoingHere"));
    }

    // assumes put works
    @Test
    public void sanityGetTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(null, b.get("starChild"));
        assertEquals(0, b.size());
        b.put("starChild", 5);
        assertTrue(((Integer) b.get("starChild")).equals(5));
        b.put("KISS", 5);
        assertTrue(((Integer) b.get("KISS")).equals(5));
        assertNotEquals(null, b.get("starChild"));
        assertEquals(2, b.size());
    }

    // assumes put works
    @Test
    public void sanitySizeTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        assertEquals(0, b.size());
        b.put("hi", 1);
        assertEquals(1, b.size());
        for (int i = 0; i < 455; i++) {
            b.put("hi" + i, 1);
        }
        assertEquals(456, b.size());
    }

    //assumes get/containskey work
    @Test
    public void sanityPutTest() {
        BSTMap<String, Integer> b = new BSTMap<String, Integer>();
        b.put("hi", 1);
        assertTrue(b.containsKey("hi"));
        assertTrue(b.get("hi") != null);
    }

    @Test
    public void testRemove() {
        BSTMap<Integer, Integer> b = new BSTMap<>();
        b.put(5, 5);
        b.put(3, 3);
        b.put(1, 1);
        b.put(6, 6);
        b.put(2, 2);
        b.put(4, 4);
        b.put(7, 7);
        assertEquals(7, b.size());
        assertEquals(Integer.valueOf(5), b.remove(5));
        assertEquals(6, b.size());
        assertEquals(Integer.valueOf(7), b.get(7));
        assertTrue(b.isBST());
    }

    @Test
    public void testRemoveRoot() {
        BSTMap<Integer, Integer> b = new BSTMap<>();
        b.put(1, 1);
        assertEquals(Integer.valueOf(1), b.remove(1));
        b.put(5, 5);
        b.put(7, 7);
        b.put(10, 10);
        b.put(8, 8);
        b.put(4, 4);
        b.put(9, 9);
        b.put(6, 6);
        b.put(2, 2);
        b.put(3, 3);
        b.put(1, 1);
        assertEquals(Integer.valueOf(5), b.remove(5));
        assertNull(b.get(5));
        assertEquals(Integer.valueOf(10), b.remove(10));
        assertNull(b.get(10));
        assertTrue(b.isBST());
    }

    public static void main(String[] args) {
        jh61b.junit.TestRunner.runTests(TestBSTMap.class);
    }
}
