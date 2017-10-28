package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.olander.android.copsandrobbers.views.layout.CircleLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.GraphLayoutHelper;

public class GraphLayout extends RelativeLayout {
    private static final String TAG = "Graph";

    private List<Node> nodes;
    private List<List<Integer>> adjacencies;

    private Paint edgePaint;

    private GraphLayoutHelper graphLayoutHelper;

    public GraphLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(10);

        nodes = new ArrayList<>();
        adjacencies = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(0),
                Arrays.asList(0),
                Arrays.asList(0)
        );

        for (int i = 0; i < adjacencies.size(); i++) {
            nodes.add(new Node(context));
        }

        for (Node node : nodes) {
            addView(node);
        }

        setWillNotDraw(false);
//        this.graphLayoutHelper = new ForceSpreadLayoutHelper(nodes, adjacencies);
        this.graphLayoutHelper = new CircleLayoutHelper(nodes, adjacencies);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i1 = 0; i1 < adjacencies.size(); i1++) {
            Node n1 = nodes.get(i1);
            List<Integer> adjacency = adjacencies.get(i1);
            for (int i2 = 0; i2 < adjacency.size(); i2++) {
                if (i2 >= i1) {
                    continue;
                }
                Node n2 = nodes.get(i2);
                float cx1 = n1.getX() + n1.getWidth() / 2f;
                float cy1 = n1.getY() + n1.getHeight() / 2f;
                float cx2 = n2.getX() + n2.getWidth() / 2f;
                float cy2 = n2.getY() + n2.getHeight() / 2f;
                canvas.drawLine(cx1, cy1, cx2, cy2, edgePaint);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        graphLayoutHelper.layout(left, top, right, bottom);
    }
}
