package chess;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import java.util.Stack;

@SuppressWarnings("serial")
public class LosersChessGame extends Game implements Runnable
{
	private Thread m_thread;
	private LosersChessBoard m_game_board;
	private StandardChessGameGraphics m_graphics;
	private StandardChessGameAnimation m_animation;
	private StandardChessGameGUI m_gui;
	private Stack<String> movesHistory = new Stack<String>();
	private boolean canUndo;
	private boolean doneInitializing;

	public void init()
	{
		m_game_board = new LosersChessBoard(this);
		m_gui = new StandardChessGameGUI();
		m_graphics = new StandardChessGameGraphics(m_gui);
		m_animation = new StandardChessGameAnimation(m_graphics);
		canUndo = false;
		
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
			doneInitializing = false;
			return;
		}

		addMouseListener(m_gui);
		addFocusListener(m_gui);
		m_thread = new Thread(this);
		m_thread.start();

		if (m_game_board.whoseTurn() == Definitions.Color.WHITE)
			p1.promptMove();
		else
			p2.promptMove();

		try {
			EasyButton b = new EasyButton("buttonUndo", 480, 220, 90, 30, new EasyButtonAction() {
				public void on_press()
				{
					undo();
				}
			});
			m_gui.addButton(b);
		}
		catch (Exception ex) {}
		doneInitializing = true;
	}
	
	public boolean initialized()
	{
		return doneInitializing;
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
				canUndo = true;
			if (cur.isDone())
			{
				canUndo = false;
				movesHistory.push(m_game_board.toFEN(true));
				Move m = cur.getMove();
				if (m == null)
					break;

				m_game_board.processMove(m);
				flipTurn();
				state = m_game_board.getState();
			}
			try { Thread.sleep(30); }
			catch (InterruptedException e) {}
			repaint();
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

	public void paint(Graphics g)
	{
		//Painting with a backbuffer reduces flickering
		Image backbuffer = createImage(g.getClipBounds().width, g.getClipBounds().height);
		Graphics backg = backbuffer.getGraphics();

		m_graphics.drawBackground(backg);
		m_graphics.drawBoard(backg);
		if (p1 instanceof HumanPlayer)
		{
			int sq = ((HumanPlayer)p1).getSelected();
			m_graphics.drawMovable(backg, m_game_board.allMovesPiece(7 - (sq / 8), 7 - (sq % 8)));
			m_graphics.drawSelected(backg, ((HumanPlayer)p1).getSelected());
		}
		if (p2 instanceof HumanPlayer)
		{
			int sq = ((HumanPlayer)p2).getSelected();
			m_graphics.drawMovable(backg, m_game_board.allMovesPiece(7 - (sq / 8), 7 - (sq % 8)));
			m_graphics.drawSelected(backg, ((HumanPlayer)p2).getSelected());
		}
		m_graphics.drawBorders(backg);
		m_graphics.drawMarkers(backg);
		m_graphics.drawNames(backg, p1, p2, m_game_board.whoseTurn());
		m_graphics.drawPieces(backg, m_game_board);
		m_graphics.drawGUI(backg);

		g.drawImage(backbuffer, 0, 0, this);
	}

	public LosersChessBoard getBoard()
	{
		return m_game_board;
	}

	public StandardChessGameAnimation getAnimation()
	{
		return m_animation;
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

		if (canUndo)
		{
			m_game_board.decrementTurncount();

			movesHistory.pop();
			String returnMove = movesHistory.pop();

			m_game_board.FENtoPosition(returnMove);
		}
	}

	//Prevents flickering when repainting
	public void update(Graphics g)
	{
		paint(g);
	}

	public void stop()
	{
		if (m_thread == null)
		{
			super.stop();
		}
		else if (m_thread.isAlive()) 
		{
			m_thread.interrupt();
		}
	}

	//Useless for now
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}

	
}
