package synthesizer;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(4);
        Assert.assertTrue(arb.isEmpty());
        arb.enqueue(5);
        arb.enqueue(6);
        arb.enqueue(7);
        arb.enqueue(8);
        Assert.assertTrue(arb.isFull());
        Assert.assertEquals(5, (long) arb.dequeue());
        Assert.assertEquals(6, (long) arb.peek());
        assertFalse(arb.isFull());
        assertEquals(6, (long) arb.dequeue());
        arb.enqueue(9);
        arb.enqueue(10);
        assertTrue(arb.isFull());
        assertEquals(7, (long) arb.dequeue());
        for (Integer integer : arb) {
            System.out.println(integer);
        }
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
