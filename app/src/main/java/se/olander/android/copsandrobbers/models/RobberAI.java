package se.olander.android.copsandrobbers.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class RobberAI {
    private final Graph graph;

    private GameState initialState;
    private Map<GameState, Collection<GameState>> gameTree;
    private Map<GameState, Collection<GameState>> reverseGameTree;
    private Map<GameState, Integer> values;
    private Collection<GameState> robberWinStates;

    public RobberAI(Graph graph) {
        this.graph = graph;
    }

    public void initialize() {
        initialState = computeCurrentGameState(true);
        computeGameTree(initialState);
        computeRobberWinStates();
        computeValues();
    }

    private void computeRobberWinStates() {
        Set<GameState> states = gameTree.keySet();

        robberWinStates = new HashSet<>();
        for (GameState state : states) {
            if (isRobberWinState(state, new HashSet<GameState>())) {
                robberWinStates.add(state);
            }
        }
    }

    private boolean isRobberWinState(GameState state, Set<GameState> visited) {
        visited.add(state);
        if (state.allRobbersAreDead()) {
            return false;
        }
        Collection<GameState> nextStates = gameTree.get(state);
        if (state.copMove) {
            for (GameState nextState : nextStates) {
                if (visited.contains(nextState)) {
                    continue;
                }

                if (!isRobberWinState(nextState, visited)) {
                    return false;
                }
            }
            return true;
        }
        else {
            for (GameState nextState : nextStates) {
                if (visited.contains(nextState)) {
                    continue;
                }

                if (isRobberWinState(nextState, visited)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void computeValues() {
        values = new HashMap<>();
        Queue<GameState> queue = new LinkedList<>();
        for (GameState state : gameTree.keySet()) {
            if (!state.copMove && state.allRobbersAreDead()) {
                values.put(state, 0);
                queue.add(state);
            }
        }

        int n = 0;
        while (!queue.isEmpty() && n < 100000) {
            GameState state = queue.poll();
            int value = values.get(state);
            Collection<GameState> parentStates = reverseGameTree.get(state);
            if (state.copMove) {
                for (GameState parentState : parentStates) {
                    int parentValue = values.containsKey(parentState)
                            ? values.get(parentState)
                            : 0;
                    values.put(parentState, Math.max(parentValue, value + 1));
                }
            }
            else {
                for (GameState parentState : parentStates) {
                    int parentValue = values.containsKey(parentState)
                            ? values.get(parentState)
                            : Integer.MAX_VALUE;
                    values.put(parentState, Math.min(parentValue, value + 1));
                }
            }
            queue.addAll(parentStates);
            n += 1;
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

    private void computeGameTree(GameState initialState) {
        gameTree = new HashMap<>();
        reverseGameTree = new HashMap<>();
        Queue<GameState> queue = new LinkedList<>();
        queue.add(initialState);
        while (!queue.isEmpty()) {
            GameState state = queue.poll();
            if (!gameTree.containsKey(state)) {
                Collection<GameState> nextStates = state.copMove
                    ? copActions(state)
                    : robberActions(state);
                gameTree.put(state, nextStates);
                for (GameState nextState : nextStates) {
                    Collection<GameState> parentStates;
                    if (!reverseGameTree.containsKey(nextState)) {
                        parentStates = new HashSet<>();
                        reverseGameTree.put(nextState, parentStates);
                    }
                    else {
                        parentStates = reverseGameTree.get(nextState);
                    }
                    parentStates.add(state);
                }
                queue.addAll(nextStates);
            }
        }
    }

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
        GameState state = computeCurrentGameState(false);
        Collection<GameState> nextStates = gameTree.get(state);
        GameState bestState = state;
        int bestValue = Integer.MIN_VALUE;
        for (GameState nextState : nextStates) {
            int value = values.get(nextState);
            if (value > bestValue) {
                bestState = nextState;
                bestValue = value;
            }
        }

        Map<Robber, Node> moves = new HashMap<>();
        for (int r = 0; r < graph.getRobbers().size(); r++) {
            Robber robber = graph.getRobbers().get(r);
            moves.put(robber, graph.getNode(bestState.robbers[r]));
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
