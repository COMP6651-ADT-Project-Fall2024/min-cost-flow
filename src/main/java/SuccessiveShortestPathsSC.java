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
        int[][] residualCap = findResidualCapacity(adjacencyMatrix, flow, cap);

        int scalingFactor = getMaxCapacity(cap);

        while (scalingFactor >= 1) {
            while (d > 0 && augmentedPathExists(s, t, scalingFactor, residualCap)) {
                List<Integer> minCostPath = findMinimumCostPath(s, t, scalingFactor, unitCost, cap);
                int maxFlowThatCanBePushed = findMaxFlowThatCanBePushed(minCostPath, cap, flow);
                if (maxFlowThatCanBePushed > d) {
                    maxFlowThatCanBePushed = d;
                }
                augmentFlow(maxFlowThatCanBePushed, flow, minCostPath);
                residualCap = findResidualCapacity(adjacencyMatrix, flow, cap);
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
            for (int j = 0; j < cap[0].length; j ++) {
                maxCapacity = Math.max(maxCapacity, cap[i][j]);
            }
        }

        return maxCapacity;
    }

    int[][] findResidualCapacity(int[][] adjacencyMatrix, int[][] flow, int[][] cap) {
        int[][] residualCap = new int[adjacencyMatrix.length][adjacencyMatrix.length];
        for (int i = 0; i < cap.length; i ++) {
            for (int j = 0; j < cap.length; j ++) {
                if (adjacencyMatrix[i][j] == 1) {
                    residualCap[i][j] = cap[i][j] - flow[i][j];
                } else {
                    residualCap[i][j] = flow[i][j];
                }
            }
        }

        return residualCap;
    }

    boolean augmentedPathExists(int s, int t, int scalingFactor, int[][] residualCap) {
        int[] visited = new int[residualCap.length];
        return checkIfPathExists(s, t, scalingFactor, residualCap, visited);
    }

    boolean checkIfPathExists(int s, int t, int scalingFactor, int[][] residualCap, int[] visited) {
        if (s == t) {
            return true;
        }

        visited[s] = 1;
        for (int i = 0; i < residualCap.length; i ++) {
            if (visited[i] == 0 && residualCap[s][i] >= scalingFactor) {
                checkIfPathExists(i, t, scalingFactor, residualCap, visited);
            }
        }
        visited[s] = 0;

        return false;
    }

    List<Integer> findMinimumCostPath(int s, int t, int scalingFactor, int[][] unitCost, int[][] cap) {
        int[] visited = new int[cap.length];
        int[] parent = new int[cap.length];
        Arrays.fill(parent, -1);
        int minCost = getCostOfMinimumCostPath(s, t, scalingFactor, unitCost, cap, visited, parent);
        List<Integer> minCostPath = new ArrayList<>();

        int k = t;
        while (parent[k] != -1) {
            minCostPath.add(k);
            k = parent[k];
        }

        return minCostPath;
    }

    int getCostOfMinimumCostPath(int s, int t, int scalingFactor, int[][] unitCost, int[][] cap, int[] visited, int[] parent) {
        if (s == t) {
            return 0;
        }

        visited[s] = 1;
        int minCost = Integer.MAX_VALUE;
        for (int i = 0; i < cap.length; i ++) {
            if (visited[i] == 0 && cap[s][i] >= scalingFactor) {
                int costOfPathFromI = unitCost[s][i] + getCostOfMinimumCostPath(i, t, scalingFactor, unitCost, cap, visited, parent);
                if (minCost > costOfPathFromI) {
                    minCost = costOfPathFromI;
                    parent[i] = s;
                }
            }
        }
        visited[s] = 0;
        return minCost;
    }

    int findMaxFlowThatCanBePushed(List<Integer> minCostPath, int[][] cap, int[][] flow) {
        int maxFlowThatCanBePushed = Integer.MAX_VALUE;
        for (int i = 0; i < minCostPath.size() - 1; i ++) {
            maxFlowThatCanBePushed = Math.min(maxFlowThatCanBePushed, cap[minCostPath.get(i)][minCostPath.get(i+1)] - flow[minCostPath.get(i)][minCostPath.get(i+1)]);
        }

        return maxFlowThatCanBePushed;
    }

    void augmentFlow(int maxFlowThatCanBePushed, int[][] flow, List<Integer> minCostPath) {
        for (int i = 0; i < minCostPath.size() - 1; i ++) {
            flow[minCostPath.get(i)][minCostPath.get(i+1)] = flow[minCostPath.get(i)][minCostPath.get(i+1)] + maxFlowThatCanBePushed;
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
}
