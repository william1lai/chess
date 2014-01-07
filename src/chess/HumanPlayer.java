package chess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class HumanPlayer extends Player implements MouseListener
{	
	protected int m_selected;

	public HumanPlayer(String name, Definitions.Color c, Game g)
	{
		setName(name);
		setGame(g);
		setColor(c);
		m_selected = -1;
	}

	public void promptMove() //override in subclass if necessary
	{
		m_done = false;
		m_move = null;
		getGame().m_applet.addMouseListener(this);
	}

	public void run() {}

	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

}
