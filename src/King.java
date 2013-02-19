import java.util.ArrayList;

public class King extends Piece
{
	King(int row, int col)
	{
		super(row, col);
	}

	public ArrayList<Move> moves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		for (int r = row() - 1; r <= row() + 1; r++)
		{
			for (int c = col() - 1; c <= col() + 1; c++)
			{
				if (Board.isLegal(r, c))
				{
					if ((r != row()) || (c != col())) //not same square
					{
						legalMoves.add(new Move(row(), col(), r, c));
					}
				}
			}
		}
		return legalMoves;
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
