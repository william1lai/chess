package chess.losers;

import java.util.ArrayList;
//import java.util.HashMap;

import javax.swing.JOptionPane;

import chess.Board;
import chess.Definitions;
import chess.HumanPlayer;
import chess.Move;
import chess.Player;

/* Rules: No castling, no checks, no checkmate
 * 	Pawns may promote to Kings also
 * 	Stalemate is won by the player who is stalemated
 * 	En passant is legal
 */
public class LosersBoard extends Board 
{
	public class GameData
	{
		public int m_enpassantCol; //the column (0-7) of the pawn to move two spaces last turn, -1 if no pawn moved two spaces
		public int m_fiftymoverulecount;
		public int m_turncount;
		public LosersGame m_game;
		//public HashMap<String, Integer> positionTable;

		public GameData(LosersGame lcg)
		{
			m_enpassantCol = -1;
			m_fiftymoverulecount = 0;
			m_turncount = 0;
			m_game = lcg;
		}

		public GameData(GameData other)
		{
			m_enpassantCol = other.m_enpassantCol;
			m_fiftymoverulecount = other.m_fiftymoverulecount;
			m_turncount = other.m_turncount;
			m_game = other.m_game;
		}
	};
	private GameData m_data;
	
	private Definitions.Color m_turn;
	
	
	public LosersBoard(LosersGame lcg) //standard setup
	{
		m_turn = Definitions.Color.WHITE;
		m_data = new GameData(lcg);

		setWhite(0x000000000000FFFFL);
		setBlack(0xFFFF000000000000L);
		setPawns(0x00FF00000000FF00L);
		setKnights(0x4200000000000042L);
		setBishops(0x2400000000000024L);
		setRooks(0x8100000000000081L);
		setQueens(0x1000000000000010L);
		setKings(0x0800000000000008L);
	}

	public LosersBoard(LosersBoard other)
	{
		m_turn = other.m_turn;
		m_data = new GameData(other.m_data);

		setWhite(other.getWhite());
		setBlack(other.getBlack());
		setPawns(other.getPawns());
		setKnights(other.getKnights());
		setBishops(other.getBishops());
		setRooks(other.getRooks());
		setQueens(other.getQueens());
		setKings(other.getKings());
	}

	public GameData getData()
	{
		return m_data;
	}
	
	public char getPiece(int r, int c) //returns 0 if no piece exists
	{
		long bit = 1L << ((7-r)*8 + (7-c));
		boolean isWhite = false;
		if ((bit & getWhite()) != 0)
		{
			isWhite = true;
		}
		if ((bit & getPawns()) != 0)
		{
			if (isWhite)
				return 'P';
			else
				return 'p';
		}
		if ((bit & getKnights()) != 0)
		{
			if (isWhite)
				return 'N';
			else
				return 'n';
		}
		if ((bit & getBishops()) != 0)
		{
			if (isWhite)
				return 'B';
			else
				return 'b';
		}
		if ((bit & getRooks()) != 0)
		{
			if (isWhite)
				return 'R';
			else
				return 'r';
		}
		if ((bit & getQueens()) != 0)
		{
			if (isWhite)
				return 'Q';
			else
				return 'q';
		}
		if ((bit & getKings()) != 0)
		{
			if (isWhite)
				return 'K';
			else
				return 'k';
		}

		return 0;
	}

	//trying to place an invalid piece on an occupied square will remove the piece and do nothing more
	public void placePiece(char piece, Definitions.Color color, int r, int c)
	{
		int sq = (7-r)*8 + (7-c);
		long s = (1L << sq);
		long mask = ~s;

		setWhite(getWhite() & mask);
		setBlack(getBlack() & mask);
		setPawns(getPawns() & mask);
		setKnights(getKnights() & mask);
		setBishops(getBishops() & mask);
		setRooks(getRooks() & mask);
		setQueens(getQueens() & mask);
		setKings(getKings() & mask);

		if (color == Definitions.Color.WHITE)
		{
			setWhite(getWhite() | s);
		}
		else
		{
			setBlack(getBlack() | s);
		}

		switch (piece)
		{
		case 'p':
		case 'P':
			setPawns(getPawns() | s);
			break;
		case 'n':
		case 'N':
			setKnights(getKnights() | s);
			break;
		case 'b':
		case 'B':
			setBishops(getBishops() | s);
			break;
		case 'r':
		case 'R':
			setRooks(getRooks() | s);
			break;
		case 'q':
		case 'Q':
			setQueens(getQueens() | s);
			break;
		case 'k':
		case 'K':
			setKings(getKings() | s);
			break;
		}
	}

	public void removePiece(int r, int c)
	{
		int sq = (7-r)*8 + (7-c);
		long mask = ~(1L << sq);

		setWhite(getWhite() & mask);
		setBlack(getBlack() & mask);
		setPawns(getPawns() & mask);
		setKnights(getKnights() & mask);
		setBishops(getBishops() & mask);
		setRooks(getRooks() & mask);
		setQueens(getQueens() & mask);
		setKings(getKings() & mask);
	}

	public void clearBoard()
	{
		setWhite(0);
		setBlack(0);
		setPawns(0);
		setKnights(0);
		setBishops(0);
		setRooks(0);
		setQueens(0);
		setKings(0);
	}

	public void incrementFiftymoverulecount()
	{
		m_data.m_fiftymoverulecount++;
	}

	public int getFiftymoverulecount()
	{
		return m_data.m_fiftymoverulecount;
	}

	public void incrementTurncount()
	{
		m_data.m_turncount++;
	}

	public void decrementTurncount()
	{
		m_data.m_turncount--;
	}

	public int getTurnCount()
	{
		return m_data.m_turncount;
	}

	public Definitions.Color whoseTurn()
	{
		return m_turn;
	}

	public void setTurn(Definitions.Color color)
	{
		m_turn = color;
	}
	
	public int toSq(int row, int col)
	{
		return (7-row)*8 + (7-col);
	}
	
	public int toRow(int sq)
	{
		return 7 - (sq / 8);
	}
	
	public int toCol(int sq)
	{
		return 7 - (sq % 8);
	}

	public Definitions.State getState()
	{
		if (allMoves().size() == 0)
		{
			if (whoseTurn() == Definitions.Color.WHITE)
			{
				if (getWhite() != 0)
					return Definitions.State.STALEMATE; //remember that in Loser's chess, stalemate is a win
				else
					return Definitions.State.CHECKMATE;
			}
			else
			{
				if (getBlack() != 0)
					return Definitions.State.STALEMATE;
				else
					return Definitions.State.CHECKMATE;
			}
		}
		else
		{
			return Definitions.State.NORMAL;
		}
	}

	public void move(Move m)
	{
		int orig = (7-m.r0)*8 + (7-m.c0);
		long origMask = ~(1L << orig);
		int dest = (7-m.rf)*8 + (7-m.cf);
		long destMask = ~(1L << dest);

		if ((getPawns() & (1L << orig)) != 0)
		{
			setPawns(getPawns() & origMask);
			setPawns(getPawns() | (1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			setKings(getKings() & ~(1L << dest));
		}
		else if ((getKnights() & (1L << orig)) != 0)
		{
			setKnights(getKnights() & origMask);
			setKnights(getKnights() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			setKings(getKings() & ~(1L << dest));
		}
		else if ((getBishops() & (1L << orig)) != 0)
		{
			setBishops(getBishops() & origMask);
			setBishops(getBishops() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			setKings(getKings() & ~(1L << dest));
		}
		else if ((getRooks() & (1L << orig)) != 0)
		{
			setRooks(getRooks() & origMask);
			setRooks(getRooks() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			setKings(getKings() & ~(1L << dest));
		}
		else if ((getQueens() & (1L << orig)) != 0)
		{
			setQueens(getQueens() & origMask);
			setQueens(getQueens() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setKings(getKings() & ~(1L << dest));
		}
		else if ((getKings() & (1L << orig)) != 0)
		{
			setKings(getKings() & origMask);
			setKings(getKings() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
		}
		else
			return; //not a valid move (no piece selected)

		if (m_turn == Definitions.Color.WHITE)
		{
			setWhite(getWhite() & origMask);
			setBlack(getBlack() & destMask);
			setWhite(getWhite() | (1L << dest));
		}
		else
		{
			setBlack(getBlack() & origMask);
			setWhite(getWhite() & destMask);
			setBlack(getBlack() | (1L << dest));
		}		
		m_turn = Definitions.flip(m_turn);
	}

	public boolean isLegalMove(Move m)
	{
		return allMoves().contains(m);
	}

	public ArrayList<Move> allMovesPiece(int r, int c)
	{
		ArrayList<Move> all = allMoves();
		ArrayList<Move> pmoves = new ArrayList<Move>();
		for (Move m : all)
		{
			if (m.r0 == r && m.c0 == c)
				pmoves.add(m);
		}
		return pmoves;
	}

	public ArrayList<Move> allMoves()
	{
		ArrayList<Move> captures = new ArrayList<Move>();
		ArrayList<Move> mvs = new ArrayList<Move>();

		long turnpieces, allpieces, pawns, knights, bishops, rooks, queens, kings;

		if (m_turn == Definitions.Color.WHITE)
			turnpieces = getWhite();
		else
			turnpieces = getBlack();

		pawns = turnpieces & getPawns();
		knights = turnpieces & getKnights();
		bishops = turnpieces & getBishops();
		rooks = turnpieces & getRooks();
		queens = turnpieces & getQueens();
		kings = turnpieces & getKings();
		allpieces = getWhite() | getBlack();

		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				int sq = (7-r)*8 + (7-c);

				if (((turnpieces >>> sq) & 1) == 1)
				{
					if (((pawns >>> sq) & 1) == 1) //pawn
					{
						long moves;
						long attacks;
						long bitsq = 1L << sq;
						int epcol = m_data.m_enpassantCol;

						if (m_turn == Definitions.Color.WHITE)
						{							
							attacks = Definitions.wpawnAttacks(pawns);
							moves = Definitions.wpawnMoves(pawns, ~allpieces);

							if (((bitsq << 7) & attacks & ~Definitions.allA) != 0 && 
									(((bitsq << 7) & getBlack()) != 0 || (epcol >= 0 && epcol == c+1 && r == 3)))
							{
								int dsq = sq + 7;
								captures.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}
							if (((bitsq << 9) & attacks & ~Definitions.allH) != 0 && 
									((bitsq << 9) & getBlack()) != 0 || (epcol >= 0 && epcol == c-1 && r == 3))
							{
								int dsq = sq + 9;
								captures.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}
							if (((bitsq << 8) & moves) != 0 && ((bitsq << 8) & allpieces) == 0)
							{
								int dsq = sq + 8;
								mvs.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}

							if (((bitsq << 16) & moves) != 0 && ((bitsq << 16) & allpieces) == 0
									&& ((bitsq << 8) & allpieces) == 0)
							{
								int dsq = sq + 16;
								mvs.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}
						}
						else
						{
							attacks = Definitions.bpawnAttacks(pawns);
							moves = Definitions.bpawnMoves(pawns, ~allpieces);

							if (((bitsq >>> 7) & attacks & ~Definitions.allH) != 0 && 
									((bitsq >>> 7) & getWhite()) != 0 || (epcol >= 0 && epcol == c-1 && r == 4))
							{
								int dsq = sq - 7;
								captures.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}
							if (((bitsq >>> 9) & attacks & ~Definitions.allA) != 0 && 
									((bitsq >>> 9) & getWhite()) != 0 || (epcol >= 0 && epcol == c+1 && r == 4))
							{
								int dsq = sq - 9;
								captures.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}
							if (((bitsq >>> 8) & moves) != 0 && ((bitsq >>> 8) & allpieces) == 0)
							{
								int dsq = sq - 8;
								mvs.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}

							if (((bitsq >>> 16) & moves) != 0 && ((bitsq >>> 16) & allpieces) == 0
									&& ((bitsq >>> 8) & allpieces) == 0)
							{
								int dsq = sq - 16;
								mvs.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
							}
						}
					}

					else if (((knights >>> sq) & 1L) == 1) //knight
					{
						long moves = Definitions.knightAttacks(1L << sq) & ~turnpieces;
						for (int i = 0; i < 64; i++)
						{
							if (((moves >>> i) & 1L) == 1)
							{
								if (((1L << i) & allpieces) != 0)
									captures.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
								else
									mvs.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
							}
						}
					}
					else if (((bishops >>> sq) & 1L) == 1) //bishop
					{
						long moves = Definitions.bishopAttacks(sq, ~allpieces) & ~turnpieces;
						for (int i = 0; i < 64; i++)
						{
							if (((moves >>> i) & 1L) == 1)
							{
								if (((1L << i) & allpieces) != 0)
									captures.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
								else
									mvs.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
							}
						}
					}
					else if (((rooks >>> sq) & 1L) == 1) //rook
					{
						long moves = Definitions.rookAttacks(sq, ~allpieces) & ~turnpieces;
						for (int i = 0; i < 64; i++)
						{
							if (((moves >>> i) & 1L) == 1)
							{
								if (((1L << i) & allpieces) != 0)
									captures.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
								else
									mvs.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
							}
						}
					}
					else if (((queens >>> sq) & 1L) == 1) //queen
					{
						long moves = Definitions.queenAttacks(sq, ~allpieces) & ~turnpieces;
						for (int i = 0; i < 64; i++)
						{
							if (((moves >>> i) & 1L) == 1)
							{
								if (((1L << i) & allpieces) != 0)
									captures.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
								else
									mvs.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
							}
						}
					}
					else //king
					{
						long moves = Definitions.kingAttacks(kings) & ~turnpieces;
						for (int i = 0; i < 64; i++)
						{
							if (((moves >>> i) & 1L) == 1)
							{
								if (((1L << i) & allpieces) != 0)
									captures.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));										
								else
									mvs.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
							}
						}
					}
				}
			}
		}

		if (captures.size() > 0)
			return captures;
		else
			return mvs;
	}

	public void promotePawn(int r, int c)
	{
		Player cur = (Definitions.flip(whoseTurn()) == Definitions.Color.WHITE ? m_data.m_game.getP1() : m_data.m_game.getP2());

		if (cur instanceof HumanPlayer)
		{
			String[] param = { "Queen", "Rook", "Knight", "Bishop", "King" };
			String input = (String) JOptionPane.showInputDialog(null, "Which piece do you want to promote to?", "Pawn Promotion", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

			int sq = (7-r)*8 + (7-c);
			setPawns(getPawns() & ~(1L << sq));
			if (input == "Queen")
			{
				setQueens(getQueens() | (1L << sq));
			}
			else if (input == "Rook")
			{
				setRooks(getRooks() | (1L << sq));
			}
			else if (input == "Knight")
			{
				setKnights(getKnights() | (1L << sq));		
			}
			else if (input == "Bishop")
			{
				setBishops(getBishops() | (1L << sq));
			}
			else //King
			{
				setKings(getKings() | (1L << sq));
			}
		}
		else //AI chooses knight for now
		{
			int sq = (7-r)*8 + (7-c);
			setPawns(getPawns() & ~(1L << sq));
			setKnights(getKnights() | (1L << sq));
		}
	}
	
	public LosersBoard clone()
	{
		return new LosersBoard(this);
	}
	
	public String toString()
	{
		String str = "";
		for (int i = 63; i >= 0; i--)
		{
			long s = (1L << i);
			if ((s & getWhite()) != 0)
			{
				if ((s & getPawns()) != 0)
					str = str + "P";
				else if ((s & getKnights()) != 0)
					str = str + "N";
				else if ((s & getBishops()) != 0)
					str = str + "B";
				else if ((s & getRooks()) != 0)
					str = str + "R";
				else if ((s & getQueens()) != 0)
					str = str + "Q";
				else if ((s & getKings()) != 0)
					str = str + "K";
			}
			else if ((s & getBlack()) != 0)
			{
				if ((s & getPawns()) != 0)
					str = str + "p";
				else if ((s & getKnights()) != 0)
					str = str + "n";
				else if ((s & getBishops()) != 0)
					str = str + "b";
				else if ((s & getRooks()) != 0)
					str = str + "r";
				else if ((s & getQueens()) != 0)
					str = str + "q";
				else if ((s & getKings()) != 0)
					str = str + "k";
			}
			else
				str = str + "-";

			if (i % 8 == 0)
				str = str + '\n';
		}

		return str;
	}
	
	public String toFEN(boolean complete)
	{
		String FEN = "";
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				int count = 0; //count up consecutive empty squares
				char p = getPiece(r, c);

				if (p == 0)
				{
					count++;
					c++;
					p = getPiece(r, c);
					while (p == 0 && c < Definitions.NUMCOLS)
					{
						count++;
						c++;
						p = getPiece(r, c);
					}
					c--; //we want to look at this piece in the next iteration
					FEN = FEN + count;
				}
				else
				{
					if (Character.isUpperCase(p)) //White
					{
						if (Character.toLowerCase(p) == 'p')
							FEN = FEN + "P";
						else if (Character.toLowerCase(p) == 'n')
							FEN = FEN + "N";
						else if (Character.toLowerCase(p) == 'b')
							FEN = FEN + "B";
						else if (Character.toLowerCase(p) == 'r')
							FEN = FEN + "R";
						else if (Character.toLowerCase(p) == 'q')
							FEN = FEN + "Q";
						else if (Character.toLowerCase(p) == 'k')
							FEN = FEN + "K";
					}
					else
					{
						if (Character.toLowerCase(p) == 'p')
							FEN = FEN + "p";
						else if (Character.toLowerCase(p) == 'n')
							FEN = FEN + "n";
						else if (Character.toLowerCase(p) == 'b')
							FEN = FEN + "b";
						else if (Character.toLowerCase(p) == 'r')
							FEN = FEN + "r";
						else if (Character.toLowerCase(p) == 'q')
							FEN = FEN + "q";
						else if (Character.toLowerCase(p) == 'k')
							FEN = FEN + "k";
					}
				}
			}
			if (r != 7) //last row doesn't need slash
				FEN = FEN + "/";
		}

		String tstr = "w";
		if (whoseTurn() == Definitions.Color.BLACK)
			tstr = "b";
		FEN = FEN + " " + tstr + " ";

		String cstr = "-";
		FEN = FEN + cstr + " ";

		String epstr = "";
		if (m_data.m_enpassantCol >= 0 && m_data.m_enpassantCol < Definitions.NUMCOLS)
		{
			epstr = epstr + (char)(m_data.m_enpassantCol + 'a');
			if (whoseTurn() == Definitions.Color.WHITE)
				epstr = epstr + "6";
			else
				epstr = epstr + "3";
		}
		else
		{
			epstr = "-";
		}
		FEN = FEN + epstr;

		if (complete)
			FEN = FEN + " " + Integer.toString(m_data.m_fiftymoverulecount) + " " + Integer.toString(m_data.m_turncount);

		return FEN;
	}
	
	public void FENtoPosition(String srcFEN)
	{
		String[] FEN = srcFEN.split("/");
		String details = FEN[7].split(" ", 2)[1];

		String[] detailElems = details.split(" "); //[0]=turn, [1]=castling, [2]=enpassant, [3]=50-move count, [4]=turn num
		String turn = detailElems[0];
		if (turn.charAt(0) == 'w')
			m_turn = Definitions.Color.WHITE;
		else
			m_turn = Definitions.Color.BLACK;
	
		//String castling = detailElems[1]; //hold '-' because no castling in Loser's Chess

		int epcol = detailElems[2].charAt(0) - 'a';
		if (epcol >= 0 && epcol < 8)
			m_data.m_enpassantCol = epcol;
		else
			m_data.m_enpassantCol = -1;
		m_data.m_fiftymoverulecount = Integer.parseInt(detailElems[3]);
		m_data.m_turncount = Integer.parseInt(detailElems[4]);

		clearBoard();
		for (int r = 0; r < 8; r++)
		{
			String rFEN = FEN[r];
			int index = 0;
			for (int c = 0; c < 8; c++, index++)
			{
				char p = rFEN.charAt(index);

				int emptysquares = p - '1';
				if (emptysquares >= 0 && emptysquares <= 8)
				{
					c = c + emptysquares; //skip the empty squares, remember that the loop increments c by 1
					continue;
				}

				Definitions.Color color = Definitions.Color.WHITE;
				if (Character.isLowerCase(p))
					color = Definitions.Color.BLACK;
	
				placePiece(p, color, r, c);
			}
		}
	}
}
