import java.util.ArrayList;

public class Bishop extends Piece
{
	public Bishop(int row, int col, Definitions.Color color)
	{
		super(row, col, color, "B");
	}
	
	public Bishop(Bishop other)
	{
		super(other.row(), other.col(), other.color(), "B");
	}
	
	public ArrayList<Move> moves()
	{
		return getDiagonals();
	}
	
	public ArrayList<Move> threats()
	{
		return getDiagonals();
	}

	public Piece clone()
	{
		return new Bishop(this);
	}
}
