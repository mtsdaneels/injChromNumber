package src;

import java.util.*;

/**
 * Class that implements the backtracking algorithm.
 *
 * @author Matias Daneels
 */
public class BTA {

    /**
     * The adjacency matrix of the graph we are working with.
     */
    private final int[][] adjMatrix;

    public int[][] getAdjMatrix(){
        return adjMatrix;
    }

    /**
     * The coloring used when running the algorithm, this is not the final coloring!
     */
    private int[] color;

    /**
     * The coloring that is used for the smallest injective chromatic number.
     */
    private int[] finalColoring;

    public int[] getFinalColoring(){
        if (injChromaticNum == (int) Double.POSITIVE_INFINITY){
            throw new RuntimeException("Injective chromatic number not calculated yet!");
        }
        return finalColoring;
    }

    /**
     * The number of vertices in the graph.
     */
    private final int dimension;

    public int getDimension(){
        return dimension;
    }

    /**
     * The injective chromatic number of the graph, initialized at infinity.
     */
    private int injChromaticNum = (int) Double.POSITIVE_INFINITY;

    public int getInjChromaticNumber(){
        if (injChromaticNum == (int) Double.POSITIVE_INFINITY){
            throw new RuntimeException("Injective chromatic number not calculated yet!");
        }
        return injChromaticNum;
    }

    /**
     * A list of bitsets representing the restrictions on the vertices when coloring the graph.
     */
    private ArrayList<BitSet> colorRestrictions = new ArrayList<>();

    /**
     * A list of bitsets representing the neighbours of the vertex with number the index in the list.
     */
    private final ArrayList<BitSet> neighbours = new ArrayList<>();

    /**
     * A list of bitsets representing the injective neighbours of the vertex with number the index in the list.
     */
    private final ArrayList<BitSet> injNeighbours = new ArrayList<>();

    /**
     * A list of Integers representing the degree of the vertex with number the index in the list.
     */
    private final ArrayList<Integer> degrees = new ArrayList<>();

    /**
     * The maximum degree of the graph, initialized at -1.
     */
    private int maxDegree = -1;

    public int getMaxDegree(){
        if (maxDegree == -1) {
            throw new RuntimeException("Maximum degree invalid!");
        }
        return maxDegree;
    }

    /**
     * The graph6 notation of the graph.
     */
    private String graph6Notation = null;

    public String getGraph6Notation() {
        if (graph6Notation == null){
            throw new RuntimeException("The graph6 notation is invalid!");
        }
        return graph6Notation;
    }

    /**
     * A list of Integers representing the vertices that do not have a single neighbour.
     */
    private final ArrayList<Integer> noNeighbours = new ArrayList<>();

    /**
     * Constructor for a new backtracking algorithm starting from the graph6 notation of the graph.
     * @param graph6 A string containing the graph6 notation of the graph
     */
    public BTA(String graph6){
        //Build the adjacency matrix from the graph6 form.
        graph6Notation = graph6;
        dimension = sizeFromGraph6(graph6);
        if (dimension == -1) {
            throw new RuntimeException("Invalid Graph6 notation");
        }

        List<Byte> bitList = parseGraph6ToBits(graph6, dimension);
        adjMatrix = (buildAdjacencyMatrix(bitList, dimension));

        color = new int[dimension];

        //Add a bitset for every node
        for (int i = 0; i < dimension; i++){
            colorRestrictions.add(new BitSet(dimension+1));
        }

        //Calc the neighbours and injective neighbours
        calcNeighbours();
        calcInjNeighbours();
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

    /**
     * Constructor for a new backtracking algorithm starting from the adjacency matrix of the graph.
     * @param adjMatrix The adjacency matrix of the graph we want the backtracking to work on.
     */
    public BTA(int[][] adjMatrix){
        this.adjMatrix = adjMatrix;
        dimension = adjMatrix.length;
        color = new int[dimension];

        //Add a bitset for every node
        for (int i = 0; i < dimension; i++){
            colorRestrictions.add(new BitSet(dimension+1));
        }

        calcNeighbours();
        calcInjNeighbours();
    }

    /**
     * Calculate the neighbours and the degree each vertex and store them in neighbours and degrees.
     */
    private void calcNeighbours(){
        int degree;
        for (int i = 0; i < dimension; i++){
            neighbours.add(new BitSet());
            degree = 0;
            for (int j = 0; j < dimension; j++){
                if (i != j && adjMatrix[i][j] == 1){
                    neighbours.get(i).set(j, true);
                    degree++;
                }
            }
            degrees.add(degree);
            if (degree == 0){
                noNeighbours.add(i);
            }
        }
    }

    /**
     * Calculate the injective neighbours of each vertex and store them in injNeighbours.
     */
    private void calcInjNeighbours(){
        for (int i = 0; i < dimension; i++){
            injNeighbours.add(new BitSet());
            for (int j = 0; j < dimension; j++){
                if (i != j && isInjNeighbour(i, j)){
                    injNeighbours.get(i).set(j, true);
                }
            }
        }
    }

    public void calcInjChromaticNumber(){
        int maxDegreeVertex = calcMaxDegreeVertex();
        int n = 1;
        int ncolor = 1;

        //Give all vertices without any neighbours color 1.
        for (Integer vertex : noNeighbours){
            color[vertex] = 1;
            n++;
        }

        //Give all the neighbours of a vertex with maximum degree a different color.
        if (!(degrees.get(maxDegreeVertex) == 0)){
            ncolor = 0;
            for (int i = neighbours.get(maxDegreeVertex).nextSetBit(0); i >= 0; i = neighbours.get(maxDegreeVertex).nextSetBit(i+1)) {
                ncolor++;
                color[i] = ncolor;
                updateRestrictionsInj(i, ncolor);
                n++;
            }
        }

        //Color the rest of the graph using backtracking
        colorGraphInj(calcNextVertex(), ncolor, n-1);
    }

    /**
     * Calculate the vertex with the maximum degree of all the vertices in the graph.
     * @return The vertex with the maximum degree out of all vertices.
     */
    private int calcMaxDegreeVertex(){
        int result = -1;
        int tempMaxDegree;
        for (int i = 0; i < dimension; i++){
            tempMaxDegree = degrees.get(i);
            if (maxDegree < tempMaxDegree){
                result = i;
                maxDegree = tempMaxDegree;
            }
        }
        return result;
    }

    /**
     * Calculate which vertex should be chosen next for the backtracking algorithm.
     * The vertex with the biggest saturation degree (least amount of colors possible) is returned.
     * @return The vertex that should be chosen next for the backtracking algorithm.
     */
    private int calcNextVertex(){
        int result = -1;
        int tempsat;
        int saturation = 0;
        for (int i = 0; i < dimension; i++){
            if (color[i] == 0){
                tempsat = colorRestrictions.get(i).cardinality();
                if (tempsat > saturation || result == -1){
                    saturation = tempsat;
                    result = i;
                }
            }
        }
        return result;
    }

    /**
     * Recursive backtracking algorithm for calculating the injective chromatic number of the graph.
     * @param vertex The vertex we are currently colouring.
     * @param numOfCol The number of colors currently already used.
     * @param n The amount of iterations we have done. Only used for termination criteria.
     */
    private void colorGraphInj(int vertex, int numOfCol, int n){
        if (n == dimension && numOfCol <= injChromaticNum){
            finalColoring = color.clone();
            injChromaticNum = numOfCol;
        }

        else{
            for (int c = 1; c <= numOfCol+1; c++){
                if (!colorRestrictions.get(vertex).get(c) && numOfCol < injChromaticNum){
                    color[vertex] = c;
                    ArrayList<Integer> bits = new ArrayList<>();

                    //Brought outside function
                    for (int i = injNeighbours.get(vertex).nextSetBit(0); i >= 0; i = injNeighbours.get(vertex).nextSetBit(i+1)) {
                        if (color[i] == 0 && !colorRestrictions.get(i).get(c)){
                            colorRestrictions.get(i).set(c, true);
                            bits.add(i);
                        }
                    }

                    int nextVertex = calcNextVertex();
                    if (c == numOfCol+1){
                        if (numOfCol+1 < injChromaticNum) {
                            colorGraphInj(nextVertex, numOfCol + 1, n+1); //Basic Pruning
                        }
                    }
                    else {
                        colorGraphInj(nextVertex, numOfCol, n+1);
                    }
                    resetBitsets(bits, c);
                    color[vertex] = 0; //Backtracking
                }
            }
        }
    }

    /**
     * Check if two vertices are neighbours of each other.
     * @param i The first vertex.
     * @param j The second vertex.
     * @return True if and only if the two vertices are neighbours.
     */
    private boolean isNeighbour(int i, int j){
        return adjMatrix[i][j] == 1;
    }

    /**
     * Check if two vertices are injective neighbours of each other.
     * @param i The first vertex.
     * @param j The second vertex.
     * @return True if and only if the two vertices are injective neighbours.
     */
    private boolean isInjNeighbour(int i, int j){
        for (int v = 0; v < dimension; v++){
            if (v != i && v != j && isNeighbour(v, i) && isNeighbour(v, j)){
                return true;
            }
        }
        return false;
    }

    /**
     * Update colorRestrictions when coloring a vertex with the restrictions of the injective chromatic number of a graph.
     * @param vertex A vertex that we colored.
     * @param c The color we gave to the vertex.
     * @return A list of vertices we changed the restrictions from.
     */
    private ArrayList<Integer> updateRestrictionsInj(int vertex, int c){
        //Vertex with index vertex gets color c
        ArrayList<Integer> bits = new ArrayList<>();

        for (int i = injNeighbours.get(vertex).nextSetBit(0); i >= 0; i = injNeighbours.get(vertex).nextSetBit(i+1)) {
            if (color[i] == 0 && !colorRestrictions.get(i).get(c)){
                colorRestrictions.get(i).set(c, true);
                bits.add(i);
            }
        }

        return bits;
    }

    /**
     * Reset the bits for the given color and the given vertices to false. (backtracking)
     * @param bits A list of ints representing the vertices we want to reset.
     * @param c The color we want the bits to reset for.
     */
    private void resetBitsets(ArrayList<Integer> bits, int c){
        for (Integer bit : bits){
            colorRestrictions.get(bit).set(c, false);
        }
    }

}