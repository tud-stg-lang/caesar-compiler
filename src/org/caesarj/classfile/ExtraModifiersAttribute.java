/*
 * Created on 16.03.2004
 *
 */
package org.caesarj.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * This class represents an generic attribute that stores the extra modifiers 
 * (caesar modifiers that are not supported by the VMS format) of a class in a 
 * classfile. The name of this attribute is <code>org.caesarj.compiler.ExtraModifiers</code>.
 * 
 * @author Karl Klose
 */
public class ExtraModifiersAttribute extends CaesarAttribute {
	
	/** Name constants */
	public final static String	AttributeName  = AttributePrefix + "ExtraModifiers";
	
	/** The binary mask for the modifiers to store */
	public final static int		EXTRA_MOD_MASK	= ~0xFFF;

	/**
	 * Create a new attribute
	 * @param modifiers	The class' modifier
	 */
	public ExtraModifiersAttribute(int modifiers) {
		super(AttributeName, CaesarAttribute.integerToByteArray(modifiers));
		extraModifiers = modifiers & EXTRA_MOD_MASK;
	}
	
	/**
	 * Read from a input stream
	 * @param in	The input stream
	 * @param cp	The constant pool to be used
	 */
	public ExtraModifiersAttribute(DataInput in, ConstantPool cp) throws IOException {
		super( new AsciiConstant(AttributeName), in, cp );
		extraModifiers = CaesarAttribute.byteArrayToInteger( getData() );
	}

	/**
	 * Get the attributes value as integer
	 * @return The stored extra modifiers
	 */
	public int getExtraModifiers(){
		return extraModifiers;
	}
	private int extraModifiers;
}
