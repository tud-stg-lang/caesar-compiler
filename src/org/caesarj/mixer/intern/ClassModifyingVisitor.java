/*
 * Created on 19.04.2004
 *
 */
package org.caesarj.mixer.intern;

import java.io.IOException;
import java.util.HashMap;

import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.caesarj.mixer.MixerException;

/**
 * @author Karl Klose
 */
public class ClassModifyingVisitor extends EmptyVisitor {

	protected String oldClassName, newClassName, newSuperclassName;
	
	boolean mustReplaceSignature( String signature ){
		return replaceTypes.containsKey( ClassModifyingVisitor.typeFromSignature(signature));
	}
	
	String getNewSignature( String signature ){
		String type = ClassModifyingVisitor.typeFromSignature(signature);
		type = (String) replaceTypes.get(type);
		return replaceType(signature,type);
	}
	
	public static String	typeFromSignature( String signature ){
		// method
		if (signature.startsWith("()")){
			return typeFromSignature( signature.substring(2));
		}
		// array
		else if (signature.startsWith("[")){
			return typeFromSignature( signature.substring(1));
		}
		// primitive type
		else if (signature.length()==1){
			return signature;
		}
		return signature.substring(1, signature.length()-1);
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
	
	protected HashMap	replaceTypes;
	
	public ClassModifyingVisitor( 
			String oldClassName, 
			String newClassName, 
			String newSuperclassName, 
			HashMap	replaceTypes ){
		this.oldClassName = oldClassName;
		this.newClassName = newClassName;
		this.newSuperclassName = newSuperclassName;
		this.replaceTypes = replaceTypes;
	}
	
	private ClassGen		classGenerator;
	private ConstantPoolGen cpoolGenerator;
	
	public void start(JavaClass clazz) throws MixerException {
		
		classGenerator = new ClassGen(clazz);
		
		cpoolGenerator = classGenerator.getConstantPool();
		int classNameIndex = classGenerator.getClassNameIndex(),
			superclassNameIndex = classGenerator.getSuperclassNameIndex();
		ConstantClass 	cc = (ConstantClass)cpoolGenerator.getConstant(classNameIndex),
						csc = (ConstantClass)cpoolGenerator.getConstant(superclassNameIndex);
		classNameIndex = cc.getNameIndex();
		superclassNameIndex = csc.getNameIndex();
		cpoolGenerator.setConstant(classNameIndex, new ConstantUtf8(newClassName));
		cpoolGenerator.setConstant(superclassNameIndex, new ConstantUtf8(newSuperclassName));
		
		
		// visit fields, methods and local variables to replace type references
		new DescendingVisitor(clazz, this).visit();
		classGenerator.setConstantPool(cpoolGenerator);
		
		try {
			// finally, write the modfied classfile
			classGenerator.getJavaClass().dump("bin/"+newClassName+".class");
		} catch (IOException e) {
			throw new MixerException( "Unable to write classfile:" + e);
		}
	}


	
	public void visitLocalVariable(LocalVariable variable) {
		if (mustReplaceSignature( variable.getSignature() ) ){
			int index = variable.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( variable.getSignature() ) ) );
			
		}
		super.visitLocalVariable(variable);
	}

	public void visitField(Field field) {
		if (mustReplaceSignature( field.getSignature() ) ){
			int index = field.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( field.getSignature() ) ) );
			
		}
		super.visitField(field);
	}
	
	public void visitMethod(Method method) {
		if (mustReplaceSignature( method.getSignature() ) ){
			int index = method.getSignatureIndex();
			cpoolGenerator.setConstant(index, 
					new ConstantUtf8( 
							getNewSignature( method.getSignature() ) ) );
		}
		super.visitMethod(method);
	}

}
