import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuccessiveShortestPaths implements Algorithm {

    @Override
    public int[] findMinCostFlowAndTotalCost(String graphFileName, int s, int t, int d) {
        int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(graphFileName);
        int[][] cap = GraphReader.getCapacityMatrix(graphFileName);
        int[][] unitCost = GraphReader.getUnitCostMatrix(graphFileName);

        int[][] flow = new int[cap.length][cap.length];
        int[][] residualCapacity = new int[cap.length][cap.length];
        computeResidualCapacity(residualCapacity, adjacencyMatrix, flow, cap);

        int scalingFactor = getMaxCapacity(cap);

        while (scalingFactor >= 1) {
            while (d > 0 && augmentingPathExists(s, t, scalingFactor, residualCapacity)) {
                List<Integer> minCostPath = findShortestPath(s, t, scalingFactor, residualCapacity);
                int maxFlowThatCanBePushed = findMaxFlowThatCanBePushed(minCostPath, residualCapacity);
                if (maxFlowThatCanBePushed > d) {
                    maxFlowThatCanBePushed = d;
                }
                augmentFlow(maxFlowThatCanBePushed, adjacencyMatrix, flow, minCostPath);
                computeResidualCapacity(residualCapacity, adjacencyMatrix, flow, cap);
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

    void computeResidualCapacity(int[][] residualCapacity, int[][] adjacencyMatrix, int[][] flow, int[][] cap) {
        for (int i = 0; i < cap.length; i ++) {
            for (int j = 0; j < cap.length; j ++) {
                if (adjacencyMatrix[i][j] == 1) {
                    residualCapacity[i][j] = cap[i][j] - flow[i][j];
                } else if (adjacencyMatrix[j][i] == 1) {
                    residualCapacity[i][j] = flow[j][i];
                }
            }
        }
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
                if (checkIfPathExists(i, t, scalingFactor, residualCapacity, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    List<Integer> findShortestPath(int s, int t, int scalingFactor, int[][] residualCapacity) {
        int[] parent = new int[residualCapacity.length];
        Arrays.fill(parent, -1);
        computeShortestPathsFromSource(s, scalingFactor, residualCapacity, parent);
        List<Integer> minCostPath = new ArrayList<>();

        int k = t;
        while (k != -1) {
            minCostPath.add(0, k);
            k = parent[k];
        }

        return minCostPath;
    }

    void computeShortestPathsFromSource(int s, int scalingFactor, int[][] residualCapacity, int[] parent) {
        int[] shortestDistance = new int[residualCapacity.length];
        Arrays.fill(shortestDistance, Integer.MAX_VALUE);
        shortestDistance[s] = 0;
        for (int i = 0; i < residualCapacity.length - 1; i ++) {
            for (int j = 0; j < residualCapacity.length; j ++) {
                for (int k = 0; k < residualCapacity.length; k ++) {
                    if(j != k) {
                        if (residualCapacity[j][k] >= scalingFactor) {
                            if (shortestDistance[k] > shortestDistance[j] + 1) {
                                shortestDistance[k] = shortestDistance[j] + 1;
                                parent[k] = j;
                            }
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
            int v = minCostPath.get(i + 1);
            if (adjacencyMatrix[u][v] == 1) {
                flow[u][v] = flow[u][v] + maxFlowThatCanBePushed;
            } else if (adjacencyMatrix[v][u] == 1) {
                flow[v][u] = flow[v][u] - maxFlowThatCanBePushed;
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
        int[] a = alg.findMinCostFlowAndTotalCost("1", 0, 9, 1);
        System.out.println(a[0]);
        System.out.println(a[1]);
    }
}
