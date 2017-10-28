package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.List;

import se.olander.android.copsandrobbers.models.Edge;
import se.olander.android.copsandrobbers.models.Graph;
import se.olander.android.copsandrobbers.models.Node;
import se.olander.android.copsandrobbers.views.layout.CircleLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.GraphLayoutHelper;

public class GraphLayout extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = GraphLayout.class.getSimpleName();

    private Graph<NodeView> graph;

    private Paint edgePaint;

    private GraphLayoutHelper graphLayoutHelper;

    private int currentRobberNodeIndex;

    public GraphLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(10);

        List<NodeView> nodes = Arrays.asList(
                new NodeView(context),
                new NodeView(context),
                new NodeView(context),
                new NodeView(context),
                new NodeView(context),
                new NodeView(context),
                new NodeView(context)
        );

        for (int i = 0; i < nodes.size(); i++) {
            NodeView node = nodes.get(i);
            node.setOnClickListener(this);
            node.setIndex(i);
            node.setRobber(this.currentRobberNodeIndex == i);
        }

        this.graph = new Graph<>(nodes);
        this.graph.setAdjacencyMatrix(new int[][] {
                new int[] {0, 0, 1, 0, 1, 0, 0},
                new int[] {0, 0, 0, 1, 0, 1, 0},
                new int[] {1, 0, 0, 0, 1, 0, 0},
                new int[] {0, 1, 0, 0, 0, 1, 1},
                new int[] {1, 0, 1, 0, 0, 0, 0},
                new int[] {0, 1, 0, 1, 0, 0, 0},
                new int[] {0, 0, 0, 1, 0, 0, 0}
        });
        this.graph.randomizeEdges();

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
        drawEdges(canvas);
    }

    private void drawEdges(Canvas canvas) {
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

    @Override
    public void onClick(View view) {
        if (view instanceof Node) {
            onClick((Node) view);
        }
    }

    private void onClick(Node node) {
        int nodeIndex = node.getIndex();

        if (nodeIndex == currentRobberNodeIndex) {
            return;
        }

        if (!graph.areNeighbours(nodeIndex, currentRobberNodeIndex)) {
            return;
        }

        NodeView currentRobberNode = graph.getNode(currentRobberNodeIndex);
        currentRobberNode.setRobber(false);
        node.setRobber(true);
        currentRobberNodeIndex = nodeIndex;
    }
}
