package algorithms;

public class AlgoResult {
    public double minimumCost;
    public int totalFlow;
    public int numberOfPaths;
    public double meanLength;
    public double meanProportionalLength;

    public AlgoResult(double minimumCost, int totalFlow, int numberOfPaths, double meanLength, double meanProportionalLength) {
        this.minimumCost = minimumCost;
        this.totalFlow = totalFlow;
        this.numberOfPaths = numberOfPaths;
        this.meanLength = meanLength;
        this.meanProportionalLength = meanProportionalLength;
    }

    public double getMinimumCost() {
        return minimumCost;
    }

    public int getTotalFlow() {
        return totalFlow;
    }

    public int getNumberOfPaths() {
        return numberOfPaths;
    }

    public double getMeanLength() {
        return meanLength;
    }

    public double getMeanProportionalLength() {
        return meanProportionalLength;
    }

    @Override
    public String toString() {
        return "Result {" +
                "\n  Minimum Cost (MC): " + minimumCost +
                ",\n  Total Flow (f): " + totalFlow +
                ",\n  Number of Paths: " + numberOfPaths +
                ",\n  Mean Length (ML): " + meanLength +
                ",\n  Mean Proportional Length (MPL): " + meanProportionalLength +
                "\n}";
    }
}
