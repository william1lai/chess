import java.util.ArrayList;

public abstract class Piece 
{
	private int m_row;
	private int m_col;
	private Definitions.Color m_color;
	
	Piece(int row, int col)
	{
		m_row = row;
		m_col = col;
	}
	
	public int row()
	{
		return m_row;
	}
	
	public int col()
	{
		return m_col;
	}
	
	public void setPos(int row, int col)
	{
		m_row = row;
		m_col = col;
	}

	public Definitions.Color color()
	{
		return m_color;
	}
	
	public abstract ArrayList<Move> moves();
}
