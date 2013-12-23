package chess;

public abstract class Board 
{
	public abstract char getPiece(int row, int col);
	
	public abstract void removePiece(int row, int col);

	public abstract void move(Move m);
	
	public static boolean isLegal(int r, int c) //checks if square is within bounds of board
	{
		return ((r >= 0) && (r < Definitions.NUMROWS) && (c >= 0) && (c < Definitions.NUMCOLS));
	}
}
