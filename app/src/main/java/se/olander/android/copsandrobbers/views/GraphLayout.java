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

import se.olander.android.copsandrobbers.models.Edge;
import se.olander.android.copsandrobbers.models.Graph;
import se.olander.android.copsandrobbers.views.layout.CircleLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.GraphLayoutHelper;

public class GraphLayout extends RelativeLayout {
    private static final String TAG = GraphLayout.class.getSimpleName();

    private Graph<NodeView> graph;

    private Paint edgePaint;

    private GraphLayoutHelper graphLayoutHelper;

    public GraphLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(10);

        this.graph = new Graph<>(Arrays.asList(
            new NodeView(context),
            new NodeView(context),
            new NodeView(context),
            new NodeView(context),
            new NodeView(context),
            new NodeView(context)
        ));
        this.graph.setAdjacencyMatrix(new int[][] {
                new int[] {0, 1, 1, 1, 1, 1},
                new int[] {1, 0, 1, 1, 1, 1},
                new int[] {1, 1, 0, 1, 1, 1},
                new int[] {1, 1, 1, 0, 1, 1},
                new int[] {1, 1, 1, 1, 0, 1},
                new int[] {1, 1, 1, 1, 1, 0}
        });

        for (NodeView nodeView : graph.getNodes()) {
            addView(nodeView);
        }

        setWillNotDraw(false);
//        this.graphLayoutHelper = new ForceSpreadLayoutHelper(nodes, adjacencies);
        this.graphLayoutHelper = new CircleLayoutHelper(graph);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Edge edge : graph.getAllEdges()) {
            NodeView n1 = graph.getNode(edge.getN1());
            NodeView n2 = graph.getNode(edge.getN2());
            float cx1 = n1.getX() + n1.getWidth() / 2f;
            float cy1 = n1.getY() + n1.getHeight() / 2f;
            float cx2 = n2.getX() + n2.getWidth() / 2f;
            float cy2 = n2.getY() + n2.getHeight() / 2f;
            canvas.drawLine(cx1, cy1, cx2, cy2, edgePaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        graphLayoutHelper.layout(left, top, right, bottom);
    }
}
