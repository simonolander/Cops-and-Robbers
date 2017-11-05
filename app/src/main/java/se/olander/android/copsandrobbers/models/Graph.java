package se.olander.android.copsandrobbers.models;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class Graph {

    private final Set<OnGraphChangeListener> onGraphChangeListeners = new HashSet<>();
    private final List<Node> nodes = new ArrayList<>();
    private final List<Set<Integer>> adjacencies = new ArrayList<>();

    public Graph() {
        this(new ArrayList<Node>());
    }

    public Graph(List<Node> nodes) {
        this.nodes.addAll(nodes);
        for (int i = 0; i < nodes.size(); i++) {
            adjacencies.add(new TreeSet<Integer>());
        }
    }

    public Graph(Level level) {
        for (int i = 0; i < level.getNumberOfNodes(); i++) {
            Node node = new Node();
            node.setIndex(i);
            if (level.getCops() != null) {
                for (int copIndex = 0; copIndex < level.getCops().size(); copIndex++) {
                    Cop cop = level.getCops().get(copIndex);
                    if (cop.getStartNode() == i) {
                        node.setCop(true);
                        break;
                    }
                }
            }
            if (level.getRobbers() != null) {
                for (int robberIndex = 0; robberIndex < level.getRobbers().size(); robberIndex++) {
                    Robber robber = level.getRobbers().get(robberIndex);
                    if (robber.getStartNode() == i) {
                        node.setRobber(true);
                        break;
                    }
                }
            }
            nodes.add(node);
        }
        for (int i = 0; i < nodes.size(); i++) {
            adjacencies.add(new TreeSet<Integer>());
        }
        new ArrayList<Node>(level.getNumberOfNodes());
        for (int n1 = 0; n1 < level.getEdges().size(); n1++) {
            for (Integer n2 : level.getEdges().get(n1)) {
                addEdge(n1, n2);
            }
        }
    }

    public void setAdjacencyMatrix(int[][] adjacencyMatrix) {
        if (adjacencyMatrix.length > adjacencies.size()) {
            throw new IllegalArgumentException("Adjacency matrix is too big: " + adjacencyMatrix.length + " > " + adjacencies.size());
        }
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            if (adjacencyMatrix[0].length != adjacencyMatrix.length) {
                throw new IllegalArgumentException("Adjacency matrix is not square: " + Arrays.toString(adjacencyMatrix));
            }
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

        clearEdges();
        for (int row = 0; row < adjacencyMatrix.length; row++) {
            for (int col = 0; col < adjacencyMatrix.length; col++) {
                if (adjacencyMatrix[row][col] != 0) {
                    addEdge(row, col);
                }
            }
        }
    }

    private void clearEdges() {
        for (Set<Integer> adjacency : adjacencies) {
            adjacency.clear();
        }
    }

    public void randomizeEdges() {
        Random random = new Random();
        clearEdges();
        for (int n1 = 0; n1 < adjacencies.size(); n1++) {
            for (int n2 = n1 + 1; n2 < adjacencies.size(); n2++) {
                if (random.nextBoolean()) {
                    addEdge(n1, n2);
                }
            }
        }
    }

    public Graph addEdge(int n1, int n2) {
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

    public boolean areNeighbours(Node n1, Node n2) {
        return areNeighbours(n1.getIndex(), n2.getIndex());
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

    public int getNumberOfNodes() {
        return adjacencies.size();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void notifyChanged() {
        for (OnGraphChangeListener listener : onGraphChangeListeners) {
            listener.onGraphChange();
        }
    }

    public Node getFocusedNode() {
        for (Node node : nodes) {
            if (node.isFocused()) {
                return node;
            }
        }
        return null;
    }

    public void addOnGraphChangeListener(OnGraphChangeListener listener) {
        this.onGraphChangeListeners.add(listener);
    }

    public void removeOnGraphChangeListener(OnGraphChangeListener listener) {
        this.onGraphChangeListeners.remove(listener);
    }

    public interface OnGraphChangeListener {
        void onGraphChange();
    }

    public List<Node> path(Node from, Node to) {
        if (from.getIndex() == to.getIndex()) {
            return new ArrayList<>();
        }

        Integer[] previous = new Integer[nodes.size()];
        boolean[] visited = new boolean[nodes.size()];
        Queue<Integer> queue = new LinkedList<>();
        queue.add(from.getIndex());
        while (!queue.isEmpty()) {
            Integer n = queue.poll();
            visited[n] = true;
            if (to.getIndex() == n) {
                break;
            }
            for (Integer neighbour : getNeighbours(n)) {
                if (!visited[neighbour]) {
                    queue.add(neighbour);
                }
            }
        }

        LinkedList<Node> path = new LinkedList<>();
        Integer c = to.getIndex();
        while (c != null) {
            path.addFirst(nodes.get(c));
            c = previous[c];
        }
        return path;
    }

    public ClosestNodeResponse getClosestCop(Node node) {
        ClosestNodeResponse response = new ClosestNodeResponse();
        for (Node n : nodes) {
            if (n.isCop()) {
                int distance = path(node, n).size();
                if (distance < response.distance) {
                    response.distance = distance;
                    response.node = n;
                }
            }
        }

        return response;
    }
}
