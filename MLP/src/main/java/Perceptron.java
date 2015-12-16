import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rueian on 2015/11/13.
 */
public class Perceptron {
    public List<Double> w = new ArrayList<>();
    public List<Double> wp = new ArrayList<>();
    public double[] input;
    public double y = 0.0;
    public double delta;

    public Perceptron(int dimension, double weight) {
        Random random = new Random(System.nanoTime());
        for (int i = 0; i <= dimension; i ++) {
            w.add(random.nextDouble() * weight);
            wp.add(0.0);
        }
    }

    public double calc(double[] input) {
        if (input.length + 1 != w.size()) {
            System.out.println("長度不符合～～");
        }

        this.input = input;

        double sum = -1 * w.get(w.size() - 1);
        for (int i = 0; i < input.length; i ++) {
            sum += input[i] * w.get(i);
        }

        this.y = sigmoid(sum); // save new output

        return this.y;
    }

    public void update(double learningRate, double inertia) {
        for (int i = 0; i < this.w.size(); i ++) {
            double original = this.w.get(i);
            double previous = this.wp.get(i);
            double input = (i == this.w.size() - 1) ? -1 : this.input[i];
            double nDiff = inertia * previous + learningRate * delta * input;
            this.w.set(i, original + nDiff);
            this.wp.set(i, nDiff);
        }
    }

    private double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
