/*
 * Created on 19.04.2004
 *
 */
package org.caesarj.mixer2.intern;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.apache.bcel.Constants;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.GraphicalVerifier;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.mixer2.MixerException;


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

	
	protected static String	outputDirectory = "bin/";
	
	public static void setOutputDirectory( String outputDirectory ){
		ClassModifyingVisitor.outputDirectory = outputDirectory;
	}
	
	
	protected String 	oldClassName, 
						newClassName, 
						newSuperclassName,
						outerClassName;

	protected ClassGen		classGenerator;
	protected ConstantPoolGen cpoolGenerator;

	public static	void modify( 
			String className, 
			String newClassName, 
			String newSuperclassName, 
			String outerClassName) throws MixerException{
		
		
		JavaClass c = Repository.lookupClass("example/A$Y$Innerst$I3");
		
		System.out.println(c);
		
		if (true) return;
		
		
		
		ClassModifyingVisitor	visitor = new ClassModifyingVisitor( 
												className, 
												newClassName, 
												newSuperclassName, 
												outerClassName );
		
		JavaClass	clazz = Repository.lookupClass(className);
		
		if (clazz == null) {
			throw new MixerException("Class not found "+ className);
		}
		
		visitor.run(clazz);
	}
	
	
	/**
	 * Create a visitor to modify a class file
	 * @param oldClassName	The original class name
	 * @param newClassName	The new class name
	 * @param newSuperclassName	The new name of super class
	 * @param outerClassName	name of the outerclass
	 */
	protected ClassModifyingVisitor( 
			String oldClassName, 
			String newClassName, 
			String newSuperclassName,
			String outerClassName ) {
		this.oldClassName = oldClassName;
		this.newClassName = newClassName;
		this.newSuperclassName = newSuperclassName;
		this.outerClassName = outerClassName;
	}
	
	/**
	 * Get the inner class entries of a java class
	 */
	InnerClass[]	getInnerClasses( JavaClass clazz ){
		Attribute[] attributes = clazz.getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			if (attribute.getTag() == Constants.ATTR_INNER_CLASSES){
				return ((InnerClasses)attribute).getInnerClasses();
			}
		}
		return new InnerClass[0];
	}
	
	String getOuterClass( JavaClass clazz ){
		String	ident = new JavaQualifiedName(oldClassName).getIdent();
		
		InnerClass[] inners = getInnerClasses(clazz);
		for (int i = 0; i < inners.length; i++) {
			InnerClass inner = inners[i];
			String innerName = loadName( inner.getInnerNameIndex(), clazz.getConstantPool() );
			if (innerName.equals(ident)){
				String outername = loadClassName( inner.getOuterClassIndex(), clazz.getConstantPool() );
				return outername;
			}
		}
		return "";
	}
	
	protected void run(JavaClass clazz) throws MixerException {
		String currentOuterClass =  getOuterClass(clazz);
		
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
		
		// Set new class & superclass name
		cpoolGenerator.setConstant(superclassNameIndex, new ConstantUtf8(newSuperclassName));
		cpoolGenerator.setConstant(classNameIndex, new ConstantUtf8(newClassName));

		// visit fields, methods and local variables to replace type references
		new DescendingVisitor(clazz, this).visit();

		// Delete all inner class references 
		Attribute[] atts = clazz.getAttributes();
		Vector	v = new Vector();
		for (int i = 0; i < atts.length; i++) {
			Attribute attribute = atts[i];
			if (attribute.getTag() == org.apache.bcel.Constants.ATTR_INNER_CLASSES){
				continue;
			}
			v.add( attribute );
		}
		atts = (Attribute[]) v.toArray(new Attribute[0]);
		JavaClass result = classGenerator.getJavaClass();
		
		result.setAttributes(atts);
		/* DEBUG */ System.out.println(result);
		/* DEBUG */	System.out.println(result.getConstantPool());
		
		writeClass( result );
		
		/* Add reference to the outer-class-file */
	
		
		if (!currentOuterClass.equals(outerClassName)){
			System.out.println(outerClassName);
			JavaClass outer = Repository.lookupClass(outerClassName);
			//addInnerClassTo(outer, result);
			writeClass(outer);
		}
	}


	/**
	 * @param outer
	 * @param inner
	 */
	private void addInnerClassTo(JavaClass outer, JavaClass inner) {
		// change outer class KK
		// get constant pool, add a new string and set it again
		ConstantPoolGen cpg = new ConstantPoolGen(outer.getConstantPool());
		
		int index = cpg.addUtf8("InnerClass");
		outer.setConstantPool(cpg.getConstantPool());

		InnerClasses	ic =null;

		Attribute[] attributes = outer.getAttributes();
		Vector newAttributes = new Vector();
		for (int i = 0; i < attributes.length; i++) {
			newAttributes.add(attributes[i]);
		}
		newAttributes.add( ic );

		attributes = (Attribute[]) newAttributes.toArray( new Attribute[0] );
		outer.setAttributes(attributes);
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
/*		// Change the type of the local variable this
		if (variable.getName().equals("this") ){
			
			int index = variable.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( variable.getSignature() ) ) );
		}
		*/
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
			
			/*		
			// Create a map with the new current classes name 
			HashMap	newMap = (HashMap) replaceTypes.clone();
			newMap.put(oldClassName.replace('/','.'), newClassName.replace('/','.'));
			// and proceed to inner class
			try{
				ClassModifyingVisitor.modify(oldClassName+"$"+innerName,newClassName+"$"+innerName, null, newMap );
			} catch( MixerException e ){
				e.printStackTrace();
			}
			*/			
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

	
	private String getOuterClassName() {
		return outerClassName;
	}

	private String loadName(int i, ConstantPool pool) {
 		ConstantUtf8	c = (ConstantUtf8)pool.getConstant(i);
 		return c.getBytes();
 	}

	private String loadClassName( int index, ConstantPool cp ){
		ConstantClass 	cclass = (ConstantClass)cp.getConstant(index);
    	
    	return cclass.getBytes(cp);
    }
 

	public void visitField(Field field) {
		/*		if ( mustReplaceSignature(field.getSignature())){
			int index = field.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( field.getSignature() ) ) );
		}
		*/
		super.visitField(field);
	}

}
