package chess;

import java.awt.event.MouseEvent;

import chess.Definitions.Color;

public class AliceHumanPlayer extends HumanPlayer 
{
	private int m_selectedBoard;
	
	public AliceHumanPlayer(String name, Color c, Game g) 
	{
		super(name, c, g);
	}

	private void select(int board, int sq)
	{
		m_selectedBoard = board;
		m_selected = sq;
	}

	private void deselect()
	{
		m_selected = -1;
	}

	public int getSelected()
	{
		return m_selected;
	}
	
	public int getSelectedBoard()
	{
		return m_selectedBoard;
	}

	public void mousePressed(MouseEvent e)
	{
		AliceGame g = ((AliceGame)getGame());
		int board = 0; //HARD CODE
		int row = g.getGraphics().getRow(e.getY());
		int col = g.getGraphics().getCol(e.getX());
		//Right-click to deselect
		if (e.getButton() == MouseEvent.BUTTON3)
			deselect();
		//Left-click to select
		else if (e.getButton() == MouseEvent.BUTTON1 && row >= 0 && col >= 0) 
		{
			int sq = (7-row)*8 + (7-col);
			AliceBoard acb = g.getBoard();
			char p = acb.getPiece(row, col, 0); //hardcoded
			if (p != 0 && ((Character.isUpperCase(p)) ^ (acb.whoseTurn() == Definitions.Color.BLACK))) //colors match
			{
				select(0, sq); //hardcoded
			}
			else if (m_selected != -1)
			{
				m_move = new Move(7 - (m_selected / 8), 7 - (m_selected % 8), row, col);
				if (g.getBoard().isLegalMove(m_move, board)) {
					m_selected = -1;
					m_done = true;
					getGame().m_applet.removeMouseListener(this);
				}
				else {
					m_move = null;
				}
			}
		}
	}
}