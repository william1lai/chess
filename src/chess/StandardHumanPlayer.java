package chess;

import java.awt.event.MouseEvent;

import chess.Definitions.Color;

public class StandardHumanPlayer extends HumanPlayer 
{
	public StandardHumanPlayer(String name, Color c, Game g) 
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
		int row = StandardChessGameGraphics.getRow(e.getY());
		int col = StandardChessGameGraphics.getCol(e.getX());
		//Right-click to deselect
		if (e.getButton() == MouseEvent.BUTTON3)
			deselect();
		//Left-click to select
		else if (e.getButton() == MouseEvent.BUTTON1 && row >= 0 && col >= 0) 
		{
			StandardChessGame g = ((StandardChessGame)getGame());
			int sq = (7-row)*8 + (7-col);
			StandardChessBoard scb = g.getBoard();
			char p = scb.getPiece(row, col);
			if (p != 0 && ((Character.isUpperCase(p)) ^ (scb.whoseTurn() == Definitions.Color.BLACK))) //colors match
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
