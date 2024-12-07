package simulations;

import graph.Graph;
import graph.Graph.Edge;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class EdmondsKarp {

    public static int computeMaxFlow(int[][] adjacencyMatrix, int[][] cap, int[][] unitCost, int source, int sink) {
        Graph graph = new Graph();
        graph.loadGraphFromMatrices(adjacencyMatrix, cap, unitCost, source, sink);
        return maxFlow(graph, source, sink);
    }

    public static int maxFlow(Graph graph, int source, int sink) {
        int maxFlow = 0;

        while (true) {
            // Find the shortest augmenting path using BFS
            Edge[] parent = new Edge[Collections.max(graph.getVertices()) + 1];
            int flow = bfs(graph, source, sink, parent);

            if (flow == 0) {
                // No more augmenting paths found
                break;
            }

            maxFlow += flow;

            // Trace the path and update the flows
            int current = sink;
            while (current != source) {
                Edge edge = parent[current];
                edge.augmentFlow(flow);
                current = edge.getSource();
            }
        }

        return maxFlow;
    }

    private static int bfs(Graph graph, int source, int sink, Edge[] parent) {
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[parent.length];
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (Edge edge : graph.getEdgesFromVertex(u)) {
                int v = edge.getDestination();

                if (!visited[v] && edge.remainingCapacity() > 0) {
                    queue.add(v);
                    visited[v] = true;
                    parent[v] = edge;

                    if (v == sink) {
                        // Found a path to sink
                        return getMinResidualCapacity(parent, source, sink);
                    }
                }
            }
        }

        return 0; // No augmenting path found
    }

    private static int getMinResidualCapacity(Edge[] parent, int source, int sink) {
        int flow = Integer.MAX_VALUE;
        int current = sink;

        while (current != source) {
            Edge edge = parent[current];
            flow = Math.min(flow, edge.remainingCapacity());
            current = edge.getSource();
        }

        return flow;
    }
}
