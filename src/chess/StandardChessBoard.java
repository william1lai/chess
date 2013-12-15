package chess;

import java.util.ArrayList;
//import java.util.HashMap;

public class StandardChessBoard extends Board 
{
	private Definitions.State m_state;	
	private Definitions.Color m_turn;
	private int m_whiteKingLoc;
	private int m_blackKingLoc;
	
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
	
	public StandardChessBoard()
	{
		super.init();
		m_state = Definitions.State.UNCHECKED;
		m_whiteKingLoc = 74; //default locations
		m_blackKingLoc = 4;
		m_turn = Definitions.Color.WHITE;
		m_data = new GameData();
	}
	
	public StandardChessBoard(StandardChessBoard other)
	{
		super.init();
		super.copyPieces(other);
		m_state = other.m_state;
		m_whiteKingLoc = other.m_whiteKingLoc;
		m_blackKingLoc = other.m_blackKingLoc;
		m_turn = other.m_turn;
		m_data = new GameData(other.m_data);
	}

	public int getWhiteKingLoc()
	{
		return m_whiteKingLoc;
	}

	public int getBlackKingLoc()
	{
		return m_blackKingLoc;
	}

	public void updateKingLocs()
	{
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece p = getPiece(r, c);
				if (p instanceof King)
				{
					if (p.color() == Definitions.Color.WHITE)
					{
						m_whiteKingLoc = r*10 + c;
					}
					else
					{
						m_blackKingLoc = r*10 + c;
					}
				}
			}
		}
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
		if (m_state == Definitions.State.UNCHECKED)
		{
			boolean isInCheck = inCheck();
			int moves = allMoves().size();

			if (moves == 0)
			{
				if (isInCheck)
				{
					setState(Definitions.State.CHECKMATE);
					return Definitions.State.CHECKMATE;
				}
				else
				{
					setState(Definitions.State.STALEMATE);
					return Definitions.State.STALEMATE;
				}
			}
			else
			{
				setState(Definitions.State.NORMAL);
				return Definitions.State.NORMAL;
			}
		}
		else
		{
			return m_state;
		}
	}
	
	public void setState(Definitions.State state)
	{
		m_state = state;
	}

	public void move(Move m)
	{
		Piece temp = super.getPiece(m.r0, m.c0);
		if (temp instanceof King)
		{
			int sq = (m.rf * 10 + m.cf);
			if (temp.color() == Definitions.Color.WHITE)
			{
				m_whiteKingLoc = sq;
			}
			else
			{
				m_blackKingLoc = sq;
			}
		}

		super.move(m);
		if (temp instanceof Pawn && (m.rf == 0 || m.rf == 7))
		{
			placePiece(new Queen(m.rf, m.cf, whoseTurn()), m.rf, m.cf); //assume new queen for now
		}

		m_state = Definitions.State.UNCHECKED;
		setTurn(Definitions.flip(whoseTurn()));
		/*String FEN = this.toFEN(false);
		if (!positionTable.keySet().contains(FEN))
		{
			positionTable.put(FEN, 1);
		}
		else
		{
			int repeats = positionTable.get(FEN);
			repeats++;
			positionTable.remove(FEN);
			positionTable.put(FEN, repeats);
		}*/ //Hashing for threefold repetition slows down program too much
	}

	public boolean isLegalMove(Move m)
	{		
		//generate moves and use board to determine legality (is there a piece in the way? etc.)
		Piece p = getPiece(m.r0, m.c0);
		if ((p == null) || (p.color() != whoseTurn()))
		{
			return false; //source square has no piece, or selected piece is opponent's piece
		}

		Piece destination = getPiece(m.rf, m.cf);
		boolean occupiedDest = (destination != null);
		ArrayList<Move> moves = p.getMoves();

		if (occupiedDest && (destination.color() == whoseTurn())) //case of trying to move to source square is handled here
		{
			return false; //can't move to square occupied by same color piece
		}

		if (moves.contains(m))
		{
			StandardChessBoard tempBoard = this.clone(); //clone board
			tempBoard.move(m);

			if (!(p instanceof Knight) && (this.hasPieceInWay(m))) 
				//if it's not a knight, it can't jump
			{
				return false;
			}

			if (tempBoard.justPutInCheck())
			{
				return false; //illegal move because you will be in check after move
			}

			if (p instanceof Knight) //Update: king moves no longer automatically legal
			{
				return true; //all possible moves are automatically legal because we already checked earlier
				//that the destination square does not contain a piece of same color
			}
			if (p instanceof King)
			{
				//Is there a better way to do this? Seems ugly
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

				if (canCastleKingside && m.cf - m.c0 > 1) //kingside castle attempt
				{
					StandardChessBoard temp = this.clone();
					Move intermediate = new Move(m.r0, m.c0, m.r0, m.c0 + 1);
					temp.move(intermediate);
					return (canCastleKingside && !temp.justPutInCheck() && !inCheck()); //can't be in check
				}
				if (canCastleQueenside && m.cf - m.c0 < -1) //queenside castle attempt
				{
					StandardChessBoard temp = this.clone();
					Move intermediate = new Move(m.r0, m.c0, m.r0, m.c0 - 1);
					temp.move(intermediate);
					return (canCastleQueenside && !temp.justPutInCheck() && !inCheck() && hasPieceInWay(new Move(m.r0, 0, m.r0, 3))); //can't be in check and no piece in way (on b1/b8 for example)
				}
				if (Math.abs(m.cf - m.c0) <= 1)
				{
					return true; //one square moves are legal
				}
				else return false; //can't castle, so >1 square moves illegal
			}
			if (p instanceof Pawn) //must split into cases
			{
				if (m.cf != m.c0) //changed columns; must be capture
				{
					boolean validCapture = (this.getPiece(m.rf, m.cf) != null); //can't be our own piece because of earlier check
					boolean enpassant;
					if (whoseTurn() == Definitions.Color.WHITE)
						enpassant = ((m.cf == m_data.m_enpassantCol) && (m.r0 == 3));
					else
						enpassant = ((m.cf == m_data.m_enpassantCol) && (m.r0 == 4));

					return (validCapture || enpassant);
				}
				else
				{
					if (m.rf - m.r0 == 2) //push two squares
					{
						return (this.getPiece((m.rf + m.r0) / 2, m.cf) == null)
								&& (this.getPiece(m.rf, m.cf) == null); //both squares empty
					}
					else //push one square
					{
						return (this.getPiece(m.rf, m.cf) == null); //square empty
					}
				}
			}
			return true;
		}
		return false; //move is not in our move list
	}

	public ArrayList<Move> allMovesPiece(Piece p)
	{
		if (p == null) return null;
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		ArrayList<Move> temp = p.getMoves();
		for (Move m : temp)
		{
			if (this.isLegalMove(m))
			{
				legalMoves.add(m);
			}
		}
		return legalMoves;
	}

	public ArrayList<Move> allMoves()
	{
		ArrayList<Move> legalMoves = new ArrayList<Move>();

		for (int r = 0; r < 8; r++)
		{
			for (int c = 0; c < 8; c++)
			{
				Piece p = getPiece(r, c);
				if (p != null && p.color() == whoseTurn())
				{
					legalMoves.addAll(allMovesPiece(p));
				}
			}
		}
		return legalMoves;
	}

	private boolean hasPieceInWay(Move m)
	{
		int dc = m.cf - m.c0;
		int dr = m.rf - m.r0; //remember rows are counted from the top
		int cinc; //1, 0, or -1, depending on which direction the piece is headed
		int rinc; //1, 0, or -1

		Piece p = getPiece(m.r0, m.c0);
		if (p == null || p instanceof Knight)
			return false; //vacuously false for no piece, and automatically false for knights 

		if (dc < 0)
		{
			cinc = -1;
		}
		else if (dc > 0)
		{
			cinc = 1;
		}
		else
		{
			cinc = 0;
		}

		if (dr < 0)
		{
			rinc = -1;
		}
		else if (dr > 0)
		{
			rinc = 1;
		}
		else
		{
			rinc = 0;
		}

		int r = m.r0 + rinc;
		int c = m.c0 + cinc;
		while (!((r == m.rf) && (c == m.cf)))
		{
			if (getPiece(r, c) != null)
			{
				return true; //there is a piece in our way
			}
			r = r + rinc;
			c = c + cinc;
		}
		return false; //no pieces in way
	}

	public boolean justPutInCheck() //if last move just put our piece in check; probably a more elegant solution somewhere
	{
		int kingR;
		int kingC;
		if (whoseTurn() == Definitions.Color.BLACK)
		{
			int temp = getWhiteKingLoc();
			kingR = temp / 10;
			kingC = temp % 10;
		}
		else
		{
			int temp = getBlackKingLoc();
			kingR = temp / 10;
			kingC = temp % 10;
		}

		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece temp = getPiece(r, c);
				Move m = new Move(r, c, kingR, kingC);
				ArrayList<Move> alm = new ArrayList<Move>(); //TODO
				if (temp != null)
					alm = temp.getThreats();
				if ((temp != null) && (temp.color() == whoseTurn()) && (alm.contains(m)))
				{
					if (temp instanceof Knight || !hasPieceInWay(m))
					{
						return true;
					}
				}
			}
		}
		return false; //no pieces can capture king
	}
	
	public boolean inCheck()
	{		
		int kingR;
		int kingC;
		if (whoseTurn() == Definitions.Color.WHITE)
		{
			int temp = getWhiteKingLoc();
			kingR = temp / 10;
			kingC = temp % 10;
		}
		else
		{
			int temp = getBlackKingLoc();
			kingR = temp / 10;
			kingC = temp % 10;
		}

		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece temp = getPiece(r, c);
				Move m = new Move(r, c, kingR, kingC);
				ArrayList<Move> alm = new ArrayList<Move>(); //TODO
				if (temp != null)
					alm = temp.getThreats();
				if ((temp != null) && (temp.color() != whoseTurn()) && (alm.contains(m)))
				{
					if (temp instanceof Knight || !hasPieceInWay(m))
					{
						return true;
					}
				}
			}
		}
		return false; //no pieces can capture king
	}

	public boolean isCheckmate()
	{		
		boolean check = inCheck();
		return (check && allMoves().size() == 0);
	}

	public boolean isStalemate()
	{		
		boolean check = inCheck();
		ArrayList<Move> mvs = allMoves();
		return (!check && mvs.size() == 0);
	}
	
	public Move processMove(Move newMove)
	{
		int row = newMove.r0;
		int col = newMove.c0;
		Piece movedPiece = getPiece(row, col);
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
		if (movedPiece instanceof Pawn)
		{
			m_data.m_fiftymoverulecount = 0; //pawn was moved
			if (Math.abs(newMove.rf - newMove.r0) == 2)
			{
				m_data.m_enpassantCol = newMove.c0; //enpassant now available on this column
			}
			else if ((Math.abs(newMove.cf - newMove.c0) == 1) && (getPiece(newMove.rf, newMove.cf) == null))
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
		else if (movedPiece instanceof King)
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

		if (getPiece(newMove.rf, newMove.cf) != null) //capture was made
		{
			m_data.m_fiftymoverulecount = 0; //reset counter
		}

		return correspondingRookMove; //so we know to animate rook	
	}
	
	public StandardChessBoard clone()
	{
		return new StandardChessBoard(this);
	}

	public String toFEN(boolean complete) //note that this only contains partial FEN, only what the board can see
	{
		String FEN = "";
		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				int count = 0; //count up consecutive empty squares
				Piece p = getPiece(r, c);
				
				if (p == null)
				{
					count++;
					c++;
					p = getPiece(r, c);
					while (p == null && c < Definitions.NUMCOLS)
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
					if (p.color() == Definitions.Color.WHITE)
					{
						if (p instanceof Pawn)
							FEN = FEN + "P";
						else if (p instanceof Knight)
							FEN = FEN + "N";
						else if (p instanceof Bishop)
							FEN = FEN + "B";
						else if (p instanceof Rook)
							FEN = FEN + "R";
						else if (p instanceof Queen)
							FEN = FEN + "Q";
						else if (p instanceof King)
							FEN = FEN + "K";
					}
					else
					{
						if (p instanceof Pawn)
							FEN = FEN + "p";
						else if (p instanceof Knight)
							FEN = FEN + "n";
						else if (p instanceof Bishop)
							FEN = FEN + "b";
						else if (p instanceof Rook)
							FEN = FEN + "r";
						else if (p instanceof Queen)
							FEN = FEN + "q";
						else if (p instanceof King)
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
		
		for (int r = 0; r < 8; r++)
		{
			String rFEN = FEN[r];
			int index = 0;
			for (int c = 0; c < 8; c++, index++)
			{
				char p = rFEN.charAt(index);
				Piece pp = null;
				
				int emptysquares = p - '1';
				if (emptysquares >= 0 && emptysquares <= 8)
				{
					c = c + emptysquares; //skip the empty squares, remember that the loop increments c by 1
					continue;
				}

				switch (p)
				{
				case 'P':
					pp = new Pawn(r, c, Definitions.Color.WHITE);
					break;
				case 'p':
					pp = new Pawn(r, c, Definitions.Color.BLACK);
					break;
				case 'B':
					pp = new Bishop(r, c, Definitions.Color.WHITE);
					break;
				case 'b':
					pp = new Bishop(r, c, Definitions.Color.BLACK);
					break;
				case 'N':
					pp = new Knight(r, c, Definitions.Color.WHITE);
					break;
				case 'n':
					pp = new Knight(r, c, Definitions.Color.BLACK);
					break;
				case 'R':
					pp = new Rook(r, c, Definitions.Color.WHITE);
					break;
				case 'r':
					pp = new Rook(r, c, Definitions.Color.BLACK);
					break;
				case 'Q':
					pp = new Queen(r, c, Definitions.Color.WHITE);
					break;
				case 'q':
					pp = new Queen(r, c, Definitions.Color.BLACK);
					break;
				case 'K':
					pp = new King(r, c, Definitions.Color.WHITE);
					break;
				case 'k':
					pp = new King(r, c, Definitions.Color.BLACK);
					break;
				}
				if (pp != null)
				{
					placePiece(pp, r, c);
				}
			}
		}
		updateKingLocs();
	}
}
