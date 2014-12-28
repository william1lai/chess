package chess;

import java.util.ArrayList;

import chess.alice.AliceGame;
import chess.losers.LosersGame;
import chess.standard.StandardGame;

public abstract class ComputerPlayer extends Player
{
	protected static final double MATE_SCORE = 9999.9999;

	public final class MovelistScore
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
			if (other == null)
				return;

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

	public ComputerPlayer(String name, Definitions.Color c, Game g)
	{
		setName(name);
		setColor(c);
		setGame(g);
	}

	public void promptMove()
	{		
		if (getGame() instanceof StandardGame || getGame() instanceof LosersGame || getGame() instanceof AliceGame)
		{
			m_done = false;
			m_move = null;
			(new Thread(this)).start();
		}
	}

	public void run() {}

	public abstract void initOpeningBook();

	protected double getDuration(long starttime, long endtime)
	{
		return ((endtime - starttime) / 100000) / 10000.0;
	}
}