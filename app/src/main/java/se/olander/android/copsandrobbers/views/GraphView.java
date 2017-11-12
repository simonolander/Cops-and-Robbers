package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import se.olander.android.copsandrobbers.models.Edge;
import se.olander.android.copsandrobbers.models.Graph;
import se.olander.android.copsandrobbers.models.Node;
import se.olander.android.copsandrobbers.views.layout.CircleLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.ForceSpreadLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.GraphLayoutHelper;
import se.olander.android.copsandrobbers.views.layout.RandomLayoutHelper;

public class GraphView extends RelativeLayout implements View.OnClickListener, Graph.OnGraphChangeListener {
    private static final String TAG = GraphView.class.getSimpleName();

    private Graph graph;

    private Paint edgePaint;
    private Paint loadingTextPaint;

    private GraphLayoutHelper graphLayoutHelper;

    private OnNodeClickListener onNodeClickListener;
    private ArrayList<NodeView> nodes;

    private boolean initialized;

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        edgePaint = new Paint();
        edgePaint.setColor(Color.BLACK);
        edgePaint.setStrokeWidth(10);
        edgePaint.setAntiAlias(true);

        loadingTextPaint = new Paint();
        loadingTextPaint.setColor(Color.GRAY);
        loadingTextPaint.setTextAlign(Paint.Align.CENTER);
        loadingTextPaint.setTextSize(200);

        setWillNotDraw(false);
        graphLayoutHelper = new ForceSpreadLayoutHelper();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawEdges(canvas);
        if (!initialized) {
            canvas.drawColor(Color.BLUE, PorterDuff.Mode.OVERLAY);
            drawLoadingText(canvas);
        }
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

    private void drawLoadingText(Canvas canvas) {
        String text = "Loading";
        float measureText = loadingTextPaint.measureText(text);



        canvas.drawText(text, getWidth() / 2, getHeight() / 2, loadingTextPaint);
    }

    @Override
    protected void onLayout(boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (false && this.graphLayoutHelper instanceof ForceSpreadLayoutHelper) {
            final ForceSpreadLayoutHelper layoutHelper = (ForceSpreadLayoutHelper) this.graphLayoutHelper;
            layoutHelper.reset(left, top, right, bottom);
            initialized = false;
            post(new Runnable() {
                @Override
                public void run() {
                    float totalMovement = layoutHelper.step(left, top, right, bottom);
                    postInvalidate();

                    if (totalMovement > ForceSpreadLayoutHelper.MINIMUM_MOVEMENT_THRESHOLD) {
                        long delayMillis = totalMovement > 10
                                ? 10
                                : (long) totalMovement;
                        postDelayed(this, delayMillis);
                    }
                    else {
                        initialized = true;
                    }
                }
            });
        }
        else {
            initialized = true;
            graphLayoutHelper.layout(left, top, right, bottom);
        }
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
    }

    @Override
    public void onGraphChange() {
        postInvalidate();
    }

    public interface OnNodeClickListener {
        void onNodeClick(Node node);
    }
}
