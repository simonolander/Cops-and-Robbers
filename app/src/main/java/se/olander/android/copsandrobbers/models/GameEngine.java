package se.olander.android.copsandrobbers.models;

import java.util.List;

import se.olander.android.copsandrobbers.views.GraphLayout;

public class GameEngine implements GraphLayout.OnNodeClickListener {

    private GameState gameState;
    private Graph graph;
    private List<Cop> cops;
    private List<Robber> robbers;

    public GameEngine(Level level) {
        graph = new Graph(level);

        cops = level.getCops();
        robbers = level.getRobbers();
        gameState = GameState.MOVE_COPS;
    }

    @Override
    public void onNodeClick(Node node) {
        if (gameState == GameState.MOVE_COPS) {

            Node focusedNode = graph.getFocusedNode();
            if (focusedNode == null) {
                node.setFocused(true);
                return;
            }

            if (focusedNode.isCop()) {
                if (graph.areNeighbours(node, focusedNode)) {
                    focusedNode.setCop(false);
                    focusedNode.setFocused(false);
                    node.setCop(true);
                    node.setFocused(true);
                    return;
                }
                else {
                    focusedNode.setFocused(false);
                    node.setFocused(true);
                    return;
                }
            }
            else {
                focusedNode.setFocused(false);
                node.setFocused(true);
                return;
            }
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
}
