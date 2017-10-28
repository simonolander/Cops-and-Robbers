package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import se.olander.android.copsandrobbers.models.Edge;
import se.olander.android.copsandrobbers.models.Graph;
import se.olander.android.copsandrobbers.models.Level;
import se.olander.android.copsandrobbers.models.Node;
import se.olander.android.copsandrobbers.views.layout.CircleLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.GraphLayoutHelper;

public class GraphLayout extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = GraphLayout.class.getSimpleName();

    private Graph<NodeView> graph;

    private Paint edgePaint;

    private GraphLayoutHelper graphLayoutHelper;

    private int currentRobberNodeIndex;
    private Level level;

    public GraphLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(10);
        edgePaint.setAntiAlias(true);

        setWillNotDraw(false);
        graphLayoutHelper = new CircleLayoutHelper(new Graph<View>());
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

    public void setLevel(Level level) {
        this.level = level;
        List<NodeView> nodes = new ArrayList<>();
        for (int i = 0; i < level.getNumberOfNodes(); i++) {
            NodeView nodeView = new NodeView(getContext());
            nodeView.setOnClickListener(this);
            nodeView.setIndex(i);
            nodeView.setRobber(this.currentRobberNodeIndex == i);
            addView(nodeView);
            nodes.add(nodeView);
        }

        graph = new Graph<>(nodes);
        for (int n1 = 0; n1 < level.getEdges().size(); n1++) {
            for (Integer n2 : level.getEdges().get(n1)) {
                graph.addEdge(n1, n2);
            }
        }

        graphLayoutHelper.setGraph(graph);

        postInvalidate();
    }
}
