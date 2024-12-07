package util;

import graph.Graph;

import java.io.IOException;

public class GraphUtils {
    public static Graph loadGraph(String fileName,int source, int sink) throws IOException {
        Graph  graph = new Graph();
        graph.loadGraphFromFile(fileName, source, sink);
        return graph;
    }
}
