package chess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HumanPlayer extends Player implements MouseListener
{	
	private int m_selected;

	public HumanPlayer(String name, Definitions.Color c, Game g)
	{
		setName(name);
		setGame(g);
		setColor(c);
		m_selected = -1;
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

	public void promptMove()
	{
		if (getGame() instanceof StandardChessGame || getGame() instanceof LosersChessGame)
		{
			m_done = false;
			m_move = null;
			getGame().m_applet.addMouseListener(this);
		}
	}

	public void run() {}

	public void mousePressed(MouseEvent e)
	{
		if (getGame() instanceof StandardChessGame || getGame() instanceof LosersChessGame)
		{
			int row = StandardChessGameGraphics.getRow(e.getY());
			int col = StandardChessGameGraphics.getCol(e.getX());
			//Right-click to deselect
			if (e.getButton() == MouseEvent.BUTTON3)
				deselect();
			//Left-click to select
			else if (e.getButton() == MouseEvent.BUTTON1 && row >= 0 && col >= 0) 
			{
				if (getGame() instanceof StandardChessGame)
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
				else if (getGame() instanceof LosersChessGame)
				{
					LosersChessGame g = ((LosersChessGame)getGame());
					int sq = (7-row)*8 + (7-col);
					LosersChessBoard lcb = g.getBoard();
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
	}

	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

}
