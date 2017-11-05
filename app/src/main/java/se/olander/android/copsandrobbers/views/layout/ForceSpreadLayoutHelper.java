package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.support.v4.math.MathUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import se.olander.android.copsandrobbers.models.Graph;

public class ForceSpreadLayoutHelper extends GraphLayoutHelper {

    public static final float ATTRACTION = 10f;
    public static final float REPULSION = 1;
    private static final int ITERATIONS = 1;

    private ArrayList<PointF> points;
    private ArrayList<PointF> tempPoints;
    private Random random;

    public ForceSpreadLayoutHelper() {
        this.random = new Random();
    }

    @Override
    public void setNodes(List<? extends View> nodes) {
        super.setNodes(nodes);
    }

    @Override
    public void setGraph(Graph graph) {
        super.setGraph(graph);
        this.points = new ArrayList<>();
        this.tempPoints = new ArrayList<>();
        for (int i = 0; i < graph.getNumberOfNodes(); i++) {
            points.add(new PointF());
            tempPoints.add(new PointF());
        }
        reset(0, 0, 700, 1800);
    }

    private void iterate(float left, float top, float right, float bottom) {
        float width = right - left;
        float height = bottom - top;
        float maxDistance = Math.max(width, height);
        for (int i1 = 0; i1 < points.size(); i1++) {
            PointF in = points.get(i1);
            PointF out = tempPoints.get(i1);
            out.set(in);
            for (int i2 = 0; i2 < tempPoints.size(); i2++) {
                if (i1 == i2) {
                    continue;
                }

                PointF other = points.get(i2);

                float dx = other.x - in.x;
                float dy = other.y - in.y;
                dx /= maxDistance;
                dy /= maxDistance;
                float attractionX = 0;
                float attractionY = 0;

                if (getGraph().areNeighbours(i1, i2)) {
                    attractionX = ATTRACTION * dx;
                    attractionY = ATTRACTION * dy;
                }

                float distanceSquared = dx*dx + dy*dy;
                float distance = (float) Math.sqrt(distanceSquared);
                float repulsionX = - REPULSION / distanceSquared * dx / distance;
                float repulsionY = - REPULSION / distanceSquared * dy / distance;
                float forceX = attractionX + repulsionX;
                float forceY = attractionY + repulsionY;

                out.x += forceX;
                out.y += forceY;
            }

            float distanceLeft = (in.x - left) / width;
            float distanceRight = (right - in.x) / width;
            float distanceTop = (in.y - top) / height;
            float distanceBottom = (bottom - in.y) / height;

            float repulsionLeft = REPULSION / (distanceLeft * distanceLeft);
            float repulsionRight = REPULSION / (distanceRight * distanceRight);
            float repulsionTop = REPULSION / (distanceTop * distanceTop);
            float repulsionBottom = REPULSION / (distanceBottom * distanceBottom);

            out.x += repulsionLeft;
            out.x -= repulsionRight;
            out.y += repulsionTop;
            out.y -= repulsionBottom;

            out.x = MathUtils.clamp(out.x, left + 100, right - 100);
            out.y = MathUtils.clamp(out.y, top + 100, bottom - 100);
        }

        for (int i = 0; i < points.size(); i++) {
            points.get(i).set(tempPoints.get(i));
        }
    }

    private void reset(int left, int top, int right, int bottom) {
        float cx = (right - left) / 2;
        float cy = (bottom - top) / 2;
        int paddingX = (int) ((right - left) * 0.1);
        int paddingY = (int) ((bottom - top) * 0.1);
        int minLeft = left + paddingX;
        int minTop = top + paddingY;
        int minRight = right - paddingX;
        int minBottom = bottom - paddingY;
        for (PointF point : points) {
            point.set(
                    minLeft + random.nextInt(minRight - minLeft),
                    minTop + random.nextInt(minBottom - minTop)
            );
        }
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        for (int i = 0; i < ITERATIONS; ++i) {
            iterate(left, top, right, bottom);
        }

        for (int i = 0; i < getNodes().size(); i++) {
            View node = getNodes().get(i);
            PointF p = points.get(i);
            centerLayout(node, p);
        }
    }
}
