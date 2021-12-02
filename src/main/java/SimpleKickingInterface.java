import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class SimpleKickingInterface extends JFrame {
    private static File fileName;
    private JPanel MAIN;
    private JComboBox startingMonthBox;
    private JComboBox startingYearBox;
    private JComboBox endingMonthBox;
    private JComboBox endingYearBox;
    private JCheckBox leftHashCheckBox;
    private JCheckBox leftMiddleCheckBox;
    private JCheckBox middleCheckBox;
    private JCheckBox rightMiddleCheckBox;
    private JCheckBox rightHashCheckBox;
    private JButton calculateButton;
    private JTextField minimumNumberOfKicksBox;
    private JTextField maximumNumberOfKicksBox;
    private JComboBox xAxisBox;
    private JComboBox yAxisBox;
    private JTextArea moreStatisticsTextArea;
    private JButton generateGraphButton;
    private JCheckBox allDataBox;
    private JComboBox minimumKickDistanceBox;
    private JComboBox maximumKickDistanceBox;

    private static final int NUM_FG_CATEGORIES = 20;
    private static final int NUM_KICKOFF_CATEGORIES = 21;
    private static final int NUM_PUNT_CATEGORIES = 24;

    private int minNumKicks_Lim;
    private int maxNumKicks_Lim;
    private boolean[] hashLimiter;
    private int startMonth;
    private int startYear;
    private int endMonth;
    private int endYear;
    private boolean allData;
    private int minimumKickDistance;
    private int maximumKickDistance;
    private static FieldGoalTotalSessions totalSessions;
    private static FieldGoalTotalSessions includedSessions;

    private static boolean wasClosed;

    private static int yMax;
    private static int xMax;
    private static int yMin;
    private static int xMin;




    //TODO: Remove the JPanel for the graph and more evenly distribute the UI layout
    //TODO: Add an image to the JPanel
    //TODO: Figure out how to switch between windows for punt and kickoff

    public SimpleKickingInterface() {

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: THIS BEGINS THE CALCULATIONS
                if(checkForErrors()) {
                    setFields();
                    try {
                        printStatistics();
                    } catch (IOException | CsvException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        generateGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(checkForErrorsGraphing()) {
                    setFields();
                    try {
                        printStatistics();
                    } catch (IOException | CsvException ioException) {
                        ioException.printStackTrace();
                    }
                    setGraph();
                }


            }
        });

    }

    private boolean checkForErrors() {

        try {
            int x = Integer.parseInt(minimumNumberOfKicksBox.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(null, "Please select minimum number of kicks.");
            return false;
        }

        try {
            int x = Integer.parseInt(maximumNumberOfKicksBox.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(null, "Please select maximum number of kicks.");
            return false;
        }

        if (!leftHashCheckBox.isSelected() && !leftMiddleCheckBox.isSelected() && !middleCheckBox.isSelected() && !rightMiddleCheckBox.isSelected() && !rightHashCheckBox.isSelected() && !rightHashCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(null, "Must select at least one hash.");
            return false;
        } else if (Integer.parseInt(minimumNumberOfKicksBox.getText()) < 1){
            JOptionPane.showMessageDialog(null, "Minimum number of kicks must be at least 1.");
            return false;
        } else if(Integer.parseInt(minimumNumberOfKicksBox.getText()) > 501) {
            JOptionPane.showMessageDialog(null, "Maximum number of kicks must be 500 or less.");
            return false;
        } else if(Integer.parseInt(minimumNumberOfKicksBox.getText()) > Integer.parseInt(maximumNumberOfKicksBox.getText())) {
            JOptionPane.showMessageDialog(null, "Minimum number of kicks can not be greater than maximum.");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(startingMonthBox.getSelectedItem()).toString().equals("--Starting Month--")) {
            JOptionPane.showMessageDialog(null, "Please select a starting month");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(endingMonthBox.getSelectedItem()).toString().equals("--Ending Month--")) {
            JOptionPane.showMessageDialog(null, "Please select an ending month");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(startingYearBox.getSelectedItem()).toString().equals("--Starting Year--")) {
            JOptionPane.showMessageDialog(null, "Please select a starting year");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(startingMonthBox.getSelectedItem()).toString().equals("--Ending Year--")) {
            JOptionPane.showMessageDialog(null, "Please select an ending year");
            return false;
        } else if(!Objects.requireNonNull(minimumKickDistanceBox.getSelectedItem()).toString().equals("ALL") &&
                !Objects.requireNonNull(maximumKickDistanceBox.getSelectedItem()).toString().equals("ALL")) {
                if(Integer.parseInt(minimumKickDistanceBox.getSelectedItem().toString()) >
                        Integer.parseInt(maximumKickDistanceBox.getSelectedItem().toString())) {
                    JOptionPane.showMessageDialog(null, "Maximum distance cannot be less than minimum distance");
                    return false;
                }
        }

        return true;

        //JOptionPane.showMessageDialog(null, "Message");

    }

    private boolean checkForErrorsGraphing() {

        try {
            int x = Integer.parseInt(minimumNumberOfKicksBox.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(null, "Please select minimum number of kicks.");
            return false;
        }

        try {
            int x = Integer.parseInt(maximumNumberOfKicksBox.getText());
        } catch (Exception NumberFormatException) {
            JOptionPane.showMessageDialog(null, "Please select maximum number of kicks.");
            return false;
        }

        if (!leftHashCheckBox.isSelected() && !leftMiddleCheckBox.isSelected() && !middleCheckBox.isSelected() && !rightMiddleCheckBox.isSelected() && !rightHashCheckBox.isSelected() && !rightHashCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(null, "Must select at least one hash.");
            return false;
        } else if (Integer.parseInt(minimumNumberOfKicksBox.getText()) < 1){
            JOptionPane.showMessageDialog(null, "Minimum number of kicks must be at least 1.");
            return false;
        } else if(Integer.parseInt(minimumNumberOfKicksBox.getText()) > 501) {
            JOptionPane.showMessageDialog(null, "Maximum number of kicks must be 500 or less.");
            return false;
        } else if(Integer.parseInt(minimumNumberOfKicksBox.getText()) > Integer.parseInt(maximumNumberOfKicksBox.getText())) {
            JOptionPane.showMessageDialog(null, "Minimum number of kicks can not be greater than maximum.");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(startingMonthBox.getSelectedItem()).toString().equals("--Starting Month--")) {
            JOptionPane.showMessageDialog(null, "Please select a starting month");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(endingMonthBox.getSelectedItem()).toString().equals("--Ending Month--")) {
            JOptionPane.showMessageDialog(null, "Please select an ending month");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(startingYearBox.getSelectedItem()).toString().equals("--Starting Year--")) {
            JOptionPane.showMessageDialog(null, "Please select a starting year");
            return false;
        } else if(!allDataBox.isSelected() && Objects.requireNonNull(startingMonthBox.getSelectedItem()).toString().equals("--Ending Year--")) {
            JOptionPane.showMessageDialog(null, "Please select an ending year");
            return false;
        } else if(!Objects.requireNonNull(minimumKickDistanceBox.getSelectedItem()).toString().equals("ALL") &&
                !Objects.requireNonNull(maximumKickDistanceBox.getSelectedItem()).toString().equals("ALL")) {
            if(Integer.parseInt(minimumKickDistanceBox.getSelectedItem().toString()) >
                    Integer.parseInt(maximumKickDistanceBox.getSelectedItem().toString())) {
                JOptionPane.showMessageDialog(null, "Maximum distance cannot be less than minimum distance");
                return false;
            }
        }

        if (Objects.requireNonNull(xAxisBox.getSelectedItem()).toString().equals("--X-AXIS--")) {
            JOptionPane.showMessageDialog(null, "Please select an X-axis");
            return false;
        } else if (Objects.requireNonNull(yAxisBox.getSelectedItem()).toString().equals("--Y-AXIS--")) {
            JOptionPane.showMessageDialog(null, "Please select an Y-axis");
            return false;
        }

        return true;

        //JOptionPane.showMessageDialog(null, "Message");

    }

    private void setFields() {
        // TODO: Sets Fields from Values Currently In All Boxes

        minNumKicks_Lim = Integer.parseInt(minimumNumberOfKicksBox.getText());

        maxNumKicks_Lim = Integer.parseInt(maximumNumberOfKicksBox.getText());

        boolean leftHash_Lim = leftHashCheckBox.isSelected();
        boolean leftMiddleHash_Lim = leftMiddleCheckBox.isSelected();
        boolean centerHash_Lim = middleCheckBox.isSelected();
        boolean rightMiddleHash_Lim = rightMiddleCheckBox.isSelected();
        boolean rightHash_Lim = rightHashCheckBox.isSelected();

        if(Objects.requireNonNull(minimumKickDistanceBox.getSelectedItem()).toString().equals("ALL")) {
            minimumKickDistance = 1;
        } else {
            minimumKickDistance = Integer.parseInt(Objects.requireNonNull(minimumKickDistanceBox.getSelectedItem()).toString());
        }

        if(Objects.requireNonNull(maximumKickDistanceBox.getSelectedItem()).toString().equals("ALL")) {
            maximumKickDistance = 100;
        } else {
            maximumKickDistance = Integer.parseInt(Objects.requireNonNull(maximumKickDistanceBox.getSelectedItem()).toString());
        }

        hashLimiter = new boolean[5];

        Arrays.fill(hashLimiter, false);

        if(leftHash_Lim) {
            hashLimiter[0] = true;
        }

        if(leftMiddleHash_Lim) {
            hashLimiter[1] = true;
        }

        if(centerHash_Lim) {
            hashLimiter[2] = true;
        }

        if(rightMiddleHash_Lim) {
            hashLimiter[3] = true;
        }

        if(rightHash_Lim) {
            hashLimiter[4] = true;
        }

        if(!allDataBox.isSelected()) {

            switch (Objects.requireNonNull(startingMonthBox.getSelectedItem()).toString()) {
                case "ALL":
                case "January":
                    startMonth = 1;
                    break;
                case "February":
                    startMonth = 2;
                    break;
                case "March":
                    startMonth = 3;
                    break;
                case "April":
                    startMonth = 4;
                    break;
                case "May":
                    startMonth = 5;
                    break;
                case "June":
                    startMonth = 6;
                    break;
                case "July":
                    startMonth = 7;
                    break;
                case "August":
                    startMonth = 8;
                    break;
                case "September":
                    startMonth = 9;
                    break;
                case "October":
                    startMonth = 10;
                    break;
                case "November":
                    startMonth = 11;
                    break;
                case "December":
                    startMonth = 12;
                    break;
            }

            switch (Objects.requireNonNull(endingMonthBox.getSelectedItem()).toString()) {
                case "January":
                    endMonth = 1;
                    break;
                case "February":
                    endMonth = 2;
                    break;
                case "March":
                    endMonth = 3;
                    break;
                case "April":
                    endMonth = 4;
                    break;
                case "May":
                    endMonth = 5;
                    break;
                case "June":
                    endMonth = 6;
                    break;
                case "July":
                    endMonth = 7;
                    break;
                case "August":
                    endMonth = 8;
                    break;
                case "September":
                    endMonth = 9;
                    break;
                case "October":
                    endMonth = 10;
                    break;
                case "November":
                    endMonth = 11;
                    break;
                case "ALL":
                case "December":
                    endMonth = 12;
                    break;
            }


            if (Objects.requireNonNull(startingYearBox.getSelectedItem()).toString().equals("ALL")) {
                startYear = 1900;
            } else {
                startYear = Integer.parseInt(Objects.requireNonNull(startingYearBox.getSelectedItem()).toString());
            }

            if (Objects.requireNonNull(endingYearBox.getSelectedItem()).toString().equals("ALL")) {
                endYear = 2100;
            } else {
                endYear = Integer.parseInt(Objects.requireNonNull(endingYearBox.getSelectedItem()).toString());
            }
        }



        if(allDataBox.isSelected()) {
            startYear = 1900;
            endYear = 2100;
            startMonth = 1;
            endMonth = 12;
        }

    }

    private void printStatistics() throws IOException, CsvException {
        // TODO: Calculates general field goal statistics for printing in text panel

        /*
            Stats to include:
                -Print the limiters (Stats for: ____ )
                -Average distance
                -Miles of FGs kicked
                -Most kicks were from
                -How many were from increment of 5 yards
                -In each session, each kick moves...
                -Need a method that creates a new list of sessions that fall into limiter categories
                -R-squared value for above

         */

        // Map the CSV to a list
        CSVReader FieldGoal_Reader = new CSVReader(new FileReader(fileName));
        List<String[]> FG_List = FieldGoal_Reader.readAll();
        Iterator<String[]> FG_it = FG_List.iterator();

        // Convert list to array of objects
        String[][] fieldGoalData = new String[FG_List.size()][NUM_FG_CATEGORIES];
        int counter = 0;
        while (FG_it.hasNext()) {
            fieldGoalData[counter] = FG_it.next();
            counter++;
        }

        // Separate data type to hold field goals by session
        FieldGoalTotalSessions allFGSessions = new FieldGoalTotalSessions();

        for (int i = 1; i < FG_List.size(); i++) {

            // Increments sum and adds kick to session data structure
            if (fieldGoalData[i][3].compareTo("") != 0 && fieldGoalData[i][15].compareTo("") != 0) {
                int year = Integer.parseInt(fieldGoalData[i][7].substring(0,4));
                int month = Integer.parseInt(fieldGoalData[i][7].substring(5,7));
                double hash = Double.parseDouble(fieldGoalData[i][13]);
                boolean make = false;
                double xVal = Double.parseDouble(fieldGoalData[i][10]);
                double yVal = Double.parseDouble(fieldGoalData[i][11]);

                if(xVal >= 0 && xVal <= 1 && yVal >= 0) {
                    make = true;

                }

                allFGSessions.addKick(fieldGoalData[i][15], Integer.parseInt(fieldGoalData[i][3]), month, year, hash, make, Integer.parseInt(fieldGoalData[i][9]));
            }
        }

        totalSessions = allFGSessions;
        includedSessions = new FieldGoalTotalSessions();

        // The code above completely organizes all sessions of field goal data.

        // Compute average field goal distance and number of kicks the limiters apply to
        int numberOfKicksIncluded = 0;

        int totalDistanceSum = 0;

        int[] totalHashSum = new int[5];

        int sumOfMakes = 0;

        for(int i = 0; i < allFGSessions.getNumSessions(); i++) {

            if(allFGSessions.getFieldGoalSession(i).getNumKicks() >= minNumKicks_Lim && allFGSessions.getFieldGoalSession(i).getNumKicks() <= maxNumKicks_Lim) {
                for(int j = 0; j < allFGSessions.getFieldGoalSession(i).getNumKicks(); j++) {
                    // hash, month and year
                    System.out.println((int) (allFGSessions.getFieldGoalSession(i).getHash(j) * 4));
                    if(hashLimiter[(int) (allFGSessions.getFieldGoalSession(i).getHash(j) * 4)] &&
                            allFGSessions.getFieldGoalSession(i).getYear() >= startYear &&
                            allFGSessions.getFieldGoalSession(i).getYear() <= endYear &&
                            allFGSessions.getFieldGoalSession(i).getKickAtIndex(j) >= minimumKickDistance &&
                            allFGSessions.getFieldGoalSession(i).getKickAtIndex(j) <= maximumKickDistance) {
                        if ((allFGSessions.getFieldGoalSession(i).getYear() == startYear &&
                        allFGSessions.getFieldGoalSession(i).getMonth() > startMonth) || allFGSessions.getFieldGoalSession(i).getYear() > startYear){
                            if((allFGSessions.getFieldGoalSession(i).getYear() == endYear &&
                                    allFGSessions.getFieldGoalSession(i).getMonth() < endMonth) || allFGSessions.getFieldGoalSession(i).getYear() < endYear) {

                                includedSessions.addKick(allFGSessions.getFieldGoalSession(i).getSessionID(),
                                        allFGSessions.getFieldGoalSession(i).getKickAtIndex(j),
                                        allFGSessions.getFieldGoalSession(i).getMonth(),
                                        allFGSessions.getFieldGoalSession(i).getYear(),
                                        allFGSessions.getFieldGoalSession(i).getHash(j),
                                        allFGSessions.getFieldGoalSession(i).getMake(j), allFGSessions.getFieldGoalSession(i).getKickNum(j));

                                // The inside of this block contains ALL kicks that abide by the limiters
                                numberOfKicksIncluded++;
                                totalDistanceSum += allFGSessions.getFieldGoalSession(i).getKickAtIndex(j);
                                //TODO: ALL CALCULATIONS TO OCCUR IN HERE

                                int currentHash = (int) (allFGSessions.getFieldGoalSession(i).getHash(j) * 4);
                                totalHashSum[currentHash]++;


                                if(allFGSessions.getFieldGoalSession(i).getMake(j)) {
                                    sumOfMakes++;
                                }

                            }
                        }


                    }
                }
            }


        }

        double FG_Average_Distance = (double) totalDistanceSum / numberOfKicksIncluded;
        double makePercentage = (((double) sumOfMakes) / ((double) numberOfKicksIncluded)) * 100;
        String mostUsedHash = "";

        switch(maxArrayValue(totalHashSum)) {
            case 0:
                mostUsedHash = "left hash";
                break;
            case 1:
                mostUsedHash = "left middle";
                break;
            case 2:
                mostUsedHash = "center";
                break;
            case 3:
                mostUsedHash = "right middle";
                break;
            case 4:
                mostUsedHash = "right hash";
                break;
            default:
                break;
        }



        moreStatisticsTextArea.setText("Number of kicks that apply to these limiters: " + numberOfKicksIncluded +
                "\nAverage distance of kicks in this set: " + ((double) Math.round(FG_Average_Distance * 100) / 100) + " Yards" +
                "\nMost kicks came from the " + mostUsedHash + ". The number of kicks at this location is " + totalHashSum[maxArrayValue(totalHashSum)] +
                "\nThe field goal percentage for all kicks in this range is: " + ((double) Math.round(makePercentage * 100) / 100) + "%");


    }

    public static int maxArrayValue(int[] x) {
        if(x == null) {
            throw new NullPointerException("Array is empty.");
        } else {
            int currentGreatest = x[0];
            int currentGreatestIndex = 0;
            for (int i = 0; i < x.length; i++) {
                if(x[i] > currentGreatest) {
                    currentGreatestIndex = i;
                    currentGreatest = x[i];
                }
            }
            return currentGreatestIndex;
        }
    }

    private XYDataset createDataset(String x, String y) {

        XYSeries xySeries = new XYSeries("X-Series");

        if (x.equals("Kick Number")) {

            xMax = maxNumKicks_Lim;
            xMin = minNumKicks_Lim;


            if (y.equals("Kick Distance")) {

                int[] numKicksAtDistance = new int[500];
                int[] sumsOfDistances = new int[500];

                for (int i = 0; i < includedSessions.getNumSessions(); i++) {
                    for (int j = 0; j < includedSessions.getFieldGoalSession(i).getNumKicks(); j++) {
                        numKicksAtDistance[includedSessions.getFieldGoalSession(i).getKickNum(j)]++;
                        sumsOfDistances[includedSessions.getFieldGoalSession(i).getKickNum(j)] += includedSessions.getFieldGoalSession(i).getKickAtIndex(j);
                    }
                }

                for(int i = 0; i < numKicksAtDistance.length; i++) {
                    xySeries.add(i, ((float) sumsOfDistances[i]) / ((float) numKicksAtDistance[i]));
                }
                yMin = minimumKickDistance;
                yMax = maximumKickDistance;

            } else if (y.equals("Make Percentage")) {
                int[] kickNumberArray = new int[500];
                int[] makesArray = new int[500];
                for (int i = 0; i < includedSessions.getNumSessions(); i++) {
                    for (int j = 0; j < includedSessions.getFieldGoalSession(i).getNumKicks(); j++) {
                        kickNumberArray[includedSessions.getFieldGoalSession(i).getKickNum(j)]++;
                        if(includedSessions.getFieldGoalSession(i).getMake(j)) {
                            makesArray[includedSessions.getFieldGoalSession(i).getKickNum(j)]++;
                        }
                    }
                }

                for(int i = 0; i < makesArray.length; i++) {
                    xySeries.add(i, (((double) makesArray[i]) / ((double) kickNumberArray[i])) * 100);
                }

                yMin = 0;
                yMax = 100;



            }
        } else if (x.equals("Kick Distance")) {

            xMin = minimumKickDistance;
            xMax = maximumKickDistance;

            if (y.equals("Kick Distance")) {
                for (int i = 0; i < includedSessions.getNumSessions(); i++) {
                    for (int j = 0; j < includedSessions.getFieldGoalSession(i).getNumKicks(); j++) {
                        xySeries.add(includedSessions.getFieldGoalSession(i).getKickAtIndex(j), includedSessions.getFieldGoalSession(i).getKickAtIndex(j));
                    }
                }
                yMin = minimumKickDistance;
                yMax = maximumKickDistance;
            } else if (y.equals("Make Percentage")) {
                int[] distancesArray = new int[71];
                int[] makesArray = new int[71];
                for (int i = 0; i < includedSessions.getNumSessions(); i++) {
                    for (int j = 0; j < includedSessions.getFieldGoalSession(i).getNumKicks(); j++) {
                        distancesArray[includedSessions.getFieldGoalSession(i).getKickAtIndex(j)]++;
                        if(includedSessions.getFieldGoalSession(i).getMake(j)) {
                            makesArray[includedSessions.getFieldGoalSession(i).getKickAtIndex(j)]++;
                        }
                    }
                }

                for(int i = 0; i < makesArray.length; i++) {
                    xySeries.add(i, (((double) makesArray[i]) / ((double) distancesArray[i])) * 100);
                }
            }

            yMin = 0;
            yMax = 100;

        }
        XYSeriesCollection ds = new XYSeriesCollection();
        ds.addSeries(xySeries);
        return ds;
    }

    public void setGraph() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Charts");

                frame.setSize(600, 400);
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setVisible(true);

                XYDataset ds = createDataset(Objects.requireNonNull(xAxisBox.getSelectedItem()).toString(), Objects.requireNonNull(yAxisBox.getSelectedItem()).toString());

                String yLabel;


                if(xAxisBox.getSelectedItem().toString().equals("Kick Number") && yAxisBox.getSelectedItem().toString().equals("Kick Distance")) {
                    yLabel = "Average Kick Distance";
                } else {
                    yLabel = yAxisBox.getSelectedItem().toString();
                }

                JFreeChart chart = ChartFactory.createScatterPlot("Test Chart",
                        xAxisBox.getSelectedItem().toString(), yLabel, ds, PlotOrientation.VERTICAL, true, true,
                        false);


                chart.setTitle(yLabel + " vs. " + xAxisBox.getSelectedItem().toString());

                XYPlot xyPlot = (XYPlot) chart.getPlot();

                xyPlot.setDomainCrosshairVisible(true);
                xyPlot.setRangeCrosshairVisible(true);

                NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
                domain.setRange(xMin, xMax);
                domain.setTickUnit(new NumberTickUnit(10));
                domain.setVerticalTickLabels(true);
                NumberAxis range = (NumberAxis) xyPlot.getRangeAxis();
                range.setRange(yMin, yMax);
                range.setTickUnit(new NumberTickUnit(10));
                range.setVerticalTickLabels(true);



                ChartPanel cp = new ChartPanel(chart);

                cp.setMouseWheelEnabled(true);
                cp.setHorizontalAxisTrace(true);
                cp.setVerticalAxisTrace(true);

                frame.getContentPane().add(cp);
            }
        });

    }

    public static void main(String[] args) throws Exception {

        JFileChooser fc = new JFileChooser();
        JFrame frame = new JFrame("File Chooser");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        wasClosed = false;


        JOptionPane.showMessageDialog(null, "Please select Field Goal CSV.");
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.showOpenDialog(frame);
        fileName = fc.getSelectedFile();


        SimpleKickingInterface form = new SimpleKickingInterface();
        form.setContentPane(new SimpleKickingInterface().MAIN);
        form.pack();
        form.setSize(1000,600);
        form.setLocationRelativeTo(null);
        form.setVisible(true);


    }
}
