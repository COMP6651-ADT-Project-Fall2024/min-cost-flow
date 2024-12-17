package simulations;

import static graph.GenerateGraph.generateSinkSourceGraph;
import static graph.GenerateGraph.getGraphNumber;
import static graph.GenerateGraph.setGraphNumber;
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

public class Simulations2 {

    private static double[][][] inputSets = {
            {
                    {100, 0.9, 8, 5},
                    {200, 0.9, 8, 5},
                    {100, 0.9, 64, 20},
                    {200, 0.9, 64, 20}
            },
            {
                    {100, 0.1, 8, 5},
                    {200, 0.1, 8, 5},
                    {100, 0.1, 64, 20},
                    {200, 0.1, 64, 20}
            },
            {
                    {100, 0.2, 8, 1},
                    {200, 0.3, 8, 1},
                    {100, 0.2, 64, 1},
                    {200, 0.3, 64, 1}
            },
            {
                    {100, 0.9, 8, 1},
                    {200, 0.9, 8, 1},
                    {100, 0.9, 64, 1},
                    {200, 0.9, 64, 1}
            },
            {
                    {100, 0.5, 1, 5},
                    {200, 0.5, 1, 5},
                    {100, 0.5, 1, 20},
                    {200, 0.5, 1, 20}
            }
    };

    private static int[][] sourceAndSink;
    private static int[] fMaxValues;

    public static void run() {
        setGraphNumber(9);

        for (int t = 0; t < inputSets.length; t ++) {
            System.out.println("\n**** RUNNING SIMULATIONS FOR SET " + (t + 1) + " **** :");
            generateGraphs(t);
            int graphNumber = getGraphNumber() - 4;
            printCharacteristicsOfGraphs(t, graphNumber);
            runAlgorithms(graphNumber);
        }
    }

    private static void generateGraphs(int t) {

        for (int i = 0; i < 4; i ++) {
            generateSinkSourceGraph((int) inputSets[t][i][0], inputSets[t][i][1], (int) inputSets[t][i][2], (int) inputSets[t][i][3]);
        }

        System.out.println("\n-- Graph files for the input set created successfully --");
        System.out.println("\n===================================================================================================================================================================");

    }

    private static void printCharacteristicsOfGraphs(int t, int graphNumber) {
        sourceAndSink = new int[4][2];
        fMaxValues = new int[4];
        System.out.format("%5s%16s%16s%16s%16s%16s%16s%16s%16s%16s", "Graph", "n", "r", "upperCap", "upperCost", "fMax", "nodesInLCC", "maxOutDegree", "maxInDegree", "avgDegree");
        System.out.println();
        for (int i = 0; i < 4; i ++) {
            int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(String.valueOf(graphNumber + i));
            int[][] cap = GraphReader.getCapacityMatrix(String.valueOf(graphNumber + i));
            int[][] unitCost = GraphReader.getUnitCostMatrix(String.valueOf(graphNumber + i));
            int n = adjacencyMatrix.length;
            sourceAndSink[i] = determineSourceSinkNetworks(adjacencyMatrix);
            int source = sourceAndSink[i][0];
            int sink = sourceAndSink[i][1];
            GraphHelper.removeEdgesNotInSourceSinkNetwork(adjacencyMatrix, cap, unitCost, source, sink);

            fMaxValues[i] = computeMaxFlow(adjacencyMatrix, cap, unitCost, source, sink);
            int[] visited = new int[n];
            int nodesInLargestConnectedComponent = findSizeOfConnectedComponent(adjacencyMatrix, visited, source);
            Arrays.fill(visited, 0);
            int maxOutDegreeInLCC = findMaxOutDegree(adjacencyMatrix, visited, source);
            Arrays.fill(visited, 0);
            int maxInDegreeInLCC = findMaxInDegree(adjacencyMatrix, visited, source);
            double averageDegreeInLCC = findEdgesInLCC(adjacencyMatrix) / (nodesInLargestConnectedComponent * 1.00);

            System.out.format("%5s%16s%16s%16s%16s%16s%16s%16s%16s%16s", graphNumber + i, (int) inputSets[t][i][0], inputSets[t][i][1], (int) inputSets[t][i][2], (int) inputSets[t][i][3], fMaxValues[i], nodesInLargestConnectedComponent, maxOutDegreeInLCC, maxInDegreeInLCC, String.format("%.3f", averageDegreeInLCC));
            System.out.println();
        }

        System.out.println("\n===================================================================================================================================================================\n\n");
    }

    private static void runAlgorithms(int graphNumber) {
        Algorithm ssp = new SuccessiveShortestPaths();
        Algorithm cs = new CapacityScaling();
        Algorithm sspcs = new SuccessiveShortestPathsSC();
        AlgoDriver pd = new AlgoDriver();
        System.out.format("%10s%16s%16s%16s%16s%16s%16s", "Algorithm", "Graph", "f", "MC", "Paths", "ML", "MPL");
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------");
        for (int i = 1; i <= 4; i ++) {
            double k = 0.95;
            AlgoResult sspResult = ssp.findMinCostFlowAndTotalCost(String.valueOf(graphNumber + i - 1), sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            AlgoResult csResult = cs.findMinCostFlowAndTotalCost(String.valueOf(graphNumber + i - 1), sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            AlgoResult sspcsResult = sspcs.findMinCostFlowAndTotalCost(String.valueOf(graphNumber + i - 1), sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            AlgoResult pdResult = pd.primalDualDriver("graph" + (graphNumber + i - 1) + ".txt", sourceAndSink[i - 1][0], sourceAndSink[i - 1][1], (int) (k * fMaxValues[i - 1]));
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "SSP", (graphNumber + i - 1), sspResult.totalFlow, sspResult.minimumCost, sspResult.numberOfPaths, String.format("%.3f", sspResult.meanLength), String.format("%.3f", sspResult.meanProportionalLength));
            System.out.println();
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "CS", (graphNumber + i - 1), csResult.totalFlow, csResult.minimumCost, csResult.numberOfPaths, String.format("%.3f", csResult.meanLength), String.format("%.3f", csResult.meanProportionalLength));
            System.out.println();
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "SSPCS", (graphNumber + i - 1), sspcsResult.totalFlow, sspcsResult.minimumCost, sspcsResult.numberOfPaths, String.format("%.3f", sspcsResult.meanLength), String.format("%.3f", sspcsResult.meanProportionalLength));
            System.out.println();
            System.out.format("%10s%16s%16s%16s%16s%16s%16s", "PD", (graphNumber + i - 1), pdResult.totalFlow, pdResult.minimumCost, pdResult.numberOfPaths, String.format("%.3f", pdResult.meanLength), String.format("%.3f", pdResult.meanProportionalLength));
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
