package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import se.olander.android.copsandrobbers.models.Node;

import static se.olander.android.copsandrobbers.views.ViewUtils.getMeasurement;

public class NodeView extends View implements Node.OnNodeChangeListener {

    private static final String TAG = NodeView.class.getSimpleName();

    private final static int RADIUS = 50;
    private final static int STROKE_WIDTH = 5;

//    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureListener());

    private final Paint fillPaint;
    private final Paint fillPaintFocused;
    private final Paint fillPaintRobber;
    private final Paint fillPaintCop;
    private final Paint focusPaint;
    private final Paint strokePaint;
    private final Paint textPaint;

    private Node node;

    private int radius;
    private int strokeWidth;

    private boolean focused;

    public NodeView(Context context) {
        this(context, null);
    }

    public NodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        fillPaintFocused = new Paint(fillPaint);
        fillPaintFocused.setColor(Color.GRAY);

        fillPaintRobber = new Paint(fillPaint);
        fillPaintRobber.setColor(0xffa03030);

        fillPaintCop = new Paint(fillPaint);
        fillPaintCop.setColor(Color.BLUE);

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setColor(0xff30a030);
        focusPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.OUTER));

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.BLACK);

        setRadius(RADIUS);
        setStrokeWidth(STROKE_WIDTH);

        setFocusable(true);
//        setClickable(true);
        setPadding(30, 30, 30, 30);
//
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                focused = !focused;
//                postInvalidate();
//            }
//        });
    }

    public int getRadius() {
        return radius;
    }

    public int getCenterX() {
        return getRadius() + strokeWidth + getPaddingLeft();
    }

    public int getCenterY() {
        return getRadius() + strokeWidth + getPaddingTop();
    }

    public boolean isInside(float x, float y) {
        float r = getRadius();
        float dx = getCenterX() - x;
        float dy = getCenterY() - y;
        return dx*dx + dy*dy <= r*r;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferredWidth = 2 * (radius + strokeWidth);
        int preferredHeight = 2 * (radius + strokeWidth);
        setMeasuredDimension(
                getMeasurement(widthMeasureSpec, preferredWidth),
                getMeasurement(heightMeasureSpec, preferredHeight)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float r = getRadius();
        float cx = getCenterX();
        float cy = getCenterY();

        canvas.drawCircle(cx, cy, r, getFillPaint());
        canvas.drawCircle(cx, cy, r, strokePaint);
        canvas.drawText("" + node.getIndex(), cx, cy, textPaint);

        if (node.getHighlight() != null) {
            focusPaint.setColor(node.getHighlight());
            canvas.drawCircle(cx, cy, r, focusPaint);
        }
    }

    private Paint getFillPaint() {

        if (node.getCops().size() > 0) {
            return fillPaintCop;
        }

        if (node.getRobbers().size() > 0) {
            return fillPaintRobber;
        }

        return fillPaint;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        focused = gainFocus;
        postInvalidate();
    }

    public void setNode(Node node) {
        if (this.node != null) {
            this.node.setOnNodeChangeListener(null);
        }
        this.node = node;
        this.node.setOnNodeChangeListener(this);
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void onNodeChange() {
        postInvalidate();
    }
}
