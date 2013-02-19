import java.util.ArrayList;

public class Bishop extends Piece
{
	Bishop(int row, int col)
	{
		super(row, col);
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

}
