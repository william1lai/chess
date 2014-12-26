package chess;

import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

import chess.AliceBoard.AliceMove;

import java.util.Stack;

public class AliceGame extends Game
{
	private AliceGameGraphics m_graphics;
	private AliceGameGUI m_gui;
	private AliceBoard m_game_board;
	private boolean m_canUndo;
	
	public AliceGame(GameApplet applet)
	{
		m_applet = applet;
	}

	public void init(GameGraphics graphics, GameGUI gui)
	{
		m_graphics = (AliceGameGraphics)graphics;
		m_gui = (AliceGameGUI)gui;
		m_game_board = new AliceBoard(this);
		m_canUndo = false;
		movesHistory = new Stack<String>();
		
		Definitions.makeInitB();
		Definitions.makeMaskB();
		Definitions.makeInitR();
		Definitions.makeMaskR();
		Definitions.makeRankR();

		//String testFEN = "8/8/7P/8/8/8/8/k5K1 w - - 0 37"; //white to promote soon; tests promotion
		//String testFEN = "6k1/8/5r2/6K1/8/8/8/5q2 w - - 0 37"; //losing badly, tests player 2 checkmate power
		//String testFEN = "k7/7Q/K7/8/8/8/8/8 w - - 0 37"; //winning badly, can use to test checkmate/stalemate
		//String testFEN = "r1b1k2B/1p5p/2p3p1/p4p2/2BK4/8/PPP1Q1PP/R6R b - - 0 37";
		//String testFEN = "8/8/8/1Q6/8/8/8/k6K w - - 0 37"; //test queen movement on b file
		//m_game_board.FENtoPosition(testFEN);

		setupStandard();

		String[] param = { "White vs AI", "Black vs AI", "Hotseat Game", "AI vs AI" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Mode?", "Choose your mode", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "White vs AI")
		{
			p1 = new AliceHumanPlayer("Human WHITE", Definitions.Color.WHITE, this);
			p2 = new AliceComputerPlayer("CPU BLACK", Definitions.Color.BLACK, this);
		}
		else if (input == "Black vs AI")
		{
			p2 = new AliceHumanPlayer("Human BLACK", Definitions.Color.BLACK, this);
			p1 = new AliceComputerPlayer("CPU WHITE", Definitions.Color.WHITE, this);
		}
		else if (input == "Hotseat Game")
		{
			p1 = new AliceHumanPlayer("Human WHITE", Definitions.Color.WHITE, this);
			p2 = new AliceHumanPlayer("Human BLACK", Definitions.Color.BLACK, this);
		}
		else if (input == "AI vs AI")
		{
			p1 = new AliceComputerPlayer("CPU WHITE", Definitions.Color.WHITE, this);
			p2 = new AliceComputerPlayer("CPU BLACK", Definitions.Color.BLACK, this);
		}
		else
		{
			return;
		}
		
		if (m_game_board.whoseTurn() == Definitions.Color.WHITE)
		{
			p1.promptMove();
		}
		else
		{
			p2.promptMove();
		}
	}

	public void setupStandard()
	{
		//by default, the board is set up in the standard fashion
		m_game_board.setTurn(Definitions.Color.WHITE);
	}

	public void run()
	{
		Definitions.State state = m_game_board.getState();
		while (state == Definitions.State.NORMAL && m_game_board.getFiftymoverulecount() < 100) //50 moves for each side
		{
			Player cur = (m_game_board.whoseTurn() == Definitions.Color.WHITE ? p1 : p2);
			if (cur.getColor() == Definitions.Color.WHITE)
				m_game_board.incrementTurncount();
			if (!m_graphics.isAnimating() && cur instanceof HumanPlayer)
				m_canUndo = true;
			if (!m_graphics.isAnimating() && cur.isDone())
			{
				m_canUndo = false;
				movesHistory.push(m_game_board.toFEN(true));
				Move m = cur.getMove();
				int board = m_graphics.getActiveBoard();
				AliceMove am = m_game_board.new AliceMove(m, board);
				if (am.m == null)
					break;

				processMove(am);
				flipTurn();
				state = m_game_board.getState();
			}
			m_graphics.updateGameState();
			m_applet.repaint();
			try { Thread.sleep(30); }
			catch (InterruptedException e) {}
		}
		while (m_graphics.isAnimating()) {
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

		JOptionPane.showMessageDialog(null, reason, "Game has ended", JOptionPane.PLAIN_MESSAGE);
		System.out.println("The game has ended.");
	}
	
	public AliceGameGraphics getGraphics()
	{
		return m_graphics;
	}
	
	public AliceGameGUI getGUI()
	{
		return m_gui;
	}

	public AliceBoard getBoard()
	{
		return m_game_board;
	}
	
	private void flipTurn() //prompts next player's move; board does actual flipping of turns
	{	
		Player next = (m_game_board.whoseTurn() == Definitions.Color.WHITE ? p1 : p2);
		next.promptMove();
	}

	public void undo()
	{
		if (movesHistory.size() >= 2 && m_canUndo)
		{
			m_game_board.decrementTurncount();
			movesHistory.pop();
			String returnMove = movesHistory.pop();

			m_game_board.FENtoPosition(returnMove);
		}
	}

	//TODO: Might need clean up
	public void processMove(AliceMove newMove)
	{
		int row = newMove.m.r0;
		int col = newMove.m.c0;
		int board = newMove.board;
		int otherboard = 0;
		if (board == 0)
			otherboard = 1;
		char movedPiece = getBoard().getPiece(row, col, board);
		getBoard().getData().m_fiftymoverulecount++;

		int castlingRow;
		if (getBoard().whoseTurn() == Definitions.Color.WHITE)
		{
			castlingRow = 7;
		}
		else //Black
		{
			castlingRow = 0;
		}

		AliceMove correspondingRookMove = null; //if we have castling
		getBoard().getData().m_enpassantCol = -1; //default
		if (Character.toLowerCase(movedPiece) == 'p')
		{
			getBoard().getData().m_fiftymoverulecount = 0; //pawn was moved
			if (Math.abs(newMove.m.rf - newMove.m.r0) == 2)
			{
				getBoard().getData().m_enpassantCol = col; //enpassant now available on this column
			}
			else if ((Math.abs(newMove.m.cf - col) == 1) && (getBoard().getPiece(newMove.m.rf, newMove.m.cf, board) == 0))
				//en passant
			{
				if (getBoard().whoseTurn() == Definitions.Color.WHITE)
				{
					getBoard().removePiece(3, newMove.m.cf, otherboard); //not sure if this is best way, but "move" call will not erase piece
				}
				else
				{
					getBoard().removePiece(4, newMove.m.cf, otherboard);
				}
			}
		}
		else if (Character.toLowerCase(movedPiece) == 'k')
		{			
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

			int kingMoveLength = newMove.m.cf - col; //should be 2 or -2, if the move was a castling move
			if (row == castlingRow)
			{
				if (kingMoveLength == 2) //kingside
				{
					correspondingRookMove = getBoard().new AliceMove(new Move(castlingRow, 7, castlingRow, 5), board);
				}
				else if (kingMoveLength == -2) //queenside
				{
					correspondingRookMove = getBoard().new AliceMove(new Move(castlingRow, 0, castlingRow, 3), board);
				}
			}
		}
		else if (row == castlingRow)
		{
			if (col == 0) //queen's rook
			{
				if (getBoard().whoseTurn() == Definitions.Color.WHITE)
				{
					getBoard().getData().m_whiteCanCastleQueenside = false;
				}
				else
				{
					getBoard().getData().m_blackCanCastleQueenside = false;
				}
			}
			else if (col == 7) //king's rook
			{			
				if (getBoard().whoseTurn() == Definitions.Color.WHITE)
				{
					getBoard().getData().m_whiteCanCastleKingside = false;
				}
				else
				{
					getBoard().getData().m_blackCanCastleKingside = false;
				}
			}
		}

		if (getBoard().getPiece(newMove.m.rf, newMove.m.cf, board) != 0) //capture was made
		{
			getBoard().getData().m_fiftymoverulecount = 0; //reset counter
		}

		if (correspondingRookMove == null)
		{
			m_graphics.animateMove(newMove, getBoard());
			getBoard().move(newMove.m, board); //has to be down here for time being because en passant needs to know dest sq is empty; fix if you can
		}
		else {
			m_graphics.animateCastlingMoves(newMove, correspondingRookMove, getBoard());
			getBoard().move(newMove.m, board);
			getBoard().setTurn(Definitions.flip(getBoard().whoseTurn())); //to undo double flipping of moving king and then rook
			getBoard().move(correspondingRookMove.m, board);
		}

		if (Character.toLowerCase(movedPiece) == 'p')
		{
			if (((getBoard().whoseTurn() == Definitions.Color.BLACK) && (newMove.m.rf == 0)) 
					|| ((getBoard().whoseTurn() == Definitions.Color.WHITE) && (newMove.m.rf == 7))) //flipped by earlier move
			{
				getBoard().promotePawn(newMove.m.rf, newMove.m.cf, board);
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
