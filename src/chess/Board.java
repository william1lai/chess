package chess;

public abstract class Board 
{
	private long m_white;
	private long m_black;
	private long m_pawns;
	private long m_knights;
	private long m_bishops;
	private long m_rooks;
	private long m_queens;
	private long m_kings;
	
	public abstract char getPiece(int row, int col);
	
	public abstract void removePiece(int row, int col);

	public abstract void move(Move m);
	
	public long getWhite()
	{
		return m_white;
	}
	
	public long getBlack()
	{
		return m_black;
	}
	
	public long getPawns()
	{
		return m_pawns;
	}
	
	public long getKnights()
	{
		return m_knights;
	}
	
	public long getBishops()
	{
		return m_bishops;
	}
	
	public long getRooks()
	{
		return m_rooks;
	}
	
	public long getQueens()
	{
		return m_queens;
	}
	
	public long getKings()
	{
		return m_kings;
	}
	
	public void setWhite(long white)
	{
		m_white = white;
	}
	
	public void setBlack(long black)
	{
		m_black = black;
	}
	
	public void setPawns(long pawns)
	{
		m_pawns = pawns;
	}
	
	public void setKnights(long knights)
	{
		m_knights = knights;
	}
	
	public void setBishops(long bishops)
	{
		m_bishops = bishops;
	}
	
	public void setRooks(long rooks)
	{
		m_rooks = rooks;
	}
	
	public void setQueens(long queens)
	{
		m_queens = queens;
	}
	
	public void setKings(long kings)
	{
		m_kings = kings;
	}
	
	public static boolean isLegal(int r, int c) //checks if square is within bounds of board
	{
		return ((r >= 0) && (r < Definitions.NUMROWS) && (c >= 0) && (c < Definitions.NUMCOLS));
	}
}
