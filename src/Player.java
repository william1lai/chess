
public abstract class Player 
{
	private String m_name;
	private Game m_game;
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public void setGame(Game g)
	{
		m_game = g;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public Game getGame()
	{
		return m_game;
	}
	
	public abstract Move promptMove();
	
	//what else do all players need?
	//remember that human players and computer players are two different kinds of players

}
