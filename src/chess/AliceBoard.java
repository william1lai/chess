package chess;

import java.util.ArrayList;
//import java.util.HashMap;

import javax.swing.JOptionPane;

public class AliceBoard //really a board pair
{
	public class AliceMove
	{
		Move m;
		int board;
		
		public AliceMove(Move mv, int b)
		{
			m = mv;
			board = b;
		}
		
		AliceMove(AliceMove other)
		{
			m.r0 = other.m.r0;
			m.c0 = other.m.c0;
			m.rf = other.m.rf;
			m.cf = other.m.cf;
			board = other.board;
		}
		
		public String toString()
		{
			return "" + (char)(m.c0 + 'a') + "" + (8 - m.r0) + "-" + (char)(m.cf + 'a') 
					+ "" + (8 - m.rf) + "[" + (char)('0' + board) + "]";
		}
		
		public boolean equals(Object obj) //we must have this so that the contains() method works
		{
			if (obj instanceof AliceMove)
			{
				AliceMove other = (AliceMove)obj;
				boolean eq = (m.equals(other.m) && (board == other.board));
				return eq;
			}
			return false;
		}
	}
	
	public class GameData
	{
		public int m_enpassantCol; //the column (0-7) of the pawn to move two spaces last turn, -1 if no pawn moved two spaces
		public boolean m_whiteCanCastleKingside; //false if king's rook or king have moved
		public boolean m_whiteCanCastleQueenside; //false if queen's rook or king have moved
		public boolean m_blackCanCastleKingside;
		public boolean m_blackCanCastleQueenside;
		public int m_fiftymoverulecount;
		public int m_turncount;
		public AliceGame m_game;
		//public HashMap<String, Integer> positionTable;

		public GameData(AliceGame maharajahGame)
		{
			m_enpassantCol = -1;
			m_whiteCanCastleKingside = true;
			m_whiteCanCastleQueenside = true;
			m_blackCanCastleKingside = true;
			m_blackCanCastleQueenside = true;
			m_fiftymoverulecount = 0;
			m_turncount = 0;
			m_game = maharajahGame;
		}

		public GameData(GameData other)
		{
			m_enpassantCol = other.m_enpassantCol;
			m_whiteCanCastleKingside = other.m_whiteCanCastleKingside;
			m_whiteCanCastleQueenside = other.m_whiteCanCastleQueenside;
			m_blackCanCastleKingside = other.m_blackCanCastleKingside;
			m_blackCanCastleQueenside = other.m_blackCanCastleQueenside;
			m_fiftymoverulecount = other.m_fiftymoverulecount;
			m_turncount = other.m_turncount;
			m_game = other.m_game;
		}
	};
	private GameData m_data;

	private StandardBoard m_boards[]; //just using StandardBoard as a container so that we can have a pair of boards
	private Definitions.Color m_turn;


	public AliceBoard(AliceGame ag) //standard setup
	{
		m_turn = Definitions.Color.WHITE;
		m_data = new GameData(ag);

		m_boards = new StandardBoard[2];
		m_boards[0] = new StandardBoard();
		m_boards[1] = new StandardBoard();
		
		m_boards[0].setWhite(0x000000000000FFFFL);
		m_boards[0].setBlack(0xFFFF000000000000L);
		m_boards[0].setPawns(0x00FF00000000FF00L);
		m_boards[0].setKnights(0x4200000000000042L);
		m_boards[0].setBishops(0x2400000000000024L);
		m_boards[0].setRooks(0x8100000000000081L);
		m_boards[0].setQueens(0x1000000000000010L);
		m_boards[0].setKings(0x0800000000000008L);

		m_boards[1].setWhite(0);
		m_boards[1].setBlack(0);
		m_boards[1].setPawns(0);
		m_boards[1].setKnights(0);
		m_boards[1].setBishops(0);
		m_boards[1].setRooks(0);
		m_boards[1].setQueens(0);
		m_boards[1].setKings(0);
	}

	public AliceBoard(AliceBoard other)
	{
		m_turn = other.m_turn;
		m_data = new GameData(other.m_data);

		m_boards = new StandardBoard[2];
		m_boards[0] = new StandardBoard();
		m_boards[1] = new StandardBoard();
		
		m_boards[0].setWhite(other.m_boards[0].getWhite());
		m_boards[0].setBlack(other.m_boards[0].getBlack());
		m_boards[0].setPawns(other.m_boards[0].getPawns());
		m_boards[0].setKnights(other.m_boards[0].getKnights());
		m_boards[0].setBishops(other.m_boards[0].getBishops());
		m_boards[0].setRooks(other.m_boards[0].getRooks());
		m_boards[0].setQueens(other.m_boards[0].getQueens());
		m_boards[0].setKings(other.m_boards[0].getKings());

		m_boards[1].setWhite(other.m_boards[1].getWhite());
		m_boards[1].setBlack(other.m_boards[1].getBlack());
		m_boards[1].setPawns(other.m_boards[1].getPawns());
		m_boards[1].setKnights(other.m_boards[1].getKnights());
		m_boards[1].setBishops(other.m_boards[1].getBishops());
		m_boards[1].setRooks(other.m_boards[1].getRooks());
		m_boards[1].setQueens(other.m_boards[1].getQueens());
		m_boards[1].setKings(other.m_boards[1].getKings());
	}

	public GameData getData()
	{
		return m_data;
	}

	public StandardBoard getBoard(int board)
	{
		if (board != 0 && board != 1)
			return null;
		return m_boards[board];
	}
	
	public char getPiece(int r, int c, int board) //returns 0 if no piece exists
	{
		long bit = 1L << ((7-r)*8 + (7-c));
		boolean isWhite = false;
		if ((bit & m_boards[board].getWhite()) != 0)
		{
			isWhite = true;
		}
		if ((bit & m_boards[board].getPawns()) != 0)
		{
			if (isWhite)
				return 'P';
			else
				return 'p';
		}
		if ((bit & m_boards[board].getKnights()) != 0)
		{
			if (isWhite)
				return 'N';
			else
				return 'n';
		}
		if ((bit & m_boards[board].getBishops()) != 0)
		{
			if (isWhite)
				return 'B';
			else
				return 'b';
		}
		if ((bit & m_boards[board].getRooks()) != 0)
		{
			if (isWhite)
				return 'R';
			else
				return 'r';
		}
		if ((bit & m_boards[board].getQueens()) != 0)
		{
			if (isWhite)
				return 'Q';
			else
				return 'q';
		}
		if ((bit & m_boards[board].getKings()) != 0)
		{
			if (isWhite)
				return 'K';
			else
				return 'k';
		}

		return 0;
	}

	//trying to place an invalid piece on an occupied square will remove the piece and do nothing more
	public void placePiece(char piece, Definitions.Color color, int r, int c, int board)
	{
		int sq = (7-r)*8 + (7-c);
		long s = (1L << sq);
		long mask = ~s;

		m_boards[board].setWhite(m_boards[board].getWhite() & mask);
		m_boards[board].setBlack(m_boards[board].getBlack() & mask);
		m_boards[board].setPawns(m_boards[board].getPawns() & mask);
		m_boards[board].setKnights(m_boards[board].getKnights() & mask);
		m_boards[board].setBishops(m_boards[board].getBishops() & mask);
		m_boards[board].setRooks(m_boards[board].getRooks() & mask);
		m_boards[board].setQueens(m_boards[board].getQueens() & mask);
		m_boards[board].setKings(m_boards[board].getKings() & mask);

		if (color == Definitions.Color.WHITE)
		{
			m_boards[board].setWhite(m_boards[board].getWhite() | s);
		}
		else
		{
			m_boards[board].setBlack(m_boards[board].getBlack() | s);
		}

		switch (piece)
		{
		case 'p':
		case 'P':
			m_boards[board].setPawns(m_boards[board].getPawns() | s);
			break;
		case 'n':
		case 'N':
			m_boards[board].setKnights(m_boards[board].getKnights() | s);
			break;
		case 'b':
		case 'B':
			m_boards[board].setBishops(m_boards[board].getBishops() | s);
			break;
		case 'r':
		case 'R':
			m_boards[board].setRooks(m_boards[board].getRooks() | s);
			break;
		case 'q':
		case 'Q':
			m_boards[board].setQueens(m_boards[board].getQueens() | s);
			break;
		case 'k':
		case 'K':
			m_boards[board].setKings(m_boards[board].getKings() | s);
			break;
		}
	}

	public void removePiece(int r, int c, int board)
	{
		int sq = (7-r)*8 + (7-c);
		long mask = ~(1L << sq);

		m_boards[board].setWhite(m_boards[board].getWhite() & mask);
		m_boards[board].setBlack(m_boards[board].getBlack() & mask);
		m_boards[board].setPawns(m_boards[board].getPawns() & mask);
		m_boards[board].setKnights(m_boards[board].getKnights() & mask);
		m_boards[board].setBishops(m_boards[board].getBishops() & mask);
		m_boards[board].setRooks(m_boards[board].getRooks() & mask);
		m_boards[board].setQueens(m_boards[board].getQueens() & mask);
		m_boards[board].setKings(m_boards[board].getKings() & mask);
	}

	public void clearBoard(int board)
	{
		m_boards[board].setWhite(0);
		m_boards[board].setBlack(0);
		m_boards[board].setPawns(0);
		m_boards[board].setKnights(0);
		m_boards[board].setBishops(0);
		m_boards[board].setRooks(0);
		m_boards[board].setQueens(0);
		m_boards[board].setKings(0);
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
		boolean isInCheck = inCheck(0) || inCheck(1);
		int moves = allMoves().size();

		if (moves == 0)
		{
			if (isInCheck)
			{
				return Definitions.State.CHECKMATE;
			}
			else
			{
				return Definitions.State.STALEMATE;
			}
		}
		else
		{
			return Definitions.State.NORMAL;
		}
	}

	public void move(Move m, int board)
	{
		int orig = (7-m.r0)*8 + (7-m.c0);
		long origMask = ~(1L << orig);
		int dest = (7-m.rf)*8 + (7-m.cf);
		long destMask = ~(1L << dest);
		int otherboard = 0;
		if (board == 0)
			otherboard = 1;
		
		if ((m_boards[board].getPawns() & (1L << orig)) != 0)
		{
			m_boards[board].setPawns(m_boards[board].getPawns() & origMask & ~(1L << dest)); //PxP now can't have pawn still exist on dest
			m_boards[otherboard].setPawns(m_boards[otherboard].getPawns() | (1L << dest));
			m_boards[board].setKnights(m_boards[board].getKnights() & ~(1L << dest));
			m_boards[board].setBishops(m_boards[board].getBishops() & ~(1L << dest));
			m_boards[board].setRooks(m_boards[board].getRooks() & ~(1L << dest));
			m_boards[board].setQueens(m_boards[board].getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((m_boards[board].getKnights() & (1L << orig)) != 0)
		{
			m_boards[board].setKnights(m_boards[board].getKnights() & origMask & ~(1L << dest));
			m_boards[otherboard].setKnights(m_boards[otherboard].getKnights() | (1L << dest));
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << dest));
			m_boards[board].setBishops(m_boards[board].getBishops() & ~(1L << dest));
			m_boards[board].setRooks(m_boards[board].getRooks() & ~(1L << dest));
			m_boards[board].setQueens(m_boards[board].getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((m_boards[board].getBishops() & (1L << orig)) != 0)
		{
			m_boards[board].setBishops(m_boards[board].getBishops() & origMask & ~(1L << dest));
			m_boards[otherboard].setBishops(m_boards[otherboard].getBishops() | (1L << dest));
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << dest));
			m_boards[board].setKnights(m_boards[board].getKnights() & ~(1L << dest));
			m_boards[board].setRooks(m_boards[board].getRooks() & ~(1L << dest));
			m_boards[board].setQueens(m_boards[board].getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((m_boards[board].getRooks() & (1L << orig)) != 0)
		{
			m_boards[board].setRooks(m_boards[board].getRooks() & origMask & ~(1L << dest));
			m_boards[otherboard].setRooks(m_boards[otherboard].getRooks() | (1L << dest));
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << dest));
			m_boards[board].setKnights(m_boards[board].getKnights() & ~(1L << dest));
			m_boards[board].setBishops(m_boards[board].getBishops() & ~(1L << dest));
			m_boards[board].setQueens(m_boards[board].getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((m_boards[board].getQueens() & (1L << orig)) != 0)
		{
			m_boards[board].setQueens(m_boards[board].getQueens() & origMask & ~(1L << dest));
			m_boards[otherboard].setQueens(m_boards[otherboard].getQueens() | (1L << dest));
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << dest));
			m_boards[board].setKnights(m_boards[board].getKnights() & ~(1L << dest));
			m_boards[board].setBishops(m_boards[board].getBishops() & ~(1L << dest));
			m_boards[board].setRooks(m_boards[board].getRooks() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((m_boards[board].getKings() & (1L << orig)) != 0)
		{
			m_boards[board].setKings(m_boards[board].getKings() & origMask & ~(1L << dest));
			m_boards[otherboard].setKings(m_boards[otherboard].getKings() | (1L << dest));
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << dest));
			m_boards[board].setKnights(m_boards[board].getKnights() & ~(1L << dest));
			m_boards[board].setBishops(m_boards[board].getBishops() & ~(1L << dest));
			m_boards[board].setRooks(m_boards[board].getRooks() & ~(1L << dest));
			m_boards[board].setQueens(m_boards[board].getQueens() & ~(1L << dest));
		}
		else
			return; //not a valid move (no piece selected)

		if (m_turn == Definitions.Color.WHITE)
		{
			m_boards[board].setWhite(m_boards[board].getWhite() & origMask);
			m_boards[board].setBlack(m_boards[board].getBlack() & destMask);
			m_boards[otherboard].setWhite(m_boards[otherboard].getWhite() | (1L << dest));
		}
		else
		{
			m_boards[board].setBlack(m_boards[board].getBlack() & origMask);
			m_boards[board].setWhite(m_boards[board].getWhite() & destMask);
			m_boards[otherboard].setBlack(m_boards[otherboard].getBlack() | (1L << dest));
		}		
		m_turn = Definitions.flip(m_turn);
	}

	public boolean isLegalMove(Move m, int board)
	{
		AliceMove am = new AliceMove(m, board);
		boolean res = allMoves().contains(am);
		return res;
	}

	public ArrayList<AliceMove> allMovesPiece(int r, int c)
	{
		ArrayList<AliceMove> all = allMoves();
		ArrayList<AliceMove> pmoves = new ArrayList<AliceMove>();
		for (AliceMove am : all)
		{
			if (am.m.r0 == r && am.m.c0 == c)
				pmoves.add(am);
		}
		return pmoves;
	}

	public ArrayList<AliceMove> allMoves()
	{
		ArrayList<AliceMove> legalMoves = new ArrayList<AliceMove>();
		long turnpieces, allpieces, pawns, knights, bishops, rooks, queens, kings, oppkings;
		int kingsq;
		int oppkingsq;

		for (int boardno = 0; boardno <= 1; boardno++) //two boards
		{
			int oppboard;
			if (boardno == 0)
				oppboard = 1;
			else
				oppboard = 0;

			if (m_turn == Definitions.Color.WHITE)
			{
				turnpieces = m_boards[boardno].getWhite();
			}
			else
			{
				turnpieces = m_boards[boardno].getBlack();
			}
			pawns = turnpieces & m_boards[boardno].getPawns();
			knights = turnpieces & m_boards[boardno].getKnights();
			bishops = turnpieces & m_boards[boardno].getBishops();
			rooks = turnpieces & m_boards[boardno].getRooks();
			queens = turnpieces & m_boards[boardno].getQueens();
			kings = turnpieces & m_boards[boardno].getKings();
			oppkings = turnpieces & m_boards[oppboard].getKings();
			allpieces = m_boards[boardno].getWhite() | m_boards[boardno].getBlack();

			kingsq = -1;
			oppkingsq = -1;
			if (kings != 0)
				kingsq = (int)((Math.log(kings)/Math.log(2)) + 0.5);
			if (oppkings != 0)
				oppkingsq = (int)((Math.log(oppkings)/ Math.log(2)) + 0.5);

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
										(((bitsq << 7) & m_boards[boardno].getBlack()) != 0 || (epcol >= 0 && epcol == c+1 && r == 3)))
								{
									int dsq = sq + 7;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
								if (((bitsq << 9) & attacks & ~Definitions.allH) != 0 && 
										((bitsq << 9) & m_boards[boardno].getBlack()) != 0 || (epcol >= 0 && epcol == c-1 && r == 3))
								{
									int dsq = sq + 9;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
								if (((bitsq << 8) & moves) != 0 && ((bitsq << 8) & allpieces) == 0)
								{
									int dsq = sq + 8;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}

								if (((bitsq << 16) & moves) != 0 && ((bitsq << 16) & allpieces) == 0
										&& ((bitsq << 8) & allpieces) == 0)
								{
									int dsq = sq + 16;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
							}
							else
							{
								attacks = Definitions.bpawnAttacks(pawns);
								moves = Definitions.bpawnMoves(pawns, ~allpieces);

								if (((bitsq >>> 7) & attacks & ~Definitions.allH) != 0 && 
										((bitsq >>> 7) & m_boards[boardno].getWhite()) != 0 || (epcol >= 0 && epcol == c-1 && r == 4))
								{
									int dsq = sq - 7;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
								if (((bitsq >>> 9) & attacks & ~Definitions.allA) != 0 && 
										((bitsq >>> 9) & m_boards[boardno].getWhite()) != 0 || (epcol >= 0 && epcol == c+1 && r == 4))
								{
									int dsq = sq - 9;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
								if (((bitsq >>> 8) & moves) != 0 && ((bitsq >>> 8) & allpieces) == 0)
								{
									int dsq = sq - 8;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}

								if (((bitsq >>> 16) & moves) != 0 && ((bitsq >>> 16) & allpieces) == 0
										&& ((bitsq >>> 8) & allpieces) == 0)
								{
									int dsq = sq - 16;
									AliceBoard temp = this.clone();
									int dr = 7 - (dsq / 8);
									int dc = 7 - (dsq % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
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
									AliceBoard temp = this.clone();
									int dr = 7 - (i / 8);
									int dc = 7 - (i % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
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
									AliceBoard temp = this.clone();
									int dr = 7 - (i / 8);
									int dc = 7 - (i % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
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
									AliceBoard temp = this.clone();
									int dr = 7 - (i / 8);
									int dc = 7 - (i % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
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
									AliceBoard temp = this.clone();
									int dr = 7 - (i / 8);
									int dc = 7 - (i % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], kingsq, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
							}
						}
						else //king
						{
							long moves = Definitions.kingAttacks(1L << sq) & ~turnpieces;
							for (int i = 0; i < 64; i++)
							{
								if (((moves >>> i) & 1L) == 1)
								{
									AliceBoard temp = this.clone();
									int dr = 7 - (i / 8);
									int dc = 7 - (i % 8);
									temp.m_boards[boardno].move(new Move(r, c, dr, dc));
									temp.removePiece(dr, dc, boardno);

									if (!Definitions.isAttacked(temp.m_boards[boardno], i, Definitions.flip(m_turn)) && 
											temp.m_boards[oppboard].getPiece(dr, dc) == 0)
									{
										temp.placePiece(temp.m_boards[boardno].getPiece(r, c), m_turn, dr, dc, oppboard);
										if (!Definitions.isAttacked(temp.m_boards[oppboard], oppkingsq, Definitions.flip(m_turn)))
										{
											legalMoves.add(new AliceMove(new Move(r, c, dr, dc), boardno));
										}
									}
								}
							}

							boolean canCastleKingside;
							boolean canCastleQueenside;
							if (whoseTurn() == Definitions.Color.WHITE)
							{
								canCastleKingside = m_data.m_whiteCanCastleKingside;
								canCastleQueenside = m_data.m_whiteCanCastleQueenside;
							}
							else
							{
								canCastleKingside = m_data.m_blackCanCastleKingside;
								canCastleQueenside = m_data.m_blackCanCastleQueenside;
							}

							long king = (1L << (kingsq));
							if (kingsq != -1)
							{
								if (canCastleKingside) //kingside castle
								{
									//not in check at original, intermediate, or final squares
									if (!(Definitions.isAttacked(m_boards[boardno], kingsq, Definitions.flip(whoseTurn())) ||
											Definitions.isAttacked(m_boards[boardno], kingsq - 1, Definitions.flip(whoseTurn())) ||
											Definitions.isAttacked(m_boards[boardno], kingsq - 2, Definitions.flip(whoseTurn()))) ||
											Definitions.isAttacked(m_boards[oppboard], kingsq - 2, Definitions.flip(whoseTurn())))
									{
										if ((((king >>> 1) & ~allpieces) != 0) && (((king >>> 2) & ~allpieces) != 0) &&
												m_boards[oppboard].getPiece(r, c + 2) == 0 && m_boards[oppboard].getPiece(r, c + 1) == 0)
										{
											legalMoves.add(new AliceMove(new Move(r, c, r, c + 2), boardno));
										}
									}
								}
								if (canCastleQueenside) //queenside castle
								{
									//not in check at original, intermediate, or final squares
									if (!(Definitions.isAttacked(m_boards[boardno], kingsq, Definitions.flip(whoseTurn())) ||
											Definitions.isAttacked(m_boards[boardno], kingsq + 1, Definitions.flip(whoseTurn())) ||
											Definitions.isAttacked(m_boards[boardno], kingsq + 2, Definitions.flip(whoseTurn()))) || 
											Definitions.isAttacked(m_boards[oppboard], kingsq + 2, Definitions.flip(whoseTurn())))
									{
										if ((((king << 1) & ~allpieces) != 0) && (((king << 2) & ~allpieces) != 0) &&
												m_boards[oppboard].getPiece(r, c - 2) == 0 && m_boards[oppboard].getPiece(r, c - 1) == 0)
										{
											legalMoves.add(new AliceMove(new Move(r, c, r, c - 2), boardno));
										}	
									}
								}
							}
						}
					}
				}
			}
		}
		return legalMoves;
	}

	public boolean inCheck(int board)
	{
		long king;
		if (whoseTurn() == Definitions.Color.WHITE)
			king = m_boards[board].getWhite() & m_boards[board].getKings();
		else
			king = m_boards[board].getBlack() & m_boards[board].getKings();
		if (king == 0) //king not on this board
			return false;

		int kingsq = (int)((Math.log(king)/Math.log(2)) + 0.5);
		return Definitions.isAttacked(m_boards[board], kingsq, Definitions.flip(whoseTurn()));
	}

	public boolean inCheckmate()
	{
		boolean check = inCheck(0) || inCheck(1);
		return (check && allMoves().size() == 0);
	}

	public boolean inStalemate()
	{
		boolean check = inCheck(0) || inCheck(1);
		return (!check && allMoves().size() == 0);
	}

	public void promotePawn(int r, int c, int board)
	{
		Player cur = (Definitions.flip(whoseTurn()) == Definitions.Color.WHITE ? m_data.m_game.p1 : m_data.m_game.p2);

		if (cur instanceof HumanPlayer)
		{
			String[] param = { "Queen", "Rook", "Knight", "Bishop" };
			String input = (String) JOptionPane.showInputDialog(null, "Which piece do you want to promote to?", "Pawn Promotion", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

			int sq = (7-r)*8 + (7-c);
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << sq));
			if (input == "Queen")
			{
				m_boards[board].setQueens(m_boards[board].getQueens() | (1L << sq));
			}
			else if (input == "Rook")
			{
				m_boards[board].setRooks(m_boards[board].getRooks() | (1L << sq));
			}
			else if (input == "Knight")
			{
				m_boards[board].setKnights(m_boards[board].getKnights() | (1L << sq));		
			}
			else //Bishop
			{
				m_boards[board].setBishops(m_boards[board].getBishops() | (1L << sq));
			}
		}
		else //AI chooses queen for now
		{
			int sq = (7-r)*8 + (7-c);
			m_boards[board].setPawns(m_boards[board].getPawns() & ~(1L << sq));
			m_boards[board].setQueens(m_boards[board].getQueens() | (1L << sq));
		}
	}

	public AliceBoard clone()
	{
		return new AliceBoard(this);
	}

	public String toString(int board)
	{
		String str = "";
		for (int i = 63; i >= 0; i--)
		{
			long s = (1L << i);
			if ((s & m_boards[board].getWhite()) != 0)
			{
				if ((s & m_boards[board].getPawns()) != 0)
					str = str + "P";
				else if ((s & m_boards[board].getKnights()) != 0)
					str = str + "N";
				else if ((s & m_boards[board].getBishops()) != 0)
					str = str + "B";
				else if ((s & m_boards[board].getRooks()) != 0)
					str = str + "R";
				else if ((s & m_boards[board].getQueens()) != 0)
					str = str + "Q";
				else if ((s & m_boards[board].getKings()) != 0)
					str = str + "K";
			}
			else if ((s & m_boards[board].getBlack()) != 0)
			{
				if ((s & m_boards[board].getPawns()) != 0)
					str = str + "p";
				else if ((s & m_boards[board].getKnights()) != 0)
					str = str + "n";
				else if ((s & m_boards[board].getBishops()) != 0)
					str = str + "b";
				else if ((s & m_boards[board].getRooks()) != 0)
					str = str + "r";
				else if ((s & m_boards[board].getQueens()) != 0)
					str = str + "q";
				else if ((s & m_boards[board].getKings()) != 0)
					str = str + "k";
			}
			else
				str = str + "-";

			if (i % 8 == 0)
				str = str + '\n';
		}

		return str;
	}

	public String toFEN(boolean complete, int board)
	{
		String FEN = "";
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				int count = 0; //count up consecutive empty squares
				char p = m_boards[board].getPiece(r, c);

				if (p == 0)
				{
					count++;
					c++;
					p = m_boards[board].getPiece(r, c);
					while (p == 0 && c < Definitions.NUMCOLS)
					{
						count++;
						c++;
						p = m_boards[board].getPiece(r, c);
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

		String cstr = "";
		if (m_data.m_whiteCanCastleKingside)
			cstr = cstr + "K";
		if (m_data.m_whiteCanCastleQueenside)
			cstr = cstr + "Q";
		if (m_data.m_blackCanCastleKingside)
			cstr = cstr + "k";
		if (m_data.m_blackCanCastleQueenside)
			cstr = cstr + "q";
		if (cstr.length() == 0)
			cstr = "-";
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

	public void FENtoPosition(String srcFEN, int board)
	{
		String[] FEN = srcFEN.split("/");
		String details = FEN[7].split(" ", 2)[1];

		m_data.m_whiteCanCastleQueenside = false;
		m_data.m_whiteCanCastleKingside = false;
		m_data.m_blackCanCastleQueenside = false;
		m_data.m_blackCanCastleKingside = false;

		String[] detailElems = details.split(" "); //[0]=turn, [1]=castling, [2]=enpassant, [3]=50-move count, [4]=turn num
		String turn = detailElems[0];
		if (turn.charAt(0) == 'w')
		{
			m_turn = Definitions.Color.WHITE;
		}
		else
		{
			m_turn = Definitions.Color.BLACK;
		}

		String castling = detailElems[1];
		if (castling.contains("Q"))
		{
			m_data.m_whiteCanCastleQueenside = true;
		}
		if (castling.contains("K"))
		{
			m_data.m_whiteCanCastleKingside = true;
		}
		if (castling.contains("q"))
		{
			m_data.m_blackCanCastleQueenside = true;
		}
		if (castling.contains("k"))
		{
			m_data.m_blackCanCastleKingside = true;
		}

		int epcol = detailElems[2].charAt(0) - 'a';
		if (epcol >= 0 && epcol < 8)
			m_data.m_enpassantCol = epcol;
		else
			m_data.m_enpassantCol = -1;
		m_data.m_fiftymoverulecount = Integer.parseInt(detailElems[3]);
		m_data.m_turncount = Integer.parseInt(detailElems[4]);

		clearBoard(board);
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
				{
					color = Definitions.Color.BLACK;
				}				

				placePiece(p, color, r, c, board);
			}
		}
	}
}
