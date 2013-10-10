
public abstract class Player 
{
	private String m_name;
	
	public void setName(String name)
	{
		m_name = name;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public abstract Move promptMove();
	
	//what else do all players need?
	//remember that human players and computer players are two different kinds of players

}
