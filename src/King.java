import java.util.ArrayList;

//TODO: Need to implement castling in moves() function

public class King extends Piece
{
	public King(int row, int col, Definitions.Color color)
	{
		super(row, col, color, "K");
	}
	
	public King(King other)
	{
		super(other.row(), other.col(), other.color(), "K");
	}

	public ArrayList<Move> getMoves()
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
		
		int castlingRow;
		if (color() == Definitions.Color.WHITE)
		{
			castlingRow = 7;
		}
		else //BLACK
		{
			castlingRow = 0;
		}
		legalMoves.add(new Move(row(), col(), castlingRow, 6)); //kingside castling
		legalMoves.add(new Move(row(), col(), castlingRow, 2)); //queenside castling
		
		return legalMoves;
	}
	
	public ArrayList<Move> getThreats()
	{
		return getMoves();
	}
	
	public Piece clone()
	{
		return new King(this);
	}

}
