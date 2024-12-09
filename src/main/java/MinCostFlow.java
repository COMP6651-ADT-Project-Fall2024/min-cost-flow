import algorithms.AlgoDriver;
import algorithms.AlgoResult;
import algorithms.Algorithm;
import algorithms.CapacityScaling;
import algorithms.SuccessiveShortestPaths;
import algorithms.SuccessiveShortestPathsSC;
import simulations.Simulations1;

import java.io.IOException;
import java.util.Scanner;

public class MinCostFlow {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!!");
        System.out.println("For testing algo press 1 and for simulation 2");
        String user_input = scanner.nextLine();

        switch (user_input) {
            case "1" -> {
                runTests();
            }
            case "2" -> {
                System.out.println("Choose which Simulation to run: \n1. Simulation1");
                String simulationChoice = scanner.nextLine();
                if (simulationChoice.equals("1")) {
                    Simulations1.run();
                }
            }
        }
    }

    private static void runTests() throws IOException {
        System.out.println("Running tests...\n");

        int[][] testInput = {
                {0, 3, 4},
                {1, 7, 7},
                {1, 17, 7}
        };

        int[][][] expectedFlowAndMinCost = {
                {
                        {4, 27},
                        {4, 30},
                        {4, 30},
                        {4, 27}
                },
                {
                        {7, 79},
                        {7, 82},
                        {7, 79},
                        {7, 79}
                },
                {
                        {7, 96},
                        {7, 108},
                        {7, 96},
                        {7, 96}
                }
        };

        boolean testsFailuresExist = false;

        for (int i = 1; i <= 3; i ++) {
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("Testing using test graph " + i + ":");
            Algorithm alg1 = new SuccessiveShortestPaths();
            AlgoResult result1 = alg1.findMinCostFlowAndTotalCost("-test-" + i, testInput[i - 1][0], testInput[i - 1][1], testInput[i - 1][2]);
            Algorithm alg2 = new CapacityScaling();
            AlgoResult result2 = alg2.findMinCostFlowAndTotalCost("-test-" + i, testInput[i - 1][0], testInput[i - 1][1], testInput[i - 1][2]);
            Algorithm alg3 = new SuccessiveShortestPathsSC();
            AlgoResult result3 = alg3.findMinCostFlowAndTotalCost("-test-" + i, testInput[i - 1][0], testInput[i - 1][1], testInput[i - 1][2]);
            AlgoDriver alg4 = new AlgoDriver(testInput[i - 1][0], testInput[i - 1][1], testInput[i - 1][2]);
            AlgoResult result4 = alg4.primalDualDriver("graph-test-" + i + ".txt");
            System.out.println("For Algorithm 1: ");
            System.out.println("Actual Flow: " + result1.totalFlow + ". Expected Flow: " + expectedFlowAndMinCost[i - 1][0][0]);
            System.out.println("Actual Minimum Cost: " + result1.minimumCost + ". Expected Minimum Cost: " + expectedFlowAndMinCost[i - 1][0][1]);
            System.out.println("For Algorithm 2: ");
            System.out.println("Actual Flow: " + result2.totalFlow + ". Expected Flow: " + expectedFlowAndMinCost[i - 1][1][0]);
            System.out.println("Actual Minimum Cost: " + result2.minimumCost + ". Expected Minimum Cost: " + expectedFlowAndMinCost[i - 1][1][1]);
            System.out.println("For Algorithm 3: ");
            System.out.println("Actual Flow: " + result3.totalFlow + ". Expected Flow: " + expectedFlowAndMinCost[i - 1][2][0]);
            System.out.println("Actual Minimum Cost: " + result3.minimumCost + ". Expected Minimum Cost: " + expectedFlowAndMinCost[i - 1][2][1]);
            System.out.println("For Algorithm 4: ");
            System.out.println("Actual Flow: " + result4.totalFlow + ". Expected Flow: " + expectedFlowAndMinCost[i - 1][3][0]);
            System.out.println("Actual Minimum Cost: " + result4.minimumCost + ". Expected Minimum Cost: " + expectedFlowAndMinCost[i - 1][3][1]);
            System.out.println();

            for (int j = 1; j <= 4; j ++) {
                if (result1.totalFlow != expectedFlowAndMinCost[i - 1][0][0] || result1.minimumCost != expectedFlowAndMinCost[i - 1][0][1]
                        || result2.totalFlow != expectedFlowAndMinCost[i - 1][1][0] || result2.minimumCost != expectedFlowAndMinCost[i - 1][1][1]
                        || result3.totalFlow != expectedFlowAndMinCost[i - 1][2][0] || result3.minimumCost != expectedFlowAndMinCost[i - 1][2][1]
                        || result4.totalFlow != expectedFlowAndMinCost[i - 1][3][0] || result4.minimumCost != expectedFlowAndMinCost[i - 1][3][1]) {
                    testsFailuresExist = true;
                }
            }
        }

        System.out.println("-----------------------------------------------------------------------------");

        if (!testsFailuresExist) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println("THERE ARE TEST FAILURES");
        }
    }
}
