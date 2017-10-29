package se.olander.android.copsandrobbers.models;

public class Node {

    private int index;
    private boolean isRobber;
    private boolean isCop;

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
    }

    public boolean isCop() {
        return isCop;
    }

    public void setCop(boolean cop) {
        isCop = cop;
    }
}
