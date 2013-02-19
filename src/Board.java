public class Board 
{
	private Piece[][] m_board;
	
	Board()
	{
		init();
	}

	public void init()
	{
		m_board = new Piece[Definitions.NUMROWS][Definitions.NUMCOLS];
		//we don't put pieces on board, because game may have different set-up
	}
	
	public void placePiece(Piece p, int row, int col)
	{
		//need to consider: should we check if location is already occupied
		//	or should that be up to the caller?
		
		m_board[row][col] = p;
		p.setPos(row, col); //in case it wasn't already done for us
	}

	public Piece getPiece(int row, int col)
	{
		//stub; still have to check for bad input for row and col
		return m_board[row][col];
	}

	public void move(Move m)
	{
		//should we check for legality or should we assume that it has already been checked?
		//probably should assume, since only Game class knows legality rules
		
		Piece temp = m_board[m.r0][m.c0];
		m_board[m.r0][m.c0] = null;
		m_board[m.rf][m.cf] = temp;
	}

	public boolean inCheck(Definitions.Color color)
	{
		//this will check to see if the king of 'color' is threatened or not
		
		return false; //stub
	}
	
	public String toString()
	{	//prints board position
		String pic = ""; //ugly variable name, needs better name
		
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				if (m_board[r][c] == null) //if square is empty
				{
					pic = pic + ' ';
				}
				else
				{
					pic = pic + m_board[r][c].toString();
				}
			}
			pic = pic + '\n';
		}		
		return pic;
	}
	
	public static boolean isLegal(int r, int c) //checks if square is within bounds of board
	{
		return ((r >= 0) && (r < Definitions.NUMROWS) && (c >= 0) && (c < Definitions.NUMCOLS));
	}

	//What else does a board need to have?
	
}
