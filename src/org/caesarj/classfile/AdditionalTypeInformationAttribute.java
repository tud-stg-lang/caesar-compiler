/*
 * Created on 04.06.2004
 *
 */
package org.caesarj.classfile;

import java.io.DataInput;
import java.io.IOException;

/**
 * This attribute stores the value of the generated and implicit flags of a class.
 * @author Karl Klose
 */
public class AdditionalTypeInformationAttribute extends CaesarAttribute {
	
	public static final String AttributeName = AttributePrefix + "AdditionalTypeInformationAttribute";
	
	protected boolean implicit, generated;
	
	public static final int	IMPLICIT = 1, GENERATED = 2;
	
	public boolean isImplicit(){
		return implicit;
	}
	
	public boolean isGenerated(){
		return generated;
	}
	
	
	public AdditionalTypeInformationAttribute( boolean implicit, boolean generated ){
		super(AttributeName, CaesarAttribute.integerToByteArray( 
								(implicit?IMPLICIT:0) + (generated?GENERATED:0))  );
		this.implicit = implicit;
		this.generated = generated;
	}

	
	
	/**
	 * Read from a input stream
	 * @param in	The input stream
	 * @param cp	The constant pool to be used
	 */
	public AdditionalTypeInformationAttribute(DataInput in, ConstantPool cp) throws IOException {
		super( new AsciiConstant(AttributeName), in, cp );
		int intValue = CaesarAttribute.byteArrayToInteger(getData());
		implicit = (intValue&IMPLICIT) != 0;
		generated= (intValue&GENERATED) != 0;
	}
	
}
