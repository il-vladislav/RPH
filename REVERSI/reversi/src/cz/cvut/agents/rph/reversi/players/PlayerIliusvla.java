/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.agents.rph.reversi.players;

import cz.cvut.agents.rph.reversi.ReversiMove;
import cz.cvut.agents.rph.reversi.ReversiPlayer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MELI
 */
public class PlayerIliusvla extends ReversiPlayer {

    private int[] dxArr = new int[]{-1, 0, 1};
    private int[] dyArr = new int[]{-1, 0, 1};

    @Override
    public String getName() {
        return "PlayerIliusvla";
    }

    @Override
    public ReversiMove makeNextMove(int[][] board) {
        // for (int i = 0; i< board.length; i++){
        //for (int j = 0; j< board.length; j++){
        //  System.out.print(board[j][i]);
        // }
        //  System.out.println();
        //  }
        List<Point> listOfPossibleMove = checkAllMoves(board);
        Point a = listOfPossibleMove.get(0);
        return new ReversiMove(a.x, a.y);
    }

    private List<Point> checkAllMoves(int[][] playground) {
        List<Point> listOfPossibleMove = new ArrayList<Point>();//Find all possibles moves
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                boolean checkMove = MoveIsLegal(i, j, playground);
                if (checkMove == true) {
                    listOfPossibleMove.add(new Point(i, j));
                }
            }
        }
        return (listOfPossibleMove);
    }

    private boolean MoveIsLegal(int m, int n, int[][] playground) {//Check this move
        if (playground[m][n] != -1) {//If it's nofree player can't do this move
            return false;
        }
        for (int dx : dxArr) {//x-Search matrix
            for (int dy : dyArr) {//y-Search matrix                
                if (outOfBounds(m + dx, n + dy)) {
                    continue;
                }
                int x;
                int y;
                for (x = m + dx, y = n + dy; playground[x][y] == getOpponentColor() && x + dx < width
                        && y + dy < height && x + dx >= 0 && y + dy >= 0; x += dx, y += dy);
                if (playground[x][y] == getMyColor() && (x - dx != m || y - dy != n)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }
}
