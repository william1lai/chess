package chess;

import java.util.Stack;

import javax.swing.JOptionPane;

public class LosersChessGame extends Game
{
	private LosersChessGameGraphics m_graphics;
	private LosersChessBoard m_game_board;
	private boolean m_canUndo;
	
	public LosersChessGame(GameApplet applet)
	{
		m_applet = applet;
	}

	public void init(GameGraphics graphics)
	{
		m_graphics = (LosersChessGameGraphics)graphics;
		m_game_board = new LosersChessBoard(this);
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
		//m_game_board.FENtoPosition(testFEN);

		setupStandard();

		String[] param = { "White vs AI", "Black vs AI", "Hotseat Game", "AI vs AI" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Mode?", "Choose your mode", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "White vs AI")
		{
			p1 = new HumanPlayer("Human WHITE", Definitions.Color.WHITE, this);
			p2 = new ComputerPlayer("CPU BLACK", Definitions.Color.BLACK, this);
		}
		else if (input == "Black vs AI")
		{
			p2 = new HumanPlayer("Human BLACK", Definitions.Color.BLACK, this);
			p1 = new ComputerPlayer("CPU WHITE", Definitions.Color.WHITE, this);
		}
		else if (input == "Hotseat Game")
		{
			p1 = new HumanPlayer("Human WHITE", Definitions.Color.WHITE, this);
			p2 = new HumanPlayer("Human BLACK", Definitions.Color.BLACK, this);
		}
		else if (input == "AI vs AI")
		{
			p1 = new ComputerPlayer("CPU WHITE", Definitions.Color.WHITE, this);
			p2 = new ComputerPlayer("CPU BLACK", Definitions.Color.BLACK, this);
		}
		else
		{
			return;
		}

		if (m_game_board.whoseTurn() == Definitions.Color.WHITE)
			p1.promptMove();
		else
			p2.promptMove();
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
			if (cur instanceof HumanPlayer)
				m_canUndo = true;
			if (cur.isDone())
			{
				m_canUndo = false;
				movesHistory.push(m_game_board.toFEN(true));
				Move m = cur.getMove();
				if (m == null)
					break;

				processMove(m);
				flipTurn();
				state = m_game_board.getState();
			}
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
			winner = m_game_board.whoseTurn();
			reason = winner.toString() + " won by Stalemate!";
		}
		else if (m_game_board.getFiftymoverulecount() >= 100)
		{
			reason = "Drawn by 50-move rule!";
		}

		JOptionPane.showMessageDialog(null, reason, "Game has ended", JOptionPane.PLAIN_MESSAGE);
		System.out.println("The game has ended.");
	}

	public LosersChessBoard getBoard()
	{
		return m_game_board;
	}

	public boolean canUndo()
	{
		return m_canUndo;
	}
	
	private void flipTurn() //prompts next player's move; board does actual flipping of turns
	{	
		Player next = (m_game_board.whoseTurn() == Definitions.Color.WHITE ? p1 : p2);
		next.promptMove();
	}

	//TODO
	public static Move algebraicToMove(Definitions.Color color, String algebraic) //STUB
	{
		return new Move(0, 0, 0, 0);
	}

	public void interpretMoveList(String movelist) //does not work yet
	{
		//start with naive format of "1.e4 c5 2.Nc3 Nc6 3.f4 g6 4.Bb5 Nd4", with proper spacing and all
		String[] moves = movelist.split(" ");

		for (int i = 0; i < moves.length; i++)
		{
			String mv = moves[i];
			if (Character.isDigit(mv.charAt(0)))
			{
				System.out.print(mv + " "); //print out moves
				m_game_board.move(algebraicToMove(Definitions.Color.WHITE, mv.split(".")[1])); //want the part after the period
			}
			else
			{
				System.out.println(mv);
				m_game_board.move(algebraicToMove(Definitions.Color.BLACK, mv));
			}
		}
	}

	public void undo()
	{
		if(movesHistory.size() < 2)
			return;

		if (m_canUndo)
		{
			m_game_board.decrementTurncount();

			movesHistory.pop();
			String returnMove = movesHistory.pop();

			m_game_board.FENtoPosition(returnMove);
		}
	}	
	
	//TODO: Might need clean up
	public void processMove(Move newMove)
	{
		m_graphics.animateMove(newMove, getBoard());
		
		int row = newMove.r0;
		int col = newMove.c0;
		char movedPiece = getBoard().getPiece(row, col);
		getBoard().getData().m_fiftymoverulecount++;

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
				{
					getBoard().removePiece(3, newMove.cf); //not sure if this is best way, but "move" call will not erase piece
				}
				else
				{
					getBoard().removePiece(4, newMove.cf);
				}
			}
		}

		if (getBoard().getPiece(newMove.rf, newMove.cf) != 0) //capture was made
		{
			getBoard().getData().m_fiftymoverulecount = 0; //reset counter
		}

		getBoard().move(newMove); //has to be down here for time being because en passant needs to know dest sq is empty; fix if you can

		if (Character.toLowerCase(movedPiece) == 'p')
		{
			if (((getBoard().whoseTurn() == Definitions.Color.BLACK) && (newMove.rf == 0)) 
					|| ((getBoard().whoseTurn() == Definitions.Color.WHITE) && (newMove.rf == 7))) //flipped by earlier move
			{
				getBoard().promotePawn(newMove.rf, newMove.cf);
			}
		}
	}
}
