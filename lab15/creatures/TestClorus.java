package creatures;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @auther Zhang Yubin
 * @date 2022/3/21 15:09
 */
public class TestClorus {
    @Test
    public void testAttack() {
        Clorus clorus = new Clorus(2);
        clorus.attack(new Plip(0.3));
        assertEquals(2.3, clorus.energy(), 0.01);
    }
}
