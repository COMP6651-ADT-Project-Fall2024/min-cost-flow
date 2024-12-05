import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuccessiveShortestPathsSC implements Algorithm {

    @Override
    public int[] findMinCostFlowAndTotalCost(String graphFileName, int s, int t, int d) {
        int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(graphFileName);
        int[][] cap = GraphReader.getCapacityMatrix(graphFileName);
        int[][] unitCost = GraphReader.getUnitCostMatrix(graphFileName);

        int[][] flow = new int[cap.length][cap.length];
        int[][] residualCapacity = findResidualCapacity(adjacencyMatrix, flow, cap);

        int scalingFactor = getMaxCapacity(cap);

        while (scalingFactor >= 1) {
            while (d > 0 && augmentingPathExists(s, t, scalingFactor, residualCapacity)) {
                List<Integer> minCostPath = findMinimumCostPath(s, t, scalingFactor, unitCost, residualCapacity);
                int maxFlowThatCanBePushed = findMaxFlowThatCanBePushed(minCostPath, residualCapacity);
                if (maxFlowThatCanBePushed > d) {
                    maxFlowThatCanBePushed = d;
                }
                augmentFlow(maxFlowThatCanBePushed, adjacencyMatrix, flow, minCostPath);
                residualCapacity = findResidualCapacity(adjacencyMatrix, flow, cap);
                d = d - maxFlowThatCanBePushed;
            }
            scalingFactor = scalingFactor / 2;
        }

        if (d > 0) {
            return new int[] {-1, -1};
        }
        return new int[] {getFlowValue(s, flow), findCost(flow, unitCost)};
    }

    int getMaxCapacity(int[][] cap) {
        int maxCapacity = 0;
        for (int i = 0; i < cap.length; i ++) {
            for (int j = 0; j < cap.length; j ++) {
                maxCapacity = Math.max(maxCapacity, cap[i][j]);
            }
        }

        return maxCapacity;
    }

    int[][] findResidualCapacity(int[][] adjacencyMatrix, int[][] flow, int[][] cap) {
        int[][] residualCap = new int[cap.length][cap.length];
        for (int i = 0; i < cap.length; i ++) {
            for (int j = 0; j < cap.length; j ++) {
                if (adjacencyMatrix[i][j] == 1) {
                    residualCap[i][j] = cap[i][j] - flow[i][j];
                } else if (adjacencyMatrix[j][i] == 1) {
                    residualCap[j][i] = flow[i][j];
                }
            }
        }

        return residualCap;
    }

    boolean augmentingPathExists(int s, int t, int scalingFactor, int[][] residualCapacity) {
        int[] visited = new int[residualCapacity.length];
        return checkIfPathExists(s, t, scalingFactor, residualCapacity, visited);
    }

    boolean checkIfPathExists(int s, int t, int scalingFactor, int[][] residualCapacity, int[] visited) {
        if (s == t) {
            return true;
        }

        visited[s] = 1;
        for (int i = 0; i < residualCapacity.length; i ++) {
            if (visited[i] == 0 && residualCapacity[s][i] >= scalingFactor) {
                checkIfPathExists(i, t, scalingFactor, residualCapacity, visited);
            }
        }

        return false;
    }

    List<Integer> findMinimumCostPath(int s, int t, int scalingFactor, int[][] unitCost, int[][] residualCapacity) {
        int[] parent = new int[residualCapacity.length];
        Arrays.fill(parent, -1);
        getCostOfMinimumCostPath(s, t, scalingFactor, unitCost, residualCapacity, parent);
        List<Integer> minCostPath = new ArrayList<>();

        int k = t;
        while (k != -1) {
            minCostPath.add(0, k);
            k = parent[k];
        }

        return minCostPath;
    }

    void getCostOfMinimumCostPath(int s, int t, int scalingFactor, int[][] unitCost, int[][] residualCapacity, int[] parent) {
        int[] minCost = new int[unitCost.length];
        Arrays.fill(minCost, Integer.MAX_VALUE);
        minCost[s] = 0;
        for (int i = 0; i < unitCost.length - 1; i ++) {
            for (int j = 0; i < unitCost.length; i ++) {
                for (int k = 0; k < minCost.length; k ++) {
                    if (j != k && residualCapacity[j][k] >= scalingFactor && unitCost[j][k] != 0) {
                        if (minCost[k] > minCost[j] + unitCost[j][k]) {
                            minCost[k] = minCost[j] + unitCost[j][k];
                            parent[k] = j;
                        }
                    }
                }
            }
        }
    }

    int findMaxFlowThatCanBePushed(List<Integer> minCostPath, int[][] residualCapacity) {
        int maxFlowThatCanBePushed = Integer.MAX_VALUE;
        for (int i = 0; i < minCostPath.size() - 1; i ++) {
            maxFlowThatCanBePushed = Math.min(maxFlowThatCanBePushed, residualCapacity[minCostPath.get(i)][minCostPath.get(i+1)]);
        }

        return maxFlowThatCanBePushed;
    }

    void augmentFlow(int maxFlowThatCanBePushed, int[][] adjacencyMatrix, int[][] flow, List<Integer> minCostPath) {
        for (int i = 0; i < minCostPath.size() - 1; i ++) {
            int u = minCostPath.get(i);
            int v = minCostPath.get(i+1);
            if (adjacencyMatrix[u][v] == 1) {
                flow[u][v] = flow[u][v] + maxFlowThatCanBePushed;
            } else if (adjacencyMatrix[v][u] == 1) {
                flow[u][v] = flow[u][v] - maxFlowThatCanBePushed;
            }
        }
    }

    int getFlowValue(int s, int[][] flow) {
        int flowValue = 0;
        for (int i = 0; i < flow.length; i ++) {
            if (flow[s][i] > 0) {
                flowValue = flowValue + flow[s][i];
            }
        }

        return flowValue;
    }

    int findCost(int[][] flow, int[][] unitCost) {
        int totalCost = 0;
        for (int i = 0; i < flow.length; i ++) {
            for (int j = 0; j < flow.length; j ++) {
                totalCost = totalCost + unitCost[i][j] * flow[i][j];
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        SuccessiveShortestPathsSC alg = new SuccessiveShortestPathsSC();
        int[] a = alg.findMinCostFlowAndTotalCost("1", 1, 7, 3);
        System.out.println(a[0]);
        System.out.println(a[1]);
    }
}
