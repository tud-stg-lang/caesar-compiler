/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CaesarWeaver.java,v 1.5 2005-01-24 16:52:58 aracic Exp $
 */

package org.caesarj.compiler.aspectj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.bridge.AbortException;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.caesarj.compiler.constants.CaesarMessages;
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
			BcelWeaver	weaver = new BcelWeaver(world.getWorld());
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
