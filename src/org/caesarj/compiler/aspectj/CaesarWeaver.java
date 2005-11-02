/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CaesarWeaver.java,v 1.9 2005-11-02 12:49:57 thiago Exp $
 */

package org.caesarj.compiler.aspectj;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.aspectj.bridge.AbortException;
import org.aspectj.util.FileUtil;
import org.aspectj.weaver.IClassFileProvider;
import org.aspectj.weaver.IWeaveRequestor;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.util.Message;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * @author Karl Klose
 * Encapsulates the AspectJ-BCelWeaver functionality.
 * A weaver instance collects the classes to be woven an weaves them
 * on command.
 */
public class CaesarWeaver {
    
    private String destination;
    		
// Attributes
	// list of UnwovenClassFile objects to weave	
	private List<UnwovenClassFile> unwovenClasses;
// Construction
	public CaesarWeaver(String destination)
	{
		unwovenClasses  = new ArrayList<UnwovenClassFile>();
		this.destination = destination;
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
	public void	performWeaving(CaesarBcelWorld world) throws IOException, PositionedError, UnpositionedError
	{
		try{
		    /*
			// Create an array containing the unwoven files...
			UnwovenClassFile unwovenClassFiles[] = 
				(UnwovenClassFile[])unwovenClasses.toArray(new UnwovenClassFile[0]);
			BcelWeaver	weaver = new BcelWeaver(world.getWorld());
			// ... and feed it into the waever ...
			for (int i = 0; i < unwovenClassFiles.length; i++)
				weaver.addClassFile(unwovenClassFiles[i]);
			// ... to do the weaving!
			weaver.weave(); */

		   
		    // Changed for version 1.2.1 of aspectj weaver
			BcelWeaver	weaver = new BcelWeaver(world.getWorld());
			
			for (int i = 0; i < unwovenClasses.size(); i++)
				weaver.addClassFile((UnwovenClassFile) unwovenClasses.get(i));
			weaver.prepareForWeave();
			IClassFileProvider provider = new IClassFileProvider() {

				public Iterator getClassFileIterator() {
					return unwovenClasses.iterator();
				}

				public IWeaveRequestor getRequestor() {
					return new IWeaveRequestor() {
						public void acceptResult(UnwovenClassFile result) {

						    String className = result.getClassName().replace('.', '/');
							 try {
								BufferedOutputStream os = FileUtil.makeOutputStream(new File(destination + "/" + className + ".class"));
								os.write(result.getBytes());
								os.close();
							} catch(IOException ex) {
							    ex.printStackTrace();
							}
							
						}
						public void processingReweavableState() {
						}
						public void addingTypeMungers() {
						}
						public void weavingAspects() {
						}
						public void weavingClasses() {
						}
						public void weaveCompleted() {
						}
					};
				}
			};
			weaver.weave(provider);
			
		}
		catch( AbortException e )
		{
			// Create a wrapper for the catched exception
			if (e.getIMessage() != null && e.getIMessage().getSourceLocation() != null) {
				throw new PositionedError(
								new TokenReference(
									e.getIMessage().getSourceLocation().getSourceFile().getName(),
									e.getIMessage().getSourceLocation().getLine()),
								new Message(CaesarMessages.WEAVER_ERROR, e.getMessage()));
			}
			else {
				throw new UnpositionedError(
						new Message(CaesarMessages.WEAVER_ERROR, e.getMessage()));
			}
		}
	}
}
