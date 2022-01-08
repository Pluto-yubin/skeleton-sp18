package synthesizer;

/**
 * @auther Zhang Yubin
 * @date 2022/1/8 0:38
 */

import java.util.Iterator;

public interface BoundedQueue<T> extends Iterable<T> {
    int capacity();
    int fillCount();
    void enqueue(T x);
    T dequeue();
    T peek();
    default boolean isEmpty() {
        return fillCount() == 0;
    }
    default boolean isFull() {
        return capacity() == fillCount();
    }
    @Override
    Iterator<T> iterator();
}
