public class Board 
{
	private Piece[][] m_board;

	public Board()
	{
		init();
	}

	public Board(Board other) 
	{
		init();
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece otherPiece = other.getPiece(r,c);
				if (otherPiece != null) {
					this.placePiece(otherPiece.clone(), r, c);					
				}
			}
		}
	}

	public void init()
	{
		m_board = new Piece[Definitions.NUMROWS][Definitions.NUMCOLS];
		//we don't put pieces on board, because game may have different set-up
	}

	public boolean isLegalSquare(int row, int col)
	{
		return ((row >= 0) && (row < Definitions.NUMROWS) && (col >= 0) && (col < Definitions.NUMCOLS));
	}

	public void removePiece(int row, int col) //mostly for en passant
	{
		m_board[row][col] = null; //delete any piece that is here
	}
	
	public void placePiece(Piece p, int row, int col)
	{
		//need to consider: should we check if location is already occupied
		//	or should that be up to the caller?
		// - Probably up to caller

		m_board[row][col] = p;
		p.setPos(row, col); //in case it wasn't already done for us
	}

	public Piece getPiece(int row, int col)
	{
		if (isLegalSquare(row, col))
		{
			return m_board[row][col];
		}
		else
		{
			return null;
		}
	}

	public void move(Move m)
	{
		//should we check for legality or should we assume that it has already been checked?
		//probably should assume, since only Game class knows legality rules
		
		Piece temp = m_board[m.r0][m.c0];
		m_board[m.r0][m.c0] = null;
		placePiece(temp, m.rf, m.cf);
	}

	public String toString() //prints board position
	{	
		String diagram = "";

		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				if (m_board[r][c] == null) //if square is empty
				{
					diagram = diagram + ' ';
				}
				else
				{
					diagram = diagram + m_board[r][c].toString();
				}
			}
			diagram = diagram + '\n';
		}		
		return diagram;
	}

	public static boolean isLegal(int r, int c) //checks if square is within bounds of board
	{
		return ((r >= 0) && (r < Definitions.NUMROWS) && (c >= 0) && (c < Definitions.NUMCOLS));
	}

	public Board clone()
	{
		return new Board(this);
	}
}
