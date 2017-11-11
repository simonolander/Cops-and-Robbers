package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import se.olander.android.copsandrobbers.models.Graph;

public abstract class GraphLayoutHelper {
    private static final String TAG = GraphLayoutHelper.class.getSimpleName();

    float TAU = (float) (Math.PI * 2);
    private Graph graph;
    private List<? extends View> nodes;

    public GraphLayoutHelper() {
        graph = new Graph();
        nodes = new ArrayList<>();
    }

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public List<? extends View> getNodes() {
        return nodes;
    }

    public void setNodes(List<? extends View> nodes) {
        this.nodes = nodes;
    }

    public abstract void layout(int left, int top, int right, int bottom);

    protected void centerLayout(View view, float cx, float cy) {
        float left = cx - view.getMeasuredWidth() / 2;
        float top = cy - view.getMeasuredHeight() / 2;
        float right = cx + view.getMeasuredWidth() / 2;
        float bottom = cy + view.getMeasuredHeight() / 2;
        view.layout(
                (int) left - view.getPaddingLeft(),
                (int) top - view.getPaddingTop(),
                (int) right + view.getPaddingRight(),
                (int) bottom + view.getPaddingBottom()
        );
    }

    protected void centerLayout(View node, PointF p) {
        centerLayout(node, p.x, p.y);
    }
}
