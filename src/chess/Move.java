package chess;

public class Move
{
	public int r0;
	public int c0;
	public int rf;
	public int cf;

	public Move(int rr0, int cc0, int rrf, int ccf)
	{
		r0 = rr0;
		c0 = cc0;
		rf = rrf;
		cf = ccf;
	}
	
	public Move(Move other)
	{
		r0 = other.r0;
		c0 = other.c0;
		rf = other.rf;
		cf = other.cf;
	}

	public String toString()
	{
		return "" + (char)(c0 + 'a') + "" + (8 - r0) + "-" + (char)(cf + 'a') + "" + (8 - rf);
	}

	public boolean equals(Object obj) //we must have this so that the contains() method works
	{
		if (obj instanceof Move)
		{
			Move other = (Move)obj;
			return ((r0 == other.r0) && (rf == other.rf) && (c0 == other.c0) && (cf == other.cf));  
		}
		return false;
	}
}
