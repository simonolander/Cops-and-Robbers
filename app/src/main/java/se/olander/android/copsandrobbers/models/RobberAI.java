package se.olander.android.copsandrobbers.models;

import android.support.annotation.NonNull;
import android.util.Log;

import java.math.BigInteger;
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
import java.util.TreeMap;
import java.util.TreeSet;

public class RobberAI {
    private static final String TAG = RobberAI.class.getSimpleName();
    private static final double ROBBER_WIN_VALUE = 100000;
    private static final double COP_WIN_VALUE = -100000;

    private final Graph graph;

    private GameState initialState;
    private Map<GameState, Collection<GameState>> gameGraph;
    private Map<GameState, Double> values;
    private Collection<GameState> robberWinStates;

    public RobberAI(Graph graph) {
        this.graph = graph;
    }

    public void initialize() {
        generateAllStates();
        initialState = computeCurrentGameState(true);
//        computeGameGraph(initialState);
//        computeRobberWinStates();
//        computeValues();
    }

    private BigInteger factorial(int n) {
        BigInteger ans = BigInteger.ONE;
        for (int i = 1; i <= n; i++) {
            ans = ans.multiply(BigInteger.valueOf(i));
        }
        return ans;
    }

    private void generateAllStates() {
        int numCops = graph.getCops().size();
        int numRobbers = graph.getRobbers().size();
        int numNodes = graph.getNumberOfNodes();
        BigInteger numberOfStates = BigInteger.valueOf(2)
                .multiply(factorial(numCops + numNodes - 1))
                .divide(factorial(numCops))
                .divide(factorial(numNodes - 1))
                .multiply(factorial(numRobbers + numNodes - 1))
                .divide(factorial(numRobbers))
                .divide(factorial(numNodes - 1));
        BigInteger naiveNumberOfStates = BigInteger.valueOf(2)
                .multiply(BigInteger.valueOf(numNodes).pow(numCops))
                .multiply(BigInteger.valueOf(numNodes).pow(numRobbers))
                .multiply(BigInteger.valueOf(2).pow(numRobbers));
        Log.d(TAG, "generateAllStates numCops: " + numCops);
        Log.d(TAG, "generateAllStates numRobbers: " + numRobbers);
        Log.d(TAG, "generateAllStates numNodes: " + numNodes);
        Log.d(TAG, "generateAllStates numberOfStates: " + numberOfStates);
        Log.d(TAG, "generateAllStates naiveNumberOfStates: " + naiveNumberOfStates);
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
//        for (GameState robberWinState : robberWinStates) {
//            Log.d(TAG, "computeRobberWinStates: " + robberWinState);
//        }
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
        gameGraph = new TreeMap<>();
        TreeSet<GameState> visited = new TreeSet<>();
        Queue<GameState> queue = new LinkedList<>();
        queue.add(initialState);
        visited.add(initialState);
        while (!queue.isEmpty()) {
            GameState state = queue.poll();
            List<GameState> nextStates = computeNextStates(state);
            gameGraph.put(state, nextStates);
            if (gameGraph.size() % 1000 == 0){
                Log.d(TAG, "computeGameGraph number of states: " + gameGraph.size());
                Log.d(TAG, "computeGameGraph objects in queue: " + queue.size());
            }
            for (GameState nextState : nextStates) {
                if (visited.add(nextState)) {
                    queue.add(nextState);
                }
            }
        }
    }

    private List<GameState> computeNextStates(GameState state) {
        ArrayList<GameState> nextStates = new ArrayList<>();
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
        nextStates.trimToSize();
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

    private static class GameState implements Comparable<GameState> {
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

        @Override
        public int compareTo(@NonNull GameState o) {
            if (copMove != o.copMove) {
                return Boolean.compare(copMove, o.copMove);
            }

            if (cops.length != o.cops.length) {
                return Integer.compare(cops.length, o.cops.length);
            }

            if (robbers.length != o.robbers.length) {
                return Integer.compare(robbers.length, o.robbers.length);
            }

            if (dead.length != o.dead.length) {
                return Integer.compare(dead.length, o.dead.length);
            }

            for (int i = 0; i < cops.length; ++i) {
                if (cops[i] != o.cops[i]) {
                    return Integer.compare(cops[i], o.cops[i]);
                }
            }
            for (int i = 0; i < robbers.length; ++i) {
                if (robbers[i] != o.robbers[i]) {
                    return Integer.compare(robbers[i], o.robbers[i]);
                }
            }
            for (int i = 0; i < dead.length; ++i) {
                if (dead[i] != o.dead[i]) {
                    return Boolean.compare(dead[i], o.dead[i]);
                }
            }

            return 0;
        }


    }
}
