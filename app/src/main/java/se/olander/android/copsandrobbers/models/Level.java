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

    public static class Cop implements Serializable {
        private int startNode;

        public int getStartNode() {
            return startNode;
        }

        public void setStartNode(int startNode) {
            this.startNode = startNode;
        }
    }

    public static class Robber implements Serializable {
        private int startNode;

        public int getStartNode() {
            return startNode;
        }

        public void setStartNode(int startNode) {
            this.startNode = startNode;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        return title != null ? title.equals(level.title) : level.title == null;
    }

    @Override
    public int hashCode() {
        return title != null ? title.hashCode() : 0;
    }
}
