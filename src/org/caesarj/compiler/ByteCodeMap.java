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
 * $Id: ByteCodeMap.java,v 1.3 2005-01-24 16:52:58 aracic Exp $
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
