package chess;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class StandardChessGameGUI implements MouseListener
{
	ArrayList<EasyButton> m_buttons;
	
	public StandardChessGameGUI()
	{
		m_buttons = new ArrayList<EasyButton>();
	}
	
	public void addButton(EasyButton b)
	{
		m_buttons.add(b);
	}
	
	public ArrayList<EasyButton> getButtons()
	{
		return m_buttons;
	}
	
	public void mousePressed(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		for (int i = 0; i < m_buttons.size(); i++) {
			int offX = x - m_buttons.get(i).getX();
			int offY = y - m_buttons.get(i).getY();
			if (offX >= 0 && offX <= m_buttons.get(i).getW() && offY >= 0 && offY <= m_buttons.get(i).getH()) {
				m_buttons.get(i).press();
			}
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
}
