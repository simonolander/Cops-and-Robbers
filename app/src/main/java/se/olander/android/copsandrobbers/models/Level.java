package se.olander.android.copsandrobbers.models;

import java.io.Serializable;
import java.util.List;

public class Level implements Serializable {

    private String title;
    private int numberOfNodes;
    private List<List<Integer>> edges;
    private List<Robber> robbers;
    private List<Cop> cops;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public List<List<Integer>> getEdges() {
        return edges;
    }

    public void setEdges(List<List<Integer>> edges) {
        this.edges = edges;
    }

    public List<Robber> getRobbers() {
        return robbers;
    }

    public void setRobbers(List<Robber> robbers) {
        this.robbers = robbers;
    }

    public List<Cop> getCops() {
        return cops;
    }

    public void setCops(List<Cop> cops) {
        this.cops = cops;
    }
}
