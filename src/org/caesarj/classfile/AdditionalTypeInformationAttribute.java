/*
 * Created on 04.06.2004
 *
 */
package org.caesarj.classfile;

import java.io.DataInput;
import java.io.IOException;

import org.caesarj.compiler.export.AdditionalCaesarTypeInformation;

/**
 * This attribute stores the value of the generated and implicit flags of a class.
 * @author Karl Klose
 */
public class AdditionalTypeInformationAttribute extends CaesarAttribute {
	
	public static final String AttributeName = AttributePrefix + "AdditionalTypeInformationAttribute";
		
	AdditionalCaesarTypeInformation additionalInfo = null;
	
	public AdditionalTypeInformationAttribute(AdditionalCaesarTypeInformation additionalInfo){
		super(AttributeName, objectToByteArray(additionalInfo));
	}
	
	/**
	 * Read from a input stream
	 * @param in	The input stream
	 * @param cp	The constant pool to be used
	 */
	public AdditionalTypeInformationAttribute(DataInput in, ConstantPool cp) throws IOException {
		super( new AsciiConstant(AttributeName), in, cp );
		additionalInfo = (AdditionalCaesarTypeInformation)byteArrayToObject(getData());
	}

    public AdditionalCaesarTypeInformation getTypeInformation() {
        return additionalInfo;
    }
	
}
