package chess;

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
	
	public ComputerPlayer(String name, Definitions.Color c, Game g)
	{
		setName(name);
		setGame(g);
		setColor(c);
	}

	public void promptMove()
	{
		if (getGame() instanceof StandardChessGame)
		{
			m_done = false;
			m_move = null;
			(new Thread(this)).start();
		}
	}

	public void run()
	{
		StandardChessGame g = (StandardChessGame)getGame();
		m_move = evaluate(g, g.whoseTurn(), g.getBoard(), Definitions.PLY_DEPTH);
		m_done = true;
	}
	
	public Move evaluate(StandardChessGame g, Definitions.Color turn, Board b, int depth) //uses brute force
	{
		ArrayList<Move> mvs = g.allMoves(turn, b);
		Move best = null;
		double highScore = Double.NEGATIVE_INFINITY;
		
		for (Move m : mvs)
		{
			Board temp = b.clone();
			temp.move(m);
			
			double score;
			
			if (depth > 1)
			{
				Move subBest = this.evaluate(g, Definitions.flip(turn), temp, depth - 1);
				temp.move(subBest);
				score = this.staticEval(turn, temp);
			}
			else
			{
				score = this.staticEval(turn, temp);
			}
			
			//if (depth == Definitions.PLY_DEPTH)
				//System.out.println(m + ", Score: " + score);
			
			if (score > highScore || highScore == Double.NEGATIVE_INFINITY)
			{
				highScore = score;
				best = m;
			}
		}
		
		return best;
	}
	
	private double staticEval(Definitions.Color color, Board b)
	{
		if (getGame() instanceof StandardChessGame)
		{
			StandardChessGame g = (StandardChessGame)getGame();
			Definitions.State their_state = g.getState(Definitions.flip(color), b);
			Definitions.State my_state = g.getState(color, b);
			if (my_state == Definitions.State.CHECKMATE) //instant loss
			{
				return Double.NEGATIVE_INFINITY;
			}
			else if (my_state == Definitions.State.STALEMATE)
			{
				return 0.0;
			}
			else if (their_state == Definitions.State.CHECKMATE) //instant win
			{
				return Double.POSITIVE_INFINITY;
			}
			
		}
		
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
