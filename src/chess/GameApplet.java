package chess;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JApplet;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class GameApplet extends JApplet implements Runnable, MouseListener
{
	private Thread m_thread;
	private Game m_game;
	private GameGraphics m_graphics;
	private boolean m_cancel;

	public void init()
	{
		m_cancel = true;
		chooseGame();
		if (m_cancel)
		{
			return;
		}
		m_game.init(m_graphics);
		m_graphics.init(m_game);
		
		add(m_graphics);
		addMouseListener(m_graphics.getGUI());
		addFocusListener(m_graphics.getGUI());
		
		m_thread = new Thread(this);
		m_thread.start();

		try {
			EasyButton b = new EasyButton("buttonUndo", 480, 220, 90, 30, new EasyButtonAction() {
				public void on_press()
				{
					undo();
				}
			});
			((StandardChessGameGUI)m_graphics.getGUI()).addButton(b);
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
			m_graphics = new StandardChessGameGraphics(this);
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
	
	public GameGraphics getGameGraphics()
	{
		return m_graphics;
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

	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		if (m_cancel)
			return;
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
