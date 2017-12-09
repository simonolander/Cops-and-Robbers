package se.olander.android.copsandrobbers.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class RobberAI {
    private static final String TAG = RobberAI.class.getSimpleName();
    private static final double ROBBER_WIN_VALUE = 100000;
    private static final double COP_WIN_VALUE = -100000;

    private final Graph graph;

    private GameState initialState;
    private Map<GameState, Collection<GameState>> gameGraph;
    private Map<GameState, Collection<GameState>> reverseGameGraph;
    private Map<GameState, Double> values;
    private Collection<GameState> robberWinStates;

    public RobberAI(Graph graph) {
        this.graph = graph;
    }

    public void initialize() {
        initialState = computeCurrentGameState(true);
        computeGameGraph(initialState);
        computeRobberWinStates();
        computeValues();
    }

    private void computeRobberWinStates() {
        Set<GameState> states = gameGraph.keySet();

        HashSet<GameState> copWinStates = new HashSet<>();
        for (GameState state : states) {
            if (state.allRobbersAreDead()) {
                copWinStates.add(state);
            }
        }
        boolean additionalCopWinStates;
        do {
            additionalCopWinStates = false;
            for (GameState state : states) {
                if (copWinStates.contains(state)) {
                    continue;
                }
                if (state.copMove) {
                    for (GameState nextState : gameGraph.get(state)) {
                        if (copWinStates.contains(nextState)) {
                            copWinStates.add(state);
                            additionalCopWinStates = true;
                        }
                    }
                }
            }
            for (GameState state : states) {
                if (copWinStates.contains(state)) {
                    continue;
                }
                if (!state.copMove) {
                    boolean allNextStatesAreCopWin = true;
                    for (GameState nextState : gameGraph.get(state)) {
                        if (!copWinStates.contains(nextState)) {
                            allNextStatesAreCopWin = false;
                            break;
                        }
                    }
                    if (allNextStatesAreCopWin) {
                        copWinStates.add(state);
                        additionalCopWinStates = true;
                    }
                }
            }
        } while (additionalCopWinStates);

        robberWinStates = new HashSet<>();
        for (GameState state : states) {
            if (!copWinStates.contains(state)) {
                robberWinStates.add(state);
            }
        }
        for (GameState robberWinState : robberWinStates) {
            Log.d(TAG, "computeRobberWinStates: " + robberWinState);
        }
    }

    private void computeValues() {
        values = new HashMap<>();
        Set<GameState> states = gameGraph.keySet();
        List<GameState> copStates = new ArrayList<>();
        List<GameState> robberStates = new ArrayList<>();
        for (GameState state : states) {
            if (state.copMove) {
                copStates.add(state);
            }
            else {
                robberStates.add(state);
            }
        }

        for (int i = 0; i < 10; i++) {
            for (GameState state : robberStates) {
                if (state.allRobbersAreDead()) {
                    values.put(state, COP_WIN_VALUE);
                }
                else if (robberWinStates.contains(state)) {
                    values.put(state, ROBBER_WIN_VALUE);
                }
                else {
                    double max = COP_WIN_VALUE;
                    for (GameState nextState : gameGraph.get(state)) {
                        double value = values.containsKey(nextState)
                                ? values.get(nextState)
                                : 0;
                        if (value > max) {
                            max = value;
                            if (max == ROBBER_WIN_VALUE) {
                                break;
                            }
                        }
                    }
                    values.put(state, max);
                }
            }
            for (GameState state : copStates) {
                if (state.allRobbersAreDead()) {
                    values.put(state, COP_WIN_VALUE);
                }
                else if (robberWinStates.contains(state)) {
                    values.put(state, ROBBER_WIN_VALUE);
                }
                else {
                    double sum = 0;
                    Collection<GameState> nextStates = gameGraph.get(state);
                    for (GameState nextState : nextStates) {
                        double value = values.containsKey(nextState)
                                ? values.get(nextState)
                                : 0;
                        sum += value;
                    }
                    double min = sum / nextStates.size();
                    values.put(state, min);
                }
            }
        }
    }

    private double computeValue(GameState state, int moves, Set<GameState> visited) {
        if (values.containsKey(state)) {
            return values.get(state);
        }

        if (visited.contains(state)) {
            values.put(state, (double) moves);
            return 0;
        }
        visited.add(state);

        if (robberWinStates.contains(state)) {
            values.put(state, ROBBER_WIN_VALUE);
            return ROBBER_WIN_VALUE;
        }

        if (state.allRobbersAreDead()) {
            values.put(state, COP_WIN_VALUE);
            return COP_WIN_VALUE;
        }

        Collection<GameState> nextStates = gameGraph.get(state);
        if (state.copMove) {
            double sum = 0;
            for (GameState nextState : nextStates) {
                double value = computeValue(nextState, moves + 1, visited);
                sum += value;
            }
            double min = sum / nextStates.size();
            values.put(state, min);
            return min;
        }
        else {
            double max = COP_WIN_VALUE;
            for (GameState nextState : nextStates) {
                double value = computeValue(nextState, moves + 1, visited);
                if (value > max) {
                    max = value;
                    if (max == COP_WIN_VALUE) {
                        break;
                    }
                }
            }
            values.put(state, max);
            return max;
        }
    }

    private GameState computeCurrentGameState(boolean copMove) {
        int[] cops = new int[graph.getCops().size()];
        for (int i = 0; i < graph.getCops().size(); i++) {
            cops[i] = graph.getCops().get(i).getCurrentNode().getIndex();
        }
        int[] robbers = new int[graph.getRobbers().size()];
        for (int i = 0; i < graph.getRobbers().size(); i++) {
            robbers[i] = graph.getRobbers().get(i).getCurrentNode().getIndex();
        }
        boolean[] dead = new boolean[graph.getRobbers().size()];
        for (int i = 0; i < robbers.length; i++) {
            if (graph.getRobber(i).isDead()) {
                dead[i] = true;
                continue;
            }
            for (int cop : cops) {
                if (robbers[i] == cop) {
                    dead[i] = true;
                    break;
                }
            }
        }

        return new GameState(
                cops,
                robbers,
                dead,
                copMove
        );
    }

    private void computeGameGraph(GameState initialState) {
        gameGraph = new HashMap<>();
        reverseGameGraph = new HashMap<>();
        Queue<GameState> queue = new LinkedList<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            GameState state = queue.poll();
            if (!gameGraph.containsKey(state)) {
                Collection<GameState> nextStates = state.copMove
                    ? copActions(state)
                    : robberActions(state);
                gameGraph.put(state, nextStates);
                for (GameState nextState : nextStates) {
                    Collection<GameState> parentStates;
                    if (!reverseGameGraph.containsKey(nextState)) {
                        parentStates = new HashSet<>();
                        reverseGameGraph.put(nextState, parentStates);
                    }
                    else {
                        parentStates = reverseGameGraph.get(nextState);
                    }
                    parentStates.add(state);
                }
                queue.addAll(nextStates);
            }
        }
    }

//    private void computeGameTree(GameState initialState) {
//        gameTree = new HashMap<>();
//        reverseGameGraph = new HashMap<>();
//        Queue<GameState> queue = new LinkedList<>();
//        queue.add(initialState);
//        while (!queue.isEmpty()) {
//            GameState state = queue.poll();
//            Collection<GameState> nextStates = gameGraph.get(state);
//            HashSet<GameState> prunedNextStates = new HashSet<>();
//            for (GameState nextState : nextStates) {
//                if (gameTree.containsKey(nextState) || state.equals(nextState)) {
//                    continue;
//                }
//                prunedNextStates.add(nextState);
//                queue.add(nextState);
//            }
//            gameTree.put(state, prunedNextStates);
//        }
//    }

    private Collection<GameState> copActions(GameState currentGameState) {
        HashSet<GameState> nextStates = new HashSet<>();
        ArrayList<int[]> nextCopPositions = calculateNextPositions(currentGameState.cops, null);
        for (int[] nextCopPosition : nextCopPositions) {
            nextStates.add(currentGameState.moveCops(nextCopPosition));
        }
        return nextStates;
    }

    private Collection<GameState> robberActions(GameState currentGameState) {
        HashSet<GameState> nextStates = new HashSet<>();
        ArrayList<int[]> nextPositions = calculateNextPositions(currentGameState.robbers, currentGameState.dead);
        for (int[] nextPosition : nextPositions) {
            nextStates.add(currentGameState.moveRobbers(nextPosition));
        }
        return nextStates;
    }

    private ArrayList<int[]> calculateNextPositions(int[] currentPosition, boolean[] dead) {
        ArrayList<ArrayList<Integer>> moves = new ArrayList<>();
        for (int i = 0; i < currentPosition.length; i++) {
            ArrayList<Integer> move = new ArrayList<>();
            move.add(currentPosition[i]);
            if (dead == null || !dead[i]) {
                move.addAll(graph.getNeighbours(currentPosition[i]));
            }
            moves.add(move);
        }

        int[] maxes = new int[moves.size()];
        for (int i = 0; i < moves.size(); i++) {
            maxes[i] = moves.get(i).size();
        }
        int[] choices = new int[moves.size()];

        ArrayList<int[]> nextPositions = new ArrayList<>();
        do {
            int[] position = new int[currentPosition.length];
            for (int i = 0; i < choices.length; i++) {
                position[i] = moves.get(i).get(choices[i]);
            }
            nextPositions.add(position);
        } while (increment(choices, maxes));

        return nextPositions;
    }

    private boolean increment(int[] choices, int[] maxes) {
        for (int i = 0; i < choices.length; i++) {
            choices[i] += 1;
            if (choices[i] == maxes[i]) {
                choices[i] = 0;
            }
            else {
                return true;
            }
        }
        return false;
    }

    public Map<Robber, Node> calculateMoves() {
        Log.d(TAG, "calculateMoves: begin");
        GameState state = computeCurrentGameState(false);
        Collection<GameState> nextStates = gameGraph.get(state);
        GameState bestState = null;
        Double bestNegamax = null;
        for (GameState nextState : nextStates) {
            double value = values.get(nextState);
            Log.d(TAG, "calculateMoves value: " + nextState + ", " + robberWinStates.contains(nextState) + ", " + value);
            if (bestNegamax == null || bestNegamax < value) {
                bestState = nextState;
                bestNegamax = value;
            }
        }

        HashMap<Robber, Node> moves = new HashMap<>();
        if (bestState == null) {
            return moves;
        }

        for (int ri = 0; ri < bestState.robbers.length; ri++) {
            moves.put(graph.getRobber(ri), graph.getNode(bestState.robbers[ri]));
        }
        return moves;
    }

    private static class GameState {
        final int[] cops;
        final int[] robbers;
        final boolean[] dead;
        final boolean copMove;

        GameState(int[] cops, int[] robbers, boolean[] dead, boolean copMove) {
            this.cops = cops;
            this.robbers = robbers;
            this.dead = dead;
            this.copMove = copMove;
        }

        boolean allRobbersAreDead() {
            for (boolean d : dead) {
                if (!d) {
                    return false;
                }
            }
            return true;
        }

        GameState moveCops(int[] cops) {
            return move(cops, robbers, false);
        }

        GameState moveRobbers(int[] robbers) {
            return move(cops, robbers, true);
        }

        GameState move(int[] cops, int[] robbers, boolean copMove) {
            boolean[] dead = Arrays.copyOf(this.dead, this.dead.length);
            for (int c = 0; c < cops.length; c++) {
                for (int r = 0; r < robbers.length; r++) {
                    if (cops[c] == robbers[r]) {
                        dead[r] = true;
                    }
                }
            }
            boolean lost = true;
            for (boolean b : dead) {
                if (!b) {
                    lost = false;
                    break;
                }
            }
            return new GameState(
                    cops,
                    robbers,
                    dead,
                    copMove
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GameState gameState = (GameState) o;

            if (copMove != gameState.copMove) return false;
            if (!Arrays.equals(cops, gameState.cops)) return false;
            if (!Arrays.equals(robbers, gameState.robbers)) return false;
            return Arrays.equals(dead, gameState.dead);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(cops);
            result = 31 * result + Arrays.hashCode(robbers);
            result = 31 * result + Arrays.hashCode(dead);
            result = 31 * result + (copMove ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "{" +
                    "cops=" + Arrays.toString(cops) +
                    ", robbers=" + Arrays.toString(robbers) +
                    ", copMove=" + copMove +
                    '}';
        }
    }
}
