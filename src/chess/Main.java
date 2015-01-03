package chess;

import java.awt.Container;
import javax.swing.JFrame;

public class Main
{
	public static void main(String[] args)
	{
		Debug.Initialize(args);		
		
		GameApplet applet = new GameApplet();
		applet.init();
		applet.start();
		if (!applet.cancelled())
		{
			JFrame jf = new JFrame();
			Container c = jf.getContentPane();
			c.add(applet);
			jf.setBounds(10, 10, 660, 520);
			jf.setTitle("Chess Applet" + (Debug.IsDebugging() ? " (DEBUG Mode)" : ""));
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.setVisible(true);
		}
	}
}
