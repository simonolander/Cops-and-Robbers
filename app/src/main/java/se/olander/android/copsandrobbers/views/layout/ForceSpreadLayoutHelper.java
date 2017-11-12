package se.olander.android.copsandrobbers.views.layout;

import android.graphics.PointF;
import android.view.View;

import java.util.List;
import java.util.Random;

import se.olander.android.copsandrobbers.models.Graph;

public class ForceSpreadLayoutHelper extends GraphLayoutHelper {

    private static final String TAG = ForceSpreadLayoutHelper.class.getSimpleName();

    private static final float ATTRACTION_CONSTANT = 1.5f;
    private static final float REPULSION_CONSTANT = 10000000f;
    public static final int MAX_ITERATIONS = 1000;
    private static final float PROXIMITY_MIN_VALUE = 1.0f;
    private static final float DEFAULT_SPRING_LENGTH = 200f;
    private static final float DAMPING = 0.3f;
    public static final float MINIMUM_MOVEMENT_THRESHOLD = 0.1f;
    private static final float PADDING = 50f;

    private PointF tempPoint;
    private PointF[] points;
    private PointF[] velocities;
    private Random random;
    private int iteration;

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
        this.tempPoint = new PointF();
        this.points = new PointF[graph.getNumberOfNodes()];
        this.velocities = new PointF[graph.getNumberOfNodes()];
        for (int i = 0; i < graph.getNumberOfNodes(); i++) {
            points[i] = new PointF();
            velocities[i] = new PointF();
        }
    }

    private float calculateLength(PointF p) {
        return (float) Math.sqrt(p.x * p.x + p.y * p.y);
    }

    private PointF calculateDirection(PointF p1, PointF p2, PointF out) {
        out.set(p2);
        out.x -= p1.x;
        out.y -= p1.y;
        float length = calculateLength(out);
        out.x /= length;
        out.y /= length;
        return out;
    }

    private float calculateProximity(PointF p1, PointF p2) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        return (float) Math.max(Math.sqrt(dx*dx + dy*dy), PROXIMITY_MIN_VALUE);
    }

    private PointF calculateRepulsion(PointF p1, PointF p2, PointF out) {
        float proximity = calculateProximity(p1, p2);
        float force = REPULSION_CONSTANT / (proximity * proximity);
        calculateDirection(p1, p2, out);
        out.negate();
        out.x *= force;
        out.y *= force;
        return out;
    }

    private PointF calculateAttraction(PointF p1, PointF p2, float springLength, PointF out) {
        float proximity = calculateProximity(p1, p2);
        float force = ATTRACTION_CONSTANT * Math.max(proximity - springLength, 0);
        calculateDirection(p1, p2, out);
        out.x *= force;
        out.y *= force;
        return out;
    }

    private float iterate(float left, float top, float right, float bottom) {
        float width = right - left;
        float height = bottom - top;
        float maxDistance = Math.max(width, height);
        float minDistance = Math.min(width, height);
        float springLength = (float) Math.max(minDistance / Math.sqrt(getGraph().getNumberOfNodes()), DEFAULT_SPRING_LENGTH);
        float totalMovement = 0;
        for (int i1 = 0; i1 < points.length; i1++) {
            PointF p1 = points[i1];
            PointF velocity = velocities[i1];
            for (int i2 = 0; i2 < points.length; i2++) {
                if (i1 == i2) {
                    continue;
                }

                PointF p2 = points[i2];

                calculateRepulsion(p1, p2, tempPoint);

                velocity.x += tempPoint.x;
                velocity.y += tempPoint.y;

                if (getGraph().areNeighbours(i1, i2)) {
                    calculateAttraction(p1, p2, springLength, tempPoint);
                    velocity.x += tempPoint.x;
                    velocity.y += tempPoint.y;
                }
            }

            float proximityLeft = p1.x - left;
            float proximityRight = right - p1.x;
            float proximityTop = p1.y - top;
            float proximityBottom = bottom - p1.y;

            if (proximityLeft > PADDING) {
                velocity.x += REPULSION_CONSTANT / (proximityLeft * proximityLeft);
            }
            else {
                velocity.x += 10;
            }

            if (proximityRight > PADDING) {
                velocity.x -= REPULSION_CONSTANT / (proximityRight * proximityRight);
            }
            else {
                velocity.x -= 10;
            }

            if (proximityTop > PADDING) {
                velocity.y += REPULSION_CONSTANT / (proximityTop * proximityTop);
            }
            else {
                velocity.y += 10;
            }

            if (proximityBottom > PADDING) {
                velocity.y -= REPULSION_CONSTANT / (proximityBottom * proximityBottom);
            }
            else {
                velocity.y -= 10;
            }

            velocity.x *= DAMPING;
            velocity.y *= DAMPING;

            float v = calculateLength(velocity);
            if (v > 50) {
                velocity.x *= 50 / v;
                velocity.y *= 50 / v;
            }
        }

        for (int i = 0; i < points.length; i++) {
            PointF point = points[i];
            PointF velocity = velocities[i];
            point.x += velocity.x;
            point.y += velocity.y;
            totalMovement += calculateLength(velocity);
        }

        return totalMovement;
    }

    public void reset(int left, int top, int right, int bottom) {
        int paddingX = (int) ((right - left) * 0.1);
        int paddingY = (int) ((bottom - top) * 0.1);
        int minLeft = left + paddingX;
        int minTop = top + paddingY;
        int minRight = right - paddingX;
        int minBottom = bottom - paddingY;
        for (int i = 0; i < points.length; ++i) {
            points[i].set(
                    minLeft + random.nextInt(minRight - minLeft),
                    minTop + random.nextInt(minBottom - minTop)
            );
            velocities[i].set(0, 0);
        }
        iteration = 0;
    }

    public float step(int left, int top, int right, int bottom) {
        float totalMovement = iterate(left, top, right, bottom);
        iteration += 1;

        for (int i = 0; i < getNodes().size(); i++) {
            View node = getNodes().get(i);
            PointF p = points[i];
            centerLayout(node, p);
        }

        if (iteration > MAX_ITERATIONS) {
            totalMovement = 0;
        }

        return totalMovement;
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        reset(left, top, right, bottom);
        float totalMovement = iterate(left, top, right, bottom);
        for (int i = 0; i < MAX_ITERATIONS && totalMovement > MINIMUM_MOVEMENT_THRESHOLD; ++i) {
            totalMovement = iterate(left, top, right, bottom);
        }

        for (int i = 0; i < getNodes().size(); i++) {
            View node = getNodes().get(i);
            PointF p = points[i];
            centerLayout(node, p);
        }
    }
}
