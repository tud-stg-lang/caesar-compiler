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
 * $Id: ClassModifier.java,v 1.8 2005-11-15 16:52:23 klose Exp $
 */

package org.caesarj.mixer.intern;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.apache.bcel.util.ClassPath;
import org.caesarj.compiler.ByteCodeMap;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeGraph;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
import org.caesarj.mixer.MixerException;

/**
 * @author Vaidas Gasiunas
 *
 * Generates mixin classes as copies of compiled classes
 *  
 */
public class ClassModifier 
{
	private String _inputDir;
	private String _outputDir;
	
	private ClassPath _classPath;
	
	private ByteCodeMap _byteCodeMap;
		
	public ClassModifier(String inputDir, String outputDir, ByteCodeMap byteCodeMap)
	{
		_inputDir = inputDir;
		_outputDir = outputDir;
		_byteCodeMap = byteCodeMap;
		
		// lookup for input classes only in input directory
		_classPath = new ClassPath(inputDir);
	}
	
	public void modify( 
			String className, 
			String newClassName, 
			String newSuperclassName, 
			String outerClassName,
            KjcEnvironment env) throws MixerException
	{
		JavaClass	clazz = null;
		
		// Load the class
		try 
		{
			InputStream is = _classPath.getInputStream(className);
			clazz = new ClassParser(is, className).parse();
		} 
		catch(IOException e)
		{
			throw new MixerException("Class not found "+ className);
		}
		
		String oldSuperclassName = clazz.getSuperclassName().replace('.','/');
		
		// use the typesystem to calculate some missing information
        String outerOfOldSuper = null;
        CCjSourceClass oldSuperClass = env.getClassReader().findSourceClass(oldSuperclassName);
        if (oldSuperClass != null && oldSuperClass.getOwner() != null){
            outerOfOldSuper = oldSuperClass.getOwner().getQualifiedName();
        }
        
      String outerOfNewSuper = null;
          CCjSourceClass newSuperClass = env.getClassReader().findSourceClass(newSuperclassName);
          if (newSuperClass != null && newSuperClass.getOwner() != null){
              outerOfNewSuper = newSuperClass.getOwner().getQualifiedName();
          }
        
		// collect all outer classes for this mixin
    	Vector<String>	outerClasses = new Vector<String>();
    	
            CClass mixinType = env.getClassReader().findSourceClass(newClassName),
                           outerType = mixinType.getOwner();
            while(outerType != null){
                String identifier = new JavaQualifiedName(outerType.getQualifiedName()).getIdent();
                outerClasses.add( identifier );
                outerType = outerType.getOwner();
            }
        
    	String[] outers = outerClasses.toArray( new String[outerClasses.size()]);
    	
    	// tranform class
    	final ClassModifyingVisitor visitor = 
    		new ClassModifyingVisitor( 
				className, 
				newClassName, 
				newSuperclassName, 
				outerClassName, 
				outerOfOldSuper,
				outerOfNewSuper,
				outers );
		
    	JavaClass newClass = visitor.transform(clazz);

    	// write tranformed class copy
    	writeClass(newClassName, newClass);

    
        CClass cc = env.getClassReader().findSourceClass(className);
            
        CReferenceType[] innerTypes = cc.getInnerClasses();
        
        // transform anonymous inner classes
        for(int i=0; i<innerTypes.length; i++){
            CReferenceType innerType = innerTypes[i];
            CClass inner = innerType.getCClass();
            if (inner.isAnonymous()){
                String  innerClassName = inner.getQualifiedName(),
                        newSuperName = inner.getSuperClass().getQualifiedName(),
                        
                        innerOuterName = className;
                
                String  ident = innerClassName.split("\\$")[1],
                        newInnerName = newClassName + "$" + ident;
                
                // TODO reactivate: modify(innerClassName, newInnerName, newSuperName, className, typeSystem, env);                        
            }
        }
    }
	
	/**
	 * Write the class to file system 
	 * @param clazz
	 * @throws MixerException
	 */
	protected void writeClass(String newClassName, JavaClass clazz ) throws MixerException{
		try 
		{
			String fileName = _outputDir + File.separator + newClassName+".class";
			clazz.dump(fileName);
			
			_byteCodeMap.addClassFile(fileName, clazz.getBytes());
		} 
		catch (IOException e) {
			throw new MixerException( "Unable to write classfile:" + e);
		}
	}
}
