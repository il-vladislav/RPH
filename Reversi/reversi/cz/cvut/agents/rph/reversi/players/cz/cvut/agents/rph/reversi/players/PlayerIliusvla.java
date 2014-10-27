package cz.cvut.agents.rph.reversi.players;

import cz.cvut.agents.rph.reversi.ReversiMove;
import cz.cvut.agents.rph.reversi.ReversiPlayer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
*  I've write this code as a first-semester freshman. 
*/

/**
 * Standart implementation of MiniMax with Alpha-Beta pruning algorithm.
 *
 * Uses 3 classes: PlayerIliusvla, MiniMax and Board
 *
 * Architecture based on http://goo.gl/XzfEF, http://goo.gl/2qKQA, http://goo.gl/m2cFK
 * Architecture is pretty simple: Class for AIPlayer(PlayerIliusvla)
 * Class for Nodes (Board) with methods hasChild(checkMove), getChilds(getPossibleMoves), cloneNode(Copy) methods.
 * Class for MiniMax algorithm (MiniMax) with MiniMax and Coast(getCoast) methods
 * Evaluation function is in Board class
 * @author Iliushin Vladislav
 */
public class PlayerIliusvla extends ReversiPlayer {

    static long startTime;
    static int myColor;
    static int opponentColor;
    static int width, height;

    @Override
    public String getName() {
        return "PlayerIliusvlaF";
    }

    @Override
    public ReversiMove makeNextMove(int[][] board) {
        width = board.length;
        height = board.length;
        ReversiMove result;

        startTime = System.nanoTime();
        myColor = getMyColor();
        opponentColor = getOpponentColor();
        MiniMax AlphaBeta;
        Board br = new Board(board, false);

        //Check corners
        result = checkCorner(br);
        if (result != null) {
            return result;
        }

        // Give-away in the early game
        if (br.totalDisks() < 40) {
            AlphaBeta = new MiniMax(br, myColor, 4);

        } // Take-back later in the game
        else {
            AlphaBeta = new MiniMax(br, myColor, 15);
        }
        result = new ReversiMove(AlphaBeta.getBestMove().x, AlphaBeta.getBestMove().y);
        return result;
    }

    /**
     * @param br board
     * @return possible ReversiMove on corner or null if no possible moves on
     * corner
     */
    ReversiMove checkCorner(Board br) {
        ReversiMove result = null;

        ArrayList<Point> corners = new ArrayList<Point>() {
            {
                add(new Point(0, 0));
                add(new Point(0, 7));
                add(new Point(7, 0));
                add(new Point(7, 7));
            }
        };

        for (Point corner : corners) {
            if (br.moveIsLegal(corner.x, corner.y, myColor, opponentColor)) {
                result = (new ReversiMove(corner.x, corner.y));
            }
        }
        return result;
    }

    /**
     * @param color Color to change
     * @return Inverse color
     */
    static int changeColor(int color) {
        if (color == myColor) {
            return opponentColor;
        } else {
            return myColor;
        }
    }
}

/**
 * Implementation of Board/Node for MiniMax algorithm
 *
 * After viewing sources about 5 games on Java/C++/Visual Basic I found, that
 * this class need: Node (board), getChildsList(getPossibleMoves),
 * hasChild(moveIsLegal), method to copy Node(Copy)
 * and evaluate function.
 *
 * @author iliusvla
 */
class Board {

    public int[][] board;
    public int size;
    private int[] dxArr = new int[]{-1, 0, 1};
    private int[] dyArr = new int[]{-1, 0, 1};

    public Board(int[][] board, boolean copy) {
        size = board.length;

        if (copy) {
            this.board = Copy(board);
        } else {
            this.board = board;
        }
    }

    /**
     * @return count of total non-free disks
     */
    int totalDisks() {
        int count = 0;
        count += diskCounter(PlayerIliusvla.myColor);
        count += diskCounter(PlayerIliusvla.changeColor(PlayerIliusvla.myColor));
        return count;
    }

    /**
     * @param color
     * @return count of disks for color
     */
    private int diskCounter(int color) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == color) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Method evaluates board using stability metric, stones counter and position
     * evaluation. In early game method use stability and stones superiority with
     * position evaluation In middle and end game method use stones superiority
     * with position evaluation TODO: find best coefficients
     *
     * @param color
     * @return score for board state
     */
    public int evaluate(int color) {
        boolean early_game = (totalDisks() < 40);

        int countGoodnes = 0;
        int enemyStones = diskCounter(PlayerIliusvla.changeColor(color));
        int friendlyStones = diskCounter(color);
        int K1 = 1, K2 = 2, K3 = 3;

        int stability = (GetStableDiscsCount(board, color, PlayerIliusvla.changeColor(color)) - GetStableDiscsCount(board, PlayerIliusvla.changeColor(color), color)) * board.length * 2 / 3;

        if (early_game) {
            // give-away in the early game
            countGoodnes = K1 * (enemyStones - friendlyStones) + stability / 2;
        } else {
            // take-back later in the game
            countGoodnes = K2 * (friendlyStones - enemyStones);
        }

        int positionalGoodness = K3 * positionMatrixEvualation(color);
        int total_board_goodness = countGoodnes + positionalGoodness;

        return total_board_goodness;
    }

    /**
     * @param color
     * @return position evaluation
     */
    int positionMatrixEvualation(int color) {

        int score = 0;

        int opColor = PlayerIliusvla.changeColor(color);

        int[][] ev = new int[][]{
            {10, -5, 10, 10, 10, 10, -5, 10},
            {-5, -10, -5, -5, -5, -5, -10, -5},
            {10, -5, 0, 0, 0, 0, -5, 10},
            {10, -5, 0, 0, 0, 0, -5, 10},
            {10, -5, 0, 0, 0, 0, -5, 10},
            {10, -5, 0, 0, 0, 0, -5, 10},
            {-5, -10, -5, -5, -5, -5, -10, -5},
            {10, -5, 10, 10, 10, 10, -5, 10},};

        for (int col = 0; col < size; col++) {
            for (int row = 0; row < size; row++) {
                if (board[col][row] == color) {
                    score += ev[col][row];
                } else if (board[col][row] == opColor) {

                    score -= ev[col][row];
                }
            }
        }
        return score;
    }

    /**
     *
     * @param board
     * @param color
     * @param oppositecolor
     * @return stable disks counter
     */
    public int GetStableDiscsCount(int[][] board, int color, int oppositecolor) {
        return this.GetStableDiscsFromCorner(board, color, 0, 0)
                + this.GetStableDiscsFromCorner(board, color, 0, board.length - 1)
                + this.GetStableDiscsFromCorner(board, color, board.length - 1, 0)
                + this.GetStableDiscsFromCorner(board, color, board.length - 1, board.length - 1)
                + this.GetStableDiscsFromEdge(board, color, 0, true)
                + this.GetStableDiscsFromEdge(board, color, board.length - 1, true)
                + this.GetStableDiscsFromEdge(board, color, 0, false)
                + this.GetStableDiscsFromEdge(board, color, board.length - 1, false);
    }

    /**
     * @param board
     * @param color
     * @param cornerRowIndex
     * @param cornerColumnIndex
     * @return stable disks counter from corner
     */
    private int GetStableDiscsFromCorner(int[][] board, int color, int cornerRowIndex, int cornerColumnIndex) {
        int result = 0;

        int rowIndexChange = (cornerRowIndex == 0) ? 1 : -1;
        int columnIndexChange = (cornerColumnIndex == 0) ? 1 : -1;

        int rowIndex = cornerRowIndex;
        int rowIndexLimit = (cornerRowIndex == 0) ? board.length : 0;
        int columnIndexLimit = (cornerColumnIndex == 0) ? board.length : 0;
        for (rowIndex = cornerRowIndex; rowIndex != rowIndexLimit; rowIndex += rowIndexChange) {
            int columnIndex;
            for (columnIndex = cornerColumnIndex; columnIndex != columnIndexLimit; columnIndex += columnIndexChange) {
                if (board[rowIndex][columnIndex] == color) {
                    result++;
                } else {
                    break;
                }
            }

            if ((columnIndexChange > 0 && columnIndex < board.length) || (columnIndexChange < 0 && columnIndex > 0)) {
                columnIndexLimit = columnIndex - columnIndexChange;

                if (columnIndexChange > 0 && columnIndexLimit == 0) {
                    columnIndexLimit++;
                } else if (columnIndexChange < 0 && columnIndexLimit == board.length - 1) {
                    columnIndexLimit--;
                }

                if ((columnIndexChange > 0 && columnIndexLimit < 0)
                        || (columnIndexChange < 0 && columnIndexLimit > board.length - 1)) {
                    break;
                }
            }
        }

        return result;
    }

    /**
     *
     * @param board
     * @param color
     * @param edgeCoordinate
     * @param isHorizontal
     * @return stable disks counter from edge
     */
    private int GetStableDiscsFromEdge(int[][] board, int color, int edgeCoordinate, boolean isHorizontal) {
        int result = 0;

        if (IsEdgeFull(board, edgeCoordinate, isHorizontal)) {
            boolean oppositeColorDiscsPassed = false;
            for (int otherCoordinate = 0; otherCoordinate < board.length; otherCoordinate++) {
                int fieldColor = (isHorizontal) ? board[edgeCoordinate][otherCoordinate] : board[otherCoordinate][edgeCoordinate];
                if (fieldColor != color) {
                    oppositeColorDiscsPassed = true;
                } else if (oppositeColorDiscsPassed) {
                    int consecutiveDiscsCount = 0;
                    while ((otherCoordinate < board.length) && (fieldColor == color)) {
                        consecutiveDiscsCount++;

                        otherCoordinate++;
                        if (otherCoordinate < board.length) {
                            fieldColor = (isHorizontal) ? board[edgeCoordinate][ otherCoordinate] : board[otherCoordinate][edgeCoordinate];
                        }
                    }
                    if (otherCoordinate != board.length) {
                        result += consecutiveDiscsCount;
                        oppositeColorDiscsPassed = true;
                    }
                }
            }
        }

        return result;
    }

    /**
     *
     * @param board
     * @param edgeCoordinate
     * @param isHorizontal
     * @return edge full state
     */
    private boolean IsEdgeFull(int[][] board, int edgeCoordinate, boolean isHorizontal) {
        for (int otherCoordinate = 0; otherCoordinate < board.length; otherCoordinate++) {
            if (isHorizontal && (board[edgeCoordinate][otherCoordinate] == -1)
                    || !isHorizontal && (board[otherCoordinate][edgeCoordinate] == -1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: Use better implementation.
     *
     * @param board
     * @return new board
     */
    private int[][] Copy(int[][] board) {
        int size = board.length;
        int[][] newBoard = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, size);
        }
        return newBoard;
    }

    /**
     * @param color
     * @return List<Point> with possibles move
     */
    List<Point> getPossibleMoves(int color) {
        List<Point> possibleMoves = new ArrayList<Point>();
        for (int i = 0; i < PlayerIliusvla.height; i++) {
            for (int j = 0; j < PlayerIliusvla.width; j++) {
                boolean checkMove = moveIsLegal(i, j, color, PlayerIliusvla.changeColor(color));
                if (checkMove == true) {
                    possibleMoves.add(new Point(i, j));
                }
            }
        }
        return (possibleMoves);
    }

    /**
     *
     * @param m x-coordinate
     * @param n y-coordinate
     * @param color
     * @param opponentColor
     * @return state, if for current board/color move is possible
     */
    boolean moveIsLegal(int m, int n, int color, int opponentColor) {

        if (this.board[m][n] != -1) {//If it's nofree player can't do this move
            return false;
        }
        for (int dx : dxArr) {//x-Search matrix
            for (int dy : dyArr) {//y-Search matrix                
                if (outOfBounds(m + dx, n + dy)) {
                    continue;
                }
                int x;
                int y;
                for (x = m + dx, y = n + dy; this.board[x][y] == opponentColor && x + dx < PlayerIliusvla.width
                        && y + dy < PlayerIliusvla.height && x + dx >= 0 && y + dy >= 0; x += dx, y += dy);
                if (this.board[x][y] == color && (x - dx != m || y - dy != n)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param x
     * @param y
     * @return state, if move out of board (-1,0 coordinate return false)
     */
    boolean outOfBounds(int x, int y) {

        return x < 0 || y < 0 || x >= PlayerIliusvla.width || y >= PlayerIliusvla.height;
    }

    /**
     *
     * @param col
     * @param row
     * @param color
     * @return true if board change correctly
     */
    boolean setBoard(int col, int row, int color) {
        return checkMove(col, row, color, true);
    }

    /**
     * Used this function: http://pastebin.com/ddi1k3N3
     *
     * @param x
     * @param y
     * @param color
     * @param change
     */
    boolean checkMove(int x, int y, int color, boolean change) {

        if (outOfBounds(x, y)) {
            return false;
        }

        if (board[x][y] >= 0) {
            return false;
        }

        boolean directionChecker = false;
        for (int i : dxArr) {
            for (int j : dyArr) {
                directionChecker |= checkDirection(i, j, x, y, color, change);
            }
        }

        if (directionChecker && change) {
            board[x][y] = color;
        }
        return directionChecker;
    }

    /**
     * Used this function: http://pastebin.com/ddi1k3N3
     *
     * @param dirX direction X
     * @param dirY direction Y
     * @param x
     * @param y
     * @param color
     * @param change
     */
    boolean checkDirection(int dirX, int dirY, int x, int y, int color, boolean change) {
        boolean neighbourOpponent = false;
        boolean endingMyStone = false;
        int steps = 0;
        while (!endingMyStone) {
            x += dirX;
            y += dirY;
            steps++;
            if (outOfBounds(x, y)) {
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
}

/**
 * Implementation of MiniMax with Aplha-Beta pruning class
 *
 * Architecture based on Wikipedia pseudo-code implementation
 *
 * @author Iliushin Vladislav
 */
class MiniMax {

    private int Alpha = Integer.MIN_VALUE;
    private int Beta = Integer.MAX_VALUE;
    private int bestValue;
    private int depth;
    private int myColor;
    private Board board;
    private Point bestMove = new Point();

    public MiniMax(Board board, int myColor, int depth) {
        bestValue = Alpha;
        this.board = board;
        this.myColor = myColor;
        this.depth = depth;
        alphaBeta();
    }

    /**
     *
     * @return best move for current MiniMax state
     */
    public Point getBestMove() {
        return bestMove;
    }

    /**
     * Wikipedia alphaBeta pseudocode
     */
    public void alphaBeta() {
        for (Point move : board.getPossibleMoves(myColor)) {

            Board nextBoardState = new Board(board.board, true);

            nextBoardState.setBoard(move.x, move.y, myColor);

            int oppBeta = Beta;

            if (bestValue != Alpha) {
                oppBeta = -1 * bestValue;
            }

            int val = -1 * getCoast(nextBoardState, depth, Alpha, oppBeta, PlayerIliusvla.changeColor(myColor));
            if (bestValue == Alpha || val > bestValue) {
                bestValue = val;
                bestMove.x = move.x;
                bestMove.y = move.y;
            }
        }
    }

    /**
     * http://pressibus.org/ataxx/autre/minimax/node2.html
     *
     * http://en.wikipedia.org/wiki/Negamax
     * http://en.wikipedia.org/wiki/Alpha-beta_pruning
     *
     * @param br board
     * @param maxDepth
     * @param alpha
     * @param beta
     * @param color
     * @return coast for board
     */
    private int getCoast(Board br, int maxDepth, int alpha, int beta, int color) {

        List<Point> possibleMoves = br.getPossibleMoves(color);

        if (possibleMoves.isEmpty()) {
            color = PlayerIliusvla.changeColor(color);
            possibleMoves = br.getPossibleMoves(color);
        }

        if (possibleMoves.isEmpty() || maxDepth == 0 || (System.nanoTime() - PlayerIliusvla.startTime) > 980000) {
            return br.evaluate(color);
        }

        for (Point move : possibleMoves) {

            Board newBoard = new Board(br.board, true);

            newBoard.setBoard(move.x, move.y, color);

            int opponentAplha = Alpha;
            int opponentBeta = Beta;

            if (alpha != Alpha) {
                opponentBeta = -alpha;
            }

            if (beta != Beta) {
                opponentAplha = -beta;
            }

            int val = -getCoast(newBoard, maxDepth - 1, opponentAplha, opponentBeta, PlayerIliusvla.changeColor(color));

            if (alpha == Alpha || val > alpha) {
                alpha = val;
            }

            if (alpha != Alpha && beta != Beta && alpha >= beta) {
                return beta;
            }
        }
        return alpha;
    }
}