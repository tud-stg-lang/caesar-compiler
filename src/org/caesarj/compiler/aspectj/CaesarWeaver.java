/*
 * Created on 17.12.2003
 */
package org.caesarj.compiler.aspectj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.bridge.AbortException;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.caesarj.compiler.CaesarMessages;
import org.caesarj.util.Message;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * @author Karl Klose
 * Encapsulates the AspectJ-BCelWeaver functionality.
 * A weaver instance collects the classes to be woven an weaves them
 * on command.
 */
public class CaesarWeaver {
	/*
	 * This class wrapps the AbortException. It is generated and thrown by 
	 * performWeaving in the case that an AbortException is theown by the
	 * BcelWeaver method.
	 * @author Karl Klose
	 */
	public class WeavingException extends RuntimeException
	{
		private AbortException	e;
		public WeavingException( AbortException e )
		{
			this.e = e;
		}
		
		public PositionedError	getError()
		{
			return 	new PositionedError(
							new TokenReference(
								e
									.getIMessage()
									.getISourceLocation()
									.getSourceFile()
									.getName(),
								e.getIMessage().getISourceLocation().getLine()),
							new Message(CaesarMessages.WEAVER_ERROR, e.getMessage()));	
		}
	}
	
// Attributes
	// list of UnwovenClassFile objects to weave	
	private List unwovenClasses;
// Construction
	public CaesarWeaver()
	{
		unwovenClasses  = new ArrayList();
	}
// Functionality
	public int fileCount()
	{
		return unwovenClasses.size();
	}
	
	public String getFileName( int n )
	{
		return ((UnwovenClassFile)unwovenClasses.get(n)).getFilename();
	}
	
	public void addUnwovenClassFile(String filename, byte[] bytes)
	{
		unwovenClasses.add(new UnwovenClassFile(filename,bytes));
	}
	public void	performWeaving(CaesarBcelWorld world) throws IOException
	{
		try{
			// Create an array containing the unwoven files...
			UnwovenClassFile unwovenClassFiles[] = 
				(UnwovenClassFile[])unwovenClasses.toArray(new UnwovenClassFile[0]);
			BcelWeaver	weaver = new BcelWeaver(world);
			// ... and feed it into the waever ...
			for (int i = 0; i < unwovenClassFiles.length; i++)
				weaver.addClassFile(unwovenClassFiles[i]);
			// ... to do the weaving!
			weaver.weave();
		}
		catch( AbortException e )
		{
			// Create a wrapper for the catched exception
			throw new CaesarWeaver.WeavingException(e);
		}
	}
}
