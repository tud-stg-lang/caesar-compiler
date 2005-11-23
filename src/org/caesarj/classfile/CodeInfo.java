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
 * $Id: CodeInfo.java,v 1.6 2005-11-23 14:36:12 gasiunas Exp $
 */

package org.caesarj.classfile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.caesarj.util.InconsistencyException;

/**
 * VMS 4.7.3 : Code Attribute.
 * It contains the Java virtual machine instructions and
 * auxiliary information for a single method.
 *
 * !!! graf 990904 add support for generic attributes.
 */
public class CodeInfo extends Attribute {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Make up a new attribute
   *
   * @param	instructions		array of VM instructions
   * @param	handlers		exception handlers
   * @param	lineNumbers		line number information
   * @param	localVariables		local variable information
   */
  public CodeInfo(Instruction[] instructions,
		  HandlerInfo[] handlers,
		  LineNumberInfo[] lineNumbers,
		  LocalVariableInfo[] localVariables)
  {
    this.instructions = instructions;
    this.handlers = (handlers == null ? new HandlerInfo[0] : handlers);

    this.attributes = new AttributeList(lineNumbers != null ? new LineNumberTable(lineNumbers) : null,
					localVariables != null ? new LocalVariableTable(localVariables) : null);
  }

  /**
   * Make up a new attribute
   *
   * @param	in		the stream to read from
   * @param	cp		the constant pool
   *
   * @exception	java.io.IOException	an io problem has occured
   * @exception	ClassFileFormatException	attempt to
   *					write a bad classfile info
   */
  public CodeInfo(DataInput in, ConstantPool cp)
    throws IOException, ClassFileFormatException
  {
    in.readInt();	// ignore length

    this.maxStack = (short)in.readUnsignedShort();
    this.maxLocals = (short)in.readUnsignedShort();

    this.instructions = InstructionIO.read(in, cp);

    this.handlers = new HandlerInfo[in.readUnsignedShort()];
    for (int i = 0; i < this.handlers.length; i++) {
      this.handlers[i] = new HandlerInfo(in, cp, instructions);
    }

    this.attributes = new AttributeList(in, cp, instructions);

    // compact instructions array
    compactInstructions();
  }

  private void compactInstructions() {
    Instruction[]	sparse = instructions;
    int			length = 0;

    // count elements
    for (int i = 0; i < sparse.length; i++) {
      if (sparse[i] != null) {
	length += 1;
      }
    }

    // rebuild array without holes
    instructions = new Instruction[length];
    for (int i = 0, j = 0; i < sparse.length; i++) {
      if (sparse[i] != null) {
	instructions[j++] = sparse[i];
      }
    }
  }

  /**
   * This constructor is only used by SkippedCodeInfo
   */
  protected CodeInfo() {}

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Transforms the accessors contained in this class.
   * @param	transformer		the transformer used to transform accessors
   */
  public void transformAccessors(AccessorTransformer transformer)
    throws BadAccessorException
  {
    AccessorContainer[]		containers;

    for (int i = 0; i < instructions.length; i++) {
      if (this.instructions[i] instanceof AccessorContainer) {
	((AccessorContainer)this.instructions[i]).transformAccessors(transformer);
      }
    }

    containers = this.handlers;
    if (containers != null) {
      for (int i = 0; i < containers.length; i++) {
	containers[i].transformAccessors(transformer);
      }
    }

    containers = getLineNumbers();
    if (containers != null) {
      for (int i = 0; i < containers.length; i++) {
	containers[i].transformAccessors(transformer);
      }
    }

    try {
	    containers = getLocalVariables();
	    if (containers != null) {
	      for (int i = 0; i < containers.length; i++) {	  
	    	  containers[i].transformAccessors(transformer);
	      }
	    }
	 }
     catch (BadAccessorException e) {
    	 /* in case of error do not generate local variable table */
    	 attributes.remove(ClassfileConstants2.ATT_LOCALVARIABLETABLE);
	 }
  }

  /**
   * Returns the attribute's tag
   */
  /*package*/ public int getTag() {
    return ClassfileConstants2.ATT_CODE;
  }

  /**
   * Returns the space in bytes used by this attribute in the classfile
   */
  /*package*/ protected int getSize() {
    if (codeLength == -1) {
      throw new InconsistencyException("code length not yet computed");
    }

    int size = 2 + 4 + 2 + 2 + 4 + codeLength + 2 + 2 + 8 * handlers.length + attributes.getSize();

    return size;
  }

  /**
   * Returns the instruction of code
   */
  public Instruction[] getInstructions() {
    return instructions;
  }

  /**
   * Returns handlers
   */
  public HandlerInfo[] getHandlers() {
    return handlers;
  }

  /**
   * Returns line number information
   */
  public LineNumberInfo[] getLineNumbers() {
    Attribute		attr = attributes.get(ClassfileConstants2.ATT_LINENUMBERTABLE);

    return attr == null ? null : ((LineNumberTable)attr).getLineNumbers();
  }

  /**
   * Returns local variable information
   */
  public LocalVariableInfo[] getLocalVariables() {
    Attribute		attr = attributes.get(ClassfileConstants2.ATT_LOCALVARIABLETABLE);

    return attr == null ? null : ((LocalVariableTable)attr).getLocalVariables();
  }

  /**
   * Returns the length in bytes of the instruction array.
   */
  public int getCodeLength() {
    return codeLength;
  }

  /**
   * Returns the highest value reached by the stack.
   */
  public int getMaxStack() {
    return maxStack;
  }

  /**
   * Returns the number of locals vars used in this method (including parameters).
   */
  public int getMaxLocals() {
    return maxLocals;
  }

  /**
   * Sets the number of parameters for this method.
   */
  public void setParameterCount(int paramCnt) {
    this.paramCnt = paramCnt;
  }

  /**
   * Gets the number of parameters for this method.
   */
  public int getParameterCount() {
    return paramCnt;
  }

  /**
   * Sets the length in bytes of the instruction array.
   */
  /*package*/ void setCodeLength(int codeLength) {
    this.codeLength = codeLength;
  }

  /**
   * Sets the highest value reached by the stack.
   */
  /*package*/ void setMaxStack(int maxStack) {
    this.maxStack = maxStack;
  }

  /**
   * Sets the number of locals var used by this method.
   */
  /*package*/ void setMaxLocals(int maxLocals) {
    this.maxLocals = maxLocals;
  }

  // --------------------------------------------------------------------
  // WRITE
  // --------------------------------------------------------------------

  /**
   * Insert or check location of constant value on constant pool
   *
   * @param	cp		the constant pool for this class
   */
  /*package*/ protected void resolveConstants(ConstantPool cp) throws ClassFileFormatException {
    cp.addItem(attr);

    for (int i = 0; i < instructions.length; i++) {
      instructions[i].resolveConstants(cp);
    }

    for (int i = 0; i < handlers.length; i++) {
      handlers[i].resolveConstants(cp);
    }

    attributes.resolveConstants(cp);

    CodeEnv.check(this);
  }

  /**
   * Write this class into the the file (out) getting data position from
   * the constant pool
   *
   * @param	cp		the constant pool that contain all data
   * @param	out		the file where to write this object info
   *
   * @exception	java.io.IOException	an io problem has occured
   * @exception	ClassFileFormatException	attempt to
   *					write a bad classfile info
   */
  /*package*/ protected void write(ConstantPool cp, DataOutput out)
    throws IOException, ClassFileFormatException
  {
    out.writeShort(attr.getIndex());
    out.writeInt(getSize() - 6);

    if (maxStack == -1) {
      throw new ClassFileFormatException("maxStack not set");
    }
    if (maxLocals == -1) {
      throw new ClassFileFormatException("maxLocals not set");
    }
    if (paramCnt == -1) {
      throw new ClassFileFormatException("paramCnt not set");
    }

    if (codeLength > ClassfileConstants2.MAX_CODE_PER_METHOD) {
      throw new InstructionOverflowException("Method exceeds maxium of allowed instructions");
    }
    // JVMS 4.10  Limitations of the Java Virtual Machine
    // The greatest number of local variables in the local variables 
    // array of a frame created upon invocation of a method is limited 
    // to 65535 by the size of the max_locals item of the Code attribute 
    // giving the code of the method.    
    if (Math.max(paramCnt, maxLocals) > MAX_LOCAL_VARIABLES) {
      throw new LocalVariableOverflowException("Method exceeds maximum of local variables");
    }


    out.writeShort(maxStack);
    out.writeShort(Math.max(paramCnt, maxLocals));

    out.writeInt(codeLength);
    for (int i = 0; i < instructions.length; i++) {
      instructions[i].write(cp, out);
    }

    out.writeShort(handlers.length);
    for (int i = 0; i < handlers.length; i++) {
      handlers[i].write(cp, out);
    }

    attributes.write(cp, out);
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  public static int MAX_LOCAL_VARIABLES = 65535;
  public static final CodeInfo		DUM_INFO = new CodeInfo();

  private static AsciiConstant		attr = new AsciiConstant("Code");
  private Instruction[]			instructions;
  private HandlerInfo[]			handlers;

  private int				paramCnt = -1;
  private int				maxStack = -1;
  private int				maxLocals = -1;
  private int				codeLength = -1;

  private AttributeList			attributes;
}
