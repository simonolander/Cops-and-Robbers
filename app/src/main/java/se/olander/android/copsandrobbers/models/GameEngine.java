package se.olander.android.copsandrobbers.models;

import java.util.List;

import se.olander.android.copsandrobbers.views.GraphView;

public class GameEngine implements GraphView.OnNodeClickListener {

    private GameState gameState;
    private Graph graph;
    private List<Cop> cops;
    private List<Robber> robbers;
    private OnGameEventHandler onGameEventHandler;

    public GameEngine(Level level) {
        graph = new Graph(level);

        cops = level.getCops();
        robbers = level.getRobbers();
        gameState = GameState.MOVE_COPS;
    }

    public void setOnGameEventHandler(OnGameEventHandler onGameEventHandler) {
        this.onGameEventHandler = onGameEventHandler;
    }

    @Override
    public void onNodeClick(Node node) {
        if (gameState == GameState.MOVE_COPS) {

            Node focusedNode = graph.getFocusedNode();
            if (focusedNode == null) {
                node.setFocused(true);
            }
            else if (focusedNode.isCop()) {
                if (graph.areNeighbours(node, focusedNode)) {
                    focusedNode.setCop(false);
                    focusedNode.setFocused(false);
                    node.setCop(true);
                    node.setFocused(true);
                    onCopMove(focusedNode, node);
                }
                else {
                    focusedNode.setFocused(false);
                    node.setFocused(true);
                }
            }
            else {
                focusedNode.setFocused(false);
                node.setFocused(true);
            }
        }

        moveRobbers();
    }

    private void moveRobbers() {
        for (Node node : graph.getNodes()) {
            if (node.isRobber()) {

            }
        }
    }

    private void onCopMove(Node from, Node to) {
        if (to.isRobber()) {
            onGameEventHandler.victory();
        }
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
