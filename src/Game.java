
public abstract class Game 
{
	private Player p1;
	private Player p2;
	private Definitions.Color m_turn;
	
	public abstract void init();
	public abstract boolean isLegal(Move m);
	
	
	public Player getP1()
	{
		return p1;
	}
	
	public Player getP2()
	{
		return p2;
	}

	public Definitions.Color whoseTurn()
	{
		return m_turn;
	}
}
