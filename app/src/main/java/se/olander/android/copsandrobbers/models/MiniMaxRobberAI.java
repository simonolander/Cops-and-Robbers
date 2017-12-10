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

public class MiniMaxRobberAI {
    private static final String TAG = MiniMaxRobberAI.class.getSimpleName();
    private static final double ROBBER_WIN_VALUE = 100000;
    private static final double COP_WIN_VALUE = -100000;

    private final Graph graph;

    public MiniMaxRobberAI(Graph graph) {
        this.graph = graph;
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

    private Set<GameState> computeNextStates(GameState state) {
        HashSet<GameState> nextStates = new HashSet<>();
        if (state.copMove) {
            ArrayList<int[]> nextCopPositions = computeNextPositions(state.cops, null);
            for (int[] nextCopPosition : nextCopPositions) {
                nextStates.add(state.moveCops(nextCopPosition));
            }
        }
        else {
            ArrayList<int[]> nextPositions = computeNextPositions(state.robbers, state.dead);
            for (int[] nextPosition : nextPositions) {
                nextStates.add(state.moveRobbers(nextPosition));
            }
        }
        return nextStates;
    }

    private ArrayList<int[]> computeNextPositions(int[] currentPosition, boolean[] dead) {
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

    private double minimax(GameState state, int depth, Map<GameState, Double> memo) {
        if (memo.containsKey(state)) {
            return memo.get(state);
        }

        if (state.allRobbersAreDead()) {
            return COP_WIN_VALUE;
        }

        if (depth == 0) {
            return 0;
        }

        Set<GameState> nextStates = computeNextStates(state);
        double best;
        if (state.copMove) {
            double sum = 0;
            for (GameState nextState : nextStates) {
                sum += minimax(nextState, depth - 1, memo);
            }
            best = sum / nextStates.size();
        }
        else {
            best = COP_WIN_VALUE;
            for (GameState nextState : nextStates) {
                best = Math.max(best, minimax(nextState, depth - 1, memo));
            }
        }

        memo.put(state, best);
        return best;
    }

    public Map<Robber, Node> calculateMoves() {
        Log.d(TAG, "calculateMoves: begin");
        GameState state = computeCurrentGameState(false);
        Set<GameState> nextStates = computeNextStates(state);
        GameState bestState = null;
        Double best = null;
        HashMap<GameState, Double> memo = new HashMap<>();
        for (GameState nextState : nextStates) {
            double value = minimax(nextState, 8, memo);
            Log.d(TAG, "calculateMoves value: " + nextState + ", " + value);
            if (best == null || best < value) {
                bestState = nextState;
                best = value;
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
