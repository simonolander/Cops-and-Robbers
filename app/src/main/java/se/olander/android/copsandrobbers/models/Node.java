package se.olander.android.copsandrobbers.models;

import java.util.Collection;
import java.util.HashSet;

public class Node {

    private final Collection<Robber> robbers = new HashSet<>();
    private final Collection<Cop> cops = new HashSet<>();

    private int index;
    private Integer highlight;
    private OnNodeChangeListener onNodeChangeListener;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Collection<Robber> getRobbers() {
        return robbers;
    }

    public void addRobber(Robber robber) {
        robbers.add(robber);
        notifyChange();
    }

    public void removeRobber(Robber robber) {
        robbers.remove(robber);
        notifyChange();
    }

    public Collection<Cop> getCops() {
        return cops;
    }

    public Cop getAnyCop() {
        return cops.iterator().next();
    }

    public void addCop(Cop cop) {
        cops.add(cop);
        notifyChange();
    }

    public void removeCop(Cop cop) {
        cops.remove(cop);
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

    public void setHighlight(Integer highlight) {
        this.highlight = highlight;
        notifyChange();
    }

    public boolean hasCop() {
        return !cops.isEmpty();
    }

    public Integer getHighlight() {
        return highlight;
    }

    public interface OnNodeChangeListener {
        void onNodeChange();
    }

    @Override
    public String toString() {
        return index + "";
    }
}
