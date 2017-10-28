package se.olander.android.copsandrobbers.models;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Graph<T> {

    private final List<T> nodes;
    private final List<Set<Integer>> adjacencies;

    public Graph(List<T> nodes) {
        this.nodes = new ArrayList<>(nodes);
        this.adjacencies = new ArrayList<>(this.nodes.size());
        for (int i = 0; i < this.nodes.size(); i++) {
            adjacencies.add(new TreeSet<Integer>());
        }
    }

    public Graph<T> setAdjacencyMatrix(int[][] adjacencyMatrix) {
        if (adjacencyMatrix.length > nodes.size()) {
            throw new IllegalArgumentException("Adjacency matrix is too big: " + adjacencyMatrix.length + " > " + nodes.size());
        }
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            if (adjacencyMatrix[0].length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Adjacency matrix is not square: " + Arrays.toString(adjacencyMatrix));
            }
//            if (adjacencyMatrix[row][row] != 0) {
//                throw new IllegalArgumentException("Adjacency matrix's diagonal is not zeroes");
//            }
        }
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            for (int col = 0; col < adjacencyMatrix.length; col++) {
                if (adjacencyMatrix[row][col] != adjacencyMatrix[col][row]) {
                    throw new IllegalArgumentException(
                            "Adjacency matrix is not symmetric (" +
                                    "M[" + row + ", " + col + "] == " + adjacencyMatrix[row][col] + ") != (" +
                                    "M[" + col + ", " + row + "] == " + adjacencyMatrix[col][row] + "): " +
                                    Arrays.toString(adjacencyMatrix)
                    );
                }
            }
        }
        for (Set<Integer> adjacency : adjacencies) {
            adjacency.clear();
        }

        for (int row = 0; row < adjacencyMatrix.length; row++) {
            for (int col = 0; col < adjacencyMatrix.length; col++) {
                if (adjacencyMatrix[row][col] != 0) {
                    addEdge(row, col);
                }
            }
        }

        return this;
    }

    public T getNode(int n) {
        return nodes.get(n);
    }

    public List<T> getNodes() {
        return nodes;
    }

    public Graph<T> addEdge(int n1, int n2) {
        if (n1 != n2) {
            adjacencies.get(n1).add(n2);
            adjacencies.get(n2).add(n1);
        }
        return this;
    }

    public Collection<Integer> getNeighbours(int n) {
        return adjacencies.get(n);
    }

    public boolean areNeighbours(int n1, int n2) {
        return adjacencies.get(n1).contains(n2);
    }

    public Collection<Edge> getAllEdges() {
        Collection<Edge> edges = new ArrayList<>();
        for (int n1 = 0; n1 < adjacencies.size(); n1++) {
            for (int n2 : adjacencies.get(n1)) {
                if (n2 <= n1) {
                    continue;
                }
                edges.add(new Edge(n1, n2));
            }
        }
        return edges;
    }
}
