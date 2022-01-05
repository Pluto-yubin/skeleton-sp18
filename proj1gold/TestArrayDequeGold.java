/**
 * @auther Zhang Yubin
 * @date 2022/1/5 20:27
 */

import static org.junit.Assert.*;
import org.junit.Test;
public class TestArrayDequeGold {
    @Test
    public void testStudentArrayDeque() {
        StudentArrayDeque<Integer> studentArrayDeque = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> arrayDequeSolution = new ArrayDequeSolution<>();
        String erroneousMsg = "";
        for (int i = 0; ; i++) {
            double numberBetweenZeroAndOne = StdRandom.uniform();

            if (numberBetweenZeroAndOne < 0.25) {
                studentArrayDeque.addLast(i);
                arrayDequeSolution.addLast(i);
                erroneousMsg += "addLast(" + i + ")\n";
            } else if (numberBetweenZeroAndOne < 0.5) {
                studentArrayDeque.addFirst(i);
                arrayDequeSolution.addFirst(i);
                erroneousMsg += "addFirst(" + i + ")\n";
            } else if (numberBetweenZeroAndOne < 0.75 && !arrayDequeSolution.isEmpty()) {
                Integer stu = studentArrayDeque.removeFirst();
                Integer solution = arrayDequeSolution.removeFirst();
                erroneousMsg += "removeFirst()\n";
                assertEquals(erroneousMsg, stu, solution);
            } else if (!arrayDequeSolution.isEmpty()) {
                Integer stu = studentArrayDeque.removeLast();
                Integer solution = arrayDequeSolution.removeLast();
                erroneousMsg += "removeLast()\n";
                assertEquals(erroneousMsg, stu, solution);
            }
        }
    }

}
