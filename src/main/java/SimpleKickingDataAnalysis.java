import com.opencsv.CSVReader;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

/**
 * This program utilizes the simple kicking database to calculate
 * the values of different kicking and punting statistics
 *
 * @author  Jacob Lurie
 * @version 1.2
 * @since   2021-11-04
 */

public class SimpleKickingDataAnalysis {

    private static final int NUM_FG_CATEGORIES = 20;
    private static final int NUM_KICKOFF_CATEGORIES = 21;
    private static final int NUM_PUNT_CATEGORIES = 24;

    public static void main(String args[]) throws Exception {


        //TODO: All of the below computations are to be used in designing a UI
        //TODO: Compute FG percentage from each distance

        /*
            Summary of code and thought process:

            The simple kicking app collects field goal, punting, and kickoff data from all different levels of kickers.
            It takes all recorded data and sorts it into an MxN CSV file where M is the number categories of data and
            N is the number of kicks recorded. Each user has an assigned user ID and each session that they record
            is given a unique ID. Data is taken in integers which in most cases is in either yards or seconds depending
            on what data is in question.

            The future implications of this project is to turn it into a UI for the company's founder. The UI will allow
            him to directly upload updated CSVs and be displayed the data he has indicated he wants to see. He will be
            able to select ranges of data such as limit data to certain distances, hashes (left, right, center, etc.),
            sessions with a certain range of kicks, and other limitations. he will then be able to choose an X and Y
            axis for a JPanel graph that will display the data he chooses with a calculated line of best fit. A further
            implication of this would be to use machine learning to better find relationships in the data.

            I decided to figure out all of the calculations in a main first before transitioning over to the UI to
            decrease the complexity of finding solutions to computing certain statistics. Once all statistics are found,
            a new class will created and the existing methods and computations will be transitioned over to work with
            the UI that is designed.

            For calculations:

            Strictly linear operations (averaging values and computing sums) are calculated from the array that
            is mapped directly from all data in the CSV.

            Operations that involve data PER SESSION are done after the data is sorted into the field goal sessions
            data type (FieldGoalTotalSessions) that contains an arraylist of a smaller abstract data type
            (FieldGoalSession) that contains the ID of the session and the distance of all kicks in that session.
            Sessions have a minimum of one kick and only have one string ID. Since adding a random kick requires the
            session ID to be found, the overarching FieldGoalTotalSessions object contains an adding function that first
            searches then adds the kick.

            From what I have found, there is no need to sort the individual kicks within each session since they are
            already sorted within the CSV.

            Questions: Is there a more a efficient way to ADD kicks to the sessions? Is it worth sorting the sessions
            by ID or potentially using a hash table based on the ID (which is a string)? Is there a more efficient
            structure that can be used for the session instead of an object containing an array list of more specific
            objects?
         */


        // Map the CSV to a list
        CSVReader FieldGoal_Reader = new CSVReader(new FileReader("FG.csv"));
        List<String[]> FG_List = FieldGoal_Reader.readAll();
        Iterator<String[]> FG_it = FG_List.iterator();

        // Convert list to array of objects
        String[][] fieldGoalData = new String[FG_List.size()][NUM_FG_CATEGORIES];
        int counter = 0;
        while (FG_it.hasNext()) {
            fieldGoalData[counter] = FG_it.next();
            counter++;
        }

        // Holds sum of all distances to be average later
        int FG_TotalDistanceSum = 0;

        // Holds number of kicks from each location
        int[] placement = new int[4];

        // Holds number of kicks from line that is increment of five, to be averaged later
        int numKicksFromFives = 0;

        // Separate data type to hold field goals by session
        FieldGoalTotalSessions allFGSessions = new FieldGoalTotalSessions();


        // Parse through all data entries
        // Ignore 0 values (denotes column title)
        for (int i = 1; i < FG_List.size(); i++) {

            // Increments sum and adds kick to session data structure
            if (fieldGoalData[i][3].compareTo("") != 0 && fieldGoalData[i][15].compareTo("") != 0) {
                FG_TotalDistanceSum += (Integer.parseInt(fieldGoalData[i][3]));
                //allFGSessions.addKick(fieldGoalData[i][15], Integer.parseInt(fieldGoalData[i][3]));
            }

            // Increments numKicksFromFives value
            if (Integer.parseInt(fieldGoalData[i][3]) % 5 == 0) {
                numKicksFromFives++;
            }

            // Computes locations of kicks and completes placement array
            if (fieldGoalData[i][13] != null) {
                switch (fieldGoalData[i][13]) {
                    case "0" :
                        placement[0]++;
                        break;
                    case "0.25" :
                        placement[1]++;
                        break;
                    case "0.5" :
                        placement[2]++;
                        break;
                    case "0.75" :
                        placement[3]++;
                        break;
                    case "1" :
                        placement[4]++;
                        break;
                    default:
                        break;

                }
            }


        }

        /*
            When referencing the "least squares slope" of the data, this is referring to a linear line of best
            fit for the data. This being with the kick number on the x-axis and the distance on the y-axis.
            A positive value indicates the kick moved backwards, away from the uprights on average. A negative
            value indicates the moved closer to the uprights as their workout progressed.
         */

        // Array for each slope of each session
        double[] leastSquaresSlopes = new double[allFGSessions.getNumSessions()];

        // Holds total sum of all least-squares slopes to be averaged later.
        double sumOfLeastSquaresSlopes = 0;

        // Holds total sum of all r-squared values to be averaged later
        double sumOfR2Values = 0;

        /*
            Holds number of slopes that were not "NaN". A slope is "NaN" when there is only one data point.
            Will be used later for averaging slope value because an average can't be calculated with "NaN" values.
         */
        int nonNaNSlopeCounter = 0;

        // Traverses through all field goal sessions (inner loop traverses through each kick in current (i) session
        for (int i = 0; i < allFGSessions.getNumSessions(); i++) {

            // Array created to be used by simple regression class
            double[][] dataPoints = new double[allFGSessions.getFieldGoalSession(i).getNumKicks()][2];

            // Traverse through each kick and assign x [0] and y [1] value in dataPoints array
            for (int j = 0; j < allFGSessions.getFieldGoalSession(i).getNumKicks(); j++) {
                dataPoints[j][0] = j;
                dataPoints[j][1] = allFGSessions.getFieldGoalSession(i).getKickAtIndex(j);
            }

            // Simple regression analysis with data from current session. Stores slope in array leastSquaresSlopes
            SimpleRegression simpleRegression = new SimpleRegression();
            simpleRegression.addData(dataPoints);
            leastSquaresSlopes[i] = simpleRegression.getSlope();

            /*
                Adds current least squares slope to sum value. Compute R-Squared value for later analysis.
                Only sessions of greater than 5 kicks were chosen to rule out large outliers from very small sessions.
                The value 5 was arbitrarily chosen and could be replaced to show the average of larger sessions.
             */
            if((!Double.isNaN(leastSquaresSlopes[i])) && allFGSessions.getFieldGoalSession(i).getNumKicks() >= 5) {

                sumOfLeastSquaresSlopes += leastSquaresSlopes[i];

                if(!Double.isNaN(simpleRegression.getRSquare())) {

                    sumOfR2Values += Math.abs(simpleRegression.getRSquare());

                }
                nonNaNSlopeCounter++;
            }


        }

        // Finds index for location with most data points
        int maxIndex = maxArrayValue(placement);

        // Holds string values of location values for comparison
        String[] placementValues = {"0","0.25","0.5","0.75","1"};

        // Assigns most popular location value to placement value with most values
        String mostPopularLocation = "";
        switch (placementValues[maxIndex]) {
            case "0" :
                mostPopularLocation = "left hash";
                break;
            case "0.25" :
                mostPopularLocation = "left upright";
                break;
            case "0.5" :
                mostPopularLocation = "center";
                break;
            case "0.75" :
                mostPopularLocation = "right upright";
                break;
            case "1" :
                mostPopularLocation = "right hash";
                break;
            default:
                break;
        }

        // Final computations for field goal statistics
        double FG_Average_Distance = (double) FG_TotalDistanceSum / (FG_List.size() - 1);
        double Miles_Of_FGs = (double) FG_TotalDistanceSum *  0.00056818;
        double percentKicksOfTotalMostPlaces = ((double) placement[maxIndex] / fieldGoalData.length) * 100;
        double percentKicksFromFives = (double) numKicksFromFives / fieldGoalData.length * 100;
        double averageSlope = sumOfLeastSquaresSlopes / nonNaNSlopeCounter;
        double averageRSquared = sumOfR2Values / nonNaNSlopeCounter;

        // Prints final values for calculated field goal statistics
        System.out.println("Average Field Goal Distance: " + ((double) Math.round(FG_Average_Distance * 100) / 100) + " Yards");
        System.out.println("Miles of Field Goals Kicked: " + ((double) Math.round(Miles_Of_FGs * 100) / 100));
        System.out.println("Most kicks were from the " + mostPopularLocation + ". " +
                ((double) Math.round(percentKicksOfTotalMostPlaces * 100) / 100) + "% were from that location.");
        System.out.println(numKicksFromFives + " kicks were from increments of 5 yards. This is " +
                ((double) Math.round(percentKicksFromFives * 100) / 100) + "% of all kicks.");
        System.out.println("On average, each kick moves " + (((double) Math.round(averageSlope * 100)) / 100) + " yards per kick.");
        System.out.println("The average r-squared value for this calculation was: " + (((double) Math.round(averageRSquared * 100)) / 100));


        // Maps the kickoff CSV to a list
        CSVReader Kickoff_Reader = new CSVReader(new FileReader("Kickoff.csv"));
        List<String[]> KO_List = Kickoff_Reader.readAll();
        Iterator<String[]> KO_it = KO_List.iterator();

        // Convert list to array of objects
        String[][] kickoffData = new String[KO_List.size()][NUM_KICKOFF_CATEGORIES];
        counter = 0;
        while (KO_it.hasNext()) {
            kickoffData[counter] = KO_it.next();
            counter++;
        }

        int Kickoff_TotalDistanceSum = 0;


        // Counter for kickoff values that have a hang time. Ones without hang time can not be included in average
        double hasKOHangTimeCount = 0;

        // Holds sum of all kickoff hang times to be average later
        double KickOff_TotalTimeSum = 0;

        // Parse through all data entries
        // Ignore 0 values (denotes column title)
        for (int i = 1; i < KO_List.size(); i++) {

            // Adds kickoff hang time to appropriate array and increments counter
            if (kickoffData[i][3].compareTo("") != 0) {
                KickOff_TotalTimeSum += (Double.parseDouble(kickoffData[i][3]));
                hasKOHangTimeCount++;
            }

            /*
                Adds kickoff distance to appropriate array. The value in the 4th column must be subtracted because the
                start distances are not all the same so the distance between landing point and start distance must be
                calculated. 10 is subtracted to account for the end zone being included in the distance.
             */
            if (kickoffData[i][4] != null && kickoffData[i][16] != null) {
                Kickoff_TotalDistanceSum += (Double.parseDouble(kickoffData[i][16]) - Double.parseDouble(kickoffData[i][4]) - 10);
            }

        }

        // Final computation of kickoff values
        double KO_HangTime_Average = KickOff_TotalTimeSum / hasKOHangTimeCount;
        double KO_Distance_Average = (double) Kickoff_TotalDistanceSum / (KO_List.size() - 1);

        // Prints final values for calculated kickoff data
        System.out.println("Average Kickoff Hang-time: " + ((double) Math.round(KO_HangTime_Average * 100) / 100) + " Seconds");
        System.out.println("Average Kickoff Distance: " + ((double) Math.round(KO_Distance_Average * 100) / 100) + " Yards");

        // Maps punt CSV to array
        CSVReader Punt_Reader = new CSVReader(new FileReader("Punt.csv"));
        List<String[]> Punt_List = Punt_Reader.readAll();
        Iterator<String[]> Punt_it = Punt_List.iterator();

        // Convert list to array of objects
        String[][] puntData = new String[Punt_List.size()][NUM_PUNT_CATEGORIES];
        counter = 0;
        while (Punt_it.hasNext()) {
            puntData[counter] = Punt_it.next();
            counter++;
        }

        // Holds sum of start distances for punts to be averaged later
        double Punt_TotalStartDistanceSum = 0;

        // Holds sum of kick distances for punts to be averaged later
        double Punt_DistanceSum = 0;

        // Holds sum of hang time for punts to be averaged later
        double Punt_TotalTimeSum = 0;

        // Holds counter for punts that have a hang time
        double hasPuntHangTimeCount = 0;

        for (int i = 1; i < Punt_List.size(); i++) {
            Punt_TotalStartDistanceSum += (Double.parseDouble(puntData[i][19]));
            Punt_DistanceSum += Double.parseDouble(puntData[i][19]) - Double.parseDouble(puntData[i][5]);

            if (puntData[i][4].compareTo("") != 0) {
                Punt_TotalTimeSum += (Double.parseDouble(puntData[i][4]));
                hasPuntHangTimeCount++;
            }

        }

        // Final computations for punt values
        double Punt_StartDistance_Average = Punt_TotalStartDistanceSum / Punt_List.size() - 10;
        double Punt_Distance_Average = Punt_DistanceSum / Punt_List.size();
        double Punt_HangTime_Average = Punt_TotalTimeSum / hasPuntHangTimeCount;
        if(Punt_StartDistance_Average > 50) {
            Punt_StartDistance_Average = 100 - Punt_StartDistance_Average;
        }

        // Prints final values for calculated punt data
        System.out.println("Average Punt Start Location: " + ((double) Math.round(Punt_StartDistance_Average * 100) / 100) + " Yard Line");
        System.out.println("Average Gross Punt Distance: " + ((double) Math.round(Punt_Distance_Average * 100) / 100) + " Yards");
        System.out.println("Average Net Punt Distance: " + (((double) Math.round(Punt_Distance_Average * 100) / 100) - 14) + " Yards");
        System.out.println("Average Punt Hang-Time: " + ((double) Math.round(Punt_HangTime_Average * 100) / 100) + " Seconds");


    }

    // Returns first occurrence of largest value of integer array
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


}
