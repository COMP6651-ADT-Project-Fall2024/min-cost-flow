package algorithms;

import graph.Graph;
import java.util.*;

public class PrimalDual {

    private Map<Integer, Double> nodePotentials;

    public void primalDualAlgo(Graph g, int s, int t, int demand) {
        nodePotentials = new HashMap<>();
        // Initialize node potentials using Bellman-Ford
        boolean success = initializePotentials(g, s);

        if (!success) {
            System.out.println("Negative cycle detected. Algorithm cannot proceed.");
            return;
        }

        int totalFlow = 0;
        double totalCost = 0.0;

        while (totalFlow < demand) {
            // Run Dijkstra's algorithm to find the shortest path with reduced costs
            PathResult pathResult = dijkstra(g, s, t);

            if (pathResult == null) {
                System.out.println("No more augmenting paths. Total flow: " + totalFlow);
                break;
            }

            // Compute the amount to augment
            int amount = Math.min(pathResult.minCapacity, demand - totalFlow);

            // Augment flow along the path
            augmentFlow(pathResult, amount, s, t);

            totalFlow += amount;
            totalCost += amount * pathResult.pathCost;

            // Update node potentials
            updatePotentials(pathResult.distances);
        }

        System.out.println("Total flow: " + totalFlow);
        System.out.println("Total cost: " + totalCost);
    }

    private boolean initializePotentials(Graph g, int s) {
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Graph.Edge> predecessors = new HashMap<>();

        // Initialize distances
        for (int v : g.getVertices()) {
            distances.put(v, Double.POSITIVE_INFINITY);
        }
        distances.put(s, 0.0);

        int V = g.getVertices().size();

        // Relax edges repeatedly
        for (int i = 0; i < V - 1; i++) {
            for (int u : g.getVertices()) {
                for (Graph.Edge edge : g.getEdgesFromVertex(u)) {
                    if (edge.remainingCapacity() > 0) {
                        int v = edge.getDestination();
                        double weight = edge.getCost();
                        if (distances.get(u) + weight < distances.get(v)) {
                            distances.put(v, distances.get(u) + weight);
                            predecessors.put(v, edge);
                        }
                    }
                }
            }
        }

        // Check for negative-weight cycles
        for (int u : g.getVertices()) {
            for (Graph.Edge edge : g.getEdgesFromVertex(u)) {
                if (edge.remainingCapacity() > 0) {
                    int v = edge.getDestination();
                    double weight = edge.getCost();
                    if (distances.get(u) + weight < distances.get(v)) {
                        // Negative cycle detected
                        return false;
                    }
                }
            }
        }

        // Initialize node potentials
        for (int v : g.getVertices()) {
            nodePotentials.put(v, distances.get(v));
        }

        return true;
    }

    private PathResult dijkstra(Graph g, int s, int t) {
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Graph.Edge> predecessors = new HashMap<>();
        PriorityQueue<VertexDistance> queue = new PriorityQueue<>();

        for (int v : g.getVertices()) {
            distances.put(v, Double.POSITIVE_INFINITY);
        }
        distances.put(s, 0.0);
        queue.add(new VertexDistance(s, 0.0));

        while (!queue.isEmpty()) {
            VertexDistance vd = queue.poll();
            int u = vd.vertex;
            if (vd.distance > distances.get(u)) {
                continue;
            }
            if (u == t) {
                break;
            }

            for (Graph.Edge edge : g.getEdgesFromVertex(u)) {
                if (edge.remainingCapacity() > 0) {
                    int v = edge.getDestination();
                    double cost = edge.getCost() + nodePotentials.get(u) - nodePotentials.get(v);
                    if (distances.get(u) + cost < distances.get(v)) {
                        distances.put(v, distances.get(u) + cost);
                        predecessors.put(v, edge);
                        queue.add(new VertexDistance(v, distances.get(v)));
                    }
                }
            }
        }

        if (!predecessors.containsKey(t)) {
            // No path found
            return null;
        }

        // Reconstruct path and find minimum residual capacity and path cost along the path
        int v = t;
        int minCapacity = Integer.MAX_VALUE;
        double pathCost = 0.0;
        while (v != s) {
            Graph.Edge edge = predecessors.get(v);
            minCapacity = Math.min(minCapacity, edge.remainingCapacity());
            pathCost += edge.getCost();
            v = edge.getSource();
        }

        PathResult result = new PathResult();
        result.distances = distances;
        result.predecessors = predecessors;
        result.minCapacity = minCapacity;
        result.pathCost = pathCost;
        return result;
    }

    private void augmentFlow(PathResult pathResult, int amount, int s, int t) {
        int v = t;
        while (v != s) {
            Graph.Edge edge = pathResult.predecessors.get(v);
            edge.augmentFlow(amount);
            v = edge.getSource();
        }
    }

    private void updatePotentials(Map<Integer, Double> distances) {
        for (int v : nodePotentials.keySet()) {
            if (distances.get(v) < Double.POSITIVE_INFINITY) {
                nodePotentials.put(v, nodePotentials.get(v) + distances.get(v) - distances.getOrDefault(0, 0.0));
            }
        }
    }

    private static class PathResult {
        Map<Integer, Double> distances;
        Map<Integer, Graph.Edge> predecessors;
        int minCapacity;
        double pathCost;
    }

    private static class VertexDistance implements Comparable<VertexDistance> {
        int vertex;
        double distance;

        public VertexDistance(int vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        @Override
        public int compareTo(VertexDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
