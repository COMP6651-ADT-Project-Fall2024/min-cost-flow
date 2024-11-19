import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphReader {

    public static int[][] getAdjacencyMatrix(String graphFileName) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader("src/main/resources/graph" + graphFileName + ".txt"))) {
            String line;
            List<String> edges = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                edges.add(line);
            }

            int n = getMaxNode(edges) + 1;
            int[][] adjacencyMatrix = new int[n][n];

            populateAdjacencyMatrix(adjacencyMatrix, edges);

            return adjacencyMatrix;
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("The file you entered doesn't exist");
            } else {
                throw new IllegalStateException(e.getMessage());
            }
            return new int[0][];
        }
    }

    public static int[][] getCapacityMatrix(String graphFileName) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader("src/main/resources/graph" + graphFileName + ".txt"))) {
            String line;
            List<String> edges = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                edges.add(line);
            }

            int n = getMaxNode(edges) + 1;
            int[][] capacityMatrix = new int[n][n];

            populateCapacityMatrix(capacityMatrix, edges);

            return capacityMatrix;
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("The file you entered doesn't exist");
            } else {
                throw new IllegalStateException(e.getMessage());
            }
            return new int[0][];
        }
    }

    public static int[][] getUnitCostMatrix(String graphFileName) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader("src/main/resources/graph" + graphFileName + ".txt"))) {
            String line;
            List<String> edges = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                edges.add(line);
            }

            int n = getMaxNode(edges) + 1;
            int[][] unitCostMatrix = new int[n][n];

            populateUnitCostMatrix(unitCostMatrix, edges);

            return unitCostMatrix;
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                System.out.println("The file you entered doesn't exist");
            } else {
                throw new IllegalStateException(e.getMessage());
            }
            return new int[0][];
        }
    }

    public static int getMaxNode(List<String> edges) {
        int maxNode = 0;
        for (String edge : edges) {
            String[] edgeAttributes = edge.split(" ");
            int fromNode = Integer.parseInt(edgeAttributes[0]);
            int toNode = Integer.parseInt(edgeAttributes[1]);
            maxNode = Math.max(maxNode, Math.max(fromNode, toNode));
        }

        return maxNode;
    }

    public static void populateAdjacencyMatrix(int[][] adjacencyMatrix, List<String> edges) {
        for (String edge : edges) {
            String[] edgeAttributes = edge.split(" ");
            int fromNode = Integer.parseInt(edgeAttributes[0]);
            int toNode = Integer.parseInt(edgeAttributes[1]);
            adjacencyMatrix[fromNode][toNode] = 1;
        }
    }

    public static void populateCapacityMatrix(int[][] capacityMatrix, List<String> edges) {
        for (String edge : edges) {
            String[] edgeAttributes = edge.split(" ");
            int fromNode = Integer.parseInt(edgeAttributes[0]);
            int toNode = Integer.parseInt(edgeAttributes[1]);
            capacityMatrix[fromNode][toNode] = Integer.parseInt(edgeAttributes[2]);
        }
    }

    public static void populateUnitCostMatrix(int[][] unitCostMatrix, List<String> edges) {
        for (String edge : edges) {
            String[] edgeAttributes = edge.split(" ");
            int fromNode = Integer.parseInt(edgeAttributes[0]);
            int toNode = Integer.parseInt(edgeAttributes[1]);
            unitCostMatrix[fromNode][toNode] = Integer.parseInt(edgeAttributes[3]);
        }
    }
}
