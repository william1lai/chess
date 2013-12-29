package chess;

import java.util.ArrayList;
//import java.util.HashMap;

public class StandardChessBoard extends Board 
{
	private Definitions.Color m_turn;

	private class GameData
	{
		public int m_enpassantCol; //the column (0-7) of the pawn to move two spaces last turn, -1 if no pawn moved two spaces
		public boolean m_whiteCanCastleKingside; //false if king's rook or king have moved
		public boolean m_whiteCanCastleQueenside; //false if queen's rook or king have moved
		public boolean m_blackCanCastleKingside;
		public boolean m_blackCanCastleQueenside;
		public int m_fiftymoverulecount;
		public int m_turncount;
		//public HashMap<String, Integer> positionTable;

		public GameData()
		{
			m_enpassantCol = -1;
			m_whiteCanCastleKingside = true;
			m_whiteCanCastleQueenside = true;
			m_blackCanCastleKingside = true;
			m_blackCanCastleQueenside = true;
			m_fiftymoverulecount = 0;
			m_turncount = 0;
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
		}
	};

	private GameData m_data;

	public StandardChessBoard() //standard setup
	{
		m_turn = Definitions.Color.WHITE;
		m_data = new GameData();

		setWhite(0x000000000000FFFFL);
		setBlack(0xFFFF000000000000L);
		setPawns(0x00FF00000000FF00L);
		setKnights(0x4200000000000042L);
		setBishops(0x2400000000000024L);
		setRooks(0x8100000000000081L);
		setQueens(0x1000000000000010L);
		setKings(0x0800000000000008L);
	}

	public StandardChessBoard(StandardChessBoard other)
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

	public Definitions.State getState()
	{
		boolean isInCheck = inCheck();
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
			//no king because it shouldn't be able to be captured
		}
		else if ((getKnights() & (1L << orig)) != 0)
		{
			setKnights(getKnights() & origMask);
			setKnights(getKnights() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((getBishops() & (1L << orig)) != 0)
		{
			setBishops(getBishops() & origMask);
			setBishops(getBishops() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((getRooks() & (1L << orig)) != 0)
		{
			setRooks(getRooks() & origMask);
			setRooks(getRooks() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setQueens(getQueens() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
		}
		else if ((getQueens() & (1L << orig)) != 0)
		{
			setQueens(getQueens() & origMask);
			setQueens(getQueens() | (1L << dest));
			setPawns(getPawns() & ~(1L << dest));
			setKnights(getKnights() & ~(1L << dest));
			setBishops(getBishops() & ~(1L << dest));
			setRooks(getRooks() & ~(1L << dest));
			//no king because it shouldn't be able to be captured
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
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		long turnpieces, allpieces, pawns, knights, bishops, rooks, queens, kings;
		int kingsq;

		if (m_turn == Definitions.Color.WHITE)
		{
			turnpieces = getWhite();
		}
		else
		{
			turnpieces = getBlack();
		}
		pawns = turnpieces & getPawns();
		knights = turnpieces & getKnights();
		bishops = turnpieces & getBishops();
		rooks = turnpieces & getRooks();
		queens = turnpieces & getQueens();
		kings = turnpieces & getKings();
		allpieces = getWhite() | getBlack();
		
		kingsq = (int)((Math.log(kings)/Math.log(2)) + 0.5);

		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				int sq = (7-r)*8 + (7-c);

				if (((turnpieces >>> sq) & 1) == 1) //white piece
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
							}
							if (((bitsq << 9) & attacks & ~Definitions.allH) != 0 && 
									((bitsq << 9) & getBlack()) != 0 || (epcol >= 0 && epcol == c-1 && r == 3))
							{
								int dsq = sq + 9;
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
							}
							if (((bitsq << 8) & moves) != 0 && ((bitsq << 8) & allpieces) == 0)
							{
								int dsq = sq + 8;
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
							}

							if (((bitsq << 16) & moves) != 0 && ((bitsq << 16) & allpieces) == 0
									&& ((bitsq << 8) & allpieces) == 0)
							{
								int dsq = sq + 16;
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
							}
							if (((bitsq >>> 9) & attacks & ~Definitions.allA) != 0 && 
									((bitsq >>> 9) & getWhite()) != 0 || (epcol >= 0 && epcol == c+1 && r == 4))
							{
								int dsq = sq - 9;
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
							}
							if (((bitsq >>> 8) & moves) != 0 && ((bitsq >>> 8) & allpieces) == 0)
							{
								int dsq = sq - 8;
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
								}
							}

							if (((bitsq >>> 16) & moves) != 0 && ((bitsq >>> 16) & allpieces) == 0
									&& ((bitsq >>> 8) & allpieces) == 0)
							{
								int dsq = sq - 16;
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (dsq / 8), 7 - (dsq % 8)));
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));

								if (!Definitions.isAttacked(temp, kingsq, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
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
								StandardChessBoard temp = this.clone();
								temp.move(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));

								if (!Definitions.isAttacked(temp, i, Definitions.flip(m_turn)))
								{
									legalMoves.add(new Move(r, c, 7 - (i / 8), 7 - (i % 8)));
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
						if (canCastleKingside) //kingside castle
						{
							//not in check at original, intermediate, or final squares
							if (!(Definitions.isAttacked(this, kingsq, Definitions.flip(whoseTurn())) ||
									Definitions.isAttacked(this, kingsq - 1, Definitions.flip(whoseTurn())) ||
									Definitions.isAttacked(this, kingsq - 2, Definitions.flip(whoseTurn()))))
							{
								if ((((king >>> 1) & ~allpieces) != 0) &&
										(((king >>> 2) & ~allpieces) != 0))
								{
									legalMoves.add(new Move(r, c, r, c + 2));
								}
							}
						}
						if (canCastleQueenside) //queenside castle
						{
							//not in check at original, intermediate, or final squares
							if (!(Definitions.isAttacked(this, kingsq, Definitions.flip(whoseTurn())) |
									Definitions.isAttacked(this, kingsq + 1, Definitions.flip(whoseTurn())) |
									Definitions.isAttacked(this, kingsq + 2, Definitions.flip(whoseTurn()))))
							{
								if ((((king << 1) & ~allpieces) != 0) &&
										(((king << 2) & ~allpieces) != 0))
								{
									legalMoves.add(new Move(r, c, r, c - 2));
								}	
							}
						}						
					}
				}
			}
		}
		return legalMoves;
	}

	public boolean inCheck()
	{
		long king;
		if (whoseTurn() == Definitions.Color.WHITE)
			king = getWhite() & getKings();
		else
			king = getBlack() & getKings();
		int kingsq = (int)((Math.log(king)/Math.log(2)) + 0.5);
		return Definitions.isAttacked(this, kingsq, Definitions.flip(whoseTurn()));
	}

	public boolean inCheckmate()
	{		
		boolean check = inCheck();
		return (check && allMoves().size() == 0);
	}

	public boolean inStalemate()
	{		
		boolean check = inCheck();
		ArrayList<Move> mvs = allMoves();
		return (!check && mvs.size() == 0);
	}

	public Move processMove(Move newMove)
	{
		int row = newMove.r0;
		int col = newMove.c0;
		char movedPiece = getPiece(row, col);
		m_data.m_fiftymoverulecount++;

		int castlingRow;
		if (whoseTurn() == Definitions.Color.WHITE)
		{
			castlingRow = 7;
		}
		else //Black
		{
			castlingRow = 0;
		}

		Move correspondingRookMove = null; //if we have castling
		m_data.m_enpassantCol = -1; //default
		if (Character.toLowerCase(movedPiece) == 'p')
		{
			m_data.m_fiftymoverulecount = 0; //pawn was moved
			if (Math.abs(newMove.rf - newMove.r0) == 2)
			{
				m_data.m_enpassantCol = newMove.c0; //enpassant now available on this column
			}
			else if ((Math.abs(newMove.cf - newMove.c0) == 1) && (getPiece(newMove.rf, newMove.cf) == 0))
				//en passant
			{
				if (whoseTurn() == Definitions.Color.WHITE)
				{
					removePiece(3, newMove.cf); //not sure if this is best way, but "move" call will not erase piece
				}
				else
				{
					removePiece(4, newMove.cf);
				}
			}
		}
		else if (Character.toLowerCase(movedPiece) == 'k')
		{			
			if (whoseTurn() == Definitions.Color.WHITE)
			{
				m_data.m_whiteCanCastleKingside = false;
				m_data.m_whiteCanCastleQueenside = false;
			}
			else
			{
				m_data.m_blackCanCastleKingside = false;
				m_data.m_blackCanCastleQueenside = false;
			}

			int kingMoveLength = newMove.cf - col; //should be 2 or -2, if the move was a castling move
			if (row == castlingRow)
			{
				if (kingMoveLength == 2) //kingside
				{
					correspondingRookMove = new Move(castlingRow, 7, castlingRow, 5);
				}
				else if (kingMoveLength == -2) //queenside
				{
					correspondingRookMove = new Move(castlingRow, 0, castlingRow, 3);
				}
			}
		}
		else if (row == castlingRow)
		{
			if (col == 0) //queen's rook
			{
				if (whoseTurn() == Definitions.Color.WHITE)
				{
					m_data.m_whiteCanCastleQueenside = false;
				}
				else
				{
					m_data.m_blackCanCastleQueenside = false;
				}
			}
			else if (col == 7) //king's rook
			{			
				if (whoseTurn() == Definitions.Color.WHITE)
				{
					m_data.m_whiteCanCastleKingside = false;
				}
				else
				{
					m_data.m_blackCanCastleKingside = false;
				}
			}
		}

		if (getPiece(newMove.rf, newMove.cf) != 0) //capture was made
		{
			m_data.m_fiftymoverulecount = 0; //reset counter
		}

		return correspondingRookMove; //so we know to animate rook	
	}

	public StandardChessBoard clone()
	{
		return new StandardChessBoard(this);
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
	
	public String toFEN(boolean complete) //note that this only contains partial FEN, only what the board can see
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

	public void FENtoPosition(String srcFEN)
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
				{
					color = Definitions.Color.BLACK;
				}				
				
				placePiece(p, color, r, c);
			}
		}
	}
}
