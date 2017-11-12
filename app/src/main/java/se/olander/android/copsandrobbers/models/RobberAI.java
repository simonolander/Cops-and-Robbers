package se.olander.android.copsandrobbers.models;

import java.util.ArrayList;
import java.util.Arrays;

public class RobberAI {
    private static final int SEARCH_DEPTH = 10;
    private static final int[] PRIMES = new int[] {
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71
    };

    private final Graph graph;

    public RobberAI(Graph graph) {
        this.graph = graph;
    }

    public GameState calculateInitialGameState() {
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
            for (int i1 = 0; i1 < cops.length; i1++) {
                if (robbers[i] == cops[i]) {
                    dead[i] = true;
                    break;
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
        GameState gameState = new GameState(
                cops,
                robbers,
                dead,
                lost,
                true
        );

        return gameState;
    }

    public void calculateGameTree() {
        GameState initialGameState = calculateInitialGameState();

        
    }

    public ArrayList<GameState> copActions(GameState currentGameState) {
        ArrayList<GameState> nextStates = new ArrayList<>();
        ArrayList<int[]> nextCopPositions = calculateNextPositions(currentGameState.cops, null);
        for (int[] nextCopPosition : nextCopPositions) {
            nextStates.add(currentGameState.moveCops(nextCopPosition));
        }
        return nextStates;
    }

    public ArrayList<GameState> robberActions(GameState currentGameState) {
        ArrayList<GameState> nextStates = new ArrayList<>();
        ArrayList<int[]> nextPositions = calculateNextPositions(currentGameState.robbers, currentGameState.dead);
        for (int[] nextPosition : nextPositions) {
            nextStates.add(currentGameState.moveRobbers(nextPosition));
        }
        return nextStates;
    }

    public ArrayList<int[]> calculateNextPositions(int[] currentPosition, boolean[] dead) {
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

    private long toHash(int[] positions) {
        long ans = 1;
        for (int i = 0; i < positions.length; i++) {
            for (int k = 0; k < positions[i]; k++) {
                ans *= PRIMES[i];
            }
        }
        return ans;
    }

    private static int hashcode(int[] cops, int[] robbers) {
        return Arrays.hashCode(cops) * 31 + Arrays.hashCode(robbers);
    }

    private static class GameTree {

    }

    private static class GameState {
        final int[] cops;
        final int[] robbers;
        final boolean[] dead;
        final boolean lost;
        final boolean copMove;

        public GameState(int[] cops, int[] robbers, boolean[] dead, boolean lost, boolean copMove) {
            this.cops = cops;
            this.robbers = robbers;
            this.dead = dead;
            this.lost = lost;
            this.copMove = copMove;
        }

        public GameState moveCops(int[] cops) {
            return move(cops, robbers, false);
        }

        public GameState moveRobbers(int[] robbers) {
            return move(cops, robbers, true);
        }

        public GameState move(int[] cops, int[] robbers, boolean copMove) {
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
                    lost,
                    copMove
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GameState gameState = (GameState) o;

            if (lost != gameState.lost) return false;
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
            result = 31 * result + (lost ? 1 : 0);
            result = 31 * result + (copMove ? 1 : 0);
            return result;
        }
    }
}
