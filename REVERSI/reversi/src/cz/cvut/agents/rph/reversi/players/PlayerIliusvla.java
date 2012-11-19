package cz.cvut.agents.rph.reversi.players;

import cz.cvut.agents.rph.*;
import cz.cvut.agents.rph.reversi.ReversiMove;
import cz.cvut.agents.rph.reversi.ReversiPlayer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PlayerIliusvla extends ReversiPlayer {

     /**
     * Dynamic list of possible moves
     */
    public List<Point> listOfPossibleMove = new ArrayList<Point>();
    /**
     * My player color (0 or 1)
     */
    public int myPlayer;
    /**
     * Opponent player color (0 or 1)
     */
    public int opponentPlayer;
    /**
     * x-Search matrix
     */
    private int[] dxArr = new int[]{-1, 0, 1};
    /**
     * y-Search matrix
     */
    private int[] dyArr = new int[]{-1, 0, 1};
    /**
     * Width of the playground
     */
    private int width;
    /**
     * Height of the playground
     */
    private int height;

 


    protected void startGame(int myColor, int opponentColor, int[][] playground) {
        // Set dimensions of the playground
        width = playground.length;
        height = playground[0].length;
        /**
         * Set my player determinant
         */
        myPlayer = myColor;
        /**
         * Set opponent player determinant
         */
        opponentPlayer = opponentColor;
    }

    /**Goro Hasegawa table:
     *D-Type Move ++
     *A-Type Move +
     *B-Type Move -
     *C-Type Move --
     *X-Type Move ---
     */
 @Override
    public ReversiMove makeNextMove(int[][] board) {
         listOfPossibleMove = new ArrayList<Point>();
        Point p = new Point(goodMove(board));
        int x = p.x;
        int y = p.y;
        ReversiMove r = new ReversiMove(x,y);
        return r;
    }


    private Point goodMove(int[][] playground) {
        listOfPossibleMove = checkAllMoves(playground, myPlayer, opponentPlayer);//Find all posible moves          
        Point possibleMove = findGoodMoves(listOfPossibleMove);
        if (possibleMove != null) {//If A or D -type move is possible, do good move            
            return (possibleMove);
        }
        listOfPossibleMove = deleteBadMoves(listOfPossibleMove);//If possible del X,C or B-type moves, delete bad moves(if more that 1 move is possible)
        delNextGoodOpponentMoves(playground, myPlayer, opponentPlayer);
        if (findBadOpponentMove(playground, myPlayer, opponentPlayer) != null) {
            return findBadOpponentMove(playground, myPlayer, opponentPlayer);
        }
        int RandomIndex = (int) (Math.random() * (listOfPossibleMove.size()));//Do random move from possible moves
        return (listOfPossibleMove.get(RandomIndex));
    }

    private List<Point> checkAllMoves(int[][] playground, int myplayer, int opponentplayer) {//Find all possibles moves
        List<Point> listOfPossibleMoves = new ArrayList<Point>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                boolean checkMove = MoveIsLegal(i, j, playground, myplayer, opponentplayer);
                if (checkMove == true) {
                    listOfPossibleMoves.add(new Point(i, j));
                }
            }
        }
        return listOfPossibleMoves;
    }

    private boolean MoveIsLegal(int m, int n, int[][] playground, int myplayer, int opponentplayer) {//Check this move
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
                for (x = m + dx, y = n + dy; playground[x][y] == opponentplayer && x + dx < width
                        && y + dy < height && x + dx >= 0 && y + dy >= 0; x += dx, y += dy);
                if (playground[x][y] == myplayer && (x - dx != m || y - dy != n)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Point> deleteBadMoves(List<Point> listOfPossibleMove) {
//Priority: X,C and B -type moves
        for (int i = 0; i < listOfPossibleMove.size(); i++) {//Delete only SSBad moves
            int x = listOfPossibleMove.get(i).x;
            int y = listOfPossibleMove.get(i).y;
            Point iPoint = new Point(x, y); // noAppleâ„¢
            if (checkXTypeMoves(iPoint) && listOfPossibleMove.size() > 1) {
                listOfPossibleMove.remove(i);
            }
        }
        for (int i = 0; i < listOfPossibleMove.size(); i++) {//Delete only SBad moves
            int x = listOfPossibleMove.get(i).x;
            int y = listOfPossibleMove.get(i).y;
            Point iPoint = new Point(x, y); // noAppleâ„¢          
            if (checkCTypeMoves(iPoint) && listOfPossibleMove.size() > 1) {
                listOfPossibleMove.remove(i);
            }
        }
        for (int i = 0; i < listOfPossibleMove.size(); i++) {//Delete only Bad moves
            int x = listOfPossibleMove.get(i).x;
            int y = listOfPossibleMove.get(i).y;
            Point iPoint = new Point(x, y); // noAppleâ„¢           
            if (checkBTypeMoves(iPoint) && listOfPossibleMove.size() > 1) {
                listOfPossibleMove.remove(i);
            }
        }
        return listOfPossibleMove;
    }

    private Point findGoodMoves(List<Point> listOfPossibleMove) {
        for (int i = 0; i < listOfPossibleMove.size(); i++) {//Check good moves 
            int x = listOfPossibleMove.get(i).x;
            int y = listOfPossibleMove.get(i).y;
            Point iPoint = new Point(x, y); // noAppleâ„¢
            if (checkDTypeMoves(iPoint)) {//(Priority: A-type, D-type)
                return iPoint;
            }
            if (checkATypeMoves(iPoint)) {
                return iPoint;
            }
        }
        return null;
    }

    private void delNextGoodOpponentMoves(int[][] playground, int myplayer, int opponentplayer) {//If we do it move and opponent can do good move, don't do it move
        for (int i = 0; i < listOfPossibleMove.size(); i++) {
            int x = listOfPossibleMove.get(i).x;
            int y = listOfPossibleMove.get(i).y;
            playground[x][y] = opponentplayer;
            List<Point> listOfOpponentMoves = checkAllMoves(playground, myplayer, opponentplayer);
            if (findGoodMoves(listOfOpponentMoves) != null && listOfPossibleMove.size() > 1) {
                listOfPossibleMove.remove(i);
            }
        }
    }

    public Point findBadOpponentMove(int[][] playground, int myplayer, int opponentplayer) {//If we do it move and opponent can do only bad move, do it move
        for (int i = 0; i < listOfPossibleMove.size(); i++) {
            int x = listOfPossibleMove.get(i).x;
            int y = listOfPossibleMove.get(i).y;
            playground[x][y] = opponentplayer;
            List<Point> listOfOpponentMoves = checkAllMoves(playground, myplayer, opponentplayer);
            listOfOpponentMoves = deleteBadMoves(listOfOpponentMoves);
            if (listOfOpponentMoves == null) {
                Point goodMove = new Point(x, y);
                return (goodMove);
            }

        }
        return null;
    }

    private boolean checkDTypeMoves(Point PossibleMove) {
        // array[0][0],array[0][7],array[7][0],array[7][7] - SuperGoodMove 
        int x = PossibleMove.x;
        int y = PossibleMove.y;
        if ((x == 0 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 0) || (x == 7 && y == 7)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkATypeMoves(Point PossibleMove) {
        // array[0][2],array[0][5],array[2][0],array[2][7],array[5][0],array[5][7],array[7][2],array[7][5] - GoodMove
        int x = PossibleMove.x;
        int y = PossibleMove.y;
        if ((x == 0 && y == 2) || (x == 0 && y == 5) || (x == 2 && y == 0) || (x == 2 && y == 7) || (x == 5 && y == 0) || (x == 5 && y == 7) || (x == 7 && y == 2) || (x == 7 && y == 5)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkXTypeMoves(Point PossibleMove) {
        //array[1][1],array[1][6],array[6][1],array[6][6] - SuperSuperBadMove
        int x = PossibleMove.x;
        int y = PossibleMove.y;
        if ((x == 1 && y == 1) || (x == 1 && y == 6) || (x == 6 && y == 1) || (x == 6 && y == 6)) {
            return true;
        }
        return false;
    }

    private boolean checkCTypeMoves(Point PossibleMove) {
//        array[0][1],array[0][6],array[1][0],array[1][7],array[6][0],array[6][7],array[7][1],array[7][6] - SuperBadMove
        int x = PossibleMove.x;
        int y = PossibleMove.y;
        if ((x == 0 && y == 1) || (x == 0 && y == 6) || (x == 1 && y == 0) || (x == 1 && y == 7) || (x == 6 && y == 0) || (x == 6 && y == 7) || (x == 7 && y == 1) || (x == 7 && y == 6)) {
            return true;
        }
        return false;
    }

    private boolean checkBTypeMoves(Point PossibleMove) {
        //        array[0][3],array[0][4],array[1][2],array[1][3],array[1][4],array[1][5],array[2][1],array[2][6] - BadMove
        //        array[3][0],array[3][1],array[3][6],array[3][7],array[4][0],array[4][1],array[4][6],array[4][7] - BadMove
        //        array[5][1],array[5][6],array[6][2],array[6][3],array[6][4],array[7][3],array[7][4]- BadMove
        int x = PossibleMove.x;
        int y = PossibleMove.y;
        if ((x == 0 && y == 3) || (x == 0 && y == 4) || (x == 1 && y == 2)
                || (x == 1 && y == 3) || (x == 1 && y == 4) || (x == 1 && y == 5)
                || (x == 2 && y == 1) || (x == 2 && y == 6) || (x == 3 && y == 0)
                || (x == 3 && y == 1) || (x == 3 && y == 6) || (x == 3 && y == 7)
                || (x == 4 && y == 0) || (x == 4 && y == 1) || (x == 4 && y == 6)
                || (x == 4 && y == 7) || (x == 5 && y == 1) || (x == 5 && y == 6)
                || (x == 6 && y == 2) || (x == 6 && y == 3) || (x == 6 && y == 4)
                || (x == 7 && y == 3) || (x == 7 && y == 4)) {
            return true;
        }
        return false;
    }

    private boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }

   

    @Override
    public String getName() {
        return "PlayerIliusvla";
    }
}