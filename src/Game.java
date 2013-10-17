import java.applet.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public abstract class Game extends Applet implements Runnable, MouseListener
{
	protected Player p1;
	protected Player p2;
	private Definitions.Color m_turn;
	
	public abstract boolean isLegalMove(Move m, Board b, Definitions.Color color);

	public Definitions.Color whoseTurn()
	{
		return m_turn;
	}	
	
	public void setTurn(Definitions.Color nextTurn)
	{
		m_turn = nextTurn;
	}
	
}
