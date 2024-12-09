package simulations;

import static graph.GenerateGraph.generateSinkSourceGraph;
import static simulations.EdmondsKarp.computeMaxFlow;

import algorithms.AlgoDriver;
import algorithms.AlgoResult;
import algorithms.Algorithm;
import algorithms.CapacityScaling;
import algorithms.SuccessiveShortestPaths;
import algorithms.SuccessiveShortestPathsSC;
import graph.GraphHelper;
import graph.GraphReader;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Simulations1 {

    private static double[][] inputValues = {
            {100, 0.2, 8, 5},
            {200, 0.2, 8, 5},
            {100, 0.3, 8, 5},
            {200, 0.3, 8, 5},
            {100, 0.2, 64, 20},
            {200, 0.2, 64, 20},
            {100, 0.3, 64, 20},
            {200, 0.3, 64, 20}
    };

    private static int[][] sourceAndSink;
    private static int[] fMaxValues;

    public static void run() {

        generateGraphs();
        printCharacteristicsOfGraphs();
        runAlgorithms();
    }

    private static void generateGraphs() {

        for (int i = 0; i < 8; i ++) {
            generateSinkSourceGraph((int) inputValues[i][0], inputValues[i][1], (int) inputValues[i][2], (int) inputValues[i][3]);
        }

        System.out.println("\n** Graph files for graphs 1 to 8 created successfully **");
        System.out.println("\n===================================================================================================================================================================");

    }

    private static void printCharacteristicsOfGraphs() {
        sourceAndSink = new int[8][2];
        fMaxValues = new int[8];
        System.out.format("%5s%16s%16s%16s%16s%16s%16s%16s%16s%16s", "Graph", "n", "r", "upperCap", "upperCost", "fMax", "nodesInLCC", "maxOutDegree", "maxInDegree", "avgDegree");
        System.out.println();
        for (int i = 0; i < 8; i ++) {
            int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(String.valueOf(i + 1));
            int[][] cap = GraphReader.getCapacityMatrix(String.valueOf(i + 1));
            int[][] unitCost = GraphReader.getUnitCostMatrix(String.valueOf(i + 1));
            int n = adjacencyMatrix.length;
            sourceAndSink[i] = determineSourceSinkNetworks(adjacencyMatrix);
            int source = sourceAndSink[i][0];
            int sink = sourceAndSink[i][1];
            GraphHelper.removeEdgesNotInSourceSinkNetwork(adjacencyMatrix, cap, unitCost, source, sink);

//            int upperCap = findUpperCap(cap);
//            int upperCost = findUpperCost(unitCost);
            fMaxValues[i] = computeMaxFlow(adjacencyMatrix, cap, unitCost, source, sink);
            int[] visited = new int[n];
            int nodesInLargestConnectedComponent = findSizeOfConnectedComponent(adjacencyMatrix, visited, source);
            Arrays.fill(visited, 0);
            int maxOutDegreeInLCC = findMaxOutDegree(adjacencyMatrix, visited, source);
            Arrays.fill(visited, 0);
            int maxInDegreeInLCC = findMaxInDegree(adjacencyMatrix, visited, source);
            double averageDegreeInLCC = findEdgesInLCC(adjacencyMatrix) / (nodesInLargestConnectedComponent * 1.00);

            System.out.format("%5s%16s%16s%16s%16s%16s%16s%16s%16s%16s", i, (int) inputValues[i][0], inputValues[i][1], (int) inputValues[i][2], (int) inputValues[i][3], fMaxValues[i], nodesInLargestConnectedComponent, maxOutDegreeInLCC, maxInDegreeInLCC, String.format("%.3f", averageDegreeInLCC));
            System.out.println();
//            System.out.println("\n"
//                    + i + "     "
//                    + (int) inputValues[i][0] + "     "
//                    + inputValues[i][1] + "     "
//                    + (int) inputValues[i][2] + "        "
//                    + (int) inputValues[i][3] + "        "
//                    + fMaxValues[i] + "     "
//                    + nodesInLargestConnectedComponent + "     "
//                    + maxOutDegreeInLCC + "        "
//                    + maxInDegreeInLCC + "        "
//                    + String.format("%.3f", averageDegreeInLCC));
        }

        System.out.println("\n===================================================================================================================================================================\n\n");
    }

    private static void runAlgorithms() {
        Algorithm ssp = new SuccessiveShortestPaths();
        Algorithm cs = new CapacityScaling();
        Algorithm sspcs = new SuccessiveShortestPathsSC();
        AlgoDriver pd = new AlgoDriver();
        System.out.format("%10s%16s%16s%16s%16s%16s%16s", "Algorithm", "Graph", "f", "MC", "Paths", "ML", "MPL");
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------");
        for (int i = 1; i <= 8; i ++) {
            double k = 0.90;
            AlgoResult sspResult = ssp.findMinCostFlowAndTotalCost(String.valueOf(i), sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            AlgoResult csResult = cs.findMinCostFlowAndTotalCost(String.valueOf(i), sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            AlgoResult sspcsResult = sspcs.findMinCostFlowAndTotalCost(String.valueOf(i), sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            AlgoResult pdResult = pd.primalDualDriver("graph" + i + ".txt", sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "SSP", i, sspResult.totalFlow, sspResult.minimumCost, sspResult.numberOfPaths, String.format("%.3f", sspResult.meanLength), String.format("%.3f", sspResult.meanProportionalLength));
            System.out.println();
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "CS", i, csResult.totalFlow, csResult.minimumCost, csResult.numberOfPaths, String.format("%.3f", csResult.meanLength), String.format("%.3f", csResult.meanProportionalLength));
            System.out.println();
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "SSPCS", i, sspcsResult.totalFlow, sspcsResult.minimumCost, sspcsResult.numberOfPaths, String.format("%.3f", sspcsResult.meanLength), String.format("%.3f", sspcsResult.meanProportionalLength));
            System.out.println();
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "PD", i, pdResult.totalFlow, pdResult.minimumCost, pdResult.numberOfPaths, String.format("%.3f", pdResult.meanLength), String.format("%.3f", pdResult.meanProportionalLength));
            System.out.println();
            System.out.println("------------------------------------------------------------------------------------------------------------");        }
    }

    private static int[] determineSourceSinkNetworks(int[][] adjacencyMatrix) {
        int n = adjacencyMatrix.length;
        int source = -1;
        int maxSize = 0;
        for (int i = 0; i < n; i ++) {
            int[] visited = new int[n];
            int sizeOfConnectedComponentReachable = findSizeOfConnectedComponent(adjacencyMatrix, visited, i);
            if (sizeOfConnectedComponentReachable > maxSize) {
                maxSize = sizeOfConnectedComponentReachable;
                source = i;
            }
        }
        int target = findFurthestNodeFrom(adjacencyMatrix, source);

        return new int[] {source, target};
    }

    private static int findSizeOfConnectedComponent(int[][] adjacencyMatrix, int[] visited, int i) {
        int n = adjacencyMatrix.length;
        visited[i] = 1;
        int c = 1;
        for (int j = 0; j < n; j ++) {
            if (i != j && visited[j] == 0 && adjacencyMatrix[i][j] == 1) {
                c += findSizeOfConnectedComponent(adjacencyMatrix, visited, j);
            }
        }

        return c;
    }

    private static int findFurthestNodeFrom(int[][] adjacencyMatrix, int source) {
        int n = adjacencyMatrix.length;
        Queue<Integer> q = new LinkedList<>();
        int[] visited = new int[n];
        q.add(source);
        visited[source] = 1;
        q.add(-1);
        int lastRemoved = -1;
        while (!q.isEmpty()) {
            int i = q.remove();
            if (i == -1) {
                if (!q.isEmpty()) {
                    q.add(-1);
                }
                continue;
            }
            lastRemoved = i;
            for (int j = 0; j < n; j ++) {
                if (i != j && visited[j] == 0 && adjacencyMatrix[i][j] == 1) {
                    q.add(j);
                    visited[j] = 1;
                }
            }
        }

        return lastRemoved;
    }

    private static int findUpperCap(int[][] cap) {
        int n = cap.length;
        int maxCapacity = 0;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                maxCapacity = Math.max(maxCapacity, cap[i][j]);
            }
        }

        return maxCapacity;
    }

    private static int findUpperCost(int[][] unitCost) {
        int n = unitCost.length;
        int maxUnitCost = 0;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                maxUnitCost = Math.max(maxUnitCost, unitCost[i][j]);
            }
        }

        return maxUnitCost;
    }

    private static int findMaxOutDegree(int[][] adjacencyMatrix, int[] visited, int i) {
        int n = adjacencyMatrix.length;
        visited[i] = 1;
        int c = 0;
        for (int j = 0; j < n; j ++) {
            if (i != j && adjacencyMatrix[i][j] == 1) {
                c ++;
            }
        }

        for (int j = 0; j < n; j ++) {
            if (i != j && adjacencyMatrix[i][j] == 1 && visited[j] == 0) {
                c = Math.max(c, findMaxOutDegree(adjacencyMatrix, visited, j));
            }
        }

        return c;
    }

    private static int findMaxInDegree(int[][] adjacencyMatrix, int[] visited, int i) {
        int n = adjacencyMatrix.length;
        visited[i] = 1;
        int c = 0;
        for (int j = 0; j < n; j ++) {
            if (i != j && adjacencyMatrix[j][i] == 1) {
                c ++;
            }
        }

        for (int j = 0; j < n; j ++) {
            if (i != j && adjacencyMatrix[i][j] == 1 && visited[j] == 0) {
                c = Math.max(c, findMaxInDegree(adjacencyMatrix, visited, j));
            }
        }

        return c;
    }

    private static int findEdgesInLCC(int[][] adjacencyMatrix) {
        int c = 0;
        int n = adjacencyMatrix.length;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if(adjacencyMatrix[i][j] == 1) {
                    c ++;
                }
            }
        }
        return c;
    }
}
