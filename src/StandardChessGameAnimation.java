import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

//Note to Vincent: Setting fTick to 3 and getting rid of the Thread.Sleep part seems to make the animation look nice

public class StandardChessGameAnimation implements Runnable
{
	private class MoveInfo
	{
		public MoveInfo(Move m, Board b)
		{
			move = m;
			board = b;
		}
		public Move move;
		public Board board;
		public int fTick = 3;
	};
	
	private StandardChessGameGraphics m_graphics;
	private Thread m_animator;
	private Graphics m_gContext;
	private MoveInfo m_moveContext;

	
	public StandardChessGameAnimation(StandardChessGameGraphics graphics)
	{
		m_graphics = graphics;
	}
	
	public void animateMove(Graphics g, Move m, Board b)
	{
		m_gContext = g;
		m_moveContext = new MoveInfo(m, b);
		m_animator = new Thread(this);
		m_animator.start();
		try {
			m_animator.join();
		}
		catch (Exception ex) { //Gotta catch them all
			System.out.println(ex);
		}
	}

	public void run()
	{
		if (m_moveContext != null) {
			Piece traveler = m_moveContext.board.getPiece(m_moveContext.move.r0, m_moveContext.move.c0);
			double curY = m_graphics.getY(m_moveContext.move.r0);
			double curX = m_graphics.getX(m_moveContext.move.c0);
			double dY = ((double)m_graphics.getY(m_moveContext.move.rf) - curY) / m_moveContext.fTick;
			double dX = ((double)m_graphics.getX(m_moveContext.move.cf) - curX) / m_moveContext.fTick;
			for (int tick = 0; tick < m_moveContext.fTick; tick++) {
				curY += dY;
				curX += dX;
				Image backbuffer = new BufferedImage(Definitions.WIDTH, Definitions.HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics backg = backbuffer.getGraphics();
				m_graphics.drawBoard(backg, m_moveContext.board);
				m_graphics.drawBlock(backg, m_moveContext.move.r0, m_moveContext.move.c0);
				m_graphics.drawBorders(backg);
				m_graphics.drawPiece(backg, traveler, (int)curX, (int)curY);
				m_gContext.drawImage(backbuffer, 0, 0, null);
				/*
		 		try { Thread.sleep(0); }
		 		catch (InterruptedException ex) {}*/
			}
			m_moveContext = null;
		}
		m_gContext = null;
	}

}
