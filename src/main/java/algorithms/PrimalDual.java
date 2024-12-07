package algorithms;

import graph.Graph;
import java.util.*;
import util.PathUtils;
public class PrimalDual {

    private Map<Integer, Double> nodePotentials;
    private List<FlowPath> flowPaths = new ArrayList<>();


    public AlgoResult primalDualAlgo(Graph g, int s, int t, int demand) {
        nodePotentials = new HashMap<>();
        // Initialize node potentials using Bellman-Ford
        boolean success = initializePotentials(g, s);

        if (!success) {
            System.out.println("Negative cycle detected. Algorithm cannot proceed.");
            return null;
        }
        int longestAcyclicPath = PathUtils.findLongestAcyclicPath(g, s, t);
        if (longestAcyclicPath == -1) {
            System.out.println("No path exists from source to sink.");
            return null;
        }


        int totalFlow = 0;
        double totalCost = 0.0;
        int pathCount = 0;
        double cumulativePathLength = 0;

        while (totalFlow < demand) {
            // Run Dijkstra's algorithm to find the shortest path with reduced costs
            PathResult pathResult = dijkstra(g, s, t);


            if (pathResult == null) {
                System.out.println("Demand cannot be met. Total flow: : " + totalFlow);
                break;
            }

            // Compute the amount to augment
            int amount = Math.min(pathResult.minCapacity, demand - totalFlow);

            // Augment flow along the path
            augmentFlow(pathResult, amount, s, t);

            totalFlow += amount;
            totalCost += amount * pathResult.pathCost;

            // Store the flow path for traceability
            flowPaths.add(new FlowPath(pathResult.path, amount, pathResult.pathCost));
            pathCount++;
            cumulativePathLength += (pathResult.path.size() - 1); // Number of edges
            // Update node potentials
            updatePotentials(pathResult.distances);
        }
        double meanLength = pathCount > 0 ? (double) cumulativePathLength / pathCount : 0.0;
        double meanProportionalLength = longestAcyclicPath > 0 ? meanLength / longestAcyclicPath : 0.0;
        displayFlowPaths();
        System.out.println(new AlgoResult(totalCost, totalFlow, pathCount, meanLength, meanProportionalLength));
        return new AlgoResult(totalCost, totalFlow, pathCount, meanLength, meanProportionalLength);

    }

    private void displayFlowPaths() {
        System.out.println("\nFlow Distribution:");
        for (FlowPath fp : flowPaths) {
            System.out.print("Flow " + fp.flow + " along path: ");
            for (int i = 0; i < fp.path.size(); i++) {
                System.out.print(fp.path.get(i));
                if (i != fp.path.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println(" | Path Cost: " + fp.cost);
        }
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
        List<Integer> path = new ArrayList<>(); // To store the path
        while (v != s) {
            Graph.Edge edge = predecessors.get(v);
            minCapacity = Math.min(minCapacity, edge.remainingCapacity());
            pathCost += edge.getCost();
            path.add(v);
            v = edge.getSource();
        }
        path.add(s); // Add source to path
        Collections.reverse(path); // Reverse to get path from s to t
        PathResult result = new PathResult();
        result.distances = distances;
        result.predecessors = predecessors;
        result.minCapacity = minCapacity;
        result.pathCost = pathCost;
        result.path = path; // Store the path
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
                nodePotentials.put(v, nodePotentials.get(v) + distances.get(v));
            }
        }

//        System.out.println("Updated Node Potentials:");
//        for (Map.Entry<Integer, Double> entry : nodePotentials.entrySet()) {
//            System.out.println("Node " + entry.getKey() + ": " + entry.getValue());
//        }
//        System.out.println();
    }


    private static class PathResult {
        Map<Integer, Double> distances;
        Map<Integer, Graph.Edge> predecessors;
        int minCapacity;
        double pathCost;
        List<Integer> path;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PathResult {\n");
            sb.append("  distances: ").append(formatMap(distances)).append(",\n");
            sb.append("  predecessors: ").append(formatMap(predecessors)).append(",\n");
            sb.append("  minCapacity: ").append(minCapacity).append(",\n");
            sb.append("  pathCost: ").append(pathCost).append(",\n");
            sb.append("  path: ").append(path).append("\n");
            sb.append("}");
            return sb.toString();
        }

        private <K, V> String formatMap(Map<K, V> map) {
            if (map == null || map.isEmpty()) {
                return "{}";
            }
            StringBuilder sb = new StringBuilder("{");
            for (Map.Entry<K, V> entry : map.entrySet()) {
                sb.append("\n    ").append(entry.getKey()).append(": ").append(entry.getValue()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); // Remove trailing comma
            sb.append("\n  }");
            return sb.toString();
        }
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

    private static class FlowPath {
        List<Integer> path;
        int flow;
        double cost;

        public FlowPath(List<Integer> path, int flow, double cost) {
            this.path = path;
            this.flow = flow;
            this.cost = cost;
        }
    }


}
