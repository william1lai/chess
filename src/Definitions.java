public class Definitions
{
	public static final int HEIGHT = 480;
	public static final int WIDTH = 640;
	public static enum Color { WHITE, BLACK };
	public static final int NUMROWS = 8;
	public static final int NUMCOLS = 8;
	public static final int NUMPIECES = 12;
	public static final String PIECENAMES[] = {"BK","BQ","BN","BB","BR","BP","WK","WQ","WN","WB","WR","WP"};
	public static final String RMARKERS[] = {"8","7","6","5","4","3","2","1"};
	public static final String CMARKERS[] = {"h","g","f","e","d","c","b","a"};
	
	public static Color flip(Color c)
	{
		if (c == Color.WHITE)
		{
			return Color.BLACK;
		}
		return Color.WHITE;
	}
}

