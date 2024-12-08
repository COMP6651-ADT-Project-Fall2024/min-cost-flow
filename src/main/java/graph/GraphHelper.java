package graph;

import java.util.Arrays;

public class GraphHelper {

    public static void removeEdgesNotInSourceSinkNetwork(int[][] adjacencyMatrix, int[][] cap, int[][] unitCost, int source, int sink) {
        int n = adjacencyMatrix.length;
        int[] visited1 = new int[n];
        dfsVisit(adjacencyMatrix, source, visited1);
        int[][] adjacencyMatrixForTranspose = new int[n][n];
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (i != j && adjacencyMatrix[i][j] == 1) {
                    adjacencyMatrixForTranspose[j][i] = 1;
                }
            }
        }
        int[] visited2 = new int[n];
        dfsVisit(adjacencyMatrixForTranspose, sink, visited2);
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
//                    System.out.println("Edge " + i + " to " + j + " removed.");
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

    public static int findLengthOfLongestAcyclicPath(int[][] adjacencyMatrix, int source, int sink) {
        int[] visited = new int[adjacencyMatrix.length];
        int[] longestPathLength = new int[adjacencyMatrix.length];
        return getLengthOfLongestAcyclicPath(adjacencyMatrix, longestPathLength, visited, source, sink);
    }

    private static int getLengthOfLongestAcyclicPath(int[][] adjacencyMatrix, int[] longestPathLength, int[] visited, int i, int t) {
        if (i == t) {
            return 0;
        }

        if (longestPathLength[i] != 0) {
            return longestPathLength[i];
        }

        int n = adjacencyMatrix.length;

        visited[i] = 1;

        int maxLen = 0;
        for (int j = 0; j < n; j ++) {
            if (i != j && visited[j] == 0 && adjacencyMatrix[i][j] == 1) {
                int lenOfLongestAcyclicPathFromJ = getLengthOfLongestAcyclicPath(adjacencyMatrix, longestPathLength, visited, j, t);
                int lenOfLongestAcyclicPathFromI = lenOfLongestAcyclicPathFromJ + 1;
                maxLen = Math.max(maxLen, lenOfLongestAcyclicPathFromI);
            }
        }

        visited[i] = 0;
        longestPathLength[i] = maxLen;

        return maxLen;
    }
}
