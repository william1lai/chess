import java.util.*;

public class ComputerPlayer extends Player
{
	private double [][] PawnVals = {
			{	0,   0,   0,   0,   0,   0,   0,   0, },
			{	5,  10,  15,  20,  20,  15,  10,   5, },
			{	4,   8,  12,  16,  16,  12,   8,   4, },
			{	3,   6,   9,  12,  12,   9,   6,   3, },
			{	2,   4,   6,   8,   8,   6,   4,   2, },
			{	1,   2,   3, -10, -10,   3,   2,   1, },
			{	0,   0,   0, -40, -40,   0,   0,   0, },
			{	0,   0,   0,   0,   0,   0,   0,   0  } };
	
	private double [][] KnightVals = {	
			{	-10, -10, -10, -10, -10, -10, -10, -10, },
			{	-10,   0,   0,   0,   0,   0,   0, -10, },
			{	-10,   0,   5,   5,   5,   5,   0, -10, },
			{	-10,   0,   5,  10,  10,   5,   0, -10, },
			{	-10,   0,   5,  10,  10,   5,   0, -10, },
			{	-10,   0,   5,   5,   5,   5,   0, -10, },
			{	-10,   0,   0,   0,   0,   0,   0, -10, },
			{	-10, -30, -10, -10, -10, -10, -30, -10  } };
	
	private double [][] BishopVals = {	
			{	-10, -10, -10, -10, -10, -10, -10, -10, },
			{	-10,   0,   0,   0,   0,   0,   0, -10, },
			{	-10,   0,   5,   5,   5,   5,   0, -10, },
			{	-10,   0,   5,  10,  10,   5,   0, -10, },
			{	-10,   0,   5,  10,  10,   5,   0, -10, },
			{	-10,   0,   5,   5,   5,   5,   0, -10, },
			{	-10,   0,   0,   0,   0,   0,   0, -10, },
			{	-10, -10, -20, -10, -10, -20, -10, -10  } };
				
	private double [][] KingVals = {	
			{	-40, -40, -40, -40, -40, -40, -40, -40, },
			{	-40, -40, -40, -40, -40, -40, -40, -40, },
			{	-40, -40, -40, -40, -40, -40, -40, -40, },
			{	-40, -40, -40, -40, -40, -40, -40, -40, },
			{	-40, -40, -40, -40, -40, -40, -40, -40, },
			{	-40, -40, -40, -40, -40, -40, -40, -40, },
			{	-20, -20, -20, -20, -20, -20, -20, -20, },
			{	  0,  20,  40, -20,   0, -20,  40,  20  } };
	
	public ComputerPlayer(String name)
	{
		setName(name);
	}
	
	public ComputerPlayer(String name, Game g)
	{
		setName(name);
		setGame(g);
	}

	public Move promptMove()
	{
		if (getGame() instanceof StandardChessGame)
		{
			Move m = evaluate((StandardChessGame) getGame());
			((StandardChessGame) getGame()).processMove(m);
			return m;
		}
		else
		{
			return null;
		}
	}
	
	public Move evaluate(StandardChessGame g)
	{
		ArrayList<Move> mvs = g.allMoves(g.whoseTurn(), g.getBoard());
		Move best = new Move(0, 0, 0, 0);
		double highScore = Double.NEGATIVE_INFINITY;
		
		Board t = g.getBoard().clone();
		System.out.println("Current Score: " + this.staticEval(g.whoseTurn(), t));
		
		for (Move m : mvs)
		{
			Board temp = g.getBoard().clone();
			temp.move(m);
			double score = this.staticEval(g.whoseTurn(), temp);
			
			//System.out.println(m + ", Score: " + score);
			
			if (score > highScore)
			{
				highScore = score;
				best = m;
			}
		}
		
		System.out.println(best + ", High Score: " + highScore);
		return best;
	}
	
	private double staticEval(Definitions.Color color, Board b)
	{
		double score = 0.0;
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				Piece p = b.getPiece(r, c);
				if (p != null)
				{
					int rr;
					if (color == Definitions.Color.WHITE)
					{
						rr = r;
					}
					else
					{
						rr = 7 - r;
					}
					
					if (p.color() == color)
					{
						if (p instanceof Pawn)
						{
							score = score + 100 + PawnVals[rr][c];
						}
						else if (p instanceof Knight)
						{
							score = score + 325 + KnightVals[rr][c];
						}
						else if (p instanceof Bishop)
						{
							score = score + 325 + BishopVals[rr][c];
						}
						else if (p instanceof Rook)
						{
							score = score + 500;
						}
						else if (p instanceof Queen)
						{
							score = score + 975;
						}
						else //King
						{
							score = score + KingVals[rr][c];
						}
					}
					else
					{
						if (p instanceof Pawn)
						{
							score = score - 100 - PawnVals[7-rr][c];
						}
						else if (p instanceof Knight)
						{
							score = score - 325 - KnightVals[7-rr][c];
						}
						else if (p instanceof Bishop)
						{
							score = score - 325 - BishopVals[7-rr][c];
						}
						else if (p instanceof Rook)
						{
							score = score - 500;
						}
						else if (p instanceof Queen)
						{
							score = score - 975;
						}
						else //King
						{
							score = score - KingVals[7-rr][c];
						}
					}
				}
			}
		}
		
		return score / 100;
	}
}
