package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to convert a graph. The injective chromatic number of the old graph is equal to the chromatic number of the new graph.
 * This can be used to computationally check the correctness of the algorithm.
 *
 * @author Matias Daneels
 */
public class ConvertGraph {

    /**
     * The graph6 notation of the input graph.
     */
    private static String graph6Notation;

    /**
     * The graph6 notation of the converted graph.
     */
    private static String newGraph6Notation;

    /**
     * The numbe of nodes of the graphs.
     */
    private static int dimension;

    /**
     * The adjacency matrix of the input graph.
     */
    private static int[][] adjMatrix;

    /**
     * The adjacency matrix of the converted graph.
     */
    private static int[][] newMatrix;

    /**
     * Can be used in commandline via pipes. The input are graphs in graph6 notation. The output are the converted graphs in graph6 notation.
     * Input is a list of graphs in graph6 notation. Output is a list of the converted graphs in graph6 notation made such that
     * the injective chromatic number of the original graph is the chromatic number of the converted graph.
     */
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line = null;

            //Start time
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            threadBean.setThreadContentionMonitoringEnabled(true);
            long threadUserTimeStart = threadBean.getCurrentThreadUserTime();

            //Keep track of how many graphs are read
            int amountOfGraphs = 0;

            while (true) {
                //While there is input
                if ((line = reader.readLine()) != null) {
                    amountOfGraphs++;
                    graph6Notation = line;
                    dimension = sizeFromGraph6(graph6Notation);

                    if (dimension == -1) {
                        throw new RuntimeException("Invalid Graph6 notation");
                    }

                    List<Byte> bitList = parseGraph6ToBits(graph6Notation, dimension);
                    adjMatrix = (buildAdjacencyMatrix(bitList, dimension));
                    newMatrix = convertAdjMatrix(adjMatrix);
                    newGraph6Notation = convertToGraph6(newMatrix);

                    System.out.println(newGraph6Notation);
                } else {
                    long threadUserTimeStop = threadBean.getCurrentThreadUserTime();
                    System.err.println("Total time : " + (threadUserTimeStop - threadUserTimeStart) /1000000000.0 + " sec");
                    System.err.println("Amount of graphs: " + amountOfGraphs);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Given an adjacencymatrix, return the graph6 string of the graph.
     */
    private static String convertToGraph6(int[][] adjacencyMatrix) {
        int size = adjacencyMatrix.length;

        List<Integer> bitVec = new ArrayList<>();

        // Build bit vector from upper triangle of the adjacency matrix
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < i; j++) {
                if (adjacencyMatrix[j][i] == 1) {
                    bitVec.add(1);
                } else {
                    bitVec.add(0);
                }
            }
        }

        // Pad bitVec with zeros to make its size a multiple of 6
        while (bitVec.size() % 6 != 0) {
            bitVec.add(0);
        }

        StringBuilder graph6 = new StringBuilder();

        // Convert size to binary and append it to bitVec
        String sizeBinary = Integer.toBinaryString(size + 63);
        graph6.append(Character.toString((char) Integer.parseInt(sizeBinary, 2)));

        // Convert 6-bit chunks to ASCII characters
        for (int i = 0; i < bitVec.size(); i += 6) {
            StringBuilder bits = new StringBuilder();
            for (int j = i; j < i + 6; j++) {
                bits.append(bitVec.get(j));
            }

            int num = Integer.parseInt(bits.toString(), 2);
            graph6.append(Character.toString((char) (num + 63)));

        }

        return graph6.toString();
    }

    /**
     * Returns true if and only if the given i and j vertices are injective neighbours in the given graph.
     * @param adjMatrix The adjacency matrix of the graph we are checking in.
     * @param i The first vertex.
     * @param j The second vertex.
     * @return True if and only if the 2 vertices are injective neighbours.
     */
    private static boolean injectiveNeighbour(int[][] adjMatrix, int i, int j){
        for (int v = 0; v < adjMatrix.length; v++){
            if (adjMatrix[i][v] == 1 && adjMatrix[j][v] == 1){
                return true;
            }
        }
        return false;
    }

    /**
     * Takes an adjacency matrix and makes a new adjacency matrix of the graph where the injective neighbours from
     * the first graph get an edge in the second graph. This makes so that the injective chromatic number of the first
     * graph is equal to the chromatic number of the second graph.
     * @param adjMatrix The adjacency matrix of the graph we want to convert.
     * @return The converted adjacency matrix.
     */
    private static int[][] convertAdjMatrix(int[][] adjMatrix){
        int[][] newMatrix = new int[adjMatrix.length][adjMatrix.length];
        for (int i = 0; i < adjMatrix.length; i++){
            for (int j = i + 1; j < adjMatrix.length; j++){
                if (injectiveNeighbour(adjMatrix, i, j)){
                    newMatrix[i][j] = 1;
                    newMatrix[j][i] = 1;
                }
                else {
                    newMatrix[i][j] = 0;
                    newMatrix[j][i] = 0;
                }
            }
        }
        return newMatrix;
    }

    /**
     * Returns the size of the graph when the graph is given in graph6 form.
     * @param graph6 String of the graph in graph6 form.
     * @return The size of the graph represented by the input.
     */
    private static int sizeFromGraph6(String graph6) {
        byte[] bytes = graph6.getBytes();
        int n = bytes[0] - 63;
        if (n < 0) {
            return -1; // Invalid Graph6 notation
        }
        return n;
    }

    /**
     * Creates a bit list that represents the adjacency matrix.
     * @param graph6 String of the graph in graph6 form.
     * @param size The size of the given graph.
     * @return A bit list that represents the adjacency matrix.
     */
    private static List<Byte> parseGraph6ToBits(String graph6, int size) {
        byte[] bytes = graph6.getBytes();
        List<Byte> bitList = new ArrayList<>();
        for (int i = 1; i < bytes.length; i++) {
            byte byteValue = (byte) (bytes[i] - 63);
            for (int shift = 5; shift >= 0; shift--) {
                byte mask = (byte) (1 << shift);
                bitList.add((byte) ((byteValue & mask) > 0 ? 1 : 0));
            }
        }
        int adjustedBitListSize = bitList.size() - (bitList.size() - (size * (size - 1)) / 2);
        return bitList.subList(0, adjustedBitListSize);
    }

    /**
     * Builds the adjacency matrix when the bit list from parseGraph6ToBits() and size is given.
     * @param bitList The bit list representing the adjacency matrix from parseGraph6ToBits().
     * @param size The size of the graph.
     * @return Returns the adjacency matrix of the graph.
     */
    private static int[][] buildAdjacencyMatrix(List<Byte> bitList, int size) {
        int[][] adjMatrix = new int[size][size];
        int bitIndex = 0;
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < i; j++) {
                adjMatrix[i][j] = bitList.get(bitIndex);
                adjMatrix[j][i] = bitList.get(bitIndex);
                bitIndex++;
            }
        }
        return adjMatrix;
    }
}
