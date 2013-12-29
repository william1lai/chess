package chess;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

interface EasyButtonAction
{
	public void on_press();
}

public class EasyButton
{
	private int m_posX, m_posY;
	private int m_sizeW, m_sizeH;
	private BufferedImage m_img;
	private EasyButtonAction m_action;
	
	public EasyButton(String imgPath, int x, int y, int w, int h, EasyButtonAction action)
	{
		m_img = null;
		try {
			m_img = ImageIO.read(getClass().getResourceAsStream(imgPath));
		}
		catch (Exception ex) {
			System.out.println("Warning: " + imgPath + " not found");
		}
		m_posX = x;
		m_posY = y;
		m_sizeW = w;
		m_sizeH = h;
		m_action = action;
	}
	
	public void press()
	{
		m_action.on_press();
	}
	
	public BufferedImage getImg()
	{
		return m_img;
	}
	
	public int getX()
	{
		return m_posX;
	}
	
	public int getY()
	{
		return m_posY;
	}
	
	public int getW()
	{
		return m_sizeW;
	}
	
	public int getH()
	{
		return m_sizeH;
	}
}
