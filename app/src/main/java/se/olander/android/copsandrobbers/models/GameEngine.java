package se.olander.android.copsandrobbers.models;

import android.graphics.Color;

import se.olander.android.copsandrobbers.views.GraphView;

public class GameEngine implements GraphView.OnNodeClickListener {
    
    private static final String TAG = GameEngine.class.getSimpleName();

    private GameState gameState;
    private Graph graph;
    private OnGameEventHandler onGameEventHandler;

    private Cop focusedCop;

    public GameEngine(Level level) {
        graph = new Graph(level);
        gameState = GameState.MOVE_COPS;

        for (Cop cop : graph.getCops()) {
            cop.getCurrentNode().setHighlight(Color.BLUE);
        }
    }

    public void setOnGameEventHandler(OnGameEventHandler onGameEventHandler) {
        this.onGameEventHandler = onGameEventHandler;
    }

    @Override
    public void onNodeClick(Node node) {
        if (focusedCop != null) {
            Node focusedNode = focusedCop.getCurrentNode();
            if (graph.areNeighbours(focusedCop.getCurrentNode(), node)) {
                focusedNode.setHighlight(null);
                for (Node neighbour : graph.getNeighbours(focusedNode)) {
                    neighbour.setHighlight(null);
                }
                focusedCop.move(node);
                focusedNode = node;
                focusedNode.setHighlight(Color.GREEN);
                for (Node neighbour : graph.getNeighbours(focusedNode)) {
                    neighbour.setHighlight(Color.GREEN);
                }
                endOfTurn();
            }
        }
        else {
            if (node.getCops().size() == 1) {
                Cop cop = node.getAnyCop();
                cop.getCurrentNode().setHighlight(Color.GREEN);
                for (Node neighbour : graph.getNeighbours(cop.getCurrentNode())) {
                    neighbour.setHighlight(Color.GREEN);
                }
                focusedCop = cop;
            }
        }
    }

    private void endOfTurn() {
        moveRobbers();
    }

    private void moveRobbers() {
        for (Robber robber : graph.getRobbers()) {
            robber.move();
        }
    }

    private void onCopMove(Node from, Node to) {
//        if (to.isRobber()) {
//            onGameEventHandler.victory();
//        }
    }

    public Graph getGraph() {
        return graph;
    }

    public enum GameState {
        DIALOG,
        MOVE_COPS,
        MOVE_ROBBERS
    }

    public interface OnGameEventHandler {
        void victory();
    }
}
