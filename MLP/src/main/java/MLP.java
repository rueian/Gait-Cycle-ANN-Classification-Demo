import org.math.plot.Plot2DPanel;
import org.math.plot.Plot3DPanel;
import org.math.plot.PlotPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by rueian on 2015/11/12.
 */
public class MLP {
    private List<Data> dataSet;
    private HashMap<Double, Output> outputSet = new HashMap<>();
    private List<List<Perceptron>> network = new ArrayList<>();

    private int learningTimes;
    private double learningRate;
    private double converge;
    private double inertia;

    private int trainingCounter = 0;
    private double runningError = 0;

    private JFrame plotFrame;

    public MLP(List<Data> dataSet, int[] structure, double learningRate, int learningTimes, double converge, double inertia, double weight) {
        this.dataSet = dataSet;
        this.learningRate = learningRate;
        this.learningTimes = learningTimes;
        this.converge = converge;
        this.inertia = inertia;

        // 打散資料集
        Collections.shuffle(this.dataSet, new Random(System.nanoTime()));

        // 正規化輸出
        normalizeOutputSet();

        // 初始化網路
        initNetworkStructure(structure, weight);
    }

    private void normalizeOutputSet() {
        double[] expected = dataSet.stream().mapToDouble(d -> d.expected).distinct().toArray();
        int outputNum = expected.length;

        for (int i = 0; i < outputNum; i ++) {
            outputSet.put(expected[i], new Output((int)expected[i]));
        }
    }

    private void initNetworkStructure(int[] structure, double weight) {
        int outputNum = outputSet.size();
        outputNum = (outputNum % 2 == 1) ? outputNum + 1 : outputNum;
        int size = (int) (Math.log(outputNum) / Math.log(2));

        List<Integer> networkStructure = new ArrayList<>();
        for(int a : structure) {
            networkStructure.add(a);
        }
        networkStructure.add(3);

        // 初始化網路
        network.add(new ArrayList<>());
        int dimension = dataSet.get(0).dimension();
        for (int i = 0; i < dimension; i ++) {
            network.get(0).add(new Perceptron(0, 0));
        }
        for (int i = 0; i < networkStructure.size(); i ++) {
            network.add(new ArrayList<>());
            for (int j = 0; j < networkStructure.get(i); j ++) {
                network.get(i + 1).add(new Perceptron(network.get(i).size(), weight));
            }
        }
    }

    private void calc(Data data) {
        int dimension = data.dimension();

        // 將輸入放在 y0
        for (int i = 0; i < dimension; i ++) {
            network.get(0).get(i).y = data.get(i);
        }

        // 開始走訪每一層
        for (int i = 1; i < network.size(); i ++) {
            double[] input = network.get(i - 1).stream().mapToDouble(s -> s.y).toArray();
            for (int j = 0; j < network.get(i).size(); j ++) {
                network.get(i).get(j).calc(input);
            }
        }
    }

    private int testInput(Data data) {
        calc(data);

        double outputs[] = network.get(network.size() - 1).stream().mapToDouble(p -> p.y).toArray();
        int result = 0;
        for (int i = 0; i < outputs.length; i ++) {
            if (outputs[i] > 0.5) {
                result += Math.pow(2, i);
            }
        }

        return result;
    }

    private double train(Data data) {
        calc(data);

        // 倒傳遞階段 1 輸出層
        double[] outputs = network.get(network.size() - 1).stream().mapToDouble(p -> p.y).toArray();
        Output output = outputSet.get(data.expected);

        if (output.isDesired(outputs)) {
            return 0;
        }

        int k = 0;
        double errors = 0;
        for (Perceptron p : network.get(network.size() - 1)) {
            double desire = output.get(k);
            double error = desire - p.y;
            p.delta = error * p.y * (1 - p.y);
            errors += error;
            k ++;
        }

        // 倒傳遞階段 2 隱藏層
        for (int i = network.size() - 2; i > 0; i --) {
            for (int j = 0; j < network.get(i).size(); j++) {
                Perceptron p = network.get(i).get(j);
                double sum = 0;
                for (Perceptron pp : network.get(i + 1)) {
                    sum += pp.delta * pp.w.get(j);
                }
                p.delta = p.y * (1 - p.y) * sum;
            }
        }

        for (List<Perceptron> list : network) {
            for (Perceptron p : list) {
                p.update(learningRate, inertia);
            }
        }

        return errors * errors / 2;
    }

    public void training() {
        System.out.println();
        System.out.println("========== 開始訓練 ==========");

        List<Data> trainningData = this.dataSet.subList(0, this.dataSet.size());
        List<Data> testData = this.dataSet.subList(0, this.dataSet.size());

        for(int i = 0; i < learningTimes; i ++) {
            Collections.shuffle(trainningData, new Random(System.nanoTime()));
            for (Data data : trainningData) {
                double error = train(data);
                runningError += error;
            }

            this.trainingCounter++;

            double avError = runningError / (this.trainingCounter * trainningData.size());

            System.out.println("第 " + this.trainingCounter + " 次平均誤差: " + avError);

            if (avError < converge) {
                break;
            }
        }

        System.out.println();
//        System.out.println("========== 開始辨識 ==========");
//
//        double successCounter = 0;
//
//        for (Data data : testData) {
//            if (testInput(data)) {
//                successCounter ++;
//            }
//        }
//
//        System.out.println("測試資料數量: " + testData.size());
//        System.out.println("辨識成功次數: " + successCounter);
//        System.out.println("辨識成功率: " + successCounter * 100 / testData.size());
//        System.out.println();
//
//        plotting(testData);
    }

    public void testing(ArrayList<Data> testDataSet) {
        ArrayList<Integer> xs = new ArrayList<>();
        ArrayList<Integer> ys = new ArrayList<>();
        for (int i = 0; i < testDataSet.size(); i ++) {
            xs.add(i);
            ys.add(testInput(testDataSet.get(i)) + 1);
        }

        this.plotFrame = new JFrame("Plot");
        this.plotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Plot2DPanel plot = new Plot2DPanel();

        double[] x = xs.stream().mapToDouble(d -> d).toArray();
        double[] y = ys.stream().mapToDouble(d -> d).toArray();
        Random r = new Random(System.nanoTime());
        plot.addLinePlot("my plot", new Color(r.nextInt(250), r.nextInt(250), r.nextInt(250)), x, y);

        this.plotFrame.setContentPane(plot);
        this.plotFrame.pack();
        this.plotFrame.setSize(500, 500);
        this.plotFrame.setVisible(true);
    }

    private void plotting(List<Data> testData) {
        if (this.plotFrame == null) {
            this.plotFrame = new JFrame("Plot");
            this.plotFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        Thread plottingThread = new Thread(() -> {
            int dimension = testData.get(0).dimension();
            if (dimension == 2) {
                Plot2DPanel plot = new Plot2DPanel();
                HashMap<Integer, ArrayList<Double>> xs = new HashMap<>();
                HashMap<Integer, ArrayList<Double>> ys = new HashMap<>();
                for(int i = 0; i < testData.size(); i++) {
                    int actual = testData.get(i).actual;
                    if (!xs.containsKey(actual)) {
                        xs.put(actual, new ArrayList<>());
                        ys.put(actual, new ArrayList<>());
                    }
                    xs.get(actual).add(testData.get(i).get(0));
                    ys.get(actual).add(testData.get(i).get(1));
                }
                Set<Integer> keys = xs.keySet();
                Random r = new Random(System.nanoTime());
                for(int key : keys) {
                    double[] x = xs.get(key).stream().mapToDouble(d -> d).toArray();
                    double[] y = ys.get(key).stream().mapToDouble(d -> d).toArray();
                    plot.addScatterPlot("my plot", new Color(r.nextInt(250), r.nextInt(250), r.nextInt(250)), x, y);
                }

                this.plotFrame.setContentPane(plot);
                this.plotFrame.pack();
                this.plotFrame.setSize(500, 500);
                this.plotFrame.setVisible(true);
            } else if (dimension == 3) {
                Plot3DPanel plot = new Plot3DPanel();
                HashMap<Integer, ArrayList<Double>> xs = new HashMap<>();
                HashMap<Integer, ArrayList<Double>> ys = new HashMap<>();
                HashMap<Integer, ArrayList<Double>> zs = new HashMap<>();
                for(int i = 0; i < testData.size(); i++) {
                    int actual = testData.get(i).actual;
                    if (!xs.containsKey(actual)) {
                        xs.put(actual, new ArrayList<>());
                        ys.put(actual, new ArrayList<>());
                        zs.put(actual, new ArrayList<>());
                    }
                    xs.get(actual).add(testData.get(i).get(0));
                    ys.get(actual).add(testData.get(i).get(1));
                    zs.get(actual).add(testData.get(i).get(2));
                }
                Set<Integer> keys = xs.keySet();
                Random r = new Random(System.nanoTime());
                for(int key : keys) {
                    double[] x = xs.get(key).stream().mapToDouble(d -> d).toArray();
                    double[] y = ys.get(key).stream().mapToDouble(d -> d).toArray();
                    double[] z = zs.get(key).stream().mapToDouble(d -> d).toArray();
                    plot.addScatterPlot("my plot", new Color(r.nextInt(250), r.nextInt(250), r.nextInt(250)), x, y, z);
                }

                this.plotFrame.setContentPane(plot);
                this.plotFrame.pack();
                this.plotFrame.setSize(500, 500);
                this.plotFrame.setVisible(true);
            } else {
                this.plotFrame.setVisible(false);
            }
        });

        plottingThread.start();
    }
}
