package se.olander.android.copsandrobbers.models;

import android.util.Log;

import java.util.Collection;
import java.util.List;

public class Robber {
    private static final String TAG = Robber.class.getSimpleName();

    private final Graph graph;

    private Node currentNode;
    private Node nextNode;

    public Robber(Level.Robber robber, Graph graph) {
        this.currentNode = graph.getNode(robber.getStartNode());
        this.graph = graph;

        this.currentNode.addRobber(this);
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void move() {
        Collection<Node> neighbours = graph.getNeighbours(currentNode);
        Log.d(TAG, "move neighbours: " + neighbours);
        ClosestNodeResponse currentClosestCopResponse = graph.getClosestCop(currentNode);
        ClosestNodeResponse bestNeighbour = null;
        for (Node neighbour : neighbours) {
            ClosestNodeResponse response = graph.getClosestCop(neighbour);
            if (bestNeighbour == null || response.distance > bestNeighbour.distance) {
                bestNeighbour = response;
            }
        }

        if (bestNeighbour != null && bestNeighbour.distance > currentClosestCopResponse.distance) {
            move(bestNeighbour.from);
        }
    }

    public void move(Node node) {
        currentNode.removeRobber(this);
        node.addRobber(this);
        currentNode = node;
    }
}
