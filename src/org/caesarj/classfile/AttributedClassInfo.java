package org.caesarj.classfile;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;


/**
 * Extends ClassInfo to support generating the class to a byte[]
 * and to add some extra attributes.
 * 
 * @author J?rgen Hallpap
 */
public class AttributedClassInfo extends ClassInfo {

	/**
	 * Constructor for ClassInfo.
	 * 
	 * @param modifiers
	 * @param thisClass
	 * @param superClass
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param innerClasses
	 * @param sourceFile
	 * @param genericSignature
	 * @param deprecated
	 * @param synthetic
	 */
	public AttributedClassInfo(
		short modifiers,
		String thisClass,
		String superClass,
		ClassConstant[] interfaces,
		FieldInfo[] fields,
		MethodInfo[] methods,
		InnerClassInfo[] innerClasses,
		String sourceFile,
		String genericSignature,
		boolean deprecated,
		boolean synthetic,
		Attribute[] extraAttributes) {

		super(
			modifiers,
			thisClass,
			superClass,
			interfaces,
			fields,
			methods,
			innerClasses,
			sourceFile,
			genericSignature,
			deprecated,
			synthetic);

		//Add the extraAttributes to the class attributes
		for (int i = 0; i < extraAttributes.length; i++) {
			getAttributes().add(extraAttributes[i]);
		}
	}

	/**
	 * Constructor for ClassInfo.
	 * 
	 * @param modifiers
	 * @param thisClass
	 * @param superClass
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param innerClasses
	 * @param sourceFile
	 * @param genericSignature
	 * @param deprecated
	 * @param synthetic
	 */
	public AttributedClassInfo(
		short modifiers,
		String thisClass,
		String superClass,
		Vector interfaces,
		Vector fields,
		Vector methods,
		InnerClassInfo[] innerClasses,
		String sourceFile,
		String genericSignature,
		boolean deprecated,
		boolean synthetic) {
		super(
			modifiers,
			thisClass,
			superClass,
			interfaces,
			fields,
			methods,
			innerClasses,
			sourceFile,
			genericSignature,
			deprecated,
			synthetic);
	}

	/**
	 * Constructor for ClassInfo.
	 * 
	 * @param in
	 * @param interfaceOnly
	 * @throws IOException
	 * @throws ClassFileFormatException
	 */
	public AttributedClassInfo(DataInput in, boolean interfaceOnly)
		throws IOException, ClassFileFormatException {
		super(in, interfaceOnly);
	}

	/**
	 * Gets the class as byte[].
	 * 
	 * @return byte[]
	 */
	public byte[] getByteArray() throws IOException, ClassFileFormatException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

		write(dataOutputStream);
		return outputStream.toByteArray();
	}

}
