import java.util.ArrayList;

public class Knight extends Piece
{
	Knight(int row, int col)
	{
		super(row, col);
	}

	public ArrayList<Move> moves()
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
