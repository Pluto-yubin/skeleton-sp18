public class ArrayDeque<T> {
    T[] item;
    int size = 0;
    int nextFirst = 0;
    int nextLast = 1;
    private static double Factory = 0.25;

    public ArrayDeque() {
        item = (T[]) new Object[8];
    }

    public ArrayDeque(T elem) {
        item = (T[]) new Object[8];
        item[nextFirst] = elem;
        nextFirst = (nextFirst - 1 + item.length) % item.length;
        size += 1;
    }

    public void addFirst(T elem) {
        if (size == item.length) {
            resize(size * 2);
        }
        item[nextFirst] = elem;
        nextFirst = (nextFirst - 1 + item.length) % item.length;
        size += 1;
    }

    public void addLast(T elem) {
        if (size == item.length) {
            resize(size * 2);
        }
        item[nextLast] = elem;
        nextLast = (nextLast + 1) % item.length;
        size += 1;
    }

    /**
     * When the number of element out of the capacity of array, it needs to amplify the capacity
     * @param capacity
     */
    private void resize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        System.arraycopy(item, 0, temp, 0, size);
        item = temp;
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

    public void printDeque() {
        String res = "";
        for (T t : item) {
            res = res + t + " ";
        }
        System.out.println(res.substring(0, res.length() - 1));
    }

    public T removeFirst() {
        if (size == 0)
            return null;
        int index = (nextFirst + 1) % item.length;
        T elem = item[index];
        item[index] = null;
        nextFirst = index;
        size -= 1;
        if (size < item.length * Factory) {
            downsize(item.length / 2);
        }
        return elem;
    }

    public T removeLast() {
        if (size == 0)
            return null;
        int index = (nextLast - 1 + item.length) % item.length;
        T elem = item[index];
        item[index] = null;
        nextLast = index;
        size -= 1;
        if (size < item.length * Factory) {
            downsize(item.length / 2);
        }
        return elem;
    }

    /**
     * Downsize the item if elements less than capacity * Factory
     * @param capacity
     */
    private void downsize(int capacity) {
        T[] temp = (T[]) new Object[capacity];
        System.arraycopy(item, 0, temp, 0, size);
        item = temp;
    }

    public T get(int index) {
        if (index < 0 || index >= item.length)
            return null;
        return item[index];
    }
}
