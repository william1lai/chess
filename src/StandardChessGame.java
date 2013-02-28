
public class StandardChessGame extends Game
{
	private Board m_board;
	
	public StandardChessGame()
	{
		
	}
	
	public void init()
	{
		
	}
	
	public boolean isLegalMove(Move m)
	{
		return false; //stub
	}
	
	//moving to Game class from Board class, since the Board doesn't necessarily know rules of game
	public boolean inCheck(Definitions.Color color)
	{
		//this will check to see if the king of 'color' is threatened or not

		return false; //stub
	}
}
