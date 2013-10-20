package chess;
import java.util.*;
import java.awt.*;
import javax.imageio.*;


import java.awt.image.*;

public class StandardChessGameGraphics
{
	private static int m_boardOffsetX, m_boardOffsetY, m_blockSize;
	private Map<String, BufferedImage> m_gPieces;
	private BufferedImage m_gMovable, m_gSelected;
	private BufferedImage[] m_gBlocks = new BufferedImage[2];
	
	public StandardChessGameGraphics()
	{
		m_boardOffsetY = Definitions.HEIGHT/8;
		m_boardOffsetX = Definitions.HEIGHT/8;
		m_blockSize = Definitions.HEIGHT*3/4 / Definitions.NUMROWS;
		
		m_gPieces = new HashMap<String, BufferedImage>();
		try {
			for (int i = 0; i < Definitions.NUMPIECES; i++) {
				m_gPieces.put(Definitions.PIECENAMES[i], ImageIO.read(getClass().getResourceAsStream("/Images/piece" + Definitions.PIECENAMES[i] + ".png")));
			}
			m_gBlocks[0] = ImageIO.read(getClass().getResourceAsStream("/Images/blockW.png"));
			m_gBlocks[1] = ImageIO.read(getClass().getResourceAsStream("/Images/blockB.png"));
			m_gMovable = ImageIO.read(getClass().getResourceAsStream("/Images/blockMovable.png"));
			m_gSelected = ImageIO.read(getClass().getResourceAsStream("/Images/blockSelected.png"));
		}
		catch (Exception ex) {
			System.out.println("Error loading images!");
		}
	}
	
	public void drawNames(Graphics g, Player p1, Player p2, Definitions.Color turn)
	{
		g.setColor(Color.BLACK);
		g.setFont(new Font("Lucida Bright", (turn == p1.getColor()? Font.BOLD : Font.PLAIN), 15));
		g.drawString(p1.getName(), m_boardOffsetX + m_blockSize * (Definitions.NUMCOLS+1) + 15, m_boardOffsetY + m_blockSize * Definitions.NUMROWS);
		g.setFont(new Font("Lucida Bright", (turn == p2.getColor()? Font.BOLD : Font.PLAIN), 15));
		g.drawString(p2.getName(), m_boardOffsetX + m_blockSize * (Definitions.NUMCOLS+1) + 15, m_boardOffsetY + 20);
		//int turnOffset = (turn == Definitions.Color.BLACK? 20 : 0);
		//g.setColor(new Color(0x006600));
		//g.fillOval(m_boardOffsetX + m_blockSize * (Definitions.NUMCOLS+1) + 5, m_boardOffsetY + 10 + turnOffset, 10, 10);
	}
	
	public void drawBlock(Graphics g, int row, int col)
	{
		g.drawImage(m_gBlocks[(row+col)%2], col * m_blockSize + m_boardOffsetX, row * m_blockSize + m_boardOffsetX, m_blockSize, m_blockSize, null);
	}
	
	public void drawPiece(Graphics g, Piece p, int x, int y)
	{
		g.drawImage(m_gPieces.get(p.toString()), x, y, m_blockSize, m_blockSize, null);
	}
	
	public void drawMarkers(Graphics g)
	{
		g.setColor(Color.BLACK);
		for (int row = 0, y = m_boardOffsetY + m_blockSize/2; row < Definitions.NUMROWS; row++, y += m_blockSize) {
			g.drawString(Definitions.RMARKERS[row], m_boardOffsetX - m_blockSize/2, y);
			g.drawString(Definitions.RMARKERS[row], m_boardOffsetX + m_blockSize * Definitions.NUMCOLS + m_blockSize/3, y);
		}
		for (int col = 0, x = m_boardOffsetX + m_blockSize/2; col < Definitions.NUMCOLS; col++, x += m_blockSize) {
			g.drawString(Definitions.CMARKERS[col], x, m_boardOffsetY - m_blockSize/3);
			g.drawString(Definitions.CMARKERS[col], x, m_boardOffsetY + m_blockSize * Definitions.NUMROWS + m_blockSize/2);
		}
	}
	
	public void drawBorders(Graphics g)
	{
		g.setColor(Color.BLACK);
		for (int line = 0, y = m_boardOffsetY; line <= Definitions.NUMROWS; line++, y += m_blockSize) {
			g.drawLine(m_boardOffsetX, y, m_boardOffsetX + Definitions.NUMCOLS*m_blockSize, y);
		}
		for (int line = 0, x = m_boardOffsetX; line <= Definitions.NUMCOLS; line++, x += m_blockSize) {
			g.drawLine(x, m_boardOffsetY, x, m_boardOffsetY + Definitions.NUMROWS*m_blockSize);
		}
	}
	
	public void drawPieces(Graphics g, Board b)
	{
		for (int r = 0, y = m_boardOffsetY; r < Definitions.NUMROWS; r++, y += m_blockSize) {
			for (int c = 0, x = m_boardOffsetX; c < Definitions.NUMCOLS; c++, x += m_blockSize) {
				if (b.getPiece(r, c) != null) {
					drawPiece(g, b.getPiece(r, c), x, y);
				}
			}
		}
	}
	
	public void drawBoard(Graphics g)
	{
		for (int r = 0; r < Definitions.NUMROWS; r++) {
			for (int c = 0; c < Definitions.NUMCOLS; c++) {
				drawBlock(g, r, c);
			}
		}
	}
	
	public void drawSelected(Graphics g, Piece p)
	{
		if (p == null) return;
		int x = getX(p.col());
		int y = getY(p.row());
		g.drawImage(m_gSelected, x, y, m_blockSize, m_blockSize, null);
	}
	
	public void drawMovable(Graphics g, ArrayList<Move> list)
	{
		if (list == null) return;
		for (Move m : list) {
			int x = getX(m.cf);
			int y = getY(m.rf);
			g.drawImage(m_gMovable, x, y, m_blockSize, m_blockSize, null);
		}
	}
	
	public static int getRow(int y)
	{
		int relativeY = y - m_boardOffsetY;
		if (relativeY < 0 || relativeY >= Definitions.NUMROWS*m_blockSize) {
			return -1;
		}
		return relativeY / m_blockSize;
	}
	
	public static int getCol(int x)
	{
		int relativeX = x - m_boardOffsetX;
		if (relativeX < 0 || relativeX >= Definitions.NUMCOLS*m_blockSize) {
			return -1;
		}
		return relativeX / m_blockSize;
	}
	
	public static int getY(int row)
	{
		if (row < 0 || row >= Definitions.NUMROWS) return -1;
		return m_boardOffsetY + row*m_blockSize;
	}
	
	public static int getX(int col)
	{
		if (col < 0 || col >= Definitions.NUMCOLS) return -1;
		return m_boardOffsetX + col*m_blockSize;
	}
}