package chess;
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

	public ArrayList<Move> getMoves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		legalMoves.addAll(getDiagonals());
		legalMoves.addAll(getOrthogonals());

		return legalMoves;
	}
	
	public ArrayList<Move> getThreats()
	{
		return getMoves();
	}
	
	public Piece clone()
	{
		return new Queen(this);
	}
}