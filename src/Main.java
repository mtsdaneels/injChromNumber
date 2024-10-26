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
        Boolean filterColoring = false;

        List<Tuple<Integer, Integer>> filtersList = new ArrayList<>();

        //Read input for filters if present (if there are not 4 input arguments, input is ignored)
        if (Arrays.asList(args).contains("-f")){ //Only if filters are active
            
            if (Arrays.asList(args).contains("-c")){
                filterColoring = true;
            }

            System.out.println(Arrays.toString(args));

            for (String arg: args) {
                if (arg.equals("-f")) {
                    continue;
                }
                if (arg.equals("-c")) {
                    break; //If -c is found, we are done with the filters
                }
                System.out.println(arg);
                String[] splitFilter = arg.split("-");
                filtersList.add(new Tuple<>(Integer.parseInt(splitFilter[0]), Integer.parseInt(splitFilter[1])));
            }
        }

        //The backtracking that will do the calculations
        BTA testBTA;

        //The table of the results
        HashMap<Tuple<Integer, Integer>, Integer> results = new HashMap<>();

        //Printed results
        HashMap<Tuple<Integer, Integer>, List<Tuple<String, BTA>>> printedResults = new HashMap<>();

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
                    boolean added = false;
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

                    for (Tuple<Integer, Integer> filter : filtersList){
                        if (filter.x == maxDeg && filter.y <= injNum){
                            boolean addedFilter = false;
                            for (Tuple<Integer, Integer> printedResult: printedResults.keySet()){
                                if (printedResult.x == maxDeg && printedResult.y == injNum){
                                    printedResults.get(printedResult).add(new Tuple<>(testBTA.getGraph6Notation(), testBTA));
                                    addedFilter = true;
                                    break;
                                }
                            }
                            if (!addedFilter){
                                Tuple<Integer, Integer> newKey = new Tuple<>(maxDeg, injNum);
                                printedResults.put(newKey, new ArrayList<>());
                                printedResults.get(newKey).add(new Tuple<>(testBTA.getGraph6Notation(), testBTA));
                            }
                            break;
                        }
                    }

                    amountOfGraphs++; //Keeps track of how many graphs we look through
                }

                //When input is completely read
                else {
                    long threadUserTimeStop = threadBean.getCurrentThreadUserTime();
                    long duration = (threadUserTimeStop - threadUserTimeStart); //Total calculation time

                    //Given the results, make a table ready to be printed
                    ArrayList<ArrayList<Integer>> table = makeTable(results, maximumDegreeFound, maximumInjFound);

                    //Print filtered graphs
                    printFilteredGraphs(printedResults, filterColoring);

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
    * Print the graphs that are filtered
     */
    private static void printFilteredGraphs(HashMap<Tuple<Integer, Integer>, List<Tuple<String, BTA>>> printedResults,  boolean filterColoring){
        if (!printedResults.isEmpty()){
            System.out.println("--------------------");
        }
        for (Tuple<Integer, Integer> filter : printedResults.keySet()){
            if (filterColoring) {
                System.out.println("Filter: max degree = " + filter.x + ", inj = " + filter.y);
                for (Tuple<String, BTA> result : printedResults.get(filter)){
                    System.out.println(result.x + "\t" + Arrays.toString(result.y.getFinalColoring()));
                }
                System.out.println("--------------------");
            }
            else {
                System.out.println("Filter: max degree = " + filter.x + ", inj = " + filter.y);
                for (Tuple<String, BTA> result : printedResults.get(filter)) {
                    System.out.println(result.x);
                }
                System.out.println("--------------------");
            }
        }
    }
}
