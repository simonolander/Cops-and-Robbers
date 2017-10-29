package se.olander.android.copsandrobbers.views.layout;

import android.view.View;

public class CircleLayoutHelper extends GraphLayoutHelper {

    private float radiusMultiplier = 0.8f;

    @Override
    public void layout(int left, int top, int right, int bottom) {
        float width = right - left;
        float height = bottom - top;
        float size = Math.min(width, height);
        float radius = size / 2 * radiusMultiplier;
        float cx = (right + left) / 2;
        float cy = (bottom + top) / 2;

        float minLeft = width, minTop = height, minRight = width, minBottom = height;
        for (int i = 0; i < getNodes().size(); i++) {
            float angle = TAU * i / getNodes().size();
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);
            minLeft = Math.min(minLeft, x);
            minTop = Math.min(minTop, y);
            minRight = Math.min(minRight, width - x);
            minBottom = Math.min(minBottom, height - y);
        }

        float offsetX = (minRight - minLeft) / 2;
        float offsetY = (minBottom - minTop) / 2;

        for (int i = 0; i < getNodes().size(); i++) {
            View node = getNodes().get(i);
            float angle = TAU * i / getNodes().size();
            float x = (float) (cx + Math.cos(angle) * radius + offsetX);
            float y = (float) (cy + Math.sin(angle) * radius + offsetY);
            centerLayout(node, x, y);
        }
    }
}
