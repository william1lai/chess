import java.util.ArrayList;

public class Queen extends Piece
{
	Queen(int row, int col)
	{
		super(row, col);
	}
	
	public ArrayList<Move> moves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{ 	//get all legal squares in this column
			if (r != row()) //if not same square
			{
				legalMoves.add(new Move(row(), col(), r, col()));
			}
		}
		
		for (int c = 0; c < Definitions.NUMCOLS; c++)
		{	//get all legal squares in this row
			if (c != col()) //if not same square
			{
				legalMoves.add(new Move(row(), col(), row(), c));
			}
		}
		
		//still need to do diagonals
		
		return legalMoves;
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
