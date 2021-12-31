public class ArrayDequeTest {
    /* Utility method for printing out empty checks. */
    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Utility method for printing out empty checks. */
    public static boolean checkSize(int expected, int actual) {
        if (expected != actual) {
            System.out.println("size() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Utility method for printing out empty checks. */
    public static boolean checkItem(int expected, int actual, int index) {
        if (expected != actual) {
            System.out.println("get() returned " + actual + " in index: "
                    +
                    index + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Prints a nice message based on whether a test passed.
     * The \n means newline. */
    public static void printTestStatus(boolean passed) {
        if (passed) {
            System.out.println("Test passed!\n");
        } else {
            System.out.println("Test failed!\n");
        }
    }

    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public static void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");

        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        boolean passed = checkEmpty(true, lld1.isEmpty());

        lld1.addFirst("front");

        passed = checkSize(1, lld1.size()) && passed;
        passed = checkEmpty(false, lld1.isEmpty()) && passed;

        lld1.addLast("middle");
        passed = checkSize(2, lld1.size()) && passed;

        lld1.addLast("back");
        passed = checkSize(3, lld1.size()) && passed;

        System.out.println("Printing out deque: ");
        lld1.printDeque();

        lld1.addLast("back");
        lld1.addLast("back");
        lld1.addLast("back");
        lld1.addLast("back");
        lld1.addLast("back");
        lld1.addLast("back");
        passed = checkSize(9, lld1.size()) && passed;

        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();
        lld1.removeFirst();
        passed = checkSize(3, lld1.size()) && passed;

        printTestStatus(passed);

    }

    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public static void addRemoveTest() {

        System.out.println("Running add/remove test.");

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        boolean passed = checkEmpty(true, lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        passed = checkEmpty(false, lld1.isEmpty()) && passed;

        lld1.removeFirst();
        // should be empty
        passed = checkEmpty(true, lld1.isEmpty()) && passed;

        printTestStatus(passed);

    }

    /** Add some items and check that all the item are in order. */
    public static void getTest() {
        System.out.println("Running get test.");

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        // should be empty
        boolean passed = checkEmpty(true, lld1.isEmpty());

        lld1.addFirst(10);
        // should be 10
        passed = checkItem(10, lld1.get(0), 0) && passed;

        lld1.addFirst(20);
        // should be 20
        passed = checkItem(20, lld1.get(0), 0) && passed;

        lld1.addFirst(30);
        lld1.addFirst(40);
        lld1.addFirst(50);
        lld1.addFirst(60);
        lld1.addFirst(70);
        passed = checkItem(70, lld1.get(0), 0) && passed;
        passed = checkItem(60, lld1.get(1), 1) && passed;
        lld1.addFirst(80);
        passed = checkItem(80, lld1.get(0), 0) && passed;
        lld1.addFirst(90);
        passed = checkItem(90, lld1.get(0), 0) && passed;
        passed = checkItem(80, lld1.get(1), 1) && passed;
        passed = checkItem(10, lld1.get(lld1.size() - 1), lld1.size() - 1) && passed;

        lld1.removeFirst();
        passed = checkItem(80, lld1.get(0), 0) && passed;
        passed = checkItem(60, lld1.get(2), 2) && passed;
        lld1.removeFirst();
        passed = checkItem(70, lld1.get(0), 0) && passed;
        passed = checkItem(10, lld1.get(lld1.size() - 1), lld1.size() - 1) && passed;
        lld1.removeLast();
        passed = checkItem(20, lld1.get(lld1.size() - 1), lld1.size() - 1) && passed;
        lld1.removeFirst();
        lld1.removeLast();
        passed = checkItem(30, lld1.get(lld1.size() - 1), lld1.size() - 1) && passed;
        passed = checkItem(60, lld1.get(0), 0) && passed;

        lld1 = new ArrayDeque<>();
        lld1.addFirst(1);
        lld1.addFirst(3);
        lld1.addLast(4);
        lld1.addLast(5);
        passed = checkItem(3, lld1.removeFirst(), 0) && passed;
        passed = checkItem(5, lld1.removeLast(), lld1.size() - 1) && passed;
        passed = checkItem(4, lld1.removeLast(), lld1.size() - 1) && passed;

        lld1 = new ArrayDeque<>();
        lld1.addLast(0);
        lld1.get(0);
        lld1.removeFirst();
        lld1.addLast(3);
        lld1.get(0);
        lld1.addFirst(5);
        lld1.addLast(6);
        lld1.removeLast();
        lld1.removeLast();
        lld1.addLast(11);
        lld1.get(0);
        lld1.removeFirst();
        lld1.addFirst(14);
        lld1.removeLast();
        lld1.addFirst(16);
        lld1.removeLast();
        lld1.get(0);
        lld1.get(0);
        lld1.removeFirst();
        lld1.addLast(21);
        lld1.removeLast();
        lld1.addFirst(23);
        printTestStatus(passed);
    }

    public static void main(String[] args) {
        System.out.println("Running tests.\n");
        addIsEmptySizeTest();
        addRemoveTest();
        getTest();
    }
}
