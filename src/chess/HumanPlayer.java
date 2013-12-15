package chess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HumanPlayer extends Player implements MouseListener
{	
	private Piece m_selected;
	
	public HumanPlayer(String name)
	{
		setName(name);
	}
	
	public HumanPlayer(String name, Definitions.Color c, Game g)
	{
		setName(name);
		setGame(g);
		setColor(c);
	}
	
	private void select(Piece p)
	{
		m_selected = p;
	}
	
	private void deselect()
	{
		m_selected = null;
	}
	
	public Piece getSelected()
	{
		return m_selected;
	}

	public void promptMove()
	{
		if (getGame() instanceof StandardChessGame)
		{
			m_done = false;
			m_move = null;
			getGame().addMouseListener(this);
		}
	}

	public void run() {}
	
	public void mousePressed(MouseEvent e)
	{
		if (getGame() instanceof StandardChessGame)
		{
			int row = StandardChessGameGraphics.getRow(e.getY());
			int col = StandardChessGameGraphics.getCol(e.getX());
			//Right-click to deselect
			if (e.getButton() == MouseEvent.BUTTON3)
				deselect();
			//Left-click to select
			else if (e.getButton() == MouseEvent.BUTTON1 && row >= 0 && col >= 0) {
				StandardChessGame g = ((StandardChessGame)getGame());
				Piece p = g.getBoard().getPiece(row, col);
				if (p != null && p.color() == getColor()) {
					select(p);
				}
				else if (m_selected != null) {
					m_move = new Move(m_selected.row(), m_selected.col(), row, col);
					if (g.getBoard().isLegalMove(m_move)) {
						m_selected = null;
						m_done = true;
						g.removeMouseListener(this);
					}
					else {
						m_move = null;
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
