package chess.losers;

import java.util.ArrayList;
import java.util.HashMap;

import chess.ComputerPlayer;
import chess.Debug;
import chess.Definitions;
import chess.Game;
import chess.Move;
import chess.standard.StandardGame;

public class LosersComputerPlayer extends ComputerPlayer 
{
	private double [][] PawnVals = {
			{ 800, 800, 800, 800, 800, 800, 800, 800, },
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


	private int branches;
	private long m_starttime;
	private boolean stop;
	private HashMap<String, Move> m_book;
	private ArrayList<Move> hashmoves;


	public LosersComputerPlayer(String name, Definitions.Color c, Game g) 
	{
		super(name, c, g);
	}

	public void run()
	{
		Game g = getGame();
		initOpeningBook();
		m_move = evaluate(((LosersGame)g).getBoard());
		m_done = true;
	}

	public Move evaluate(LosersBoard lcb)
	{
		String completeFEN = lcb.toFEN(false);
		System.out.println(completeFEN);
		Move opening = m_book.get(completeFEN);
		if (opening != null)
			return opening; //found in book

		if (lcb.getState() != Definitions.State.NORMAL)
			return null;

		double highScore = staticEval(lcb);
		System.out.println("Current Score: " + highScore);
		MovelistScore bms = new MovelistScore(null, 0);
		hashmoves = new ArrayList<Move>();

		m_starttime = System.nanoTime();
		stop = false;
		for (int d = 1; d <= 10; d++)
		{
			long starttime = System.nanoTime();
			branches = 0;
			LosersBoard temp = lcb.clone();
			bms = new MovelistScore(alphabetaMax(temp, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, 2*d, true, false));
			long endtime = System.nanoTime();
			double duration = getDuration(starttime, endtime);
			highScore = bms.getScore();
			Debug.Log("Depth " + d + ": " + bms.getMovelist() + ", " + highScore + "; " + duration + " s, B-Factor: " + Math.pow(branches, 0.5/d));

			if (stop)
				break;

			hashmoves.clear();
			for (Move m : bms.getMovelist())
				hashmoves.add(m);

			if (highScore > MATE_SCORE)
				break; //if we found checkmate, don't look deeper
		}
		System.out.println();
		return hashmoves.get(0); //the best next move
	}

	private MovelistScore alphabetaMax(LosersBoard lcb, double alpha, double beta, 
			int ply, int maxply, boolean considerHashMoves, boolean extended)
	{
		if (stop) //time's up
			return null;
		if (getDuration(m_starttime, System.nanoTime()) > Definitions.MAXTHINKINGTIME)
		{
			stop = true;
			return null;
		}

		if (ply == maxply)
		{
			branches++;
			return new MovelistScore(null, staticEval(lcb));
		}

		ArrayList<Move> mvs = lcb.allMoves();
		double score;
		Move best = null;
		MovelistScore bms;
		ArrayList<Move> movelist = new ArrayList<Move>();

		if (mvs.isEmpty())
		{
			if (lcb.getState() == Definitions.State.CHECKMATE)
				return new MovelistScore(null, -MATE_SCORE);
			else if (lcb.getState() == Definitions.State.STALEMATE)
				return new MovelistScore(null, MATE_SCORE);
			else
			{
				branches++;
				return new MovelistScore(null, staticEval(lcb));
			}
		}

		if (considerHashMoves && hashmoves.size() > ply)
		{
			LosersBoard temp = lcb.clone();

			if (!lcb.isLegalMove(hashmoves.get(ply)))
			{
				considerHashMoves = false;
			}
			else
			{
				temp.move(hashmoves.get(ply));

				bms = alphabetaMin(temp, alpha, beta, ply + 1, maxply, true, extended);

				if (stop) //time's up
					return null;
				score = bms.getScore();

				if (score >= beta)
				{
					return new MovelistScore(movelist, beta); //fail hard beta-cutoff
				}
				if (score > alpha)
				{
					alpha = score;
					best = hashmoves.get(ply);
					movelist = new ArrayList<Move>(bms.getMovelist());
					movelist.add(0, best);
				}
			}
		}
		for (Move m : mvs)
		{			
			LosersBoard temp = lcb.clone();
			temp.move(m);

			bms = alphabetaMin(temp, alpha, beta, ply + 1, maxply, true, extended);

			if (stop) //time's up
				return null;
			score = bms.getScore();

			if (score >= beta)
			{
				return new MovelistScore(movelist, beta); //fail hard beta-cutoff
			}
			if (score > alpha)
			{
				alpha = score;
				best = m;
				movelist = new ArrayList<Move>(bms.getMovelist());
				movelist.add(0, best);
			}
		}
		if (stop) //time's up
			return null;

		if (extended && movelist.isEmpty()) //no further extensions, so evaluate here
		{
			branches++;
			return new MovelistScore(null, staticEval(lcb));
		}

		return new MovelistScore(movelist, alpha);
	}

	private MovelistScore alphabetaMin(LosersBoard lcb, double alpha, double beta, 
			int ply, int maxply, boolean considerHashMoves, boolean extended)
	{
		if (stop) //time's up
			return null;
		if (getDuration(m_starttime, System.nanoTime()) > Definitions.MAXTHINKINGTIME)
		{
			stop = true;
			return null;
		}

		if (ply == maxply)
		{			
			branches++;
			return new MovelistScore(null, -staticEval(lcb));
		}

		ArrayList<Move> mvs = lcb.allMoves();
		Move best = null;
		ArrayList<Move> movelist = new ArrayList<Move>();
		double score;
		LosersBoard temp;
		MovelistScore bms = new MovelistScore(null, 0);

		if (considerHashMoves && hashmoves.size() > ply)
		{
			temp = lcb.clone();

			if (!lcb.isLegalMove(hashmoves.get(ply)))
			{
				considerHashMoves = false;
			}
			else
			{			
				temp.move(hashmoves.get(ply));

				bms = alphabetaMax(temp, alpha, beta, ply + 1, maxply, true, extended);

				if (stop) //time's up
					return null;
				score = bms.getScore();

				if (score <= alpha)
				{
					return new MovelistScore(movelist, alpha); //fail hard beta-cutoff
				}
				if (score < beta)
				{
					beta = score;
					best = hashmoves.get(ply);
					movelist = new ArrayList<Move>(bms.getMovelist());
					movelist.add(0, best);
				}
			}
		}

		if (mvs.isEmpty())
		{
			if (lcb.getState() == Definitions.State.CHECKMATE)
				return new MovelistScore(null, MATE_SCORE);
			else if (lcb.getState() == Definitions.State.STALEMATE)
				return new MovelistScore(null, -MATE_SCORE);
			else
			{
				branches++;
				return new MovelistScore(null, -staticEval(lcb));
			}
		}

		for (Move m : mvs)
		{
			temp = lcb.clone();
			temp.move(m);

			bms = alphabetaMax(temp, alpha, beta, ply + 1, maxply, true, extended);

			if (stop) //time's up
				return null;
			score = bms.getScore();
			if (score <= alpha)
			{
				return new MovelistScore(movelist, alpha); //fail hard alpha-cutoff
			}
			if (score < beta)
			{
				beta = score;
				best = m;
				movelist = new ArrayList<Move>(bms.getMovelist());
				movelist.add(0, best);				
			}
		}
		if (stop) //time's up
			return null;

		if (extended && movelist.isEmpty()) //no further extensions, so evaluate here
		{
			branches++;
			return new MovelistScore(null, -staticEval(lcb));
		}		
		return new MovelistScore(movelist, beta);
	}

	private double staticEval(LosersBoard lcb)
	{
		if (getGame() instanceof StandardGame)
		{
			Definitions.State state = lcb.getState();
			if (state == Definitions.State.CHECKMATE) //instant loss
			{
				return -MATE_SCORE;
			}
			else if (state == Definitions.State.STALEMATE)
			{
				return MATE_SCORE;
			}
		}

		double score = 0.0;
		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				char p = lcb.getPiece(r, c);
				if (p != 0)
				{
					int rr;
					if (lcb.whoseTurn() == Definitions.Color.WHITE)
					{
						rr = r;
					}
					else
					{
						rr = 7 - r;
					}

					if (Character.isUpperCase(p) ^ (lcb.whoseTurn() == Definitions.Color.BLACK)) //turn matches piece color
					{
						if (Character.toLowerCase(p) == 'p')
						{
							score = score - 100 + PawnVals[rr][c];
						}
						else if (Character.toLowerCase(p) == 'n')
						{
							score = score - 325 + KnightVals[rr][c];
						}
						else if (Character.toLowerCase(p) == 'b')
						{
							score = score - 325 + BishopVals[rr][c];
						}
						else if (Character.toLowerCase(p) == 'r')
						{
							score = score - 500;
						}
						else if (Character.toLowerCase(p) == 'q')
						{
							score = score - 975;
						}
						else //King
						{
							score = score - 400 + KingVals[rr][c];
						}
					}
					else
					{
						if (Character.toLowerCase(p) == 'p')
						{
							score = score + 100 - PawnVals[7-rr][c];
						}
						else if (Character.toLowerCase(p) == 'n')
						{
							score = score + 325 - KnightVals[7-rr][c];
						}
						else if (Character.toLowerCase(p) == 'b')
						{
							score = score + 325 - BishopVals[7-rr][c];
						}
						else if (Character.toLowerCase(p) == 'r')
						{
							score = score + 500;
						}
						else if (Character.toLowerCase(p) == 'q')
						{
							score = score + 975;
						}
						else //King
						{
							score = score + 400 - KingVals[7-rr][c];
						}
					}
				}
			}
		}

		return score / 100;
	}


	public void initOpeningBook() 
	{
		m_book = new HashMap<String, Move>();
		//White
		m_book.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - -", new Move(6, 2, 5, 2)); //1.c3

		//Black
		m_book.put("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b - e3", new Move(1, 1, 3, 1)); //1.e4 b5
		m_book.put("rnbqkbnr/pppppppp/8/8/8/4P3/PPPP1PPP/RNBQKBNR b - e3", new Move(1, 1, 3, 1)); //1.e3 b5
		m_book.put("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b - d3", new Move(1, 6, 3, 6)); //1.d4 g5
	}
}
