import java.util.ArrayList;

public class Rook extends Piece
{
	public Rook(int row, int col)
	{
		super(row, col);
	}
	
	public Rook(Rook other)
	{
		super(other.row(), other.col());
	}
	
	public ArrayList<Move> moves()
	{
		return getOrthogonals();
	}

	public String toString()
	{
		if (color() == Definitions.Color.WHITE)
		{
			return "WR";
		}
		else
		{
			return "BR";
		}
	}
	
	public Piece clone()
	{
		return new Rook(this);
	}
}
