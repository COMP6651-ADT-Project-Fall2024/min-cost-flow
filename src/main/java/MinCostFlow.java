import simulations.Simulations1;
import simulations.Simulations2;

import java.io.IOException;
import java.util.Scanner;

public class MinCostFlow {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!!");
        System.out.println("For testing algorithms press 1, and for running simulations press 2:");
        String user_input = scanner.nextLine();

        switch (user_input) {
            case "1" -> Test.runTests();
            case "2" -> {
                int t = 0;
                while (t < 3) {
                    System.out.println("Choose which Simulation to run: \n1. Simulations1 \n2. Simulations2");
                    String simulationChoice = scanner.nextLine();
                    if (simulationChoice.equals("1")) {
                        Simulations1.run();
                        break;
                    } else if (simulationChoice.equals("2")) {
                        Simulations2.run();
                        break;
                    } else {
                        t ++;
                        if (t == 3) {
                            System.out.println("You are not choosing valid input, check readme.md to know how to run the program.");
                        } else {
                            System.out.println("Not a valid input. Press 1 to run Simulations1 OR 2 to run Simulations2. Try again");
                        }
                    }
                }
            }
        }
    }
}
