package se.olander.android.copsandrobbers.models;

import java.util.List;

public class ClosestNodeResponse {
    public int distance = Integer.MAX_VALUE;
    public List<Node> path;
    public Node from;
    public Node to;
}
