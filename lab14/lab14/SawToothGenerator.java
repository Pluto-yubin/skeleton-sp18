package lab14;

import lab14lib.Generator;

/**
 * @auther Zhang Yubin
 * @date 2022/3/20 23:49
 */
public class SawToothGenerator implements Generator {
    private int state;
    private int period;

    public SawToothGenerator(int period) {
        state = 0;
        this.period = period;
    }
    @Override
    public double next() {
        state = (state + 1);
//        System.out.println(stateHelper());
        return stateHelper();
    }

    public double stateHelper() {
        return (state % period) * 2 / (double) period - 1;
    }
}
