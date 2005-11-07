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
 * $Id: CaesarWeaver.java,v 1.11 2005-11-07 20:28:52 thiago Exp $
 */

package org.caesarj.compiler.aspectj;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.aspectj.bridge.AbortException;
import org.aspectj.util.FileUtil;
import org.aspectj.weaver.IClassFileProvider;
import org.aspectj.weaver.IWeaveRequestor;
import org.aspectj.weaver.WeaverMetrics;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
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
	
	public List<UnwovenClassFile> getUnwovenClasses() {
		return this.unwovenClasses;
	}
	
	public void	performWeaving(CaesarBcelWorld world) throws IOException, PositionedError, UnpositionedError
	{
		performWeaving(world, null);
	}
	
	public void	performWeaving(CaesarBcelWorld world, final IWeaveRequestor listener) throws IOException, PositionedError, UnpositionedError
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
			BcelWeaver weaver = new CaesarBcelWeaver(world.getWorld());
			
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

							if (result != null) {
							    String className = result.getClassName().replace('.', '/');
								 try {
									BufferedOutputStream os = FileUtil.makeOutputStream(new File(destination + "/" + className + ".class"));
									os.write(result.getBytes());
									os.close();
								} catch(IOException ex) {
								    ex.printStackTrace();
								}
							}
							if (listener != null) listener.acceptResult(result);
						}
						public void processingReweavableState() {
							if (listener != null) listener.processingReweavableState();
						}
						public void addingTypeMungers() {
							if (listener != null) listener.addingTypeMungers();
						}
						public void weavingAspects() {
							if (listener != null) listener.weavingAspects();
						}
						public void weavingClasses() {
							if (listener != null) listener.weavingClasses();
						}
						public void weaveCompleted() {
							if (listener != null) listener.weaveCompleted();
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
	
	protected class CaesarBcelWeaver extends BcelWeaver {
		
		BcelWorld world = null;
		
		public CaesarBcelWeaver(BcelWorld world) {
	        super(world);
	        this.world = world;
	    }
	        
	    public CaesarBcelWeaver() {
	    	this(new BcelWorld());
	    }
	    
	    public Collection weave(IClassFileProvider input) throws IOException {
	    	Collection wovenClassNames = new ArrayList();
	    	IWeaveRequestor requestor = input.getRequestor();

	    	requestor.processingReweavableState();
			prepareToProcessReweavableState();
			// clear all state from files we'll be reweaving
			for (Iterator i = input.getClassFileIterator(); i.hasNext(); ) {
			    UnwovenClassFile classFile = (UnwovenClassFile)i.next();
				String className = classFile.getClassName();
			    BcelObjectType classType = getClassType(className);			            
				processReweavableStateIfPresent(className, classType);
			}
									
			requestor.addingTypeMungers();
			//XXX this isn't quite the right place for this...
			for (Iterator i = input.getClassFileIterator(); i.hasNext(); ) {
			    UnwovenClassFile classFile = (UnwovenClassFile)i.next();
			    String className = classFile.getClassName();
			    addTypeMungers(className);
			}

			requestor.weavingAspects();
			// first weave into aspects
			for (Iterator i = input.getClassFileIterator(); i.hasNext(); ) {
			    UnwovenClassFile classFile = (UnwovenClassFile)i.next();
				String className = classFile.getClassName();
			    BcelObjectType classType = BcelWorld.getBcelObjectType(world.resolve(className));
			    if (classType.isAspect()) {
			        weaveAndNotify(classFile, classType,requestor);
			        wovenClassNames.add(className);
			    }
			}

			requestor.weavingClasses();
			// then weave into non-aspects
			for (Iterator i = input.getClassFileIterator(); i.hasNext(); ) {
			    UnwovenClassFile classFile = (UnwovenClassFile)i.next();
				String className = classFile.getClassName();
			    BcelObjectType classType = BcelWorld.getBcelObjectType(world.resolve(className));
			    if (! classType.isAspect()) {
			        weaveAndNotify(classFile, classType, requestor);
			        wovenClassNames.add(className);
			    }
			}
			
			//addedClasses = new ArrayList();
			//deletedTypenames = new ArrayList();
			requestor.weaveCompleted();
			
	    	return wovenClassNames;
	    }
	    
		private void weaveAndNotify(UnwovenClassFile classFile,
				BcelObjectType classType, IWeaveRequestor requestor)
				throws IOException {
			LazyClassGen clazz = weaveWithoutDump(classFile, classType);
			classType.finishedWith();
			// clazz is null if the classfile was unchanged by weaving...
			if (clazz != null) {
				UnwovenClassFile[] newClasses = getClassFilesFor(clazz);
				for (int i = 0; i < newClasses.length; i++) {
					requestor.acceptResult(newClasses[i]);
				}
			} else {
				requestor.acceptResult(null);
				//requestor.acceptResult(classFile);
			}
		}
	}
}
