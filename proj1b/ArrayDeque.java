public class ArrayDeque<T> implements Deque<T> {
    private T[] item;
    private int size = 0;
    private int capacity = 0;
    private int nextFirst = 0;
    private int nextLast = 1;
    private static double factory = 0.25;

    public ArrayDeque() { }

    @Override
    public void addFirst(T elem) {
        if (size == capacity) {
            resize();
        }
        item[nextFirst] = elem;
        nextFirst = (nextFirst - 1 + capacity) % capacity;
        size += 1;
    }

    @Override
    public void addLast(T elem) {
        if (size == capacity) {
            resize();
        }
        item[nextLast] = elem;
        nextLast = (nextLast + 1) % capacity;
        size += 1;
    }

    /**
     * When the number of element out of the capacity of array, it needs to amplify the capacity
     */
    private void resize() {
        // lazy loading
        if (size == 0) {
            item = (T[]) new Object[8];
            capacity = item.length;
            nextFirst = 0;
            nextLast = 1;
            return;
        }
        capacity *= 2;
        T[] temp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            temp[i] = item[(nextFirst + 1 + i) % item.length];
        }
        item = temp;
        nextFirst = capacity - 1;
        nextLast = size;
    }


    /**
     * Returns true if deque is empty, false otherwise.
     * @return
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of items in the deque.
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        String res = "";
        for (T t : item) {
            res = res + t + " ";
        }
        System.out.println(res.substring(0, res.length() - 1));
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        int index = (nextFirst + 1) % item.length;
        T elem = item[index];
        item[index] = null;
        nextFirst = index;
        size -= 1;
        if (size <= capacity * factory) {
            downsize();
        }
        return elem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int index = (nextLast - 1 + item.length) % item.length;
        T elem = item[index];
        item[index] = null;
        nextLast = index;
        size -= 1;
        if (size <= capacity * factory) {
            downsize();
        }
        return elem;
    }

    /**
     * Downsize the item if elements less than capacity * Factory
     */
    private void downsize() {
        capacity /= 2;
        T[] temp = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            temp[i] = item[(i + nextFirst + 1) % item.length];
        }

        item = temp;
        nextFirst = capacity - 1;
        nextLast = size;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= item.length) {
            return null;
        }
        // the relative length from first
        return item[(nextFirst + index + 1) % item.length];
    }
}
