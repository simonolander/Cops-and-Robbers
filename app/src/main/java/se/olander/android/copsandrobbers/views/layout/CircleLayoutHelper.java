package se.olander.android.copsandrobbers.views.layout;

import android.view.View;

import java.util.List;

import se.olander.android.copsandrobbers.models.Graph;

public class CircleLayoutHelper extends GraphLayoutHelper {

    private float radiusMultiplier = 0.8f;

    public CircleLayoutHelper(Graph<? extends View> graph) {
        super(graph);
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        float width = right - left;
        float height = bottom - top;
        float size = Math.min(width, height);
        float radius = size / 2 * radiusMultiplier;
        float cx = (right + left) / 2;
        float cy = (bottom + top) / 2;
        for (int i = 0; i < getGraph().getNodes().size(); i++) {
            View node = getGraph().getNodes().get(i);
            float angle = TAU * i / getGraph().getNodes().size();
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);
            centerLayout(node, x, y);
        }
    }
}