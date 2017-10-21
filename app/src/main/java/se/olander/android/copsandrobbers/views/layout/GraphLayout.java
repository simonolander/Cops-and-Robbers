package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.view.View;

public abstract class GraphLayout {
    float TAU = (float) (Math.PI * 2);

    public abstract void layout(int left, int top, int right, int bottom);

    protected void centerLayout(View node, float cx, float cy) {
        float left = cx - node.getWidth() / 2;
        float top = cy - node.getHeight() / 2;
        float right = cx + node.getWidth() / 2;
        float bottom = cy + node.getHeight() / 2;
        node.layout(
                (int) left,
                (int) top,
                (int) right,
                (int) bottom
        );
    }

    protected void centerLayout(View node, PointF p) {
        centerLayout(node, p.x, p.y);
    }
}
