/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.agents.rph.reversi.players;

import cz.cvut.agents.rph.reversi.ReversiMove;
import cz.cvut.agents.rph.reversi.ReversiPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author piero
 */
public class PlayerSestapie extends ReversiPlayer {

    public List<Integer> pocetPrebarvenych = new ArrayList();
    public List<Integer> souradniceX = new ArrayList();
    public List<Integer> souradniceY = new ArrayList();

    @Override
    public String getName() {
        return "sestapie";
    }

    public void savePotencialMove(int k, int i, int j) {
        pocetPrebarvenych.add(k);
        souradniceX.add(i);
        souradniceY.add(j);
    }

    public void correctMove(int[][] board) {
        int k = 1;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (board[i][j] == getEmptySquareColor()) {
                    /*height=+1, width=-1 against EmptySquare*/
                    if (i > 1 && j > 1) {
                        while (!((i - k) > 7 || (i - k) < 0 || (j - k) > 7 || (j - k) < 0) && (board[i - k][j - k] == opponentColor)) {
                            k++;
                        }
                        if (!((i - k) > 7 || (i - k) < 0 || (j - k) > 7 || (j - k) < 0) && (board[i - k][j - k] == myColor & board[i - k + 1][j - k + 1] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=+1, width=0 against EmptySquare*/
                    if (j > 1) {
                        while (!((j - k) > 7 || (j - k) < 0) && (board[i][j - k] == opponentColor)) {
                            k++;
                        }
                        if (!((j - k) > 7 || (j - k) < 0) && (board[i][j - k] == myColor & board[i][j - k + 1] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=+1, width=+1 against EmptySquare*/
                    if (i < 6 && j > 1) {
                        while (!((i + k) > 7 || (i + k) < 0 || (j - k) > 7 || (j - k) < 0) && (board[i + k][j - k] == opponentColor)) {
                            k++;
                        }
                        if (!((i + k) > 7 || (i + k) < 0 || (j - k) > 7 || (j - k) < 0) && (board[i + k][j - k] == myColor & board[i + k - 1][j - k + 1] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=0, width=-1 against EmptySquare*/
                    if (i > 1) {
                        while (!((i - k) > 7 || (i - k) < 0) && (board[i - k][j] == opponentColor)) {
                            k++;
                        }
                        if (!((i - k) > 7 || (i - k) < 0) && (board[i - k][j] == myColor & board[i - k + 1][j] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=0, width=+1 against EmptySquare*/
                    if (i < 6) {
                        while (!((i + k) > 7 || (i + k) < 0) && (board[i + k][j] == opponentColor)) {
                            k++;
                        }
                        if (!((i + k) > 7 || (i + k) < 0) && (board[i + k][j] == myColor & board[i + k - 1][j] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=-1, width=-1 against EmptySquare*/
                    if (i > 1 && j < 6) {
                        while (!((i - k) > 7 || (i - k) < 0 || (j + k) > 7 || (j + k) < 0) && (board[i - k][j + k] == opponentColor)) {
                            k++;
                        }
                        if (!((i - k) > 7 || (i - k) < 0 || (j + k) > 7 || (j + k) < 0) && (board[i - k][j + k] == myColor & board[i - k + 1][j + k - 1] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=-1, width=0 against EmptySquare*/
                    if (j < 6) {
                        while (!((j + k) > 7 || (j + k) < 0) && (board[i][j + k] == opponentColor)) {
                            k++;
                        }
                        if (!((j + k) > 7 || (j + k) < 0) && (j + k < height) && (board[i][j + k] == myColor & board[i][j + k - 1] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                    k = 1;
                    /*height=-1, width=+1 against EmptySquare*/
                    if (j < 6 && i < 6) {
                        while (!((i + k) > 7 || (i + k) < 0 || (j + k) > 7 || (j + k) < 0) && (board[i + k][j + k] == opponentColor)) {
                            k++;
                        }
                        if (!((i + k) > 7 || (i + k) < 0 || (j + k) > 7 || (j + k) < 0) && (board[i + k][j + k] == myColor & board[i + k - 1][j + k - 1] == opponentColor)) {
                            savePotencialMove(k, i, j);
                        }
                    }
                k=1;
                }
            }
        }

    }
    public void clearCache(){
        pocetPrebarvenych.clear();
        souradniceX.clear();
        souradniceY.clear();
    }

    @Override
    public ReversiMove makeNextMove(int[][] board) {
        correctMove(board);
        if (souradniceX.contains(0)) {
            int index0 = souradniceX.indexOf(0);
            
            if (souradniceY.get(index0) == 0) {
                clearCache();
                return new ReversiMove(0, 0);
            }
            if (souradniceY.get(index0) == 7) {
                clearCache();
                return new ReversiMove(0, 7);
            }
            

        }
        if(souradniceX.contains(7)){
            int index7 = souradniceX.indexOf(7);
            if (souradniceY.get(index7) == 0) {
                clearCache();
                return new ReversiMove(7, 0);
            }
            if (souradniceY.get(index7) == 7) {
                clearCache();
                return new ReversiMove(7, 7);
            }
        }
        int maxPrebarvenych = 0;
        for (Integer k : pocetPrebarvenych) {
            if (k > maxPrebarvenych) {
                maxPrebarvenych = k;
            }
        }
        int index = pocetPrebarvenych.indexOf(maxPrebarvenych);
        int x = souradniceX.get(index);
        int y = souradniceY.get(index);
        clearCache();
        return new ReversiMove(x, y);
    }
}
