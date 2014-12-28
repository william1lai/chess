package chess;

import java.util.Stack;

public abstract class Game implements Runnable
{
	public GameApplet m_applet;
	
	protected Player p1;
	protected Player p2;	
	protected Stack<String> movesHistory;
	
	abstract public void init(GameGraphics graphics, GameGUI gui);
	
	public Player getP1()
	{
		return p1;
	}
	
	public Player getP2()
	{
		return p2;
	}
}
