package chess;

import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AliceGameGUI extends GameGUI
{
	AliceGame m_game;
	ArrayList<EasyButton> m_buttons;
	
	public AliceGameGUI(GameApplet applet)
	{
		m_applet = applet;
		m_buttons = new ArrayList<EasyButton>();
	}

	public void init(Game game)
	{
		m_game = (AliceGame)game;
		
		try {
			EasyButton b = new EasyButton("buttonUndo", 480, 220, 90, 30, new EasyButtonAction() {
				public void on_press()
				{
					m_game.undo();
				}
			});
			addButton(b);
		}
		catch (Exception ex) { System.out.println(ex.getMessage()); }
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
	
	public void mouseReleased(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		for (int i = 0; i < m_buttons.size(); i++) {
			int offX = x - m_buttons.get(i).getX();
			int offY = y - m_buttons.get(i).getY();
			boolean releasedOnButton = offX >= 0 &&
									   offX <= m_buttons.get(i).getW() &&
									   offY >= 0 &&
									   offY <= m_buttons.get(i).getH();
			m_buttons.get(i).release(releasedOnButton);
		}
	}

	public void focusLost(FocusEvent e)
	{
		for (int i = 0; i < m_buttons.size(); i++)
			m_buttons.get(i).release(false);
	}

	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) m_game.getGraphics().setActiveBoard(1);
		if (e.getKeyCode() == KeyEvent.VK_LEFT) m_game.getGraphics().setActiveBoard(0); 
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void focusGained(FocusEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
}