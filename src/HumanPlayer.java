
public class HumanPlayer extends Player
{
	public HumanPlayer(String name)
	{
		setName(name);
	}
	
	public HumanPlayer(String name, Game g)
	{
		setName(name);
		setGame(g);
	}

	public Move promptMove()
	{
		if (getGame() instanceof StandardChessGame)
		{
			this.getGame().getHumanMove();
			return ((StandardChessGame) this.getGame()).getLastMove(); //not sure if there is a better way; probably need restructure
		}
		else
		{
			return null;
		}
	}
}
