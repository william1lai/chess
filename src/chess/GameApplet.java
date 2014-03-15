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
	private GameGUI m_gui;
	private boolean m_cancel;

	public void init()
	{
		m_cancel = !chooseGame();
		if (m_cancel)
			return;
		
		m_game.init(m_graphics, m_gui);
		m_graphics.init(m_game);
		m_gui.init(m_game);
		
		add(m_graphics);
		addMouseListener(m_gui);
		addFocusListener(m_gui);
		addKeyListener(m_gui);
		
		m_thread = new Thread(this);
		m_thread.start();
	}

	public boolean chooseGame()
	{
		String[] param = { "Standard Chess", "Loser's Chess", "Alice Chess" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Type?", "Choose your game", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "Standard Chess")
		{
			m_game = new StandardGame(this);
			m_graphics = new StandardGameGraphics(this);
			m_gui = new StandardGameGUI(this);
			return true;
		}
		if (input == "Loser's Chess")
		{
			m_game = new LosersGame(this);
			m_graphics = new LosersGameGraphics(this);
			m_gui = new LosersGameGUI(this);
			return true;
		}
		if (input == "Alice Chess")
		{
			m_game = new AliceGame(this);
			m_graphics = new AliceGameGraphics(this);
			m_gui = new AliceGameGUI(this);
			return true;
		}
		return false;
	}

	public boolean cancelled()
	{
		return m_cancel;
	}

	public void run()
	{		
		m_game.run();
	}

	//Prevents flickering when repainting
	public void update(Graphics g)
	{
		paint(g);
	}

	public void stop()
	{
		if (m_thread != null && m_thread.isAlive()) 
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
