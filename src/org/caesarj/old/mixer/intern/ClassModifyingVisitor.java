/*
 * Created on 19.04.2004
 *
 */
package org.caesarj.old.mixer.intern;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;


import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.InnerClass;
import org.apache.bcel.classfile.InnerClasses;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.Class2HTML;
import org.caesarj.old.mixer.Mixer;
import org.caesarj.old.mixer.MixerException;

/**
 * Modify a <code>JavaClass</code> to have a different class and superclass name.
 * Performs the following changes:
 * 1. Change className
 * 2. Change superclassName
 * 3. Change type of local this-variable
 * 4. Modify inner class references
 
 * @author Karl Klose
 */
public class ClassModifyingVisitor extends EmptyVisitor {

	/** This Stack holds the current outer class */
	protected static Stack	outerClasses = new Stack();
	
	protected static String getOuterClassName(){
		return (String) outerClasses.peek();
	}
	
	protected static String	outputDirectory = "bin/";
	
	public static void setOutputDirectory( String outputDirectory ){
		ClassModifyingVisitor.outputDirectory = outputDirectory;
	}
	
	
	protected String oldClassName, newClassName, newSuperclassName;

	protected ClassGen		classGenerator;
	protected ConstantPoolGen cpoolGenerator;

	public static	void modify( 
			String className, 
			String newClassName, 
			String newSuperclassName, 
			HashMap replaceTypes) throws MixerException{
		
		if (replaceTypes == null)		replaceTypes = new HashMap();
		
		ClassModifyingVisitor	visitor = new ClassModifyingVisitor( 
												className, 
												newClassName, 
												newSuperclassName, 
												replaceTypes );
		
		JavaClass	clazz = Repository.lookupClass(className);
		
		outerClasses.push( newClassName );
		visitor.run(clazz);
		outerClasses.pop();
	}
	
	
	/**
	 * Create a visitor to modify a class file
	 * @param oldClassName	The original class name
	 * @param newClassName	The new class name
	 * @param newSuperclassName	The new name of super class
	 * @param replaceTypes	A map with the names of types to replace in the class
	 */
	protected ClassModifyingVisitor( 
			String oldClassName, 
			String newClassName, 
			String newSuperclassName, 
			HashMap	replaceTypes ){
		this.oldClassName = oldClassName;
		this.newClassName = newClassName;
		this.newSuperclassName = newSuperclassName;
		this.replaceTypes = replaceTypes;
	}
	

	protected void run(JavaClass clazz) throws MixerException {
		// open a class genrerator to modify the class
		classGenerator = new ClassGen(clazz);
		// retrieve constant pool generator and index values for class & superclass name
		cpoolGenerator = classGenerator.getConstantPool();
		int classNameIndex = classGenerator.getClassNameIndex(),
			superclassNameIndex = classGenerator.getSuperclassNameIndex();
		ConstantClass 	cc = (ConstantClass)cpoolGenerator.getConstant(classNameIndex),
						csc = (ConstantClass)cpoolGenerator.getConstant(superclassNameIndex);
		classNameIndex = cc.getNameIndex();
		superclassNameIndex = csc.getNameIndex();
		
		// set the new class & superclass name
		classGenerator.setClassName(newClassName);
		if (newSuperclassName != null)
			classGenerator.setSuperclassName(newSuperclassName);
		
		// visit fields, methods and local variables to replace type references
		new DescendingVisitor(clazz, this).visit();

		classGenerator.setConstantPool(cpoolGenerator);

		writeClass( classGenerator.getJavaClass());
	}


	/**
	 * Write the class to file system 
	 * @param clazz
	 * @throws MixerException
	 */
	protected void writeClass( JavaClass clazz ) throws MixerException{
		try {
			clazz.dump(outputDirectory+newClassName+".class");
		} catch (IOException e) {
			throw new MixerException( "Unable to write classfile:" + e);
		}
	}
	
	
	public void visitLocalVariable(LocalVariable variable) {
		// Change the type of the local variable this
		if (variable.getName().equals("this") ){
			
			int index = variable.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( variable.getSignature() ) ) );
		}

		super.visitLocalVariable(variable);
	}
	
	public void visitInnerClass(InnerClass inner) {
		// Read names of inner and outer class out of the attribute
		ConstantClass	outerClass = ((ConstantClass) cpoolGenerator.getConstant(inner.getOuterClassIndex()));
		int nameIndex = outerClass.getNameIndex();
		String outerClassName = ((ConstantUtf8)cpoolGenerator.getConstant(nameIndex)).getBytes();

		ConstantClass	innerClass = ((ConstantClass ) cpoolGenerator.getConstant(inner.getInnerClassIndex()));
		nameIndex = innerClass.getNameIndex();
		String innerClassName = ((ConstantUtf8)cpoolGenerator.getConstant(nameIndex)).getBytes();
		
		nameIndex = inner.getInnerNameIndex();
		String innerName = ((ConstantUtf8)cpoolGenerator.getConstant(nameIndex)).getBytes();

		// Check if it is an inner class of the current class 
		if (innerClassName.equals(oldClassName+"$"+innerName)){

			// Set correct new class names 
			int newNameIndex = cpoolGenerator.addUtf8(newClassName+"$"+innerName);
			innerClass.setNameIndex(newNameIndex);

			newNameIndex = cpoolGenerator.addUtf8(newClassName);
			outerClass.setNameIndex( newNameIndex );
			
			// Create a map with the new current classes name 
			HashMap	newMap = (HashMap) replaceTypes.clone();
			newMap.put(oldClassName.replace('/','.'), newClassName.replace('/','.'));
			
			// and proceed to inner class
			try{
				ClassModifyingVisitor.modify(oldClassName+"$"+innerName,newClassName+"$"+innerName, null, newMap );
			} catch( MixerException e ){
				e.printStackTrace();
			}
			
		} 
		// or, if it is a reference to the outer class 
		else if (oldClassName.equals(innerClassName) ){
			// Set new class names
			int newNameIndex;
			
			newNameIndex = cpoolGenerator.addUtf8(newClassName);
			innerClass.setNameIndex(newNameIndex);

			newNameIndex = cpoolGenerator.addUtf8(getOuterClassName());
			outerClass.setNameIndex( newNameIndex );
			
		}
		
		super.visitInnerClass(inner);
	}

	
	public void visitField(Field field) {
		if ( mustReplaceSignature(field.getSignature())){
			int index = field.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( field.getSignature() ) ) );
		}
		super.visitField(field);
	}

	
	/* A map of all type-names that have to be replaced */
	protected HashMap	replaceTypes;
	
	
	/*
	 * Some tool functions to handle signatures&types
	 */
	
	boolean mustReplaceSignature( String signature ){
		return replaceTypes.containsKey( ClassModifyingVisitor.typeFromSignature(signature));
	}
	
	/**
	 * Looks up the referenced type in the replaceTypes map, and replaces the
	 * type in this signature with the on found (if any).
	 */
	String getNewSignature( String signature ){
		String type = ClassModifyingVisitor.typeFromSignature(signature);
		type = (String) replaceTypes.get(type);
		return replaceType(signature,type);
	}

	public static String	typeFromSignature( String signature ){
		Type	t = Type.getType(signature);
		return t.toString();
	}
	
	public static String	replaceType( String signature, String with ){
		// method
		if (signature.startsWith("()")){
			return "()"+replaceType( signature.substring(2), with );
		}
		// array
		else if (signature.startsWith("[")){
			return "["+replaceType( signature.substring(1), with );
		}
		return "L"+with+";";
	}
	
}
