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
 * $Id: ClassModifyingVisitor.java,v 1.15 2005-03-10 10:42:58 gasiunas Exp $
 */

package org.caesarj.mixer.intern;

import java.util.Vector;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
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
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.mixer.MixerException;

/**
 * Modify a <code>JavaClass</code> to have a different class, superclass and outerclass.
 * Performs the following changes:
 * 1. Change className
 * 2. Change superclassName
 * 3. Change type of local this-variable
 * 4. Change super-constructor calls
 * 5. Change cosntructor signatures
 *
 * It is asserted that the modified class is not used as an argument, returntype or field type 
 * by itself. 
 *  
 * @author Karl Klose
 */
public class ClassModifyingVisitor extends EmptyVisitor  {

	protected String[]	outerClasses;
	
	protected String 	oldClassName; 
	protected String 	newClassName; 
	protected String 	newSuperclassName;
	protected String 	newOuterClassName;
	protected String 	oldOuterClassName;
	protected String 	oldSuperclassName;
	protected String 	outerOfOldSuper; 
	protected String 	outerOfNewSuper;
		
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
			String outerClassName,
			String outerOfOldSuper,
			String outerOfNewSuper,
			String []outers ) {
		this.oldClassName = oldClassName;
		this.newClassName = newClassName;
		this.newSuperclassName = newSuperclassName;
		this.newOuterClassName = outerClassName;
		this.outerOfOldSuper = outerOfOldSuper;
		this.outerOfNewSuper = outerOfNewSuper;
		outerClasses = outers;
	}
		
	protected JavaClass transform(JavaClass clazz) throws MixerException {
		oldOuterClassName =  Tools.getOuterClass(clazz,oldClassName);
		oldSuperclassName = clazz.getSuperclassName();
		
		// create a copy as work base
		JavaClass newClass = clazz.copy();
		
		// find indices of class and super class name
		int classNameIndex = newClass.getClassNameIndex(),
		superclassNameIndex = newClass.getSuperclassNameIndex();
		ConstantClass 	cc = (ConstantClass)newClass.getConstantPool().getConstant(classNameIndex),
						csc = (ConstantClass)newClass.getConstantPool().getConstant(superclassNameIndex);
		classNameIndex = cc.getNameIndex();
		superclassNameIndex = csc.getNameIndex();
		
		// Set new class & superclass name
		newClass.getConstantPool().setConstant(superclassNameIndex, new ConstantUtf8(newSuperclassName));
		newClass.getConstantPool().setConstant(classNameIndex, new ConstantUtf8(newClassName));
		
		
		// visit fields, methods and local variables to replace type references
		new DescendingVisitor(newClass, this).visit();
		
		// Delete all inner class references 
		Attribute[] atts = newClass.getAttributes();
		Vector	v = new Vector();
		for (int i = 0; i < atts.length; i++) {
			Attribute attribute = atts[i];
			if (attribute.getTag() == org.apache.bcel.Constants.ATTR_INNER_CLASSES){
				InnerClasses ic = (InnerClasses)attribute;
				ic.setInnerClasses(new InnerClass[0]);
				ic.setLength(2);
				
			}
			v.add( attribute );
		}
		atts = (Attribute[]) v.toArray(new Attribute[0]);
		newClass.setAttributes(atts);
		

		newClass = removeFactoryMethods(newClass);
		
		// Mixin classes must be abstract, because they do not implement factory methods
		newClass.setAccessFlags(newClass.getAccessFlags() | Constants.ACC_ABSTRACT);

		// take a look at all methodrefs
		modifyMethodRefs(newClass);

		// return generated class
		return newClass;	
	}

	/**
	 * Remove all methods from <code>clazz</code> whose names start
	 * with '$new'.
	 * @param clazz	The class to modify	
	 * @return	The class with removed methods
	 */
	JavaClass removeFactoryMethods( JavaClass clazz ){
		ClassGen gen = new ClassGen(clazz);
		
		Method[] methods = gen.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().startsWith(CaesarConstants.FACTORY_METHOD_PREFIX)){
				gen.removeMethod(method);
			}
		}
		
		return gen.getJavaClass();
	}
	
	/**
	 * Checks method references to super-constructors and outerclass methods
	 * and modifies them to refer to the correct new outer classes. 
	 */
	void modifyMethodRefs( JavaClass clazz ){
		ConstantPool cp = clazz.getConstantPool();
		for (int i=1; i<cp.getLength(); i++){
			Constant c = cp.getConstant(i);
			
			if(c == null) continue;
			
			if (c.getTag() == Constants.CONSTANT_Methodref){
				ConstantMethodref mr = (ConstantMethodref) c;
				String targetClassName = mr.getClass(cp);

				int nameAndTypeIndex = mr.getNameAndTypeIndex();
				
				ConstantNameAndType	nat = ((ConstantNameAndType) cp.getConstant(nameAndTypeIndex));
			
				// Check for superconstructor calls with otuer class parameter
				if (Tools.sameClass(targetClassName, newSuperclassName)){
					if (nat.getName(cp).equals("<init>")){		
						Type[] args = Type.getArgumentTypes(nat.getSignature(cp));
						if (args.length == 1){
							String argumentType = args[0].toString();
/*							System.out.println(
									"Call to method  :\t"+targetClassName+"."+nat.getSignature(cp)+
									"\n Argument type  :\t"+ args[0].toString()+
									"\n outerOfOldSuper:\t"+outerOfOldSuper+
									"\n outerOfNewSuper:\t"+outerOfNewSuper+
									"\n newClassName   :\t"+newClassName+
									"\n oldClassName   :\t"+oldClassName+
									"\n newOuterName   :\t"+newOuterClassName+
									"\n oldouterName   :\t"+oldOuterClassName+
									"\n newSuperName   :\t"+newSuperclassName+
									"\n oldSuperName   :\t"+oldSuperclassName
									);*/
							// if parameter is of old super-outer-class type, set new signature
							if (Tools.sameClass(argumentType, outerOfOldSuper)){
								cp.setConstant( 
										nat.getSignatureIndex(),
										new ConstantUtf8("(L"+outerOfNewSuper+";)V")
									);
							}
						}
					}
				}
				// check whether its a call to our old outer class
				if (Tools.isPrefix(targetClassName, oldOuterClassName)){
					String newTargetClass = Tools.getNewOuterName(
												oldClassName,
												targetClassName,
												outerClasses);
					int classIndex = mr.getClassIndex();
					ConstantClass cc = (ConstantClass)cp.getConstant(classIndex);
					int nameIndex = cc.getNameIndex();
					cp.setConstant(nameIndex, new ConstantUtf8(newTargetClass));
				}
			}
		}
	}
	
	public void visitLocalVariable(LocalVariable variable) {
		// Change the type of the local variable this
		if (variable.getName().equals("this") ){
			int index = variable.getSignatureIndex();
			variable.getConstantPool().setConstant(index, 
					new ConstantUtf8( 
							new ObjectType(newClassName).getSignature()));
		} 
		super.visitLocalVariable(variable);
	}

	public void visitField(Field field) {
		// and of outer this
		if (field.getName().startsWith("this$")){
			int index = field.getSignatureIndex();
			field.getConstantPool().setConstant(index,
					new ConstantUtf8( 
							new ObjectType(newOuterClassName).getSignature()));
		}
		super.visitField(field);
	}
	
	
	public void visitMethod(Method obj) {
		// we search for outer-class-access functions, which
		// are static, have exactly one argument of this class' type and
		// return an instance of the outer class' type
		if (obj.getName().startsWith("access$")){
			if (!obj.isStatic() ) return;
			
			String returnType = Type.getReturnType(obj.getSignature()).toString(); 
			
			if (!Tools.sameClass(returnType,oldOuterClassName)) return;
			Type[]	argTypes = Type.getArgumentTypes(obj.getSignature());
			if (argTypes.length != 1) return;
			
			// construct the new signature & use it to overwrite the old one
			String newSignature = "(L"+newClassName+";)L"+newOuterClassName+";";// + " Just a little test";
			
			int index = obj.getSignatureIndex();
			
			obj.getConstantPool().setConstant(index, new ConstantUtf8(newSignature));
		}
		// and we check for constructors 
		else if (obj.getName().equals("<init>")){
			Type[]	argTypes = Type.getArgumentTypes(obj.getSignature());
			if (argTypes.length != 1) return;
			// modify the signature if neccessary
			if (Tools.sameClass(argTypes[0].toString(),oldOuterClassName)){
				// construct the new signature & use it to overwrite the old one
				String newSignature = "(L"+newOuterClassName+";)V";
				
				int index = obj.getSignatureIndex();
				
				obj.getConstantPool().setConstant(index, new ConstantUtf8(newSignature));
			}
			// check code for super-cosntructor call and adjust its signature
			Code code = obj.getCode();
			
		}
	}

}
