package util;


import graph.Graph;
import java.util.*;

public class PathUtils {
    public static int findLongestAcyclicPath(Graph g, int s, int t) {
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> memo = new HashMap<>();
        return dfsLongestPath(g, s, t, visited, memo);
    }

    private static int dfsLongestPath(Graph g, int current, int t, Set<Integer> visited, Map<Integer, Integer> memo) {
        if (current == t) {
            return 0;
        }
        if (memo.containsKey(current)) {
            return memo.get(current);
        }
        visited.add(current);
        int maxLength = -1;
        for (Graph.Edge edge : g.getEdgesFromVertex(current)) {
            int neighbor = edge.getDestination();
            if (!visited.contains(neighbor)) {
                int length = dfsLongestPath(g, neighbor, t, visited, memo);
                if (length != -1) {
                    maxLength = Math.max(maxLength, 1 + length);
                }
            }
        }
        visited.remove(current);
        memo.put(current, maxLength);
        return maxLength;
    }
}
