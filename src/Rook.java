import java.util.ArrayList;

public class Rook extends Piece
{
	Rook(int row, int col)
	{
		super(row, col);
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
}
