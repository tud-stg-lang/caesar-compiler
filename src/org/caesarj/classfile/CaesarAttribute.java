/*
 * Created on 14.04.2004
 *
 */
package org.caesarj.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * This class is the base class for all attributes that
 * caesar elements need to write. It serves as a factory 
 * when these attributes are read from classfiles.
 * @author Karl Klose
 */
public abstract class CaesarAttribute extends GenericAttribute {

	public final static String	AttributePrefix = "org.caesarj.compiler.classfile.";
	
	public final static String	extraModifiersAttribute = "ExtraModifiers";
	public final static String 	extraMethodInfo = "ExtraMethodInfo";
	
	protected CaesarAttribute(String name, byte[] data) {
		super(name, data);
	}

	
	
}
