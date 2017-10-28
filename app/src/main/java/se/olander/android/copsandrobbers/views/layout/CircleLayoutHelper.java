package se.olander.android.copsandrobbers.views.layout;

import android.view.View;

import java.util.List;

public class CircleLayoutHelper extends GraphLayoutHelper {

    private final List<? extends View> nodes;
    private final List<List<Integer>> adjacencies;
    private float radiusMultiplier = 0.8f;

    public CircleLayoutHelper(List<? extends View> nodes, List<List<Integer>> adjacencies) {
        this.nodes = nodes;
        this.adjacencies = adjacencies;
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        float width = right - left;
        float height = bottom - top;
        float size = Math.min(width, height);
        float radius = size / 2 * radiusMultiplier;
        float cx = (right + left) / 2;
        float cy = (bottom + top) / 2;
        for (int i = 0; i < nodes.size(); i++) {
            View node = nodes.get(i);
            float angle = TAU * i / nodes.size();
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);
            centerLayout(node, x, y);
        }
    }
}
