package src;

import java.io.*;
import java.util.*;

/**
 * Class that takes as input a file and interprets the file as a graph.
 *
 * @author Matias Daneels
 */
public class FileReader {

    /**
     * List containing the adjacency matrices of the graphs in the input file.
     */
    private List<int[][]> adjacencyMatrix = new ArrayList<>();

    public List<int[][]> getAdjacencyMatrix(){
        return adjacencyMatrix;
    }

    /**
     * Name/path of the input file.
     */
    private final String fileName;

    public String getFileName() {
        return fileName;
    }

    /**
     * If the input is in graph6 notation, stringsOfGraphs contains all the graph6 notations of the graphs.
     */
    private List<String> stringsOfGraphs = new ArrayList<>();

    public List<String> getStringsOfGraphs() {
        return stringsOfGraphs;
    }

    /**
     * Constructor for a new FileReader
     * @param fileName The name/path of the input file.
     */
    public FileReader(String fileName) {
        this.fileName = fileName;
    }

    public void readGraph6() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        String nextLine;

        while(scanner.hasNext()){
            nextLine = scanner.nextLine();
            stringsOfGraphs.add(nextLine);
            int size = sizeFromGraph6(nextLine);
            if (size == -1) {
                throw new RuntimeException("Invalid Graph6 notation");
            }

            List<Byte> bitList = parseGraph6ToBits(nextLine, size);
            adjacencyMatrix.add(buildAdjacencyMatrix(bitList, size));
        }
        scanner.close();
    }

    /**
     * Returns the size of the graph when the graph is given in graph6 form.
     * @param graph6 String of the graph in graph6 form.
     * @return The size of the graph represented by the input.
     */
    private static int sizeFromGraph6(String graph6) {
        byte[] bytes = graph6.getBytes();
        int n = bytes[0] - 63;
        if (n == 63){
            n = (short)((bytes[1] <<= 16 | (bytes[2] <<= 8) ) | (bytes[3]));
        }
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
        int i = 1;
        if (size > 62){
            i = 4;
        }
        for (; i < bytes.length; i++) {
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

    public void readAdjMatrix() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        String nextLine;
        int dimension;

        for (int g = 0; scanner.hasNext(); g++){
            nextLine = scanner.nextLine();
            String[] arrayOfLine = nextLine.split(" ");
            dimension = arrayOfLine.length;

            adjacencyMatrix.add(new int[dimension][dimension]);

            for (int i = 0; i < dimension; i++){

                for (int j = 0; j < dimension; j++){
                    adjacencyMatrix.get(g)[i][j] = Integer.parseInt(arrayOfLine[j]);
                }

                nextLine = scanner.nextLine();
                arrayOfLine = nextLine.split(" ");
            }
        }
        scanner.close();
    }

    public void readWeirdFormat() throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(fileName));
        String nextLine;
        int dimension;

        while(scanner.hasNext()){
            nextLine = scanner.nextLine();
            String[] arrayOfLine = nextLine.split(" ");

            if (Objects.equals(arrayOfLine[0], "c") || Objects.equals(arrayOfLine[0], "d") || Objects.equals(arrayOfLine[0], "v") ||
                    Objects.equals(arrayOfLine[0], "x") || Objects.equals(arrayOfLine[0], "n"));

            else if (Objects.equals(arrayOfLine[0], "p")){
                if (!(Objects.equals(arrayOfLine[1], "edge")) && !(Objects.equals(arrayOfLine[1], "col"))){
                    throw new RuntimeException("Input file is not valid");
                }
                dimension = Integer.parseInt(arrayOfLine[2]);
                adjacencyMatrix.add(new int[dimension][dimension]);
            }

            else if (Objects.equals(arrayOfLine[0], "e")){
                adjacencyMatrix.get(0)[Integer.parseInt(arrayOfLine[1])-1][Integer.parseInt(arrayOfLine[2])-1] = 1;
                adjacencyMatrix.get(0)[Integer.parseInt(arrayOfLine[2])-1][Integer.parseInt(arrayOfLine[1])-1] = 1;
            }
        }
        scanner.close();
    }
}