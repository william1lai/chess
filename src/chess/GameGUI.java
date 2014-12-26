package chess;

import java.awt.event.*;

abstract public class GameGUI implements MouseListener, FocusListener, KeyListener
{
	protected GameApplet m_applet;
	
	abstract public void init(Game game);
}
