package chess;

import java.util.Stack;

public abstract class Game implements Runnable
{
	protected GameApplet m_applet;
	
	protected Player p1;
	protected Player p2;	
	protected Stack<String> movesHistory;
	
	abstract public void init(GameGraphics graphics, GameGUI gui);
}
