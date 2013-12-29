package chess;

import java.applet.Applet;
import java.awt.event.MouseListener;

@SuppressWarnings("serial")
public abstract class Game extends Applet implements Runnable, MouseListener
{
	protected Player p1;
	protected Player p2;	
}
