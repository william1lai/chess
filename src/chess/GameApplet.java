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
			if (m_game instanceof StandardGame)
			{
				((StandardGameGUI)m_graphics.getGUI()).addButton(b);
			}
			else if (m_game instanceof LosersGame)
			{
				((LosersGameGUI)m_graphics.getGUI()).addButton(b);	
			}
			else if (m_game instanceof AliceGame)
			{
				((AliceGameGUI)m_graphics.getGUI()).addButton(b);
			}
		}
		catch (Exception ex) {}
	}

	public void chooseGame()
	{
		String[] param = { "Standard Chess", "Loser's Chess", "Alice Chess" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Type?", "Choose your game", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "Standard Chess")
		{
			m_game = new StandardGame(this);
			m_graphics = new StandardGameGraphics(this);
			m_cancel = false;
		}
		else if (input == "Loser's Chess")
		{
			m_game = new LosersGame(this);
			m_graphics = new LosersGameGraphics(this);
			m_cancel = false;
		}
		else if (input == "Alice Chess")
		{
			m_game = new AliceGame(this);
			m_graphics = new AliceGameGraphics(this);
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
		if (getGame() instanceof StandardGame)
		{
			((StandardGame)getGame()).run();
		}
		else if (getGame() instanceof LosersGame)
		{
			((LosersGame)getGame()).run();
		}
		else if (getGame() instanceof AliceGame)
		{
			((AliceGame)getGame()).run();
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

		if (m_game instanceof StandardGame)
		{
			StandardGame scg = (StandardGame)m_game;
			if (scg.canUndo())
			{
				scg.getBoard().decrementTurncount();

				m_game.movesHistory.pop();
				String returnMove = m_game.movesHistory.pop();

				scg.getBoard().FENtoPosition(returnMove);
			}			
		}
		else if (m_game instanceof LosersGame)
		{
			LosersGame lcg = (LosersGame)m_game;
			if (lcg.canUndo())
			{
				lcg.getBoard().decrementTurncount();

				m_game.movesHistory.pop();
				String returnMove = m_game.movesHistory.pop();

				lcg.getBoard().FENtoPosition(returnMove);
			}
		}
		else if (m_game instanceof AliceGame) //TODO
		{
			int board = 0; //TODO
			AliceGame acg = (AliceGame)m_game;
			if (acg.canUndo())
			{
				acg.getBoard().decrementTurncount();
				
				m_game.movesHistory.pop();
				String returnMove = m_game.movesHistory.pop();
				
				acg.getBoard().FENtoPosition(returnMove, board);
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
