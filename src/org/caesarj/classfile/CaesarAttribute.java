/*
 * Created on 14.04.2004
 *
 */
package org.caesarj.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * This class is the base class for all attributes that
 * caesar elements need to preserve in classfiles. It serves as a factory 
 * when these attributes are read from classfiles.
 * @author Karl Klose
 */
public class CaesarAttribute extends GenericAttribute {
	
	public CaesarAttribute(AsciiConstant name, DataInput in, ConstantPool cp) throws IOException {
		super(name, in, cp);
	}
	
	public CaesarAttribute(String name, byte[] data) {
		super(name, data);
	}

	/**
	 * This factory method constructs the right subclass for this attribute 
	 * from the input stream.
	 */
	static public CaesarAttribute	readAttribute(
			AsciiConstant name, 
			DataInput in, 
			ConstantPool cp) throws IOException{
		//	Create class corresponding to this attributename
		if (name.getValue().equals(ExtraModifiersAttribute.AttributeName))
			return new ExtraModifiersAttribute(in,cp);
		// ???: Better throw exception? Control flow should never reach here,
		// because we don't know such attributes. They are currently simply ignored
		// since names may change in future.
		return new CaesarAttribute(name,in,cp);
	}
	
	
	public final static String	AttributePrefix = "org.caesarj.compiler.classfile.";
}
