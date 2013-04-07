import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

public class StandardChessGame extends Game
{
	private Thread m_process;
	private Board m_game_board;
	private StandardChessGameGraphics m_graphics;
	private Piece m_selected;

	public StandardChessGame()
	{

	}

	public void init()
	{
		m_process = new Thread(this);
		m_game_board = new Board();
		m_graphics = new StandardChessGameGraphics();
		m_selected = null;

		addMouseListener(this);

		//Placing standard pieces
		//William y u do dis 2 me
		m_game_board.placePiece(new Rook(0, 0, Definitions.Color.BLACK), 0, 0);
		m_game_board.placePiece(new Knight(0, 1, Definitions.Color.BLACK), 0, 1);
		m_game_board.placePiece(new Bishop(0, 2, Definitions.Color.BLACK), 0, 2);
		m_game_board.placePiece(new Queen(0, 3, Definitions.Color.BLACK), 0, 3);
		m_game_board.placePiece(new King(0, 4, Definitions.Color.BLACK), 0, 4);
		m_game_board.placePiece(new Bishop(0, 5, Definitions.Color.BLACK), 0, 5);
		m_game_board.placePiece(new Knight(0, 6, Definitions.Color.BLACK), 0, 6);
		m_game_board.placePiece(new Rook(0, 7, Definitions.Color.BLACK), 0, 7);
		for (int c = 0; c < 8; c++) {
			m_game_board.placePiece(new Pawn(1, c, Definitions.Color.BLACK), 1, c); 
		}
		m_game_board.placePiece(new Rook(7, 0, Definitions.Color.WHITE), 7, 0);
		m_game_board.placePiece(new Knight(7, 1, Definitions.Color.WHITE), 7, 1);
		m_game_board.placePiece(new Bishop(7, 2, Definitions.Color.WHITE), 7, 2);
		m_game_board.placePiece(new Queen(7, 3, Definitions.Color.WHITE), 7, 3);
		m_game_board.placePiece(new King(7, 4, Definitions.Color.WHITE), 7, 4);
		m_game_board.placePiece(new Bishop(7, 5, Definitions.Color.WHITE), 7, 5);
		m_game_board.placePiece(new Knight(7, 6, Definitions.Color.WHITE), 7, 6);
		m_game_board.placePiece(new Rook(7, 7, Definitions.Color.WHITE), 7, 7);
		for (int c = 0; c < 8; c++) {
			m_game_board.placePiece(new Pawn(6, c, Definitions.Color.WHITE), 6, c); 
		}
		setTurn(Definitions.Color.BLACK);
	}

	public void run()
	{
	}

	public void paint(Graphics g)
	{
		//Painting with a backbuffer reduces flickering
		Image backbuffer = createImage(g.getClipBounds().width, g.getClipBounds().height);
		Graphics backg = backbuffer.getGraphics();

		m_graphics.drawBoard(backg, m_game_board);
		m_graphics.drawSelected(backg, m_selected);

		g.drawImage(backbuffer, 0, 0, this);
	}

	public boolean isLegalMove(Move m)
	{
		//generate moves and use board to determine legality (is there a piece in the way? etc.)

		//TODO: Check for check somehow

		Piece p = m_game_board.getPiece(m.r0, m.c0);

		if ((p == null) || (p.color() != whoseTurn()))
		{
			return false; //source square has no piece, or selected piece is opponent's piece
		}

		Piece destination = m_game_board.getPiece(m.rf, m.cf);
		boolean occupiedDest = (destination != null);
		ArrayList<Move> moves = p.moves();

		if (occupiedDest && (destination.color() == whoseTurn())) //case of trying to move to source square is handled here
		{
			return false; //can't move to square occupied by same color piece
		}

		//moves.contains(m) doesn't work for me. Below is a super nasty temporary solution
			//W - I think I fixed it by implementing equals() in Move class, so I'll comment out the workaround for now
		/*
		boolean flag = false;
		for (int i = 0; i < moves.size() && !flag; i++)
			flag = moves.get(i).r0 == m.r0 && moves.get(i).rf == m.rf && moves.get(i).c0 == m.c0 && moves.get(i).cf == m.cf;
		*/
		if (moves.contains(m))
		{
			Board tempBoard = new Board(m_game_board); //clone board
			tempBoard.move(m);
			if (inCheck(whoseTurn(), tempBoard))
			{
				return false; //illegal move because you will be in check after move
			}

			if ((p instanceof Knight) || (p instanceof King))
			{
				return true; //all possible moves are automatically legal because we already checked earlier
							 //that the destination square does not contain a piece of same color
			}
			if (p instanceof Pawn) //must split into cases
			{
				if (m.cf != m.c0) //changed columns; must be capture
				{
					return (m_game_board.getPiece(m.rf, m.cf) != null); //can't be our own piece because of earlier check
				}
				else
				{
					if (m.rf - m.r0 == 2) //push two squares
					{
						return (m_game_board.getPiece((m.rf + m.r0) / 2, m.cf) == null)
								&& (m_game_board.getPiece(m.rf, m.cf) == null); //both squares empty
					}
					else //push one square
					{
						return (m_game_board.getPiece(m.rf, m.cf) == null); //square empty
					}
				}
			}


			//all other pieces have the similar property of not being able to get to
			//target location if there is a piece in the way

			//TODO: The following code seems a bit inefficient. Maybe find way to reduce length
			// 	and/or put it in another function for readability

			int dc = m.cf - m.c0;
			int dr = m.rf - m.r0; //remember rows are counted from the top

			int cinc; //1, 0, or -1, depending on which direction the piece is headed
			int rinc; //1, 0, or -1

			if (dc < 0)
			{
				cinc = -1;
			}
			else if (dc > 0)
			{
				cinc = 1;
			}
			else
			{
				cinc = 0;
			}

			if (dr < 0)
			{
				rinc = -1;
			}
			else if (dr > 0)
			{
				rinc = 1;
			}
			else
			{
				rinc = 0;
			}

			int r = m.r0 + rinc;
			int c = m.c0 + cinc;
			while ((r != m.rf) && (c != m.cf))
			{
				if (m_game_board.getPiece(r, c) != null)
				{
					return false; //there is a piece in our way
				}
				r = r + rinc;
				c = c + cinc;
			}
			return true; //no pieces in way
		}

		return false; //move is not in our move list
	}

	//moving to Game class from Board class, since the Board doesn't necessarily know rules of game
	//added Board argument since you might want to check temporary boards too
	public boolean inCheck(Definitions.Color color, Board b)
	{
		//this will check to see if the king of 'color' is threatened or not
		int kingR = -1; //default values to satisfy compiler
		int kingC = -1;
		//might want to have a king location variable in each board
		//or we can use an arraylist to hold all pieces on board, maybe separated by color?
		//efficiency considerations?

		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece temp = b.getPiece(r, c);
				if ((temp instanceof King) && (temp.color() == color))
				{
					kingR = r;
					kingC = c;
					break; //is there a way to jump outside both loops without using goto?
				}
			}
		}

		for (int r = 0; r < Definitions.NUMROWS; r++)
		{
			for (int c = 0; c < Definitions.NUMCOLS; c++)
			{
				Piece temp = b.getPiece(r, c);
				Move m = new Move(r, c, kingR, kingC);
				if ((temp.color() != color) && (temp.threats().contains(m)))
				{
					return true;
				}
			}
		}
		return false; //no pieces can capture king
	}

	public void mousePressed(MouseEvent e)
	{
		int row = m_graphics.getRow(e.getY());
		int col = m_graphics.getCol(e.getX());
		//Left-click to select
		if (e.getButton() == MouseEvent.BUTTON1) select(row, col);
		//Right-click to move selected
		else if (e.getButton() == MouseEvent.BUTTON3) moveSelected(row, col);
		repaint();
	}

	private void select(int row, int col)
	{
		m_selected = null;
		if (row >= 0 && col >= 0) {
			Piece p = m_game_board.getPiece(row, col);
			if (p != null && p.color() == whoseTurn()) m_selected = p;
		}
	}

	private void moveSelected(int row, int col)
	{
		if (m_selected == null) return;
		Move newMove = new Move(m_selected.row(), m_selected.col(), row, col);
		if (!isLegalMove(newMove)) return;
		m_game_board.move(newMove);
	}

	//Useless for now
	public void mouseReleased(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	//Prevents flickering when repainting
	public void update(Graphics g)
	{
		paint(g);
	}

	//Upon exiting applet, stop all processes
	public void stop()
	{
		if (m_process != null) m_process.interrupt();
	}
}
