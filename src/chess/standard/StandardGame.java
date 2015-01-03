package chess.standard;

import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

import chess.Debug;
import chess.Definitions;
import chess.Game;
import chess.GameApplet;
import chess.GameGUI;
import chess.GameGraphics;
import chess.HumanPlayer;
import chess.Move;
import chess.Player;

import java.util.HashMap;
import java.util.Stack;

public class StandardGame extends Game
{
	private StandardGameGraphics m_graphics;
	private StandardGameGUI m_gui;
	private StandardBoard m_game_board;
	private boolean m_canUndo;
	private HashMap<String, Integer> repeats;
	
	public StandardGame(GameApplet applet)
	{
		m_applet = applet;
	}

	public void init(GameGraphics graphics, GameGUI gui)
	{
		m_graphics = (StandardGameGraphics)graphics;
		m_gui = (StandardGameGUI)gui;
		m_game_board = new StandardBoard(this);
		m_canUndo = false;
		movesHistory = new Stack<String>();
		repeats = new HashMap<String, Integer>();

		//initializes stuff we need for our bitboard functions
		Definitions.makeInitB();
		Definitions.makeMaskB();
		Definitions.makeInitR();
		Definitions.makeMaskR();
		Definitions.makeRankR();

		//These are some basic test scenarios we can load via FEN
		//String testFEN = "8/8/7P/8/8/8/8/k5K1 w - - 0 37"; //white to promote soon; tests promotion
		//String testFEN = "6k1/8/5r2/6K1/8/8/8/5q2 w - - 0 37"; //losing badly, tests player 2 checkmate power
		//String testFEN = "k7/7Q/K7/8/8/8/8/8 w - - 0 37"; //winning badly, can use to test checkmate/stalemate
		//String testFEN = "r1b1k2B/1p5p/2p3p1/p4p2/2BK4/8/PPP1Q1PP/R6R b - - 0 37";
		//String testFEN = "8/8/8/1Q6/8/8/8/k6K w - - 0 37"; //test queen movement on b file
		//m_game_board.FENtoPosition(testFEN);

		setupStandard();

		String[] param = { "White vs AI", "Black vs AI", "Hotseat Game", "AI vs AI" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Mode?", "Choose your mode" + (Debug.IsDebugging() ? " (DEBUG Mode)" : ""), JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "White vs AI")
		{
			p1 = new StandardHumanPlayer("Human WHITE", Definitions.Color.WHITE, this);
			p2 = new StandardComputerPlayer("CPU BLACK", Definitions.Color.BLACK, this);
		}
		else if (input == "Black vs AI")
		{
			p2 = new StandardHumanPlayer("Human BLACK", Definitions.Color.BLACK, this);
			p1 = new StandardComputerPlayer("CPU WHITE", Definitions.Color.WHITE, this);
		}
		else if (input == "Hotseat Game")
		{
			p1 = new StandardHumanPlayer("Human WHITE", Definitions.Color.WHITE, this);
			p2 = new StandardHumanPlayer("Human BLACK", Definitions.Color.BLACK, this);
		}
		else if (input == "AI vs AI")
		{
			p1 = new StandardComputerPlayer("CPU WHITE", Definitions.Color.WHITE, this);
			p2 = new StandardComputerPlayer("CPU BLACK", Definitions.Color.BLACK, this);
		}
		else //for cases like user closing window
		{
			System.exit(0);
			return;
		}
		
		if (Debug.IsDebugging())
		{
			//just testing
			interpretMoveList("1.e4 e5 2.Nf3 Nc6 3.Bb5 a6 4.Bxc6+ dxc6 5.O-O Nf6");
		}
		
		if (m_game_board.whoseTurn() == Definitions.Color.WHITE)
			p1.promptMove();
		else
			p2.promptMove();
	}

	public void setupStandard()
	{
		//by default, the board is set up in the standard fashion, so we need not do anything extra
		m_game_board.setTurn(Definitions.Color.WHITE);
	}

	public void run()
	{
		mainGameLoop();		
	}
	
	public void mainGameLoop()
	{
		Definitions.State state = m_game_board.getState();
		while (state == Definitions.State.NORMAL && m_game_board.getFiftymoverulecount() < 100) //50 moves for each side
		{
			Player cur = (m_game_board.whoseTurn() == Definitions.Color.WHITE ? p1 : p2);
			if (cur.getColor() == Definitions.Color.WHITE)
				m_game_board.incrementTurncount();
			if (!m_graphics.isAnimating() && cur instanceof HumanPlayer) //only allow undo if not animating and is human player turn
				m_canUndo = true;
			if (!m_graphics.isAnimating() && cur.isDone())
			{
				String FEN = m_game_board.toFEN(false); //we don't want turncounts making each position unique
				if (!repeats.containsKey(FEN))
					repeats.put(FEN, 1);
				else
				{
					if (repeats.get(FEN) >= 2)
						break;
					else
					{
						System.out.println("This position has repeated itself. One more and game will be drawn. hi");
						repeats.put(FEN, 2);
					}
				}
				m_canUndo = false;
				movesHistory.push(m_game_board.toFEN(true));
				Move m = cur.getMove();
				if (m == null) //null move might indicate CPU acknowledging getting checkmated
					break;

				processMove(m, true);
				flipTurn();
				state = m_game_board.getState();
			}
			m_graphics.updateGameState();
			m_applet.repaint();
			try { Thread.sleep(30); }
			catch (InterruptedException e) {}
		}
		while (m_graphics.isAnimating())
		{
			m_graphics.updateGameState();
			m_applet.repaint();
			try { Thread.sleep(30); }
			catch (InterruptedException e) {}
		}
		String reason = "";
		Definitions.Color winner = null; //indicating stalemate by default
		if (state == Definitions.State.CHECKMATE)
		{
			winner = Definitions.flip(m_game_board.whoseTurn());
			reason = winner.toString() + " won by Checkmate!";
		}
		else if (state == Definitions.State.STALEMATE)
		{
			reason = "Drawn by Stalemate!";
		}
		else if (m_game_board.getFiftymoverulecount() >= 100)
		{
			reason = "Drawn by 50-move rule!";
		}
		else
		{
			reason = "Drawn by threefold repetition!";
		}

		JOptionPane.showMessageDialog(null, reason, "Game has ended", JOptionPane.PLAIN_MESSAGE);
		System.out.println("The game has ended.");
	}

	//Special case for use with parsing algebraic notation, since that requires going move by move
	private void inputOneMove(Move mv)
	{
		Player cur = (m_game_board.whoseTurn() == Definitions.Color.WHITE ? p1 : p2);
		if (cur.getColor() == Definitions.Color.WHITE)
			m_game_board.incrementTurncount();
		String FEN = m_game_board.toFEN(false); //we don't want turncounts making each position unique
		if (!repeats.containsKey(FEN))
			repeats.put(FEN, 1);
		else
		{
			if (repeats.get(FEN) >= 2)
				repeats.put(FEN, repeats.get(FEN) + 1);
			else
			{
			        
				System.out.println("This position has repeated itself. One more and game will be drawn.");
				repeats.put(FEN, 2);
			}
		}
		m_canUndo = false;
		movesHistory.push(m_game_board.toFEN(true));
		processMove(mv, false);
		flipTurn();
	}
	
	public StandardGameGraphics getGraphics()
	{
		return m_graphics;
	}
	
	public StandardGameGUI getGUI()
	{
		return m_gui;
	}

	public StandardBoard getBoard()
	{
		return m_game_board;
	}
	
	//prompts next player's move; board does actual flipping of turns
	private void flipTurn()
	{	
		Player next = (m_game_board.whoseTurn() == Definitions.Color.WHITE ? p1 : p2);
		next.promptMove();
	}

	public static Move algebraicHelper(long possorig, int orow, int ocol, int drow, int dcol)
	{
		//by default, go from 0 to 63 and check
		int count = 0;
		int start = 0;
		int end = 64;
		int inc = 1;
		if (orow != -1) //if we know the origin row
		{
			start = (7 - orow)*8;
			end = start + 8;
		}
		else if (ocol != -1) //if we know the origin column
		{
			start = 7 - ocol;
			inc = 8;
		}
		
		for (int i = start; i < end; i = i + inc)
		{
			if (((possorig >>> i) & 1L) == 1)
			{
				orow = i / 8;
				ocol = i % 8;
				count++;
			}
		}
		
		if (count > 1 || count == 0)
			return null; //not specific enough or no choices
		return new Move(7 - orow, 7 - ocol, drow, dcol);
	}
	
	//Parses algebraic move list into moves and processes them; input not entirely sanitized yet
	public static Move algebraicToMove(StandardBoard b, Definitions.Color color, String algebraic)
	{
		//e4 Ne2 Nge2 Bxc6 Bxc6+ O-O O-O-O a8=Q+ Qxg7# are some cases
		//we don't check for legality here, so no fear of en passant or checks/checkmates etc.
		boolean isWhite = (color == Definitions.Color.WHITE);
		int len = algebraic.length();
		if (algebraic.endsWith("+") || algebraic.endsWith("#")) //remove check/checkmate, as they're irrelevant to actual move
		{
			algebraic = algebraic.substring(0, len - 1);
			len = len - 1;
		}
		if (len < 2)
			return null;
		if (algebraic.equals("O-O")) //castling kingside
		{
			if (isWhite)
				return new Move(7, 4, 7, 6);
			else
				return new Move(0, 4, 0, 6);
		}
		if (algebraic.equals("O-O-O")) //castling queenside
		{
			if (isWhite)
				return new Move(7, 4, 7, 2);
			else
				return new Move(0, 4, 0, 2);
		}
		
		char first = algebraic.charAt(0);
		if (Character.isLowerCase(first)) //pawn move
		{
			long allpieces = b.getWhite() | b.getBlack();
			int col = first - 'a';
			if (Character.isDigit(algebraic.charAt(1))) //e4 or e8=Q cases
			{
				int row = algebraic.charAt(1) - '1';
				int dsq = row*8 + col;
				if (isWhite)
				{
					if (dsq < 16)
						return null;
					if (row == 3 && (((allpieces >>> (dsq - 8)) & 1L) != 1))
					{
						return new Move(6, col, 4, col);
					}
					return new Move(8 - row, col, 7 - row, col);
				}
				else //black
				{
					if (dsq >= 48)
						return null;
					if (row == 4 && (((allpieces >>> (dsq + 8)) & 1L) != 1))
					{
						return new Move(1, col, 3, col);
					}
					return new Move(6 - row, col, 7 - row, col);					
				}
			}
			else if (algebraic.charAt(1) == 'x') //exf6 or exf8=Q cases
			{
				if (len < 4)
					return null;
				
				int othercol = (algebraic.charAt(2) - 'a');
				int row = (algebraic.charAt(3) - '1');
				if (isWhite)
				{
					if (row < 2)
						return null;
					
					char promo = 0;
					if (len == 6) //exf8=Q case
						promo = algebraic.charAt(5);
					return new Move(8 - row, col, 7 - row, othercol, promo);
				}
				else //black
				{
					if (row > 5)
						return null;
					
					char promo = 0;
					if (len == 6)
						promo = algebraic.charAt(5);
					return new Move(6 - row, col, 7 - row, othercol, promo);
				}
			}
		}
		
		//for nonpawns, cases are Ne2, Nge2, N1e2, Nxe2, Ngxe2, N1xe2
		//note it's never necessary to need both row and column
		long piecesofinterest;
		long free = ~(b.getWhite() | b.getBlack());
		char second = algebraic.charAt(1);
		int drow = 7 - (algebraic.charAt(len - 1) - '1');
		int dcol = algebraic.charAt(len - 2) - 'a';
		int dsq = (7 - drow)*8 + (7 - dcol);
		long possorigin = 0; //1 bits represent possible origins
		
		if (first == 'N')
		{
			piecesofinterest = b.getKnights() & b.getBlack();
			if (isWhite)
				piecesofinterest = b.getKnights() & b.getWhite();
			possorigin = Definitions.knightAttacks(1L << dsq) & piecesofinterest;
		}
		else if (first == 'B')
		{
			piecesofinterest = b.getBishops() & b.getBlack();
			if (isWhite)
				piecesofinterest = b.getBishops() & b.getWhite();
			possorigin = Definitions.bishopAttacks(dsq, free) & piecesofinterest;
			//System.out.println(String.format("0x%16s", Long.toHexString(possorigin)).replace(' ', '0'));
		}
		else if (first == 'R')
		{
			piecesofinterest = b.getRooks() & b.getBlack();
			if (isWhite)
				piecesofinterest = b.getRooks() & b.getWhite();
			possorigin = Definitions.rookAttacks(1L << dsq, free) & piecesofinterest;
		}
		else if (first == 'Q')
		{
			piecesofinterest = b.getQueens() & b.getBlack();
			if (isWhite)
				piecesofinterest = b.getQueens() & b.getWhite();
			possorigin = Definitions.queenAttacks(1L << dsq, free) & piecesofinterest;
		}
		else if (first == 'K')
		{
			piecesofinterest = b.getKings() & b.getBlack();
			if (isWhite)
				piecesofinterest = b.getKings() & b.getWhite();
			possorigin = Definitions.kingAttacks(1L << dsq) & piecesofinterest;
		}
		
		if (possorigin == 0)
			return null;
		if (len < 3)
			return null;
		int orow = -1;
		int ocol = -1;
		if ((len == 4 && second != 'x') || len > 4)
		{
			if (Character.isDigit(second))
				orow = second - '1';
			else
				ocol = second - 'a';
		}
		return algebraicHelper(possorigin, orow, ocol, drow, dcol);
	}

	public void interpretMoveList(String movelist) //needs testing
	{
		//start with naive format of "1.e4 c5 2.Nc3 Nc6 3.f4 g6 4.Bb5 Nd4", with proper spacing and all
		String[] moves = movelist.split(" ");

		for (int i = 0; i < moves.length; i++)
		{
			String mv = moves[i];
			Move nextmove = null;
			if (Character.isDigit(mv.charAt(0)))
			{
				System.out.print(mv + " "); //print out moves
				//we want the part after the period
				nextmove = algebraicToMove(getBoard(), Definitions.Color.WHITE, mv.split("\\.")[1]); //freaking regexes
			}
			else
			{
				System.out.println(mv);
				nextmove = (algebraicToMove(getBoard(), Definitions.Color.BLACK, mv));
			}
			if (nextmove == null)
			{
				System.out.println("Invalid movelist.");
				break;
			}
			inputOneMove(nextmove);
		}
	}

	public void undo()
	{	    
		if (movesHistory.size() >= 2 && m_canUndo)
		{
			m_game_board.decrementTurncount();

			String returnMove2 = movesHistory.pop();
			String returnMove1 = movesHistory.pop();
			System.out.println(returnMove2 + " || " + returnMove1); //TODO

			//Here, we reconstruct the FEN from movesHistory so as not to have turncount and 50movecount
			String[] returnMove2Parts = returnMove2.split(" ");
			String returnMove2Key = "";
			int i;
			for (i = 0; i < returnMove2Parts.length - 3; i++)
				returnMove2Key = returnMove2Key + returnMove2Parts[i] + " ";			
			returnMove2Key += returnMove2Parts[i];
			
			String[] returnMove1Parts = returnMove1.split(" ");
			String returnMove1Key = "";
			for (i = 0; i < returnMove1Parts.length - 3; i++)
				returnMove1Key = returnMove1Key + returnMove1Parts[i] + " ";

			returnMove1Key += returnMove1Parts[i];

			//decrement three move repetition hash table as necessary
			if (repeats.get(returnMove2Key) > 1)
				repeats.put(returnMove2Key, repeats.get(returnMove2Key) - 1);
			else //remove from hash table if it only occurred once
				repeats.remove(returnMove2Key);

			//do same for other move that got undone
			if (repeats.get(returnMove1Key) > 1)
				repeats.put(returnMove1Key, repeats.get(returnMove1Key) - 1);
			else
				repeats.remove(returnMove1Key);

			m_game_board.FENtoPosition(returnMove1); //restore position from two ply prior
		}
	}

	//TODO: Might need clean up
	public void processMove(Move newMove, boolean animatePlease)
	{
		int row = newMove.r0;
		int col = newMove.c0;
		char movedPiece = getBoard().getPiece(row, col);
		getBoard().getData().m_fiftymoverulecount++;

		int castlingRow;
		if (getBoard().whoseTurn() == Definitions.Color.WHITE)
			castlingRow = 7;
		else //Black
			castlingRow = 0;

		Move correspondingRookMove = null; //if we have castling
		getBoard().getData().m_enpassantCol = -1; //default
		if (Character.toLowerCase(movedPiece) == 'p')
		{
			getBoard().getData().m_fiftymoverulecount = 0; //pawn was moved
			if (Math.abs(newMove.rf - newMove.r0) == 2)
			{
				getBoard().getData().m_enpassantCol = newMove.c0; //enpassant now available on this column
			}
			else if ((Math.abs(newMove.cf - newMove.c0) == 1) && (getBoard().getPiece(newMove.rf, newMove.cf) == 0))
				//en passant
			{
				if (getBoard().whoseTurn() == Definitions.Color.WHITE)
					getBoard().removePiece(3, newMove.cf); //not sure if this is best way, but "move" call will not erase piece
				else
					getBoard().removePiece(4, newMove.cf);
			}
		}
		else if (Character.toLowerCase(movedPiece) == 'k')
		{
			//king that moves can no longer castle
			if (getBoard().whoseTurn() == Definitions.Color.WHITE)
			{
				getBoard().getData().m_whiteCanCastleKingside = false;
				getBoard().getData().m_whiteCanCastleQueenside = false;
			}
			else
			{
				getBoard().getData().m_blackCanCastleKingside = false;
				getBoard().getData().m_blackCanCastleQueenside = false;
			}

			int kingMoveLength = newMove.cf - col; //should be 2 or -2, if the move was a castling move
			if (row == castlingRow)
			{
				if (kingMoveLength == 2) //kingside
					correspondingRookMove = new Move(castlingRow, 7, castlingRow, 5);
				else if (kingMoveLength == -2) //queenside
					correspondingRookMove = new Move(castlingRow, 0, castlingRow, 3);
			}
		}
		else if (row == castlingRow)
		{
			//rook that moves can no longer be used for castling
			if (col == 0) //queen's rook
			{
				if (getBoard().whoseTurn() == Definitions.Color.WHITE)
					getBoard().getData().m_whiteCanCastleQueenside = false;
				else
					getBoard().getData().m_blackCanCastleQueenside = false;
			}
			else if (col == 7) //king's rook
			{			
				if (getBoard().whoseTurn() == Definitions.Color.WHITE)
					getBoard().getData().m_whiteCanCastleKingside = false;
				else
					getBoard().getData().m_blackCanCastleKingside = false;
			}
		}

		if (getBoard().getPiece(newMove.rf, newMove.cf) != 0) //capture was made
			getBoard().getData().m_fiftymoverulecount = 0; //reset fifty move counter

		if (correspondingRookMove == null)
		{
			if (animatePlease)
				m_graphics.animateMove(newMove, getBoard());
			//TODO: has to be down here for time being because en passant needs to know dest sq is empty; fix if you can
			getBoard().move(newMove);
		}
		else 
		{
			if (animatePlease)
				m_graphics.animateCastlingMoves(newMove, correspondingRookMove, getBoard());
			getBoard().move(newMove);
			getBoard().setTurn(Definitions.flip(getBoard().whoseTurn())); //to undo double flipping of moving king and then rook
			getBoard().move(correspondingRookMove);
		}

		if (Character.toLowerCase(movedPiece) == 'p')
		{
			if (((getBoard().whoseTurn() == Definitions.Color.BLACK) && (newMove.rf == 0)) 
					|| ((getBoard().whoseTurn() == Definitions.Color.WHITE) && (newMove.rf == 7))) //flipped by earlier move
			{
				getBoard().promotePawn(newMove.rf, newMove.cf);
			}
		}
	}
	
	//Useless for now
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
}
