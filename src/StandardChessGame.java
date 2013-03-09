import java.util.ArrayList;


public class StandardChessGame extends Game
{
	private Board m_board;

	public StandardChessGame()
	{

	}

	public void init()
	{

	}

	public boolean isLegalMove(Move m)
	{
		//generate moves and use board to determine legality (is there a piece in the way? etc.)
		
		//TODO: Check for check somehow
		
		Piece p = m_board.getPiece(m.r0, m.c0);

		if ((p == null) || (p.color() != whoseTurn()))
		{
			return false; //source square has no piece, or selected piece is opponent's piece
		}

		Piece destination = m_board.getPiece(m.rf, m.cf);
		boolean occupiedDest = (destination != null);

		ArrayList<Move> moves = p.moves();

		if (occupiedDest && (destination.color() == whoseTurn())) //case of trying to move to source square is handled here
		{
			return false; //can't move to square occupied by same color piece
		}

		if (moves.contains(m))
		{	
			if (p instanceof Knight)
			{
				return true; //can jump to any of its valid squares no matter what
			}
			if (p instanceof Pawn) //must split into cases
			{
				if (m.cf != m.c0) //changed columns; must be capture
				{
					return (m_board.getPiece(m.rf, m.cf) != null); //can't be our own piece because of earlier check
				}
				else
				{
					if (m.rf - m.r0 == 2) //push two squares
					{
						return (m_board.getPiece((m.rf + m.r0) / 2, m.cf) == null)
								&& (m_board.getPiece(m.rf, m.cf) == null); //both squares empty
					}
					else //push one square
					{
						return (m_board.getPiece(m.rf, m.cf) == null); //square empty
					}
				}
			}
			if (p instanceof King) //must account for check
			{
				//TODO: Need to implement
				return true; //stub; will fix later
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
				if (m_board.getPiece(r, c) != null)
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
	public boolean inCheck(Definitions.Color color)
	{
		//this will check to see if the king of 'color' is threatened or not

		return false; //stub
	}
}
