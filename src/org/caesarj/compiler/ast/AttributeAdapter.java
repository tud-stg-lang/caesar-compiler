package org.caesarj.compiler.ast;

import java.io.DataOutput;
import java.io.IOException;

import org.aspectj.weaver.AjAttribute;

import org.caesarj.classfile.AsciiConstant;
import org.caesarj.classfile.Attribute;
import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.ConstantPool;

/**
 * Adapts the AjAttributes to the KOPI world.
 * 
 * @author Jürgen Hallpap
 */
public class AttributeAdapter extends Attribute {

	/** The AjAttribute that should be adapted.*/
	private AjAttribute ajAttribute; //adaptee

	/** The name of the attribute.*/
	private AsciiConstant attributeName;

	/**
	 * Method AttributeAdapter.
	 * 
	 * @param adaptee
	 */
	public AttributeAdapter(AjAttribute adaptee) {
		ajAttribute = adaptee;
		attributeName = new AsciiConstant(ajAttribute.getNameString());
	}

	/**
	 * Every class attribute needs a different tag.
	 * 
	 * @return int
	 */
	protected int getTag() {
		//XXX
		return ajAttribute.hashCode();
	}

	/**
	 * Returns the size of the attribute construct.
	 * 
	 * @return int
	 */
	protected int getSize() {
		// 2 byte for the name index
		// 4 byte for the length of the attribute data
		// x byte for the data
		return 2 + 4 + ajAttribute.getBytes().length;
	}

	/**
	 * Adds the attributeName to the constantPool.
	 */
	protected void resolveConstants(ConstantPool cp)
		throws ClassFileFormatException {

		cp.addItem(attributeName);
	}

	/**
	 * @see org.caesarj.classfile.Attribute#write(ConstantPool, DataOutput)
	 */
	protected void write(ConstantPool cp, DataOutput out)
		throws IOException, ClassFileFormatException {
		out.write(ajAttribute.getAllBytes(attributeName.getIndex()));
	}

}
