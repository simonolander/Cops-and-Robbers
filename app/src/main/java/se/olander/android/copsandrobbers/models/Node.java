package se.olander.android.copsandrobbers.models;

public class Node {

    private int index;
    private boolean isRobber;
    private boolean isCop;
    private boolean isFocused;
    private OnNodeChangeListener onNodeChangeListener;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isRobber() {
        return isRobber;
    }

    public void setRobber(boolean robber) {
        isRobber = robber;
        notifyChange();
    }

    public boolean isCop() {
        return isCop;
    }

    public void setCop(boolean cop) {
        isCop = cop;
        notifyChange();
    }

    public void setOnNodeChangeListener(OnNodeChangeListener onNodeChangeListener) {
        this.onNodeChangeListener = onNodeChangeListener;
    }

    private void notifyChange() {
        if (onNodeChangeListener != null) {
            onNodeChangeListener.onNodeChange();
        }
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        isFocused = focused;
        notifyChange();
    }

    public interface OnNodeChangeListener {
        void onNodeChange();
    }
}
