package rph.reversi.players;
 
import java.util.ArrayList;
import java.util.List;
import rph.reversi.Player;
 
/**
 * Reversi player using MinMax + AlfaBeta algorythms and simple evaluation function
 *
 * @author Matej Uhrin
 */
public class IliuOLD extends Player {
 
    private int myColor;
 
    @Override
    protected String getName() {
        return "Goofy";
    }
 
    @Override
    protected void startGame(int myColor, int opponentColor, int[][] playground) {
        //only myColor is used
        this.myColor = myColor;
    }
 
    /**
     * Takes int[][] playground as parameter
     * ends with sendMove(x,y) and return;
     *
     * @param playground
     */
    @Override
    protected void makeNextTurn(int[][] playground) {
        PlayGround pg = new PlayGround(playground, false);
 
        //First try corners
        if (pg.checkMove(0, 0, myColor, false)) {
            sendMove(0, 0);
            return;
        }
        if (pg.checkMove(0, pg.size - 1, myColor, false)) {
            sendMove(0, pg.size - 1);
            return;
        }
        if (pg.checkMove(pg.size - 1, 0, myColor, false)) {
            sendMove(pg.size - 1, 0);
            return;
        }
        if (pg.checkMove(pg.size - 1, pg.size - 1, myColor, false)) {
            sendMove(pg.size - 1, pg.size - 1);
            return;
        }
 
        //calculate best move using minmax
        AlphaBeta AB = new AlphaBeta(pg, myColor, 5);
 
        //one last check before sending valid move
        if (pg.checkMove(AB.bestCol, AB.bestRow, myColor, false)) {
            sendMove(AB.bestCol, AB.bestRow);
            return;
        }
 
        //just in case minMax calculated move is not valid (never)
        for (int x = 0; x < pg.size; x++) {
            for (int y = 0; y < pg.size; y++) {
                if (pg.checkMove(x, y, myColor, false)) {
                    sendMove(x, y);
                    return;
                }
            }
        }
    }
 
    /**
     * Class PlayGround represents game playground.
     * NOTE:Important functions are evaluate, getPossibleMoves
     *
     */
    private class PlayGround {
 
        private int[][] board;
        private int size;
 
        private PlayGround(int[][] board, boolean copy) {
            size = board.length;
            //Only if deepCopy of game board is needed.
            if (copy) {
                this.board = deepCopy(board);
            } else {
                this.board = board;
            }
        }
 
        private int changeColor(int color) {
            return (color + 1) % 2;
        }
 
        /**
         *
         * @param col
         * @param row
         * @param color
         * @return true if game board was update successfully
         */
        private boolean setField(int col, int row, int color) {
            return checkMove(col, row, color, true);
        }
 
        /**
         * Evaluates board for current player
         * @param color
         * @return
         */
        private int evaluate(int color) {
            int score = 0;
 
            int opColor = changeColor(color);
 
            int payOut = 0;
 
            // edge gets a value of 5
            // corner gets a value of 10
            // subtract 10 for the four diagonal square near the corners
            // subtract 5 for the rows and cols near the edge
            for (int col = 0; col < size; col++) {
                for (int row = 0; row < size; row++) {
                    payOut = 0;
 
                    if (col == 0 || row == size - 1) {
                        payOut += 10;
                    }
                    if (row == 0 || col == size - 1) {
                        payOut += 10;
                    }
                    if (col == 1 || row == size - 2) {
                        payOut -= 5;
                    }
                    if (row == 1 || col == size - 2) {
                        payOut -= 5;
                    }
 
 
 
                    if (board[col][row] == color) {
                        score += payOut;
                    } else if (board[col][row] == opColor) {
                        score -= payOut;
                    }
                }
            }
            return score;
        }
 
        private List<int[]> getPossibleMoves(int color) {
            List<int[]> list = new ArrayList<int[]>();
            for (int col = 0; col < size; col++) {
                for (int row = 0; row < size; row++) {
                    if (checkMove(col, row, color, false)) {
                        int[] arr = {col, row};
                        list.add(arr);
                    }
 
                }
            }
            return list;
        }
 
        private boolean checkMove(int x, int y, int color, boolean change) {
            //size exceeds
            if (x < 0 || x >= size || y < 0 || y >= size) {
                return false;
            }
            //check whether its not taken
            if (board[x][y] >= 0) {
                return false;
            }
            boolean direction = false;
 
            direction |= checkDirection(1, 0, x, y, color, change);
            direction |= checkDirection(1, 1, x, y, color, change);
            direction |= checkDirection(0, 1, x, y, color, change);
            direction |= checkDirection(-1, 1, x, y, color, change);
            direction |= checkDirection(-1, 0, x, y, color, change);
            direction |= checkDirection(-1, -1, x, y, color, change);
            direction |= checkDirection(0, -1, x, y, color, change);
            direction |= checkDirection(1, -1, x, y, color, change);
 
            if (direction && change) {
                board[x][y] = color;
            }
 
            return direction;
        }
 
        private boolean checkDirection(int dirX, int dirY, int x, int y, int color, boolean change) {
            boolean neighbourOpponent = false;
            boolean endingMyStone = false;
            int steps = 0;
            while (!endingMyStone) {
                x += dirX;
                y += dirY;
                steps++;
                if (x < 0 || x >= size || y < 0 || y >= size) {
                    return false;
                }
                int colorXY = board[x][y];
                if (colorXY < 0) {
                    return false;
                }
                if (colorXY != color) {
                    neighbourOpponent = true;
                } else {
                    endingMyStone = true;
                }
            }
            if (neighbourOpponent && endingMyStone) {
                if (change) {
                    dirX *= -1;
                    dirY *= -1;
                    for (int i = 1; i < steps; i++) {
                        x += dirX;
                        y += dirY;
                        board[x][y] = color;
                    }
                }
                return true;
            }
            return false;
        }
 
        private int[][] deepCopy(int[][] arr) {
            int[][] copy = new int[arr.length][arr[0].length];
 
            for (int i = 0; i < arr.length; i++) {
                System.arraycopy(arr[i], 0, copy[i], 0, arr[0].length);
            }
            return copy;
 
        }
    }
 
    private class AlphaBeta {
 
        private final int BETA_NONE = Integer.MAX_VALUE;
        private final int ALPHA_NONE = Integer.MIN_VALUE;
        private long startTime;
        private int bestVal;
        private int depth;
        private int myColor;
        private PlayGround pg;
        private int bestCol;
        private int bestRow;
 
        private AlphaBeta(PlayGround pg, int myColor, int depth) {
            bestVal = ALPHA_NONE;
            this.pg = pg;
            this.myColor = myColor;
            this.depth = depth;
 
            alphaBeta();
        }
        /**
         * Calculates the score for specified move
         * @param pg
         * @param maxDepth
         * @param alpha
         * @param beta
         * @param color
         * @return
         */
        private int alphaBetaValue(PlayGround pg, int maxDepth, int alpha, int beta, int color) {
 
            List<int[]> possibleMoves = pg.getPossibleMoves(color);
 
            if (possibleMoves.isEmpty()) {
                color = pg.changeColor(color);
                possibleMoves = pg.getPossibleMoves(color);
            }
            if (maxDepth == 0 || possibleMoves.isEmpty() || (System.nanoTime() - startTime) / 1000000 > 900) {
                return pg.evaluate(color);
            }
            for (int[] move : possibleMoves) {
 
                int col = move[0];
                int row = move[1];
                PlayGround newPg = new PlayGround(pg.board, true);
                newPg.setField(col, row, color);
 
                int oppAlpha = ALPHA_NONE;
                int oppBeta = BETA_NONE;
 
                if (beta != BETA_NONE) {
                    oppAlpha = -1 * beta;
                }
                if (alpha != ALPHA_NONE) {
                    oppBeta = -1 * alpha;
                }
 
                int val = -1 * alphaBetaValue(newPg, maxDepth - 1, oppAlpha, oppBeta, pg.changeColor(color));
                if (alpha == ALPHA_NONE || val > alpha) {
                    alpha = val;
                }
 
                if (alpha != ALPHA_NONE && beta != BETA_NONE && alpha >= beta) {
                    return beta;
                }
            }
            return alpha;
        }
        /**
         * Chooses best move
         */
        private void alphaBeta() {
            startTime = System.nanoTime();
 
            for (int[] move : pg.getPossibleMoves(myColor)) {
                int col = move[0];
                int row = move[1];
                PlayGround nextPg = new PlayGround(pg.board, true);
                nextPg.setField(col, row, myColor);
 
                int oppBeta = BETA_NONE;
 
                if (bestVal != ALPHA_NONE) {
                    oppBeta = -1 * bestVal;
                }
                int val = -1 * alphaBetaValue(nextPg, depth, ALPHA_NONE, oppBeta, pg.changeColor(myColor));
                if (bestVal == ALPHA_NONE || val > bestVal) {
                    bestVal = val;
                    bestCol = col;
                    bestRow = row;
                }
            }
        }
    }
}