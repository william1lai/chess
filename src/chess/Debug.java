package chess;

import java.io.PrintWriter;

public final class Debug {
	private static boolean m_isDebug = false;
	private static PrintWriter logWriter = null;
	
	public static void Initialize(String[] args)
	{
		for( String arg : args )
		{
			if ( arg.toUpperCase().equals("-DEBUG") )
			{
				m_isDebug = true;				
			}
		}
		
		if ( IsDebugging() )
		{
			try
			{
				logWriter = new PrintWriter("ChessDebug.log", "UTF-8");
				Log("Debug Mode is enabled.");
			}
			catch( Exception ex )
			{
				System.out.println( ex.toString() );
				assert( false );
			}
			assert( logWriter != null );
		}
	}
	
	public static boolean IsDebugging()
	{
		return m_isDebug;
	}
	
	public static void Log( String message )
	{
		if (IsDebugging())
		{
			assert( logWriter != null );
			logWriter.println( message );
			logWriter.flush();
		}
		System.out.println( message );
	}
}
