package chess;

public class Move
{
	//a8 is row 0 column 0
	public int r0;
	public int c0;
	public int rf;
	public int cf;
	public char promo; //what piece to promote to, 0 if not applicable

	public Move(int rr0, int cc0, int rrf, int ccf)
	{
		r0 = rr0;
		c0 = cc0;
		rf = rrf;
		cf = ccf;
		promo = 0;
	}
	
	public Move (int rr0, int cc0, int rrf, int ccf, char ppromo)
	{
		r0 = rr0;
		c0 = cc0;
		rf = rrf;
		cf = ccf;
		promo = ppromo;
	}
	
	public Move(Move other)
	{
		r0 = other.r0;
		c0 = other.c0;
		rf = other.rf;
		cf = other.cf;
		promo = other.promo;
	}

	public String toString()
	{
		String ret = "" + (char)(c0 + 'a') + "" + (8 - r0) + "-" + (char)(cf + 'a') + "" + (8 - rf);
		if (promo != 0)
		{
			ret = ret + "=" + promo;
		}
		return ret;
	}

	public boolean equals(Object obj) //we must have this so that the contains() method works
	{
		if (obj instanceof Move)
		{
			Move other = (Move)obj;
			return ((r0 == other.r0) && (rf == other.rf) && (c0 == other.c0) && (cf == other.cf) && (promo == other.promo));  
		}
		return false;
	}
}
