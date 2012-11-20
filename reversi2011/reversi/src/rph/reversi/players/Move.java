/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rph.reversi.players;

/**
 *
 * @author Vlad
 */
public class Move
{
  private int m_x, m_y;
  private int m_player;

  public Move(Move m) { m_x = m.m_x; m_y = m.m_y; m_player = m.m_player; }
  public Move(int x, int y, int player) { m_x = x; m_y = y; m_player =player; }

  public int GetX() { return m_x; }
  public int GetY() { return m_y; }
  public int GetPlayer() { return m_player; }
}