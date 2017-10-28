package se.olander.android.copsandrobbers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import se.olander.android.copsandrobbers.models.Node;

import static se.olander.android.copsandrobbers.views.ViewUtils.getMeasurement;

public class NodeView extends View implements Node {

    private final static String TAG = "Node";

    private final static int RADIUS = 50;
    private final static int STROKE_WIDTH = 5;

//    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureListener());

    private final Paint fillPaint;
    private final Paint fillPaintFocused;
    private final Paint fillPaintRobber;
    private final Paint strokePaint;

    private int radius;
    private int strokeWidth;

    private int index;

    private boolean focused;
    private boolean isRobber;

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

        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(STROKE_WIDTH);

        setRadius(RADIUS);
        setStrokeWidth(STROKE_WIDTH);

        setFocusable(true);
        setClickable(true);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                focused = !focused;
                postInvalidate();
            }
        });
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void setRobber(boolean isRobber) {
        if (isRobber != this.isRobber) {
            this.isRobber = isRobber;
            postInvalidate();
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRadius() {
        return radius;
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
        float cx = r + strokeWidth;
        float cy = r + strokeWidth;
        canvas.drawCircle(cx, cy, r, getFillPaint());
        canvas.drawCircle(cx, cy, r, strokePaint);
    }

    private Paint getFillPaint() {
        if (isRobber) {
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
}
