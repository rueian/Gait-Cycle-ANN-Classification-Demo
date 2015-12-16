import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    private static ArrayList<Data> dataSet;
    private static ArrayList<Data> testDataSet;
    private static MLP mlp;
    private static String filePath = "";
    private static String structureStr = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JTextArea console = new JTextArea();
        JScrollPane consolePane = new JScrollPane(console,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JLabel filePathLabel = new JLabel("訓練資料集");
        JTextField filePathField = new JTextField("/Users/rueian/Code/python/tracking/result-processed");
        JButton loadFileDialog = new JButton("選擇");
        JLabel testPathLabel = new JLabel("測試資料集");
        JTextField testPathField = new JTextField("/Users/rueian/Code/python/tracking/result-test");
        JButton loadTestDialog = new JButton("選擇");
        JLabel structureLabel = new JLabel("隱藏層架構");
        JTextField structureField = new JTextField("13,13");
        JLabel scalarLabel = new JLabel("縮放");
        JTextField scalarField = new JTextField("1");
        JLabel weightLabel = new JLabel("初始權重");
        JTextField weightField = new JTextField("1");
        JLabel learnRateLabel = new JLabel("學習率");
        JTextField learnRateField = new JTextField("0.4");
        JLabel inertiaLabel = new JLabel("慣性");
        JTextField inertiaField = new JTextField("0.5");
        JLabel learnTimesLabel = new JLabel("學習次數");
        JTextField learnTimesField = new JTextField("10000");
        JLabel convergeLabel = new JLabel("收斂小於");
        JTextField convergeField = new JTextField("0.04");
        JButton startButton = new JButton("訓練");
        JButton testButton = new JButton("辨識");
        JButton resetButton = new JButton("重置");

        loadFileDialog.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser("選擇");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        resetButton.addActionListener(e -> {
            mlp = null;
            console.setText("");
        });

        startButton.addActionListener(e -> {
            if (mlp == null ||
                    !filePathField.getText().equals(filePath) ||
                    !structureField.getText().equals(structureStr)) {

                try {
                    double scalar = Double.parseDouble(scalarField.getText());
                    loadDataSet(new File(filePathField.getText()), scalar);
                } catch (IOException e1) {
                    return;
                }

                filePath = filePathField.getText();
                structureStr = structureField.getText();

                double learnRate = Double.parseDouble(learnRateField.getText());
                double converge = Double.parseDouble(convergeField.getText());
                double inertia = Double.parseDouble(inertiaField.getText());
                double weight = Double.parseDouble(weightField.getText());
                int learnTimes = Integer.parseInt(learnTimesField.getText());
                int[] structure = Arrays.stream(structureField.getText().split(",")).mapToInt(Integer::parseInt).toArray();

                mlp = new MLP(dataSet, structure, learnRate, learnTimes, converge, inertia, weight);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            mlp.training();

            System.out.flush();
            System.setOut(old);

            console.append(baos.toString());
        });

        testButton.addActionListener(e -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            try {
                loadtestDataSet(new File(testPathField.getText()), 1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            mlp.testing(testDataSet);

            System.out.flush();
            System.setOut(old);

            console.append(baos.toString());
        });

        consolePane.setPreferredSize(new Dimension(450, 300));
        filePathLabel.setPreferredSize(new Dimension(60, 40));
        filePathField.setPreferredSize(new Dimension(340, 40));
        loadFileDialog.setPreferredSize(new Dimension(60, 40));
        testPathLabel.setPreferredSize(new Dimension(60, 40));
        testPathField.setPreferredSize(new Dimension(340, 40));
        loadTestDialog.setPreferredSize(new Dimension(60, 40));
        structureLabel.setPreferredSize(new Dimension(60, 40));
        structureField.setPreferredSize(new Dimension(160, 40));
        scalarLabel.setPreferredSize(new Dimension(60, 40));
        scalarField.setPreferredSize(new Dimension(60, 40));
        weightLabel.setPreferredSize(new Dimension(60, 40));
        weightField.setPreferredSize(new Dimension(60, 40));
        learnRateLabel.setPreferredSize(new Dimension(50, 40));
        learnRateField.setPreferredSize(new Dimension(50, 40));
        inertiaLabel.setPreferredSize(new Dimension(50, 40));
        inertiaField.setPreferredSize(new Dimension(50, 40));
        learnTimesLabel.setPreferredSize(new Dimension(60, 40));
        learnTimesField.setPreferredSize(new Dimension(50, 40));
        convergeLabel.setPreferredSize(new Dimension(60, 40));
        convergeField.setPreferredSize(new Dimension(50, 40));
        startButton.setPreferredSize(new Dimension(150, 40));
        testButton.setPreferredSize(new Dimension(150, 40));
        resetButton.setPreferredSize(new Dimension(150, 40));

        panel.setLayout(new FlowLayout());
        panel.setPreferredSize(new Dimension(500, 500));
        panel.add(filePathLabel);
        panel.add(filePathField);
        panel.add(loadFileDialog);
        panel.add(testPathLabel);
        panel.add(testPathField);
        panel.add(loadTestDialog);
        panel.add(structureLabel);
        panel.add(structureField);
        panel.add(scalarLabel);
        panel.add(scalarField);
        panel.add(weightLabel);
        panel.add(weightField);
        panel.add(learnRateLabel);
        panel.add(learnRateField);
        panel.add(inertiaLabel);
        panel.add(inertiaField);
        panel.add(learnTimesLabel);
        panel.add(learnTimesField);
        panel.add(convergeLabel);
        panel.add(convergeField);
        panel.add(startButton);
        panel.add(testButton);
        panel.add(resetButton);
        panel.add(consolePane);

        ( ( DefaultCaret ) console.getCaret() ).setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );

        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void loadDataSet(File f, double scalar) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            dataSet = new ArrayList<>();
            for (String line; (line = br.readLine()) != null;) {
                double[] values = Arrays.stream(line.split(" |\t"))
                        .filter(s -> s.length() > 0)
                        .mapToDouble(Double::parseDouble).toArray();
                dataSet.add(new Data(values, scalar));
            }
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "無法解析所選檔案，請換一個試試看");
            throw e1;
        }
    }

    private static void loadtestDataSet(File f, double scalar) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            testDataSet = new ArrayList<>();
            for (String line; (line = br.readLine()) != null;) {
                double[] values = Arrays.stream(line.split(" |\t"))
                        .filter(s -> s.length() > 0)
                        .mapToDouble(Double::parseDouble).toArray();
                testDataSet.add(new Data(values, scalar));
            }
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "無法解析所選檔案，請換一個試試看");
            throw e1;
        }
    }
}
