import java.util.ArrayList;

public abstract class Piece 
{
	private int m_row;
	private int m_col;
	private Definitions.Color m_color;
	private String m_name;
	
	public Piece(int row, int col, Definitions.Color color, String name)
	{
		m_row = row;
		m_col = col;
		m_color = color;
		m_name = name;
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
	
	public ArrayList<Move> getOrthogonals()
	{
		ArrayList<Move> orthogonals = new ArrayList<Move>();
		
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{ 	//get all legal squares in this column
			if (r != row()) //if not same square
			{
				orthogonals.add(new Move(row(), col(), r, col()));
			}
		}

		for (int c = 0; c < Definitions.NUMCOLS; c++)
		{	//get all legal squares in this row
			if (c != col()) //if not same square
			{
				orthogonals.add(new Move(row(), col(), row(), c));
			}
		}
		
		return orthogonals;
	}
	
	public ArrayList<Move> getDiagonals()
	{
		ArrayList<Move> diagonals = new ArrayList<Move>();
		
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			if (r != row()) //make sure not same square
			{
				int leftc = col() - (row() - r); //the column on the NWSE diagonal
				if (Board.isLegal(r, leftc))
				{
					diagonals.add(new Move(row(), col(), r, leftc));
					//System.out.println(r + " " + leftc); //for testing
				}

				int rightc = col() + (row() - r); //the column on the NESW diagonal
				if (Board.isLegal(r, rightc))
				{
					diagonals.add(new Move(row(), col(), r, rightc));
					//System.out.println(r + " " + rightc); //for testing
				}
			}
		}
		return diagonals;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	public String toString()
	{
		if (m_color == Definitions.Color.WHITE) return "W" + getName();
		return "B" + getName();
	}
	
	public abstract ArrayList<Move> moves();
	public abstract Piece clone();
}
