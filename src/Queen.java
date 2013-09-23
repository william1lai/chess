import java.util.ArrayList;

public class Queen extends Piece
{
	public Queen(int row, int col, Definitions.Color color)
	{
		super(row, col, color, "Q");
	}
	
	public Queen(Queen other)
	{
		super(other.row(), other.col(), other.color(), "Q");
	}

	public ArrayList<Move> moves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		legalMoves.addAll(getDiagonals());
		legalMoves.addAll(getOrthogonals());

		return legalMoves;
	}
	
	public ArrayList<Move> threats()
	{
		return moves();
	}
	
	public Piece clone()
	{
		return new Queen(this);
	}
}