package chess;

import java.util.*;


public class ComputerPlayer extends Player
{
	private static final double MATE_SCORE = 9999.9999;
	
	final class MovelistScore
	{
		private ArrayList<Move> m_movelist;
		private double m_score;
		
		public MovelistScore(ArrayList<Move> mlist, double score)
		{
			m_movelist = new ArrayList<Move>();
			if (mlist != null)
			{
				for (Move m : mlist)
					m_movelist.add(m);
			}
			m_score = score;
		}
		
		public MovelistScore(MovelistScore other)
		{
			m_movelist = new ArrayList<Move>();
			if (other.m_movelist != null)
			{
				for (Move m : other.m_movelist)
					m_movelist.add(m);
			}
			m_score = other.m_score;
		}
		
		public ArrayList<Move> getMovelist()
		{
			return m_movelist;
		}
		
		public double getScore()
		{
			return m_score;
		}
		
		public void replaceMove(Move m)
		{
			//m_movelist.remove(m_movelist.size() - 1);
			m_movelist.remove(0);
			m_movelist.add(0, m);
		}
		
		public void addMove(Move m)
		{
			m_movelist.add(0, m);
		}
		
		public void setScore(double score)
		{
			m_score = score;
		}
		
		public String toString()
		{
			if (m_move == null)
				return "No move";
			
			return ("[" + m_movelist.toString() + ", " + m_score + "]");
		}
	}

	private HashMap<String, Move> m_book;
	
	private ArrayList<Move> hashmoves;
	
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
		initOpeningBook();
		m_move = evaluate(g, g.whoseTurn(), g.getBoard(), Definitions.DEPTH);
		m_done = true;
	}
	
	public void initOpeningBook()
	{
		m_book = new HashMap<String, Move>();
		
		//White
		m_book.put("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq 0", new Move(6, 4, 4, 4)); //1.e4
		m_book.put("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6", new Move(7, 1, 5, 2)); //1.e4 c5 2.Nc3
		m_book.put("r1bqkbnr/pp1ppppp/2n5/2p5/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq -", new Move(6, 5, 4, 5)); //1.e4 c5 2.Nc3 Nc6 3.f4
		m_book.put("rnbqkbnr/pp1p1ppp/4p3/2p5/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq -", new Move(6, 5, 4, 5)); //1.e4 c5 2.Nc3 e6 3.f4
		m_book.put("rnbqkbnr/pp2pppp/3p4/2p5/4P3/2N5/PPPP1PPP/R1BQKBNR w KQkq -", new Move(6, 5, 4, 5)); //1.e4 c5 2.Nc3 d6 3.f4
		m_book.put("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6", new Move(7, 6, 5, 5)); //1.e4 e5 2.Nf3
		m_book.put("r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -", new Move(6, 5, 3, 1)); //1.e4 e5 2.Nf3 Nc6 3.Bb5
				
		//Black
		m_book.put("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3", new Move(1, 4, 3, 4)); //1.e4 e5
		m_book.put("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -", new Move(0, 1, 2, 2)); //1.e4 e5 2.Nf3 Nc6		
		m_book.put("r1bqkbnr/pppp1ppp/2n5/1B2p3/4P3/5N2/PPPP1PPP/RNBQK2R b KQkq -", new Move(1, 5, 3, 5)); //1.e4 e5 2.Nf3 Nc6 3.Bb5 f5
		m_book.put("rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3", new Move(1, 5, 3, 5)); //1.d4 f5
		/*m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));
		m_book.put("", new Move(6, 4, 4, 4));*/
	}
	
	public Move evaluate(StandardChessGame g, Definitions.Color turn, StandardChessBoard scb, int depth)
	{
		String completeFEN = scb.toFEN() + " " + g.getFEN(false);
		System.out.println(completeFEN);
		Move opening = m_book.get(completeFEN);
		if (opening != null)
			return opening; //found in book
		
		double highScore = staticEval(turn, scb);
		System.out.println("Current Score: " + highScore);
		MovelistScore bms = new MovelistScore(null, 0);
		hashmoves = new ArrayList<Move>();
		
		for (int d = 1; d <= depth; d++)
		{
			long starttime = System.nanoTime();			
			bms = new MovelistScore(alphabetaMax(g, turn, scb, highScore - 5.0,
					Double.POSITIVE_INFINITY, 0, 2*d, true));
			long endtime = System.nanoTime();
			double duration = ((endtime - starttime) / 100000) / 10000.0;
			highScore = bms.getScore();
			System.out.println("Depth " + d + ": " + bms.getMovelist() + ", " + highScore + "; " + duration + " s");
						
			hashmoves.clear();
			for (Move m : bms.getMovelist())
				hashmoves.add(m);
			
			if (highScore == MATE_SCORE)
				break; //if we found checkmate, don't look deeper
		}
		System.out.println();
		return hashmoves.get(0); //the best next move
	}

	private MovelistScore alphabetaMax(StandardChessGame g, Definitions.Color turn, StandardChessBoard scb, 
			double alpha, double beta, int ply, int maxply, boolean considerHashMoves)
	{
		if (ply == maxply)
		{
			if (g.inCheck(turn, scb))
				maxply++;
			else
				return new MovelistScore(null, staticEval(turn, scb));
		}
		
		double score;
		Move best = null;
		MovelistScore bms;
		ArrayList<Move> movelist = new ArrayList<Move>();
		ArrayList<Move> mvs = g.allMoves(turn, scb);
		ArrayList<Move> capts = new ArrayList<Move>();
		
		if (mvs.isEmpty())
			return new MovelistScore(null, staticEval(turn, scb));
		
		for (int i = 0; i < mvs.size(); i++)
		{
			Move m = mvs.get(i);
			if (scb.getPiece(m.rf, m.cf) != null)
			{
				mvs.remove(m);
				capts.add(m);
				i--;
			}
		}
		
		if (considerHashMoves && hashmoves.size() > ply)
		{
			StandardChessBoard temp = scb.clone();
			temp.move(hashmoves.get(ply));
			bms = alphabetaMin(g, turn, temp, alpha, beta, ply + 1, maxply, true);
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
		for (Move m : capts) //captures are more forcing, so look at them first to help alpha-beta pruning
		{
			StandardChessBoard temp = scb.clone();
			temp.move(m);
			bms = alphabetaMin(g, turn, temp, alpha, beta, ply + 1, maxply, false);
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
		for (Move m : mvs)
		{
			StandardChessBoard temp = scb.clone();
			temp.move(m);
			bms = new MovelistScore(alphabetaMin(g, turn, temp, alpha, beta, ply + 1, maxply, false));
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
		return new MovelistScore(movelist, alpha);
	}
	
	private MovelistScore alphabetaMin(StandardChessGame g, Definitions.Color turn, StandardChessBoard scb, 
			double alpha, double beta, int ply, int maxply, boolean considerHashMoves)
	{
		if (ply == maxply)
		{
			if (g.inCheck(turn, scb))
				maxply++;
			else
				return new MovelistScore(null, -staticEval(Definitions.flip(turn), scb));
		}
		Move best = null;
		ArrayList<Move> movelist = new ArrayList<Move>();
		double score;
		StandardChessBoard temp;
		MovelistScore bms = new MovelistScore(null, 0);
		
		if (considerHashMoves && hashmoves.size() > ply)
		{
			temp = scb.clone();
			temp.move(hashmoves.get(ply));
			bms = alphabetaMax(g, turn, temp, alpha, beta, ply + 1, maxply, true);
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
		
		ArrayList<Move> mvs = g.allMoves(Definitions.flip(turn), scb);
		if (mvs.isEmpty())
		{
			return new MovelistScore(null, -staticEval(Definitions.flip(turn), scb));
		}
		
		for (Move m : mvs)
		{
			temp = scb.clone();
			temp.move(m);
			bms = alphabetaMax(g, turn, temp, alpha, beta, ply + 1, maxply, false);
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
		return new MovelistScore(movelist, beta);
	}
	
	private double staticEval(Definitions.Color color, StandardChessBoard b)
	{
		if (getGame() instanceof StandardChessGame)
		{
			StandardChessGame g = (StandardChessGame)getGame();
			Definitions.State their_state = g.getState(Definitions.flip(color), b);
			Definitions.State my_state = g.getState(color, b);
			if (my_state == Definitions.State.CHECKMATE) //instant loss
			{
				return -MATE_SCORE;
			}
			else if (my_state == Definitions.State.STALEMATE)
			{
				return 0.0;
			}
			else if (their_state == Definitions.State.CHECKMATE) //instant win
			{
				return MATE_SCORE;
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
