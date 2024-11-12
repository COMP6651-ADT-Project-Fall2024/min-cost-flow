import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateGraph {

    public static int graphNumber = 1;

    public static void generateSinkSourceGraph(int n, double r, int upperCap, int upperCost) {

        Vertex[] vertices = new Vertex[n];
        int[][] adjacencyMatrix = new int[n][n];
        int[][] cap = new int[n][n];
        int[][] unitCost = new int[n][n];

        for (int i = 0; i < n; i ++) {
            vertices[i] = new Vertex(i);
        }

        Random random = new Random();
        for (int i = 0; i < n; i ++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            vertices[i].setX(x);
            vertices[i].setY(y);
        }

        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j ++) {
                if (i == j) {
                    continue;
                }

                double squareOfDistance = (vertices[i].getX() - vertices[j].getX()) * (vertices[i].getX() - vertices[j].getX())
                        + (vertices[i].getY() - vertices[j].getY()) * (vertices[i].getY() - vertices[j].getY());
                if (squareOfDistance <= r * r) {
                    double rand = random.nextDouble();
                    if (rand < 0.3) {
                        if (adjacencyMatrix[i][j] == 0 && adjacencyMatrix[j][i] == 0) {
                            adjacencyMatrix[i][j] = 1;
                        }
                    } else if (rand < 0.6) {
                        if (adjacencyMatrix[i][j] == 0 && adjacencyMatrix[j][i] == 0) {
                            adjacencyMatrix[j][i] = 1;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < n; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    cap[i][j] = random.nextInt(upperCap - 1) + 1;
                    unitCost[i][j] = random.nextInt(upperCost - 1) + 1;
                }
            }
        }

        writeToFile(adjacencyMatrix, cap, unitCost);
    }

    public static void writeToFile(int[][] adjacencyMatrix, int[][] cap, int[][] unitCost) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter("src/main/resources/graph" + graphNumber + ".txt"));
            graphNumber ++;

            for (int i = 0; i < adjacencyMatrix.length; i ++) {
                for (int j = 0; j < adjacencyMatrix[0].length; j++) {
                    if (adjacencyMatrix[i][j] == 1) {
                        bufferedWriter.write(i + " " + j + " " + cap[i][j] + " " + unitCost[i][j] + "\n");
                    }
                }
            }
            System.out.print("File is created successfully with the content.");
            bufferedWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException("Some issue when writing to file");
        }
    }

    public static void main(String[] args) {
        generateSinkSourceGraph(10, 0.8, 5, 4);
    }
}
