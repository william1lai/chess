public class Board 
{
	private Piece[][] m_board;
	
	Board()
	{
		init();
	}

	public void init()
	{
		//stub
	}

	public Piece getPiece(int row, int col)
	{
		//stub; still have to check for bad input for row and col
		return m_board[row][col];
	}

	public void move(Move m)
	{
		//stub
	}

	public boolean inCheck(Definitions.Color color)
	{
		return false; //stub
	}
	
	public String toString()
	{
		return ""; //stub; instead, should print out board
	}

	//What else does a board need to have?
	
	
}
