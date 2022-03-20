package lab14;

import lab14lib.Generator;

/**
 * @auther Zhang Yubin
 * @date 2022/3/21 0:44
 */
public class StrangeBitwiseGenerator implements Generator {
    private int state;
    private int period;

    public StrangeBitwiseGenerator(int period) {
        this.period = period;
    }

    public double next() {
        state = (state + 1);
        int weirdState = state & (state >> 3) & (state >> 8) % period;
        return stateHelper(weirdState);
    }

    private double stateHelper(int states) {
        return (states % period) * 2 / (double) period - 1;
    }
}
