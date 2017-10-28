package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.support.v4.math.MathUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import se.olander.android.copsandrobbers.models.Graph;

public class ForceSpreadLayoutHelper extends GraphLayoutHelper {

    private final ArrayList<PointF> points;
    private final ArrayList<PointF> tempPoints;
    private Random random;

    public ForceSpreadLayoutHelper(Graph<? extends View> graph) {
        super(graph);
        this.random = new Random();
        this.points = new ArrayList<>(graph.getNodes().size());
        this.tempPoints = new ArrayList<>(this.points.size());
        for (int i = 0; i < this.points.size(); i++) {
            points.add(new PointF());
            tempPoints.add(new PointF());
        }
    }

    private void iterate(float left, float top, float right, float bottom) {
        float force = 0.1f;
        float maxDistance = Math.max(right - left, bottom - top);
        for (int i1 = 0; i1 < points.size(); i1++) {
            PointF p1 = points.get(i1);
            PointF po = tempPoints.get(i1);
            po.set(p1);
            for (int i2 = 0; i2 < tempPoints.size(); i2++) {
                if (i1 == i2) {
                    continue;
                }

                PointF p2 = points.get(i2);
                boolean adjacent = getGraph().areNeighbours(i1, i2);
                float f = adjacent ? force/2 : force;
                float distanceX = p1.x - p2.x;
                float distanceY = p1.y - p2.y;
                float dx = f / (distanceX / maxDistance);
                float dy = f / (distanceY / maxDistance);
                po.x += dx;
                po.y += dy;
            }

            po.x = (float) MathUtils.clamp(po.x, left + 10, right - 10);
            po.y = (float) MathUtils.clamp(po.y, top + 10, bottom - 10);

            float distanceLeft = p1.x - left;
            float distanceRight = right - p1.x;
            float distanceTop = p1.y - top;
            float distanceBottom = bottom - p1.y;

            po.x += force / (distanceLeft / maxDistance);
            po.x -= force / (distanceRight / maxDistance);
            po.y += force / (distanceTop / maxDistance);
            po.y -= force / (distanceBottom / maxDistance);
        }
        for (int i = 0; i < points.size(); i++) {
            points.get(i).set(tempPoints.get(i));
        }
    }

    private void reset(int left, int top, int right, int bottom) {
        for (PointF point : points) {
            float cx = (right - left) / 2;
            float cy = (bottom - top) / 2;
            point.set(cx + random.nextFloat()*10, cy + random.nextFloat()*10);
        }
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        reset(left, top, right, bottom);

        for (int i = 0; i < 1000; ++i) {
            iterate(left, top, right, bottom);
        }

        for (int i = 0; i < getGraph().getNodes().size(); i++) {
            View node = getGraph().getNode(i);
            PointF p = points.get(i);
            centerLayout(node, p);
        }
    }
}
