import java.applet.*;
import java.awt.event.*;

public abstract class Game extends Applet implements Runnable, MouseListener
{
	private Player p1;
	private Player p2;
	private Definitions.Color m_turn;
	
	public abstract boolean isLegalMove(Move m);
	
	
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
	
	public void setTurn(Definitions.Color nextTurn)
	{
		m_turn = nextTurn;
	}

}
