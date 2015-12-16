/**
 * Created by rueian on 2015/11/13.
 */
public class Output {
    public int desired;

    public Output(int desired) {
        this.desired = desired;
    }

    public int calc(double[] inputs) {
        int result = 0;
        for (int i = 0; i < inputs.length; i ++) {
            if (inputs[i] > 0.5) {
                result += Math.pow(2, i);
            }
        }
        return result;
    }

    public int get(int i) {
        return (this.desired >> i) & 1;
    }

    public boolean isDesired(double[] inputs) {
        return this.calc(inputs) == this.desired;
    }
}
