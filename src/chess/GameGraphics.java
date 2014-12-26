package chess;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class GameGraphics extends JPanel
{
	protected GameApplet m_applet;
	
	abstract public void init(Game game);
}
