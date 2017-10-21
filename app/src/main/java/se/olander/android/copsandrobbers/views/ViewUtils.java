package se.olander.android.copsandrobbers.views;

import android.graphics.PointF;
import android.view.View;

import java.util.List;

public class ViewUtils {
    private ViewUtils() { }

    public static int getMeasurement(int measureSpec, int preferredSize) {
        int size = View.MeasureSpec.getSize(measureSpec);
        switch (View.MeasureSpec.getMode(measureSpec)) {
            case View.MeasureSpec.AT_MOST:
                return Math.min(size, preferredSize);
            case View.MeasureSpec.EXACTLY:
                return size;
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return preferredSize;
        }
    }

    public static float distanceSquared(PointF p1, PointF p2) {
        return distanceSquared(p1.x, p1.y, p2.x, p2.y);
    }

    private static float distanceSquared(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}
