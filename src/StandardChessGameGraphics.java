import java.util.*;
import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;

public class StandardChessGameGraphics
{
	private final int HEIGHT = 480;
	private final int WIDTH = 640;
	private int m_boardOffsetX, m_boardOffsetY, m_blockSize;
	private ArrayList<BufferedImage> m_gPieces;
	private Map<String, Integer> m_pieceMap;
	
	public StandardChessGameGraphics()
	{
		m_boardOffsetY = HEIGHT/8;
		m_boardOffsetX = HEIGHT/8;
		m_blockSize = HEIGHT*3/4 / Definitions.NUMROWS;
		
		m_gPieces = new ArrayList<BufferedImage>();
		m_pieceMap = new HashMap<String, Integer>();
		try {
			for (int i = 0; i < Definitions.NUMPIECES; i++) {
				m_gPieces.add(ImageIO.read(getClass().getResourceAsStream("Images/piece" + Definitions.PIECENAMES[i] + ".png")));
				m_pieceMap.put(Definitions.PIECENAMES[i], i);
			}
		}
		catch (Exception ex) {
			System.out.println("Error loading images!");
		}
	}
	
	public void drawBoard(Graphics g, Board b)
	{
		for (int r = 0, y = m_boardOffsetY; r < Definitions.NUMROWS; r++, y += m_blockSize) {
			for (int c = 0, x = m_boardOffsetX; c < Definitions.NUMCOLS; c++, x += m_blockSize) {
				if (b.getPiece(r, c) != null) {
					g.drawImage(m_gPieces.get(m_pieceMap.get(b.getPiece(r, c).getName())), x, y, m_blockSize, m_blockSize, null);
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
}