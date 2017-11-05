package se.olander.android.copsandrobbers.views.layout;

import android.view.View;

import java.util.Random;

public class RandomLayoutHelper extends GraphLayoutHelper {

    private final Random random = new Random();

    @Override
    public void layout(int left, int top, int right, int bottom) {
        int paddingX = (int) ((right - left) * 0.1);
        int paddingY = (int) ((bottom - top) * 0.1);
        int minLeft = left + paddingX;
        int minTop = top + paddingY;
        int minRight = right - paddingX;
        int minBottom = bottom - paddingY;
        for (int i = 0; i < getNodes().size(); i++) {
            View node = getNodes().get(i);
            int x = minLeft + random.nextInt(minRight - minLeft);
            int y = minTop + random.nextInt(minBottom - minTop);
            centerLayout(node, x, y);
        }
    }
}
