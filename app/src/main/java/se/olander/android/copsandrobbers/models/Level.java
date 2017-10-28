package se.olander.android.copsandrobbers.models;

import java.io.Serializable;
import java.util.List;

public class Level implements Serializable {

    private String title;
    private int numberOfNodes;
    private List<List<Integer>> edges;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        if (numberOfNodes != level.numberOfNodes) return false;
        if (title != null ? !title.equals(level.title) : level.title != null) return false;
        return edges != null ? edges.equals(level.edges) : level.edges == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + numberOfNodes;
        result = 31 * result + (edges != null ? edges.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Level{" +
                "title='" + title + '\'' +
                ", numberOfNodes=" + numberOfNodes +
                ", edges=" + edges +
                '}';
    }
}
