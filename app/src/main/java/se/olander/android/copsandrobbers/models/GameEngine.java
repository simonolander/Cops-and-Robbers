package se.olander.android.copsandrobbers.models;

import android.graphics.Color;
import android.util.Log;

import java.util.Map;

import se.olander.android.copsandrobbers.views.GraphView;

public class GameEngine implements GraphView.OnNodeClickListener {
    
    private static final String TAG = GameEngine.class.getSimpleName();

    private final MiniMaxRobberAI robberAI;

    private GameState gameState;
    private Graph graph;
    private OnGameEventHandler onGameEventHandler;
    private int numberOfTurns;
    private long startTime;

    private Cop focusedCop;

    public GameEngine(Level level) {
        graph = new Graph(level);
        gameState = GameState.MOVE_COPS;
        robberAI = new MiniMaxRobberAI(graph);
        numberOfTurns = 0;
        startTime = System.currentTimeMillis();

        for (Cop cop : graph.getCops()) {
            cop.getCurrentNode().setHighlight(Color.GRAY);
        }
    }

    public void setOnGameEventHandler(OnGameEventHandler onGameEventHandler) {
        this.onGameEventHandler = onGameEventHandler;
    }

    @Override
    public void onNodeClick(Node node) {
        if (node.hasCop()) {
            Cop cop = node.getAnyCop();
            clearHighlights();
            cop.getCurrentNode().setHighlight(Color.BLUE);
            for (Node neighbour : graph.getNeighbours(cop.getCurrentNode())) {
                if (neighbour.hasCop()) {
                    neighbour.setHighlight(Color.GRAY);
                }
                else {
                    neighbour.setHighlight(Color.GREEN);
                }
            }
            focusedCop = cop;
        }
        else if (focusedCop != null && graph.areNeighbours(focusedCop.getCurrentNode(), node)) {
            clearHighlights();
            focusedCop.move(node);
            node.setHighlight(Color.BLUE);
            for (Node neighbour : graph.getNeighbours(node)) {
                if (neighbour.hasCop()) {
                    neighbour.setHighlight(Color.GRAY);
                }
                else {
                    neighbour.setHighlight(Color.GREEN);
                }
            }
            endOfTurn();
        }
        else {
            clearHighlights();
            focusedCop = null;
            node.setHighlight(Color.BLUE);
            for (Node neighbour : graph.getNeighbours(node)) {
                neighbour.setHighlight(Color.GRAY);
            }
        }
    }

    private void clearHighlights() {
        for (Node n : graph.getNodes()) {
            n.setHighlight(null);
        }
    }

    private void endOfTurn() {
        numberOfTurns += 1;
        killRobbers();
        if (allRobbersAreDead()) {
            victory();
        }
        else {
            moveRobbers();
        }
    }

    private void victory() {
        onGameEventHandler.victory();
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
            Robber robber = move.getKey();
            if (robber.isDead()) {
                continue;
            }

            Node node = move.getValue();
            robber.move(node);
        }
    }

    private boolean allRobbersAreDead() {
        for (Robber robber : graph.getRobbers()) {
            if (!robber.isDead()) {
                return false;
            }
        }
        return true;
    }

    public Graph getGraph() {
        return graph;
    }

    public int getNumberOfTurns() {
        return numberOfTurns;
    }

    public long getTotalTime() {
        return System.currentTimeMillis() - startTime;
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
