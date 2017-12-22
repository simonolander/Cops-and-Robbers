package se.olander.android.copsandrobbers.models;

import android.graphics.Color;

import java.util.Map;

import se.olander.android.copsandrobbers.views.GraphView;

public class GameEngine implements GraphView.OnNodeClickListener {
    
    private static final String TAG = GameEngine.class.getSimpleName();

    private final MiniMaxRobberAI robberAI;

    private GameState gameState;
    private Graph graph;
    private OnGameEventHandler onGameEventHandler;

    private Cop focusedCop;

    public GameEngine(Level level) {
        graph = new Graph(level);
        gameState = GameState.MOVE_COPS;
        robberAI = new MiniMaxRobberAI(graph);
//        robberAI.initialize();

        for (Cop cop : graph.getCops()) {
            cop.getCurrentNode().setHighlight(Color.GRAY);
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
                for (Node n : graph.getNodes()) {
                    n.setHighlight(null);
                }
                focusedCop.move(node);
                focusedNode = node;
                focusedNode.setHighlight(Color.BLUE);
                for (Node neighbour : graph.getNeighbours(focusedNode)) {
                    neighbour.setHighlight(Color.GREEN);
                }
                endOfTurn();
            }
        }
        else {
            if (node.getCops().size() == 1) {
                Cop cop = node.getAnyCop();
                cop.getCurrentNode().setHighlight(Color.BLUE);
                for (Node neighbour : graph.getNeighbours(cop.getCurrentNode())) {
                    neighbour.setHighlight(Color.GREEN);
                }
                focusedCop = cop;
            }
        }
    }

    private void endOfTurn() {
        killRobbers();
        moveRobbers();
    }

    private void killRobbers() {
        for (Cop cop : graph.getCops()) {
            Node node = cop.getCurrentNode();
            for (Robber robber : node.getRobbers()) {
                robber.setDead(true);
            }
        }
    }

    private void moveRobbers() {
        Map<Robber, Node> moves = robberAI.calculateMoves();
        for (Map.Entry<Robber, Node> move : moves.entrySet()) {
            move.getKey().move(move.getValue());
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
