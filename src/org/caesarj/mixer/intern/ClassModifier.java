/*
 * Created on 04.08.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.caesarj.mixer.intern;

import org.apache.bcel.util.ClassPath;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.ClassParser;

import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
import org.caesarj.compiler.ByteCodeMap;

import org.caesarj.mixer.MixerException;

import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

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
			CaesarTypeSystem typeSystem) throws MixerException
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
		JavaTypeNode	oldSuperClass = typeSystem.getJavaTypeGraph().getNode(new JavaQualifiedName(oldSuperclassName));
		String outerOfOldSuper = null;
		if (oldSuperClass != null && oldSuperClass.getOuter() != null)
			outerOfOldSuper = oldSuperClass.getOuter().getQualifiedName().toString();
		
		JavaTypeNode	newSuperClass = typeSystem.getJavaTypeGraph().getNode(new JavaQualifiedName(newSuperclassName));
		String outerOfNewSuper = null;
		if (newSuperClass != null && newSuperClass.getOuter() != null)
			outerOfNewSuper = newSuperClass.getOuter().getQualifiedName().toString();
		
		// collect all outer classes for this mixin
    	Vector	outerClasses = new Vector();
    	
    	JavaTypeNode mixinType = typeSystem.getJavaTypeGraph().getNode(new JavaQualifiedName(newClassName)),
					outerType = mixinType.getOuter();
    	while ( outerType != null){
       	  outerClasses.add( outerType.getQualifiedName().getIdent() );  		
		  outerType = outerType.getOuter();
    	}
    	String[] outers = (String[])outerClasses.toArray( new String[0]);
    	
//    	System.out.println(
//    			" outer of old super: "+outerOfOldSuper+
//				"\n outer of new super: "+outerOfNewSuper);
    			
    	// tranform class
    	ClassModifyingVisitor visitor = 
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
