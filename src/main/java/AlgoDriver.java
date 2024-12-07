import graph.Graph;
import algorithms.*;
import java.io.IOException;
import util.GraphUtils;

public class AlgoDriver {
    int source;
    int sink;
    int demand;

    public AlgoDriver(int source, int sink, int demand) {
        this.source = source;
        this.sink = sink;
        this.demand = demand;
    }


    public void primalDualDriver(String fileName) throws IOException {
        Graph graph = GraphUtils.loadGraph(fileName, this.source, this.sink);
        PrimalDual primalDual = new PrimalDual();
        primalDual.primalDualAlgo(graph, this.source, this.sink, this.demand);

    }
}

