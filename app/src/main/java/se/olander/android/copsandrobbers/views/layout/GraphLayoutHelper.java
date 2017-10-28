package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.view.View;

import se.olander.android.copsandrobbers.models.Graph;

public abstract class GraphLayoutHelper {
    float TAU = (float) (Math.PI * 2);
    private Graph<? extends View> graph;

    public GraphLayoutHelper(Graph<? extends View> graph) {
        this.graph = graph;
    }

    public Graph<? extends View> getGraph() {
        return graph;
    }

    public void setGraph(Graph<? extends View> graph) {
        this.graph = graph;
    }

    public abstract void layout(int left, int top, int right, int bottom);

    protected void centerLayout(View view, float cx, float cy) {
        float left = cx - view.getWidth() / 2;
        float top = cy - view.getHeight() / 2;
        float right = cx + view.getWidth() / 2;
        float bottom = cy + view.getHeight() / 2;
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
