import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuccessiveShortestPaths implements Algorithm {

    private int n;

    @Override
    public int[] findMinCostFlowAndTotalCost(String graphFileName, int s, int t, int d) {
        int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(graphFileName);
        int[][] cap = GraphReader.getCapacityMatrix(graphFileName);
        int[][] unitCost = GraphReader.getUnitCostMatrix(graphFileName);

        n = adjacencyMatrix.length;

        int[][] flow = new int[n][n];
        int[][] residualGraph = new int[n][n];
        computeResidualCapacity(residualGraph, adjacencyMatrix, flow, cap);

        while (d > 0 && augmentingPathExists(s, t, residualGraph)) {
            List<Integer> minCostPath = findMinimumCostPath(s, t, unitCost, residualGraph);
            int maxFlowThatCanBePushed = findMaxFlowThatCanBePushed(minCostPath, residualGraph);
            if (maxFlowThatCanBePushed > d) {
                maxFlowThatCanBePushed = d;
            }
            augmentFlow(maxFlowThatCanBePushed, adjacencyMatrix, flow, minCostPath);
            computeResidualCapacity(residualGraph, adjacencyMatrix, flow, cap);
            d = d - maxFlowThatCanBePushed;
        }

        if (d > 0) {
            return new int[] {-1, -1};
        }
        return new int[] {getFlowValue(s, flow), findCost(flow, unitCost)};
    }

    private void computeResidualCapacity(int[][] residualGraph, int[][] adjacencyMatrix, int[][] flow, int[][] cap) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    residualGraph[i][j] = cap[i][j] - flow[i][j];
                } else if (adjacencyMatrix[j][i] == 1) {
                    residualGraph[i][j] = flow[j][i];
                }
            }
        }
    }

    private boolean augmentingPathExists(int s, int t, int[][] residualGraph) {
        int[] visited = new int[n];
        return checkIfPathExists(s, t, residualGraph, visited);
    }

    private boolean checkIfPathExists(int s, int t, int[][] residualGraph, int[] visited) {
        if (s == t) {
            return true;
        }

        visited[s] = 1;
        for (int i = 0; i < n; i++) {
            if (residualGraph[s][i] > 0 && visited[i] == 0) {
                if (checkIfPathExists(i, t, residualGraph, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<Integer> findMinimumCostPath(int s, int t, int[][] unitCost, int[][] residualGraph) {
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        computeMinCostPathsFromSource(s, unitCost, residualGraph, parent);
        List<Integer> minCostPath = new ArrayList<>();

        int k = t;
        while (k != -1) {
            minCostPath.add(0, k);
            k = parent[k];
        }

        return minCostPath;
    }

    private void computeMinCostPathsFromSource(int s, int[][] unitCost, int[][] residualGraph, int[] parent) {
        int[] minCost = new int[n];
        Arrays.fill(minCost, Integer.MAX_VALUE);
        minCost[s] = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if (residualGraph[j][k] > 0 && unitCost[j][k] != 0) {
                        if (minCost[k] > minCost[j] + unitCost[j][k]) {
                            minCost[k] = minCost[j] + unitCost[j][k];
                            parent[k] = j;
                        }
                    }
                }
            }
        }
    }

    private int findMaxFlowThatCanBePushed(List<Integer> minCostPath, int[][] residualGraph) {
        int maxFlowThatCanBePushed = Integer.MAX_VALUE;
        for (int i = 0; i < minCostPath.size() - 1; i++) {
            maxFlowThatCanBePushed = Math.min(maxFlowThatCanBePushed, residualGraph[minCostPath.get(i)][minCostPath.get(i + 1)]);
        }
        return maxFlowThatCanBePushed;
    }

    private void augmentFlow(int maxFlowThatCanBePushed, int[][] adjacencyMatrix, int[][] flow, List<Integer> minCostPath) {
        for (int i = 0; i < minCostPath.size() - 1; i++) {
            int u = minCostPath.get(i);
            int v = minCostPath.get(i + 1);
            if (adjacencyMatrix[u][v] == 1) {
                flow[u][v] += maxFlowThatCanBePushed;
            } else if (adjacencyMatrix[v][u] == 1) {
                flow[v][u] -= maxFlowThatCanBePushed;
            }
        }
    }

    private int getFlowValue(int s, int[][] flow) {
        int flowValue = 0;
        for (int i = 0; i < n; i++) {
            flowValue += flow[s][i] - flow[i][s];
        }
        return flowValue;
    }

    private int findCost(int[][] flow, int[][] unitCost) {
        int totalCost = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                totalCost += unitCost[i][j] * flow[i][j];
            }
        }
        return totalCost;
    }

    public static void main(String[] args) {
        SuccessiveShortestPaths alg = new SuccessiveShortestPaths();
        int[] result = alg.findMinCostFlowAndTotalCost("1", 0, 9, 1);
        System.out.println("Flow: " + result[0]);
        System.out.println("Cost: " + result[1]);
    }
}
