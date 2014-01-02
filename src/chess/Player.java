package chess;


public abstract class Player implements Runnable
{	
	private String m_name;
	private Game m_game;
	private Definitions.Color m_color;
	protected Move m_move;
	protected boolean m_done;
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public void setColor(Definitions.Color c)
	{
		m_color = c;
	}
	
	public void setGame(Game g)
	{
		m_game = g;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public Definitions.Color getColor()
	{
		return m_color;
	}
	
	public Game getGame()
	{
		return m_game;
	}
	
	public boolean isDone()
	{
		return m_done;
	}
	
	public Move getMove()
	{
		return m_move;
	}
	
	public abstract void promptMove();
	
	//what else do all players need?
	//remember that human players and computer players are two different kinds of players

}
