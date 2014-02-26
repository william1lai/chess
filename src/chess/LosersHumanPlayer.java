package chess;

import java.awt.event.MouseEvent;

import chess.Definitions.Color;

public class LosersHumanPlayer extends HumanPlayer 
{
	public LosersHumanPlayer(String name, Color c, Game g) 
	{
		super(name, c, g);
	}

	private void select(int sq)
	{
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

	public void mousePressed(MouseEvent e)
	{
		int row = LosersGameGraphics.getRow(e.getY());
		int col = LosersGameGraphics.getCol(e.getX());

		//Right-click to deselect
		if (e.getButton() == MouseEvent.BUTTON3)
			deselect();
		//Left-click to select
		else if (e.getButton() == MouseEvent.BUTTON1 && row >= 0 && col >= 0) 
		{
			LosersGame g = ((LosersGame)getGame());
			int sq = (7-row)*8 + (7-col);
			LosersBoard lcb = g.getBoard();
			char p = lcb.getPiece(row, col);
			if (p != 0 && ((Character.isUpperCase(p)) ^ (lcb.whoseTurn() == Definitions.Color.BLACK))) //colors match
			{
				select(sq);
			}
			else if (m_selected != -1)
			{
				m_move = new Move(7 - (m_selected / 8), 7 - (m_selected % 8), row, col);
				if (g.getBoard().isLegalMove(m_move)) {
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
