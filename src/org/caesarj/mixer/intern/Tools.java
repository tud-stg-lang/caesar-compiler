/*
 * Created on 25.06.2004
 *
 */
package org.caesarj.mixer.intern;

import java.util.Vector;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.util.InconsistencyException;

/**
 * This class provide some tool function for (higher-level) bytecode 
 * manipulation via BCEL, which is itself very lowlevel..
 * @author Karl Klose
 */
public class Tools {

	public static boolean	isElement(String element, String[] array){
		for (int i=0; i<array.length; i++)
			if (array[i].equals(element))
				return true;
		return false;
	}

	/*
	 * Loads the name (Utf8-Constant) indexed by i from the constant pool
	 */
	public static String loadName(int i, ConstantPool pool) {
 		ConstantUtf8	c = (ConstantUtf8)pool.getConstant(i);
 		return c.getBytes();
 	}

	/*
	 * Loads the class name at index in the constant pool
	 */
	public static String loadClassName( int index, ConstantPool cp ){
		ConstantClass 	cclass = (ConstantClass)cp.getConstant(index);
    	
    	return cclass.getBytes(cp);
    }
	
	public static InnerClasses	getInnerClassesAttribute( JavaClass clazz ){
		Attribute[] attributes = clazz.getAttributes();
		for (int i = 0; i < attributes.length; i++) {
			Attribute attribute = attributes[i];
			if (attribute.getTag() == Constants.ATTR_INNER_CLASSES){
				return ((InnerClasses)attribute);
			}
		}
		return null;
	}
	
	public static InnerClass[] add( InnerClass ic, InnerClass[] ics ){
		InnerClass[]	res = new InnerClass[ics.length+1];
		for (int i=0; i<res.length; i++){
			res[i] = i<ics.length ? ics[i] : ic;
		}
		return res;
	}
	
	/*
	 * Get the inner class entries of a java class
	 */
	public static InnerClass[]	getInnerClasses( JavaClass clazz ){
		InnerClasses	ic = getInnerClassesAttribute(clazz);
		if (ic==null){
			return new InnerClass[0];
		}
		return ic.getInnerClasses();
	}
	
	/**
	 * Returns the outer class of the class <code>forName</code> from the 
	 * Innerclass list of <code>clazz</code>.
	 * Returns "" if no matching entry is found.
	 */
	public static String getOuterClass( JavaClass clazz, String forName ){
		String	ident = new JavaQualifiedName(forName).getIdent();
		
		InnerClass[] inners = getInnerClasses(clazz);
		for (int i = 0; i < inners.length; i++) {
			InnerClass inner = inners[i];
			String innerName = Tools.loadName( inner.getInnerNameIndex(), clazz.getConstantPool() );
			if (innerName.equals(ident)){
				return Tools.loadClassName( inner.getOuterClassIndex(), clazz.getConstantPool() );
			}
		}
		return "";
	}
	
	public static String	dottedClassName( String name ){
		if (name==null) return null;
		return name.replace('/','.').replace('$','.');
	}

	/**
	 * Calculates the new outerclass name given an old outerclass name
	 * @param	oldClassName	The context in the name should be calculated
	 * @param	oldOuterName	The name of the outer class of oldClassName
	 * @param	outerNames		The list of new outer names 
	 */
	public static String	getNewOuterName( 
			String oldClassName, 
			String oldOuterName, 
			String[] outerNames ){
		int nthOuter=0;
		
		if (!isPrefix(oldOuterName,oldClassName)){
			throw new InconsistencyException(oldOuterName+" is not an outer class of "+oldClassName);
		}
		
		String	remainder = oldClassName.substring(oldOuterName.length()+1);
		for (int i=0; i<remainder.length(); i++){
			if (remainder.charAt(i)=='$')	nthOuter++;
		}
		
		return dottedClassName(outerNames[nthOuter]);
	}

	public static void addAttribute( JavaClass clazz, Attribute att ){
		Attribute[] attributes 		= clazz.getAttributes();
		Attribute[] newAttributes 	= new Attribute[attributes.length+1];
		for (int i=0; i<attributes.length+1; i++){
			if (i<attributes.length)	newAttributes[i] = attributes[i];
			else						newAttributes[i] = att;
		}
		clazz.setAttributes(newAttributes);
	}
	
	public static boolean isPrefix( String pre, String s ){
		return dottedClassName(s).startsWith(dottedClassName(pre));
	}
	
	/**
	 * @param returnType
	 * @param oldOuterClassName
	 * @return
	 */
	public static boolean sameClass(String class1, String class2) {
		if (class1==null || class2==null)	return false;
		return dottedClassName(class1).equals(dottedClassName(class2));
	}

	/**
	 * @param outer
	 * @param outerName
	 * @param innerName
	 * @return
	 */
	public static InnerClass createInnerClass(JavaClass outerClass, String outerName, String innerName) {
		ConstantPoolGen cpg = new ConstantPoolGen( outerClass.getConstantPool() );
		
		String ident = innerName.substring(outerName.length()+1);
		
		int outerClassIndex = cpg.addClass(outerName),
			innerClassIndex = cpg.addClass(outerName+"$"+ident),
			nameIndex		= cpg.addUtf8(ident);
		
		InnerClass result = new InnerClass(innerClassIndex,outerClassIndex,nameIndex, 0);//,Constants.ACC_PUBLIC);
		
		outerClass.setConstantPool(cpg.getFinalConstantPool());
		return result;
	}
}
