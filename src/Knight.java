import java.util.ArrayList;

public class Knight extends Piece
{
	Knight(int row, int col)
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
			return "WN";
		}
		else
		{
			return "BN";
		}
	}

}
