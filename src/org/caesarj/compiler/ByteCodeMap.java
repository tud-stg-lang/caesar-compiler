/*
 * Created on 04.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.caesarj.compiler;

import java.util.HashMap;
import java.util.Iterator;
import java.io.File;

import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.util.Utils;

/**
 * @author Vaidas Gasiunas
 *
 * Keeps generated classes and their byte code
 * Used for weaving
 */
public class ByteCodeMap 
{
	/*
	 * State variables
	 */
	private HashMap _byteCodeMap = null; 
	
	private String _outputDir;
	
	/*
	 * Construction
	 */
	public ByteCodeMap(String outputDir)
	{
		_outputDir = outputDir;
		_byteCodeMap = new HashMap(100);
	}
	
	/*
	 * Queries
	 */
	public Iterator iterator()
	{
		return _byteCodeMap.entrySet().iterator();		
	}
	
	/*
	 * Operations
	 */
	public void addSourceClass(CSourceClass cclass, byte[] codeBuf)
	{
		_byteCodeMap.put(getFileName(cclass), codeBuf);
	}
	
	public void addClassFile(String fileName, byte[] codeBuf)
	{
		_byteCodeMap.put(fileName, codeBuf);
	}
	
	/*
	 * Implementation
	 */
	private String getFileName(CSourceClass sourceClass) 
	{
        String destination = _outputDir;
        
        String[] classPath =
            Utils.splitQualifiedName(sourceClass.getQualifiedName());
        if (destination == null || destination.equals("")) {
            destination = System.getProperty("user.dir");
        }

        if (classPath[0] != null && !classPath[0].equals("")) {
            // the class is part of a package
            destination += File.separator
                + classPath[0].replace('/', File.separatorChar);
        }

        String filename =
            destination
                + File.separatorChar
                + classPath[classPath.length
                - 1]
                + ".class";
        return filename;
    }
}
