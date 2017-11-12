package se.olander.android.copsandrobbers.models;

public class Cop {
    private final Graph graph;

    private Node currentNode;
    private Node nextNode;

    public Cop(Level.Cop cop, Graph graph) {
        this.currentNode = graph.getNode(cop.getStartNode());
        this.graph = graph;

        this.currentNode.addCop(this);
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void move(Node node) {
        currentNode.removeCop(this);
        node.addCop(this);
        currentNode = node;
    }
}
