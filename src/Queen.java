import java.util.ArrayList;

public class Queen extends Piece
{
	public Queen(int row, int col)
	{
		super(row, col);
	}
	
	public Queen(Queen other)
	{
		super(other.row(), other.col());
	}

	public ArrayList<Move> moves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		legalMoves.addAll(getDiagonals());
		legalMoves.addAll(getOrthogonals());

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
	
	public Piece clone()
	{
		return new Queen(this);
	}
}