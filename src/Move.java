
public class Move
{
	public int r0;
	public int c0;
	public int rf;
	public int cf;
	
	Move(int rr0, int cc0, int rrf, int ccf)
	{
		r0 = rr0;
		c0 = cc0;
		rf = rrf;
		cf = ccf;
	}
	
	public String toString()
	{
		return "(" + r0 + ", " + c0 + ")-(" + rf + ", " + cf + ")";
	}
}
