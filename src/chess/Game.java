package chess;

import java.applet.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public abstract class Game extends Applet implements Runnable, MouseListener
{
	protected Player p1;
	protected Player p2;	
}
