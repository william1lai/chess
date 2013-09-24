import java.util.ArrayList;

public class Knight extends Piece
{
	public Knight(int row, int col, Definitions.Color color)
	{
		super(row, col, color, "N");
	}
	
	public Knight(Knight other)
	{
		super(other.row(), other.col(), other.color(), "N");
	}

	public ArrayList<Move> getMoves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		if (Board.isLegal(row() - 2, col() - 1))
		{
			legalMoves.add(new Move(row(), col(), row() - 2, col() - 1));
		}

		if (Board.isLegal(row() - 2, col() + 1))
		{
			legalMoves.add(new Move(row(), col(), row() - 2, col() + 1));
		}

		if (Board.isLegal(row() + 2, col() - 1))
		{
			legalMoves.add(new Move(row(), col(), row() + 2, col() - 1));
		}

		if (Board.isLegal(row() + 2, col() + 1))
		{
			legalMoves.add(new Move(row(), col(), row() + 2, col() + 1));
		}

		if (Board.isLegal(row() - 1, col() - 2))
		{
			legalMoves.add(new Move(row(), col(), row() - 1, col() - 2));
		}

		if (Board.isLegal(row() - 1, col() + 2))
		{
			legalMoves.add(new Move(row(), col(), row() - 1, col() + 2));
		}

		if (Board.isLegal(row() + 1, col() - 2))
		{
			legalMoves.add(new Move(row(), col(), row() + 1, col() - 2));
		}

		if (Board.isLegal(row() + 1, col() + 2))
		{
			legalMoves.add(new Move(row(), col(), row() + 1, col() + 2));
		}

		return legalMoves;
	}
	
	public ArrayList<Move> getThreats()
	{
		return moves();
	}
	
	public Piece clone()
	{
		return new Knight(this);
	}
}
