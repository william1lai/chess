package chess;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main 
{
	public static void main(String[] args)
	{		
		String[] param = { "Standard Chess", "Loser's Chess" };
		String input = (String) JOptionPane.showInputDialog(null, "Game Type?", "Choose your game", JOptionPane.QUESTION_MESSAGE, null, param, param[0]);

		if (input == "Standard Chess")
		{
			StandardChessGame scg = new StandardChessGame();
			scg.init();
			scg.start();
			if (scg.initialized())
			{
				JFrame jf = new JFrame();
				Container c = jf.getContentPane();
				c.add(scg);
				jf.setBounds(10, 10, 660, 520);
				jf.setTitle("Chess Applet");
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setVisible(true);
			}
		}
		else if (input == "Loser's Chess")
		{
			LosersChessGame lcg = new LosersChessGame();
			lcg.init();
			lcg.start();
			if (lcg.initialized())
			{
				JFrame jf = new JFrame();
				Container c = jf.getContentPane();
				c.add(lcg);
				jf.setBounds(10, 10, 660, 520);
				jf.setTitle("Chess Applet");
				jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				jf.setVisible(true);
			}
		}
	}
}
