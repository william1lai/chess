import java.util.*;
import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;

public class StandardChessGameGraphics
{
	public static final int HEIGHT = 480;
	public static final int WIDTH = 640;
	private int m_boardOffsetX, m_boardOffsetY, m_blockSize;
	private Map<String, BufferedImage> m_gPieces;
	
	public StandardChessGameGraphics()
	{
		m_boardOffsetY = HEIGHT/8;
		m_boardOffsetX = HEIGHT/8;
		m_blockSize = HEIGHT*3/4 / Definitions.NUMROWS;
		
		m_gPieces = new HashMap<String, BufferedImage>();
		try {
			for (int i = 0; i < Definitions.NUMPIECES; i++) {
				m_gPieces.put(Definitions.PIECENAMES[i], ImageIO.read(getClass().getResourceAsStream("Images/piece" + Definitions.PIECENAMES[i] + ".png")));
			}
		}
		catch (Exception ex) {
			System.out.println("Error loading images!");
		}
	}
	
	public void drawBoard(Graphics g, Board b)
	{
		g.setColor(Color.BLACK);
		for (int r = 0, y = m_boardOffsetY; r < Definitions.NUMROWS; r++, y += m_blockSize) {
			for (int c = 0, x = m_boardOffsetX; c < Definitions.NUMCOLS; c++, x += m_blockSize) {
				if (b.getPiece(r, c) != null) {
					g.drawImage(m_gPieces.get(b.getPiece(r, c).toString()), x, y, m_blockSize, m_blockSize, null);
				}
			}
		}
		for (int line = 0, y = m_boardOffsetY; line <= Definitions.NUMROWS; line++, y += m_blockSize) {
			g.drawLine(m_boardOffsetX, y, m_boardOffsetX + Definitions.NUMCOLS*m_blockSize, y);
		}
		for (int line = 0, x = m_boardOffsetX; line <= Definitions.NUMCOLS; line++, x += m_blockSize) {
			g.drawLine(x, m_boardOffsetY, x, m_boardOffsetY + Definitions.NUMROWS*m_blockSize);
		}
	}
	
	public void drawSelected(Graphics g, Piece p)
	{
		if (p == null) return;
		int x = m_boardOffsetX + p.col()*m_blockSize;
		int y = m_boardOffsetY + p.row()*m_blockSize;
		g.setColor(Color.RED);
		g.drawLine(x, y, x, y + m_blockSize);
		g.drawLine(x, y, x + m_blockSize, y);
		g.drawLine(x, y + m_blockSize, x + m_blockSize, y + m_blockSize);
		g.drawLine(x + m_blockSize, y, x + m_blockSize, y + m_blockSize);
	}
	
	public int getRow(int y)
	{
		int relativeY = y - m_boardOffsetY;
		if (relativeY < 0 || relativeY >= Definitions.NUMROWS*m_blockSize) {
			return -1;
		}
		return relativeY / m_blockSize;
	}
	
	public int getCol(int x)
	{
		int relativeX = x - m_boardOffsetX;
		if (relativeX < 0 || relativeX >= Definitions.NUMCOLS*m_blockSize) {
			return -1;
		}
		return relativeX / m_blockSize;
	}
}