import graph.Graph;
import algorithms.*;
import java.io.IOException;

public class AlgoDriver {
    int source;
    int sink;
    int demand;

    public AlgoDriver(int source, int sink, int demand) {
        this.source = source;
        this.sink = sink;
        this.demand = demand;
    }

    private Graph loadGraph(String fileName) throws IOException {
        Graph  graph = new Graph();
        graph.loadGraphFromFile(fileName, this.source, this.sink);
        return graph;
    }
    public void primalDualDriver(String fileName) throws IOException {
        Graph graph = loadGraph(fileName);
        PrimalDual primalDual = new PrimalDual();
        primalDual.primalDualAlgo(graph, this.source, this.sink, this.demand);

    }
}

