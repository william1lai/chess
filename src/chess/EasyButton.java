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
	private boolean m_pressed;
	private BufferedImage m_releasedImg, m_pressedImg;
	private EasyButtonAction m_action;
	
	public EasyButton(String name, int x, int y, int w, int h, EasyButtonAction action)
	{
		try {
			m_releasedImg = ImageIO.read(getClass().getResourceAsStream("/Images/" + name + "0.png"));
			m_pressedImg = ImageIO.read(getClass().getResourceAsStream("/Images/" + name + "1.png"));
		}
		catch (Exception ex) {
			System.out.println("Warning: " + name + " not found");
			m_releasedImg = null;
			m_pressedImg = null;
		}
		m_posX = x;
		m_posY = y;
		m_sizeW = w;
		m_sizeH = h;
		m_pressed = false;
		m_action = action;
	}
	
	public void release(boolean releasedOn)
	{
		if (m_pressed && releasedOn) m_action.on_press();
		m_pressed = false;
	}
	
	public void press()
	{
		m_pressed = true;
	}
	
	public boolean isPressed()
	{
		return m_pressed;
	}
	
	public BufferedImage getPressedImg()
	{
		return m_pressedImg;
	}
	
	public BufferedImage getReleasedImg()
	{
		return m_releasedImg;
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
