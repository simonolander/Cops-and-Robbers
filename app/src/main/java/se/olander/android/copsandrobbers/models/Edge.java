package se.olander.android.copsandrobbers.models;

public class Edge {
    private final int n1, n2;

    public Edge(int n1, int n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public int getN1() {
        return n1;
    }

    public int getN2() {
        return n2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (n1 != edge.n1) return false;
        return n2 == edge.n2;
    }

    @Override
    public int hashCode() {
        int result = n1;
        result = 31 * result + n2;
        return result;
    }
}
