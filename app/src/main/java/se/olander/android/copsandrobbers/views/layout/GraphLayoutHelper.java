package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.olander.android.copsandrobbers.models.Graph;

public abstract class GraphLayoutHelper {
    private static final String TAG = GraphLayoutHelper.class.getSimpleName();

    float TAU = (float) (Math.PI * 2);
    private final Random random = new Random();
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

    public void randomLayout(int left, int top, int right, int bottom) {
        int paddingX = (int) ((right - left) * 0.1);
        int paddingY = (int) ((bottom - top) * 0.1);
        int minLeft = left + paddingX;
        int minTop = top + paddingY;
        int minRight = right - paddingX;
        int minBottom = bottom - paddingY;
        for (int i = 0; i < nodes.size(); ++i) {
            float x = minLeft + random.nextInt(minRight - minLeft);
            float y = minTop + random.nextInt(minBottom - minTop);
            centerLayout(nodes.get(i), x, y);
        }
    }

    public abstract void layout(int left, int top, int right, int bottom);

    public void centerLayout(View view, float cx, float cy) {
        float left = cx - view.getMeasuredWidth() / 2;
        float top = cy - view.getMeasuredHeight() / 2;
        float right = cx + view.getMeasuredWidth() / 2;
        float bottom = cy + view.getMeasuredHeight() / 2;
        int layoutLeft = (int) left - view.getPaddingLeft();
        int layoutTop = (int) top - view.getPaddingTop();
        int layoutRight = (int) right + view.getPaddingRight();
        int layoutBottom = (int) bottom + view.getPaddingBottom();
        view.layout(
                layoutLeft,
                layoutTop,
                layoutRight,
                layoutBottom
        );
    }

    public void centerLayout(View node, PointF p) {
        centerLayout(node, p.x, p.y);
    }

    float getCenterX(int index) {
        View node = this.nodes.get(index);
        return node.getLeft() + node.getMeasuredWidth() / 2 + node.getPaddingLeft();
    }

    float getCenterY(int index) {
        View node = this.nodes.get(index);
        return node.getTop() + node.getMeasuredHeight() / 2 + node.getPaddingTop();
    }
}
