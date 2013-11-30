package chess;

public class StandardChessBoard extends Board 
{
	private Definitions.State m_white_state;
	private Definitions.State m_black_state;

	private int m_whiteKingLoc;
	private int m_blackKingLoc;
	
	public StandardChessBoard()
	{
		super.init();
		m_white_state = Definitions.State.UNCHECKED;
		m_black_state = Definitions.State.UNCHECKED;
		m_whiteKingLoc = 74; //default locations
		m_blackKingLoc = 4;
	}
	
	public StandardChessBoard(StandardChessBoard other)
	{
		super.init();
		super.copyPieces(other);
		m_white_state = other.m_white_state;
		m_black_state = other.m_black_state;
		m_whiteKingLoc = other.m_whiteKingLoc;
		m_blackKingLoc = other.m_blackKingLoc;
	}

	public int getWhiteKingLoc()
	{
		return m_whiteKingLoc;
	}

	public int getBlackKingLoc()
	{
		return m_blackKingLoc;
	}

	public void updateKingLocs()
	{
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece p = getPiece(r, c);
				if (p instanceof King)
				{
					if (p.color() == Definitions.Color.WHITE)
					{
						m_whiteKingLoc = r*10 + c;
					}
					else
					{
						m_blackKingLoc = r*10 + c;
					}
				}
			}
		}
	}
	
	public Definitions.State getState(Definitions.Color color)
	{
		if (color == Definitions.Color.WHITE)
		{
			return m_white_state;
		}
		else
		{
			return m_black_state;
		}
	}

	public void setState(Definitions.Color color, Definitions.State state)
	{
		if (color == Definitions.Color.WHITE)
		{
			m_white_state = state;
		}
		else
		{
			m_black_state = state;
		}
	}

	public void move(Move m)
	{
		Piece temp = super.getPiece(m.r0, m.c0);
		if (temp instanceof King)
		{
			int sq = (m.rf * 10 + m.cf);
			if (temp.color() == Definitions.Color.WHITE)
			{
				m_whiteKingLoc = sq;
			}
			else
			{
				m_blackKingLoc = sq;
			}
		}

		m_white_state = Definitions.State.UNCHECKED;
		m_black_state = Definitions.State.UNCHECKED;

		super.move(m);
	}

	public StandardChessBoard clone()
	{
		return new StandardChessBoard(this);
	}
}
