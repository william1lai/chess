import java.util.ArrayList;

public class King extends Piece
{
	King(int row, int col)
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
			return "WK";
		}
		else
		{
			return "BK";
		}
	}
	
}
