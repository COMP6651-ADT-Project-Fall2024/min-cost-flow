package algorithms;

import static graph.GraphHelper.findLengthOfLongestAcyclicPath;

import graph.GraphHelper;
import graph.GraphReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CapacityScaling implements Algorithm {

    private int n;
    private int numOfAugmentingPaths = 0;
    private double sumOfLengthsOfAugmentingPaths = 0;

    @Override
    public AlgoResult findMinCostFlowAndTotalCost(String graphFileName, int s, int t, int d) {
        int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(graphFileName);
        int[][] cap = GraphReader.getCapacityMatrix(graphFileName);
        int[][] unitCost = GraphReader.getUnitCostMatrix(graphFileName);
        GraphHelper.removeEdgesNotInSourceSinkNetwork(adjacencyMatrix, cap, unitCost, s, t);

        n = adjacencyMatrix.length;

        int[][] flow = new int[n][n];
        int[][] residualGraph = new int[n][n];
        computeResidualCapacity(residualGraph, adjacencyMatrix, flow, cap);

        if (d == 0) {
            return new AlgoResult(0, 0, 0, 0, 0);
        }

        int scalingFactor = getMaxCapacity(cap);

        while (scalingFactor >= 1) {
            while (d > 0 && augmentingPathExists(s, t, scalingFactor, residualGraph)) {
                List<Integer> shortestPath = findShortestPath(s, t, scalingFactor, residualGraph);
                int maxFlowThatCanBePushed = findMaxFlowThatCanBePushed(shortestPath, residualGraph);
                if (maxFlowThatCanBePushed > d) {
                    maxFlowThatCanBePushed = d;
                }
                augmentFlow(maxFlowThatCanBePushed, adjacencyMatrix, flow, shortestPath);
                computeResidualCapacity(residualGraph, adjacencyMatrix, flow, cap);
                d = d - maxFlowThatCanBePushed;
            }
            scalingFactor = scalingFactor / 2;
        }

        AlgoResult result;
        if (d > 0) {
            result = new AlgoResult(-1, -1, -1, -1, -1);
        } else {
            double avgLengthOfAugmentingPath = sumOfLengthsOfAugmentingPaths == 0 ? sumOfLengthsOfAugmentingPaths : sumOfLengthsOfAugmentingPaths / (numOfAugmentingPaths * 1.00);
            double meanProportionalLength = avgLengthOfAugmentingPath == 0 ? 0 : avgLengthOfAugmentingPath / findLengthOfLongestAcyclicPath(adjacencyMatrix, s, t);
            result = new AlgoResult(findCost(flow, unitCost),
                    getFlowValue(s, flow),
                    numOfAugmentingPaths,
                    avgLengthOfAugmentingPath,
                    meanProportionalLength);
        }
        return result;
    }

    private int getMaxCapacity(int[][] cap) {
        int maxCapacity = 0;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                maxCapacity = Math.max(maxCapacity, cap[i][j]);
            }
        }

        return maxCapacity;
    }

    private void computeResidualCapacity(int[][] residualGraph, int[][] adjacencyMatrix, int[][] flow, int[][] cap) {
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (adjacencyMatrix[i][j] == 1) {
                    residualGraph[i][j] = cap[i][j] - flow[i][j];
                } else if (adjacencyMatrix[j][i] == 1) {
                    residualGraph[i][j] = flow[j][i];
                }
            }
        }
    }

    private boolean augmentingPathExists(int s, int t, int scalingFactor, int[][] residualGraph) {
        int[] visited = new int[n];
        return checkIfPathExists(s, t, scalingFactor, residualGraph, visited);
    }

    private boolean checkIfPathExists(int s, int t, int scalingFactor, int[][] residualGraph, int[] visited) {
        if (s == t) {
            return true;
        }

        visited[s] = 1;
        for (int i = 0; i < n; i ++) {
            if (residualGraph[s][i] >= scalingFactor && visited[i] == 0) {
                if (checkIfPathExists(i, t, scalingFactor, residualGraph, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    List<Integer> findShortestPath(int s, int t, int scalingFactor, int[][] residualGraph) {
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        computeShortestPathsFromSource(s, scalingFactor, residualGraph, parent);
        List<Integer> shortestPath = new ArrayList<>();

        int k = t;
        while (k != -1) {
            shortestPath.add(0, k);
            k = parent[k];
        }

        return shortestPath;
    }

    void computeShortestPathsFromSource(int s, int scalingFactor, int[][] residualGraph, int[] parent) {
        int[] shortestDistance = new int[n];
        Arrays.fill(shortestDistance, Integer.MAX_VALUE);
        shortestDistance[s] = 0;
        for (int i = 0; i < n - 1; i ++) {
            for (int j = 0; j < n; j ++) {
                for (int k = 0; k < n; k ++) {
                    if(j != k) {
                        if (residualGraph[j][k] >= scalingFactor) {
                            if (shortestDistance[k] - shortestDistance[j] > 1) {
                                shortestDistance[k] = shortestDistance[j] + 1;
                                parent[k] = j;
                            }
                        }
                    }
                }
            }
        }
    }

    private int findMaxFlowThatCanBePushed(List<Integer> shortestPath, int[][] residualCapacity) {
        int maxFlowThatCanBePushed = Integer.MAX_VALUE;
        for (int i = 0; i < shortestPath.size() - 1; i ++) {
            maxFlowThatCanBePushed = Math.min(maxFlowThatCanBePushed, residualCapacity[shortestPath.get(i)][shortestPath.get(i + 1)]);
        }

        // I think this needs to return infinite if shortestPath.size() <= 1. So I think it's okay
        return maxFlowThatCanBePushed;
    }

    private void augmentFlow(int maxFlowThatCanBePushed, int[][] adjacencyMatrix, int[][] flow, List<Integer> shortestPath) {
        numOfAugmentingPaths ++;
        if (!shortestPath.isEmpty()) {
            sumOfLengthsOfAugmentingPaths += shortestPath.size() - 1;
        }

        for (int i = 0; i < shortestPath.size() - 1; i ++) {
            int u = shortestPath.get(i);
            int v = shortestPath.get(i + 1);
            if (adjacencyMatrix[u][v] == 1) {
                flow[u][v] = flow[u][v] + maxFlowThatCanBePushed;
            } else if (adjacencyMatrix[v][u] == 1) {
                flow[v][u] = flow[v][u] - maxFlowThatCanBePushed;
            }
        }
    }

    private int getFlowValue(int s, int[][] flow) {
        int flowValue = 0;
        for (int i = 0; i < n; i ++) {
            if (s != i && flow[s][i] > 0) {
                flowValue = flowValue + flow[s][i];
            }
        }

        for (int i = 0; i < n; i ++) {
            if (s != i && flow[i][s] > 0) {
                flowValue = flowValue - flow[i][s];
            }
        }

        return flowValue;
    }

    private int findCost(int[][] flow, int[][] unitCost) {
        int totalCost = 0;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (i != j) {
                    totalCost = totalCost + unitCost[i][j] * flow[i][j];
                }
            }
        }

        return totalCost;
    }
}
