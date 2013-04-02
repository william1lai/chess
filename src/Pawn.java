import java.util.ArrayList;

public class Pawn extends Piece
{
	public Pawn(int row, int col, Definitions.Color color)
	{
		super(row, col, color, "P");
	}
	
	public Pawn(Pawn other)
	{
		super(other.row(), other.col(), other.color(), "P");
	}
	
	public ArrayList<Move> moves()
	{
		//knows nothing of board, so just generate all possible moves given position
		
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		
		if (color() == Definitions.Color.WHITE)
		{
			if (row() == 6) //if on second rank
			{
				legalMoves.add(new Move(row(), col(), row() - 2, col())); //ability to move two spaces
			}
			legalMoves.add(new Move(row(), col(), row() - 1, col())); //one space move
			
			if (col() > 0)
			{
				legalMoves.add(new Move(row(), col(), row() - 1, col() - 1)); //capture northwest
			}
			if (col() < Definitions.NUMCOLS - 1)
			{
				legalMoves.add(new Move(row(), col(), row() - 1, col() + 1)); //capture northeast
			}
		}		
		else //Black
		{
			if (row() == 1) //if on second rank
			{
				legalMoves.add(new Move(row(), col(), row() + 2, col())); //ability to move two spaces
			}
			legalMoves.add(new Move(row(), col(), row() + 1, col())); //one space move
			
			if (col() > 0)
			{
				legalMoves.add(new Move(row(), col(), row() + 1, col() - 1)); //capture southwest
			}
			if (col() < Definitions.NUMCOLS - 1)
			{
				legalMoves.add(new Move(row(), col(), row() + 1, col() + 1)); //capture southeast
			}
		}
		
		return legalMoves;
	}

	public Piece clone()
	{
		return new Pawn(this);
	}
}
