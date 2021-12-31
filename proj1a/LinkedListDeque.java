public class LinkedListDeque<T> {
    private Node sentinel;
    private int size;
    private class Node {
        T value;
        Node prev;
        Node next;

        Node(T value) {
            this.value = value;
        }

        Node() { }
    }

    public LinkedListDeque() {
        sentinel = new Node();
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    public LinkedListDeque(T value) {
        sentinel = new Node();
        Node node = new Node(value);
        sentinel.next = node;
        sentinel.prev = node;
        node.prev = sentinel;
        node.next = sentinel;
        size = 0;
    }

    /**
     * get the index value by recursion
     * @param index
     * @return
     * @throws Exception
     */
    public T getRecursive(int index) {
        Node first = sentinel.next;
        return helpRecursive(first, index - 1);
    }

    /**
     * Help to find the node by recursion
     * @param node
     * @param index
     * @return
     * @throws Exception
     */
    private T helpRecursive(Node node, int index) {
        if (index == 0) {
            return node.value;
        } else {
            return helpRecursive(node.next, index - 1);
        }
    }

    /**
     *  Adds an item of type T to the front of the deque.
     * @param item
     */
    public void addFirst(T item) {
        Node first = new Node(item);
        first.next = sentinel.next;
        first.prev = sentinel;
        sentinel.next = first;
        first.next.prev = first;
        size += 1;
    }

    /**
     * A Adds an item of type T to the back of the deque.
     * @param item
     */
    public void addLast(T item) {
        Node last = new Node(item);
        sentinel.prev.next = last;
        last.prev = sentinel.prev;
        last.next = sentinel;
        sentinel.prev = last;
        size += 1;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the deque.
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     */
    public void printDeque() {
        String res = "";
        Node temp = sentinel.next;
        while (!temp.equals(sentinel)) {
            res += temp.value + " ";
            temp = temp.next;
        }
        System.out.println(res.substring(0, res.length() - 1));
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     * @return
     */
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node node = sentinel.next;
        sentinel.next = node.next;
        node.next.prev = sentinel;
        node.prev = null;
        node.next = null;
        size -= 1;
        return node.value;
    }

    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     * @return
     */
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node node = sentinel.prev;
        node.prev.next = sentinel;
        sentinel.prev = node.prev;
        node.prev = null;
        node.next = null;
        size -= 1;
        return node.value;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     * @param index
     * @return
     */
    public T get(int index) {
        Node node = sentinel.next;
        while (index > 0) {
            node = node.next;
            index -= 1;
        }
        return node.value;
    }
}
