package lab14;

import lab14lib.Generator;

/**
 * @auther Zhang Yubin
 * @date 2022/3/21 0:19
 */
public class AcceleratingSawToothGenerator implements Generator {
    private int state;
    private int period;
    private double factor;
    private int round;

    public AcceleratingSawToothGenerator(int period, double factor) {
        this.period = period;
        this.factor = factor;
        round = 0;
        state = 0;
    }

    @Override
    public double next() {
        state += 1;
        double v = stateHelper();
        if (v == -1.0) {
            round += 1;
            period *= factor;
            System.out.println(period);
            state = 0;
        }
        return v;
    }

    private double stateHelper() {
        return (state % period) * 2 / (double) period - 1;
    }
}
