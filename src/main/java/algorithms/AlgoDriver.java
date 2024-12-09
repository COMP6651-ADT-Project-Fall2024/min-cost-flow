package algorithms;

import graph.Graph;
import java.io.IOException;
import util.GraphUtils;

public class AlgoDriver {

    int source;
    int sink;
    int demand;

    public AlgoDriver() {}

    public AlgoDriver(int source, int sink, int demand) {
        this.source = source;
        this.sink = sink;
        this.demand = demand;
    }

    public AlgoResult primalDualDriver(String fileName) throws IOException {
        Graph graph = GraphUtils.loadGraph(fileName, this.source, this.sink);
        PrimalDual primalDual = new PrimalDual();
        return primalDual.primalDualAlgo(graph, this.source, this.sink, this.demand);
    }

    public AlgoResult primalDualDriver(String fileName, int s, int t, int d) {
        Graph graph;
        try {
            graph = GraphUtils.loadGraph(fileName, s, t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PrimalDual primalDual = new PrimalDual();
        return primalDual.primalDualAlgo(graph, s, t, d);
    }
}

