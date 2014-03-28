package chess;

import javax.swing.JFrame;

public class Main 
{
	public static void main(String[] args)
	{
		GameApplet applet = new GameApplet();
		applet.init();
		applet.start();
		if (!applet.cancelled())
		{
			JFrame jf = new JFrame("Chess Applet");
			jf.setContentPane(applet);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.pack();
			jf.setVisible(true);
		}
	}
}
