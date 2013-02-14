import java.util.ArrayList;

public class Pawn extends Piece
{
	Pawn(int row, int col)
	{
		super(row, col);
	}
	
	public ArrayList<Move> moves()
	{
		return null; //stub
	}
	
	public String toString()
	{
		if (color() == Definitions.Color.WHITE)
		{
			return "WP";
		}
		else
		{
			return "BP";
		}
	}

}
