package synthesizer;

/**
 * @auther Zhang Yubin
 * @date 2022/1/8 17:04
 */
public abstract class AbstractBoundedQueue<T> implements BoundedQueue<T>{
    protected int fillCount;
    protected int capacity;
    public int capacity() {
        return capacity;
    }
    public int fillCount() {
        return fillCount;
    }

}
