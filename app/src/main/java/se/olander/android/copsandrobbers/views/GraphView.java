package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import se.olander.android.copsandrobbers.models.Edge;
import se.olander.android.copsandrobbers.models.Graph;
import se.olander.android.copsandrobbers.models.Node;
import se.olander.android.copsandrobbers.views.layout.ForceSpreadLayoutHelper;

public class GraphView extends RelativeLayout implements View.OnClickListener, Graph.OnGraphChangeListener, View.OnTouchListener {
    private static final String TAG = GraphView.class.getSimpleName();

    private Graph graph;

    private Paint edgePaint;
    private Paint loadingTextPaint;

    private ForceSpreadLayoutHelper graphLayoutHelper;

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


        setOnTouchListener(this);
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
        graphLayoutHelper.randomLayout(left, top, right, bottom);
        graphLayoutHelper.layout(left, top, right, bottom);
        initialized = true;
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
            nodeView.setOnTouchListener(new NodeTouchListener());
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: " + event);
        return true;
    }

    public void relayout() {
//        graphLayoutHelper.reset();
//        Runnable animator = new Runnable() {
//            @Override
//            public void run() {
//                float totalMovement = graphLayoutHelper.step(getLeft(), getTop(), getRight(), getBottom());
//                postInvalidate();
//
//                if (totalMovement > ForceSpreadLayoutHelper.MINIMUM_MOVEMENT_THRESHOLD) {
//                    long delayMillis = totalMovement > 10
//                        ? 10
//                        : (long) totalMovement;
//                    postDelayed(this, 10);
//                }
//                else {
//                    initialized = true;
//                }
//            }
//        };
//        initialized = false;
//        post(animator);
        graphLayoutHelper.layout(getLeft(), getTop(), getRight(), getBottom());
    }

    public interface OnNodeClickListener {
        void onNodeClick(Node node);
    }

    private class NodeTouchListener implements OnTouchListener {
        private float lastX, lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return v instanceof NodeView && onTouch((NodeView) v, event);
        }

        private boolean onTouch(NodeView v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!v.isInside(event.getX(), event.getY())) {
                        return false;
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX();
                    float newY = event.getRawY();
                    v.setLeft((int) (v.getLeft() + newX - lastX));
                    v.setTop((int) (v.getTop() + newY - lastY));
                    v.setRight(v.getLeft() + v.getMeasuredWidth() + v.getPaddingRight() + v.getPaddingLeft());
                    v.setBottom(v.getTop() + v.getMeasuredHeight() + v.getPaddingBottom() + v.getPaddingTop());
                    lastX = newX;
                    lastY = newY;
                    postInvalidate();
            }
            return true;
        }
    }
}
