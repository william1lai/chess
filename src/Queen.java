import java.util.ArrayList;

public class Queen extends Piece
{
	Queen(int row, int col)
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
			return "WQ";
		}
		else
		{
			return "BQ";
		}
	}
}
