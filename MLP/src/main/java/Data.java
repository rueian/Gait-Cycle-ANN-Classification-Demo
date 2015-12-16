import java.util.ArrayList;
import java.util.List;

/**
 * Created by rueian on 2015/11/12.
 */
public class Data {

    public List<Double> values = new ArrayList<>();

    public double expected;
    public int actual;

    public Data(double[] raw, double scalar) {
        System.out.println(scalar);
        for (int i = 0; i < raw.length - 1; i ++) {
            this.values.add(raw[i] * scalar);
        }
        this.expected = raw[raw.length - 1];
    }

    public int size() {
        return this.values.size();
    }

    public int dimension() {
        return this.size();
    }

    public double get(int i) {
        return this.values.get(i);
    }
}
