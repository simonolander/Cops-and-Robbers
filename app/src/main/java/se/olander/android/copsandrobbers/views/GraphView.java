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

import se.olander.android.copsandrobbers.models.Edge;
import se.olander.android.copsandrobbers.models.Graph;
import se.olander.android.copsandrobbers.models.Node;
import se.olander.android.copsandrobbers.views.layout.CircleLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.ForceSpreadLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.GraphLayoutHelper;

public class GraphView extends RelativeLayout implements View.OnClickListener, Graph.OnGraphChangeListener {
    private static final String TAG = GraphView.class.getSimpleName();

    private Graph graph;

    private Paint edgePaint;

    private GraphLayoutHelper graphLayoutHelper;

    private OnNodeClickListener onNodeClickListener;
    private ArrayList<NodeView> nodes;

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(10);
        edgePaint.setAntiAlias(true);

        setWillNotDraw(false);
        graphLayoutHelper = new ForceSpreadLayoutHelper();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawEdges(canvas);
    }

    private void drawEdges(Canvas canvas) {
        for (Edge edge : graph.getAllEdges()) {
            NodeView n1 = nodes.get(edge.getN1());
            NodeView n2 = nodes.get(edge.getN2());
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
        if (view instanceof NodeView) {
            if (this.onNodeClickListener != null) {
                this.onNodeClickListener.onNodeClick(((NodeView) view).getNode());
            }
        }
    }

    public void setOnNodeClickListener(OnNodeClickListener listener) {
        this.onNodeClickListener = listener;
    }

    public void setGraph(Graph graph) {
        nodes = new ArrayList<>();
        removeAllViews();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            Node node = graph.getNodes().get(i);
            NodeView nodeView = new NodeView(getContext());
            nodeView.setNode(node);
            nodeView.setOnClickListener(this);
            addView(nodeView);
            nodes.add(nodeView);
        }

        graphLayoutHelper.setGraph(graph);
        graphLayoutHelper.setNodes(nodes);

        if (this.graph != null) {
            this.graph.removeOnGraphChangeListener(this);
        }

        graph.addOnGraphChangeListener(this);
        this.graph = graph;

        postInvalidate();
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
                postInvalidate();
                postDelayed(this, 100);
            }
        });
    }

    @Override
    public void onGraphChange() {
        postInvalidate();
    }

    public interface OnNodeClickListener {
        void onNodeClick(Node node);
    }
}
