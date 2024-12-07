import java.util.LinkedList;
import java.util.Queue;

public class SimulationsI {

    public static void main(String[] args) {

        int[][] sourceAndSink = new int[8][2];
        for (int i = 0; i <= 8; i ++) {
            int[][] adjacencyMatrix = GraphReader.getAdjacencyMatrix(String.valueOf(i));
            int[][] cap = GraphReader.getCapacityMatrix(String.valueOf(i));
            int[][] unitCost = GraphReader.getUnitCostMatrix(String.valueOf(i));
            int n = adjacencyMatrix.length;
            sourceAndSink[i] = determineSourceSinkNetworks(adjacencyMatrix);
            int source = sourceAndSink[i][0];
            int sink = sourceAndSink[i][1];
            removeEdgesNotInSourceSinkNetwork(adjacencyMatrix, cap, unitCost, source, sink);

            int upperCap = findUpperCap(cap);
            int upperCost = findUpperCost(unitCost);
            // find fMax
            int[] visited = new int[n];
            int nodesInLargestConnectedComponent = findSizeOfConnectedComponent(adjacencyMatrix, visited, source);
            visited = new int[n];
            int maxOutDegreeInLCC = findMaxOutDegree(adjacencyMatrix, visited, source);
            visited = new int[n];
            int maxInDegreeInLCC = findMaxInDegree(adjacencyMatrix, visited, source);
            double averageDegreeInLCC = findEdgesInLCC(adjacencyMatrix) / (nodesInLargestConnectedComponent * 1.00);
            // print all
        }
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

    public static void removeEdgesNotInSourceSinkNetwork(int[][] adjacencyMatrix, int[][] cap, int[][] unitCost, int source, int sink) {
        int n = adjacencyMatrix.length;
        int[] visited1 = new int[n];
        dfsVisit(adjacencyMatrix, source, visited1);
        int[][] adjacencyMatrixForTranspose = new int[n][n];
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (adjacencyMatrix[j][i] == 1) {
                    adjacencyMatrixForTranspose[i][j] = 1;
                }
            }
        }
        int[] visited2 = new int[n];
        dfsVisit(adjacencyMatrix, sink, visited2);
        int[] visitedCombined = new int[n];
        for (int i = 0; i < n; i ++) {
            if (visited1[i] == 1 && visited2[i] == 1) {
                visitedCombined[i] = 1;
            }
        }

        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (visitedCombined[i] != 1 || visitedCombined[j] != 1) {
                    adjacencyMatrix[i][j] = 0;
                    cap[i][j] = 0;
                    unitCost[i][j] = 0;
                }
            }
        }
    }

    private static void dfsVisit(int[][] adjacencyMatrix, int i, int[] visited) {
        int n = adjacencyMatrix.length;
        visited[i] = 1;
        for (int j = 0; j < n; j ++) {
            if (i != j && visited[j] == 0 && adjacencyMatrix[i][j] == 1) {
                dfsVisit(adjacencyMatrix, j, visited);
            }
        }
    }

    private static int findSizeOfConnectedComponent(int[][] adjacencyMatrix, int[] visited, int i) {
        int n = adjacencyMatrix.length;
        visited[i] = 1;
        int c = 1;
        for (int j = 0; j < n; j ++) {
            if (i != j && visited[j] == 0 && adjacencyMatrix[i][j] == 1) {
                c = c + findSizeOfConnectedComponent(adjacencyMatrix, visited, j);
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
                q.add(-1);
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
