package chess;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class GameApplet extends Applet implements Runnable, MouseListener
{
	private Thread m_thread;
	private Game m_game;
	private StandardChessGameGraphics m_graphics;
	private StandardChessGameAnimation m_animation;
	private StandardChessGameGUI m_gui;
	private boolean m_cancel;

	public void init()
	{
		m_cancel = true;
		chooseGame();
		if (m_cancel)
		{
			return;
		}
		
		m_gui = new StandardChessGameGUI();
		m_graphics = new StandardChessGameGraphics(m_gui);
		m_animation = new StandardChessGameAnimation(m_graphics);

		addMouseListener(m_gui);
		addFocusListener(m_gui);
		m_thread = new Thread(this);
		m_thread.start();

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

		m_thread = new Thread(this);
		m_thread.start();

		if (getGame() instanceof StandardChessGame)
		{
			StandardChessGame scg = (StandardChessGame)getGame();
			if (scg.getBoard().whoseTurn() == Definitions.Color.WHITE)
				scg.p1.promptMove();
			else
				scg.p2.promptMove();
		}
		else if (getGame() instanceof LosersChessGame)
		{
			LosersChessGame lcg = (LosersChessGame)getGame();
			if (lcg.getBoard().whoseTurn() == Definitions.Color.WHITE)
				lcg.p1.promptMove();
			else
				lcg.p2.promptMove();
		}
	}

	public void chooseGame()
	{
		String[] param = { "Standard Chess", "Loser's Chess" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Type?", "Choose your game", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "Standard Chess")
		{
			m_game = new StandardChessGame(this);
			m_cancel = false;
		}
		else if (input == "Loser's Chess")
		{
			m_game = new LosersChessGame(this);
			m_cancel = false;
		}
	}

	public Game getGame()
	{
		return m_game;
	}

	public boolean cancelled()
	{
		return m_cancel;
	}

	public void run()
	{		
		if (getGame() instanceof StandardChessGame)
		{
			((StandardChessGame)getGame()).run();
		}
		else if (getGame() instanceof LosersChessGame)
		{
			((LosersChessGame)getGame()).run();
		}
	}

	public void paint(Graphics g)
	{
		if (m_cancel)
			return;
		
		//Painting with a backbuffer reduces flickering
		Image backbuffer = createImage(g.getClipBounds().width, g.getClipBounds().height);
		Graphics backg = backbuffer.getGraphics();

		m_graphics.drawBackground(backg);
		m_graphics.drawBoard(backg);
		if (getGame().p1 instanceof HumanPlayer)
		{
			int sq = ((HumanPlayer)getGame().p1).getSelected();

			if (getGame() instanceof StandardChessGame)
			{
				StandardChessGame scg = (StandardChessGame)getGame();
				m_graphics.drawMovable(backg, scg.getBoard().allMovesPiece(7 - (sq / 8), 7 - (sq % 8)));
			}
			else if (getGame() instanceof LosersChessGame)
			{
				LosersChessGame lcg = (LosersChessGame)getGame();
				m_graphics.drawMovable(backg, lcg.getBoard().allMovesPiece(7 - (sq / 8), 7 - (sq % 8)));
			}

			m_graphics.drawSelected(backg, ((HumanPlayer)getGame().p1).getSelected());
		}
		if (getGame().p2 instanceof HumanPlayer)
		{
			int sq = ((HumanPlayer)getGame().p2).getSelected();

			if (getGame() instanceof StandardChessGame)
			{
				StandardChessGame scg = (StandardChessGame)getGame();
				m_graphics.drawMovable(backg, scg.getBoard().allMovesPiece(7 - (sq / 8), 7 - (sq % 8)));
			}
			else if (getGame() instanceof LosersChessGame)
			{
				LosersChessGame lcg = (LosersChessGame)getGame();
				m_graphics.drawMovable(backg, lcg.getBoard().allMovesPiece(7 - (sq / 8), 7 - (sq % 8)));
			}

			m_graphics.drawSelected(backg, ((HumanPlayer)getGame().p2).getSelected());
		}
		m_graphics.drawBorders(backg);
		m_graphics.drawMarkers(backg);

		if (getGame() instanceof StandardChessGame)
		{
			StandardChessGame scg = (StandardChessGame)getGame();
			m_graphics.drawNames(backg, getGame().p1, getGame().p2, scg.getBoard().whoseTurn());
			m_graphics.drawPieces(backg, scg.getBoard());			
		}
		else if (getGame() instanceof LosersChessGame)
		{
			LosersChessGame lcg = (LosersChessGame)getGame();
			m_graphics.drawNames(backg, getGame().p1, getGame().p2, lcg.getBoard().whoseTurn());
			m_graphics.drawPieces(backg, lcg.getBoard());
		}

		m_graphics.drawGUI(backg);

		g.drawImage(backbuffer, 0, 0, this);
	}

	public StandardChessGameAnimation getAnimation()
	{
		return m_animation;
	}

	public void undo()
	{
		if (m_game.movesHistory.size() < 2)
			return;

		if (m_game instanceof StandardChessGame)
		{
			StandardChessGame scg = (StandardChessGame)m_game;
			if (scg.canUndo())
			{
				scg.getBoard().decrementTurncount();

				m_game.movesHistory.pop();
				String returnMove = m_game.movesHistory.pop();

				scg.getBoard().FENtoPosition(returnMove);
			}			
		}
		else if (m_game instanceof LosersChessGame)
		{
			LosersChessGame lcg = (LosersChessGame)m_game;
			if (lcg.canUndo())
			{
				lcg.getBoard().decrementTurncount();

				m_game.movesHistory.pop();
				String returnMove = m_game.movesHistory.pop();

				lcg.getBoard().FENtoPosition(returnMove);
			}
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
