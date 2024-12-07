import graph.GenerateGraph;
import graph.Graph;
import simulations.EdmondsKarp;
import util.GraphUtils;

import java.io.IOException;
import java.util.Scanner;

public class MinCostFlow {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!!");
        System.out.println("For Graph generation press 1 for testing algo press 2 and for simulation 3");
        String user_input = scanner.nextLine();
        if (user_input.equals("1")){
            GenerateGraph.generateSinkSourceGraph(10, 0.8, 5, 4);
        }
        else if(user_input.equals("2")){
            System.out.println("Choose which algo to use: \n1. Primal Dual");
            AlgoDriver driver = new AlgoDriver(1,17,10);
            String algoChoice = scanner.nextLine();
            if(algoChoice.equals("1")){
                driver.primalDualDriver("graph3.txt");
            }
        }
        else if(user_input.equals("3")){
            System.out.println("Choose which Simulation to run: \n1. Simulation1");
            String algoChoice = scanner.nextLine();
            if(algoChoice.equals("1")){
                EdmondsKarp edmondsKarpObj = new EdmondsKarp();
                Graph graph = GraphUtils.loadGraph("graph4.txt", 1, 7);
                int maxFlow = edmondsKarpObj.maxFlow(graph, 1, 7);
                System.out.println(maxFlow);
            }
        }

    }
}
