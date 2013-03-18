import java.util.ArrayList;

public class Bishop extends Piece
{
	public Bishop(int row, int col)
	{
		super(row, col);
	}
	
	public Bishop(Bishop other)
	{
		super(other.row(), other.col());
	}
	
	public ArrayList<Move> moves()
	{
		return getDiagonals();
	}
	
	public String toString()
	{
		if (color() == Definitions.Color.WHITE)
		{
			return "WB";
		}
		else
		{
			return "BB";
		}
	}

	public Piece clone()
	{
		return new Bishop(this);
	}
}
