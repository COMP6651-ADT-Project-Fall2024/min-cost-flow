package algorithms;

public interface Algorithm {

    AlgoResult findMinCostFlowAndTotalCost(String graphFileName, int s, int t, int d);
}
