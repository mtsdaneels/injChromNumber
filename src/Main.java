package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Main class to calculate the injective chromatic number of a given graph.
 */
public class Main {


    public static void main(String[] args) {

        //Used to time the process
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        threadBean.setThreadContentionMonitoringEnabled(true);
        long threadUserTimeStart = threadBean.getCurrentThreadUserTime();

        //Used to check if there are filters on
        Boolean filterInj = false;
        Boolean filterDegr = false;
        Boolean filterColoring = false;

        int injNumFilter = -1;
        int maxDegrFilter = -1;

        //Read input for filters if present (if there are not 4 input arguments, input is ignored)
        if (args.length > 3){
            if (Objects.equals(args[0], "-d")){
                maxDegrFilter = Integer.parseInt(args[1]);
                filterDegr = true;
            }
            if (Objects.equals(args[2], "-i")){
                injNumFilter = Integer.parseInt(args[3]);
                filterInj = true;
            }
            if (args.length > 4){
                if (Objects.equals(args[4], "-c")){
                    filterColoring = true;
                }
            }
        }

        //The backtracking that will do the calculations
        BTA testBTA;

        //The table of the results
        HashMap<Tuple<Integer, Integer>, Integer> results = new HashMap<>();

        int maximumDegreeFound = -1;
        int maximumInjFound = -1;

        //HashMap maxDegree bevat hashmaps per maximale graad
        HashMap<Integer, HashMap<Integer, Integer>> maxDegree = new HashMap<Integer, HashMap<Integer, Integer>>();

        //Keep track of how many graphs are read
        int amountOfGraphs = 0;

        //INJECTIEF CHROMATIC NUMBER
        //Reads from pipe
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;
            while (true) {

                //While there is input
                if ((line = reader.readLine()) != null) {
                    testBTA = new BTA(line);
                    testBTA.calcInjChromaticNumber();
                    int injNum = testBTA.getInjChromaticNumber();
                    int maxDeg = testBTA.getMaxDegree();

                    //Put the results in the results list and update max degree and inj
                    Boolean added = false;
                    for (Tuple<Integer, Integer> result : results.keySet()){
                        if (result.x == maxDeg && result.y == injNum){
                            results.put(result, results.get(result) + 1);
                            added = true;
                            break;
                        }
                    }
                    if (!added){
                        results.put(new Tuple<Integer, Integer>(maxDeg, injNum), 1);
                    }

                    if (maxDeg > maximumDegreeFound){
                        maximumDegreeFound = maxDeg;
                    }
                    if (injNum > maximumInjFound){
                        maximumInjFound = injNum;
                    }

                    //Check if filters are up and we need to print the graph
                    filter(filterColoring, filterDegr, filterInj, maxDegrFilter, maxDeg, injNumFilter, injNum, testBTA);

                    amountOfGraphs++; //Keeps track of how many graphs we look through
                }

                //When input is completely read
                else {
                    long threadUserTimeStop = threadBean.getCurrentThreadUserTime();
                    long duration = (threadUserTimeStop - threadUserTimeStart); //Total calculation time

                    //Given the results, make a table ready to be printed
                    ArrayList<ArrayList<Integer>> table = makeTable(results, maximumDegreeFound, maximumInjFound);

                    //Report the found results
                    printResults(duration, amountOfGraphs, table);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    /**
     * Given the results of the graph, return a table ready for output.
     */
    private static ArrayList<ArrayList<Integer>> makeTable(HashMap<Tuple<Integer, Integer>, Integer> results, int maximumDegreeFound, int maximumInjFound){
        ArrayList<ArrayList<Integer>> table = new ArrayList<>();

        //Make an empty table that is big enough to hold all the results
        for (int i = 1; i < maximumInjFound + 1; i++){
            table.add(new ArrayList<>());
            for (int j = 0; j < maximumDegreeFound + 1; j++){
                table.get(i - 1).add(0);
            }
        }

        for (Tuple<Integer, Integer> result : results.keySet()){
            int deg = result.x;
            int inj = result.y;

            table.get(inj - 1).set(deg, results.get(result));
        }
        return table;
    }

    /**
     * Print the results.
     */
    private static void printResults(long duration, int amountOfGraphs, ArrayList<ArrayList<Integer>> table) {
        System.err.println("Total time : " + duration /1000000000.0 + " sec");
        System.err.println("Amount of graphs: " + amountOfGraphs);

        System.out.printf("max degree: \t");

        for (int i = 0; i <= table.get(0).size() - 1; i++){
            System.out.printf(i + "\t");
        }

        System.out.printf("\n");

        for (int i = 1; i < table.size() + 1; i++){
            //Index of list we are looking at is i - 2, this list has the values for inj = 1
            System.out.printf("inj = " + i + ":\t");
            for (int j : table.get(i - 1)){
                System.out.printf(j + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Check if the filters are up and if we need to print the graph.
     */
    private static void filter(Boolean filterColoring, Boolean filterDegr, Boolean filterInj, int maxDegrFilter, int maxDeg, int injNumFilter, int result, BTA testBTA) {
        if (filterDegr && filterInj) {
            if (maxDegrFilter == maxDeg && injNumFilter == result){
                System.out.println(testBTA.getGraph6Notation());
                if (filterColoring){
                    System.out.println(Arrays.toString(testBTA.getFinalColoring()));
                }
            }
        }
    }
}