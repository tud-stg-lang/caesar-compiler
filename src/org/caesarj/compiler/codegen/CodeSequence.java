/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: CodeSequence.java,v 1.1 2004-02-08 16:47:55 ostermann Exp $
 */

/**
 * Code sequence is used as a bag to hold lists of instructions
 * until it is time to put them into a class file.
 *
 * Note: Local variables are currently not used
 */
package org.caesarj.compiler.codegen;

import java.util.ArrayList;
import java.util.Stack;

import org.caesarj.classfile.AccessorContainer;
import org.caesarj.classfile.AccessorTransformer;
import org.caesarj.classfile.BadAccessorException;
import org.caesarj.classfile.ClassRefInstruction;
import org.caesarj.classfile.FieldRefInstruction;
import org.caesarj.classfile.HandlerInfo;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.InstructionAccessor;
import org.caesarj.classfile.JumpInstruction;
import org.caesarj.classfile.LineNumberInfo;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.LocalVariableInfo;
import org.caesarj.classfile.MethodRefInstruction;
import org.caesarj.classfile.NewarrayInstruction;
import org.caesarj.classfile.NoArgInstruction;
import org.caesarj.compiler.ast.JLocalVariable;
import org.caesarj.compiler.ast.JReturnStatement;
import org.caesarj.compiler.ast.JStatement;
import org.caesarj.compiler.ast.JSynchronizedStatement;
import org.caesarj.compiler.ast.JTryFinallyStatement;
import org.caesarj.compiler.context.GenerationContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.InconsistencyException;

public final class CodeSequence extends org.caesarj.util.Utils implements org.caesarj.compiler.Constants {

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  /**
   * Constructs a code attribute.
   */
  private CodeSequence() {
    instructions = new Instruction[org.caesarj.classfile.Constants.MAX_CODE_PER_METHOD];
    handlers = new ArrayList();
    lines = new ArrayList();
    /* LOCAL VARIABLES NOT USED
    locals = new Hashtable();
    LOCAL VARIABLES NOT USED */
  }

  /**
   * Constructs a code sequence.
   */
  public static CodeSequence getCodeSequence() {
    CodeSequence	seq;
    if (!org.caesarj.compiler.Constants.ENV_USE_CACHE || stack.empty()) {
      seq = new CodeSequence();
    } else {
      seq = (CodeSequence)stack.pop();
    }
    seq.pc = 0;
    seq.labelAtEnd = false;
    seq.lineNumber = 0;
    seq.lastLine = -1;
    return seq;
  }

  /**
   * Release a code sequence
   */
  public void release() {
    if (org.caesarj.compiler.Constants.ENV_USE_CACHE) {
      stack.push(this);
      handlers.clear();
      lines.clear();
      /* LOCAL VARIABLES NOT USED
      seq.locals.clear();
      LOCAL VARIABLES NOT USED */
    }
  }

  public static void endSession() {
    while (!stack.empty()) {
      stack.pop();
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Adds an instruction to the code of the current method.
   *
   * @param	insn		the instruction to append
   */
  public final void plantInstruction(Instruction insn) {
    instructions[pc++] = insn;

    labelAtEnd = false;
    if (lineNumber != lastLine) {
      lastLine = lineNumber;
      lines.add(new LineNumberInfo((short)lineNumber, insn));
    }
  }

  /**
   * Appends an instruction without arguments to the code
   * of the current method.
   *
   * @param	opcode		the instruction opcode
   */
  public final void plantNoArgInstruction(int opcode) {
    plantInstruction(new NoArgInstruction(opcode));
  }

  /**
   * Appends an instruction to the code of the current method
   * which pops the top-most element from the stack.
   *
   * @param	type		the type of the top-most element
   */
  public final void plantPopInstruction(CType type) {
    switch (type.getSize()) {
    case 0:
      verify(type.getTypeID() == TID_VOID);
      break;
    case 1:
      plantNoArgInstruction(opc_pop);
      break;
    case 2:
      plantNoArgInstruction(opc_pop2);
      break;
    default:
      verify(false);
    }
  }

  /**
   * Adds a local var instruction to the code of the current method.
   *
   * @param	opcode		the instruction opcode
   * @param	var		the referenced variable
   */
  public final void plantLocalVar(int opcode, JLocalVariable var) {
    LocalVarInstruction	insn = new LocalVarInstruction(opcode,
						       var.getPosition());
    plantInstruction(insn);
    addLocalVarInfo(insn, var);
  }

  /**
   * Adds a load of this (local var 0) to the code of the current method.
   */
  public final void plantLoadThis() {
    plantInstruction(new LocalVarInstruction(opc_aload, 0));
  }

  /**
   * Adds a field reference instruction to the code of the current method.
   *
   * @param	opcode		the instruction opcode
   * @param	owner		the qualified name of the class containing the field
   * @param	name		the simple name of the referenced field
   * @param	type		the signature of the referenced field
   */
  public final void plantFieldRefInstruction(int opcode,
					     String owner,
					     String name,
					     String type)
  {
    plantInstruction(new FieldRefInstruction(opcode, owner, name, type));
  }

  /**
   * Adds a method reference instruction to the code of the current method.
   *
   * @param	opcode		the instruction opcode
   * @param	owner		the qualified name of the class containing the method
   * @param	name		the simple name of the referenced method
   * @param	type		the signature of the referenced method
   */
  public final void plantMethodRefInstruction(int opcode,
					      String owner,
					      String name,
					      String type)
  {
    plantInstruction(new MethodRefInstruction(opcode, owner, name, type));
  }

  /**
   * Adds a class reference instruction to the code of the current method.
   *
   * @param	opcode		the instruction opcode
   * @param	name		the qualified name of the referenced object
   */
  public final void plantClassRefInstruction(int opcode, String name) {
    plantInstruction(new ClassRefInstruction(opcode, name));
  }

  /**
   * Adds an jump instruction to the code of the current method.
   *
   * @param	opcode		the instruction opcode
   * @param	target		the jump target
   */
  public final void plantJumpInstruction(int opcode, CodeLabel target) {
    plantInstruction(new JumpInstruction(opcode, target));
  }

  /**
   * Appends an array creation instruction to the code of the current method.
   *
   * @param	type		the element type
   */
  public final void plantNewArrayInstruction(CType type) {
    if (type.isReference()) {
      plantClassRefInstruction(opc_anewarray, ((CReferenceType)type).getQualifiedName());
    } else {
      byte	num;

      switch (type.getTypeID()) {
      case TID_BYTE:
	num = 8;
	break;
      case TID_BOOLEAN:
	num = 4;
	break;
      case TID_CHAR:
	num = 5;
	break;
      case TID_SHORT:
	num = 9;
	break;
      case TID_INT:
	num = 10;
	break;
      case TID_LONG:
	num = 11;
	break;
      case TID_FLOAT:
	num = 6;
	break;
      case TID_DOUBLE:
	num = 7;
	break;
      default:
	throw new InconsistencyException();
      }
      plantInstruction(new NewarrayInstruction(num));
    }
  }

  /**
   * Adds an instruction to the code of the current method.
   *
   * @param	insn		the instruction to append
   */
  public final void plantLabel(CodeLabel label) {
    label.setAddress(pc);
    labelAtEnd = true;
  }

  // --------------------------------------------------------------------
  // ENVIRONEMENT
  // --------------------------------------------------------------------

  /**
   * @param	lineNumber		the current line number in source code
   */
  public final void setLineNumber(int lineNumber) {
    if (lineNumber != 0) {
      this.lineNumber = lineNumber;
    }
  }

  /**
   * @return	an array of line number information
   */
  public final LineNumberInfo[] getLineNumbers() {
    return (LineNumberInfo[])lines.toArray(new LineNumberInfo[lines.size()]); 
  }

  /**
   * @return	an array of local vars information
   */
  public final LocalVariableInfo[] getLocalVariableInfos() {
    return null;
    /* LOCAL VARIABLES NOT USED
    if (locals.size() == 0) {
      return null;
    }

    Vector	compress = new Vector();

    Enumeration stacks = locals.elements();
    while (stacks.hasMoreElements()) {
      Stack current = (Stack)stacks.nextElement();
      while (!current.empty()) {
	compress.addElement(((JLocalVariableEntry)current.pop()).getInfo());
      }
    }

    return (LocalVariableInfo[])Utils.toArray(compress, LocalVariableInfo.class);
    LOCAL VARIABLES NOT USED */
  }

  /**
   * Ask the code handler to generate the necessary code to call every
   * finally clause of all try statements
   */
  public final void plantReturn(JReturnStatement ret, GenerationContext context) {
    for (int i = contexts.size() - 1; i >= 0; i--) {
      JStatement stmt = (JStatement)contexts.elementAt(i);

      if (stmt instanceof JTryFinallyStatement) {
	((JTryFinallyStatement)stmt).genFinallyCall(context, ret);
      } else if (stmt instanceof JSynchronizedStatement) {
	((JSynchronizedStatement)stmt).genMonitorExit(context);
      }
    }
  }

  /**
   * Ask the code handler to generate the necessary code to call every
   * finally and monitorexit
   */
  public final void plantBreak(JStatement top, GenerationContext context) {
    for (int i = contexts.size() - 1; i >= 0 && contexts.elementAt(i) != top; i--) {
      JStatement stmt = (JStatement)contexts.elementAt(i);

      if (stmt instanceof JTryFinallyStatement) {
	((JTryFinallyStatement)stmt).genFinallyCall(context, null);
      } else if (stmt instanceof JSynchronizedStatement) {
	((JSynchronizedStatement)stmt).genMonitorExit(context);
      }
    }
  }

  // --------------------------------------------------------------------
  // PUSH CONTEXT
  // --------------------------------------------------------------------

  /**
   * Informs the code handlers that we begin a portion of breakable code.
   */
  public final void pushContext(JStatement stmt) {
    contexts.push(stmt);
  }

  /**
   * Informs the code handlers that we exit a breakable code.
   * Checks that contexts match.
   */
  public final void popContext(JStatement stmt) {
    verify((JStatement)contexts.pop() == stmt);
  }


  // --------------------------------------------------------------------
  // EXCEPTIONS
  // --------------------------------------------------------------------

  /*
   * Adds an exception handler to the code of this method.
   *
   * @param	start		the beginning of the checked area (inclusive)
   * @param	end		the end of the checked area (exclusive !)
   * @param	handler		the entrypoint into the exception handling routine.
   * @param	thrown		the exceptions handled by this routine
   */
  public final void addExceptionHandler(int start,
					int end,
					int handler,
					String thrown)
  {
    // no handler if checked area is empty
    if (start != end) {
      handlers.add(new HandlerInfo(getInstructionAt(start),
					  getInstructionAt(end - 1),	// inclusive !
					  getInstructionAt(handler),
					  thrown));
    }
  }

  /**
   * Returns an array of all exception handler
   */
  public final HandlerInfo[] getHandlers() {
    return (HandlerInfo[])handlers.toArray(new HandlerInfo[handlers.size()]); 
  }

  // --------------------------------------------------------------------
  // PC
  // --------------------------------------------------------------------

  /**
   * Gets the location in code sequence
   */
  public final int getPC() {
    return pc;
  }

  /**
   * Returns the actual size of code (number of instruction)
   */
  public final int size() {
    return pc;
  }

  /**
   * Returns the instruction at a given position
   */
  public final Instruction getInstructionAt(int pc) {
    return instructions[pc];
  }

  /**
   * Return the instruction as a list
   *
   * @param	insn		the instruction to append
   * WARNING: AFTER a call to release() this array will be reused
   */
  public Instruction[] getInstructionArray() {
    // we resolve the labels here, since the array will be reused
    resolveLabels();

    Instruction[]	code = new Instruction[pc];
    System.arraycopy(instructions, 0, code, 0, pc);
    return code;
  }

  private void resolveLabels() {
    // if there is a label planted as last instruction, add a dummy
    // instruction at the end: it will never be reached
    if (labelAtEnd) {
      plantNoArgInstruction(org.caesarj.compiler.Constants.opc_nop);
    }

    try {
      AccessorTransformer	transformer = new AccessorTransformer() {
	  public InstructionAccessor transform(InstructionAccessor accessor,
					       AccessorContainer container)
	  {
	    // the only accessors to resolve are labels
	    return getInstructionAt(((CodeLabel)accessor).getAddress());
	  }
	};

      for (int i = 0; i < pc; i++) {
	if (instructions[i] instanceof AccessorContainer) {
	  ((AccessorContainer)instructions[i]).transformAccessors(transformer);
	}
      }
    } catch (BadAccessorException e) {
      throw new InconsistencyException();
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  /**
   * Add a local variable name information
   */
  private final void addLocalVarInfo(LocalVarInstruction insn,
				     JLocalVariable var) {
/* //  DISABLED, SLOW AND NOT USED AFTER
    Integer		pos = new Integer(var.getLocalIndex());
    Stack		elemsAtPos;
    JLocalVariableEntry	entry = new JLocalVariableEntry(insn, var);

    elemsAtPos  = (Stack)locals.get(pos);
    if (elemsAtPos == null) {
      locals.put(pos, elemsAtPos = new Stack());
      elemsAtPos.push(entry);
    } else if (!((JLocalVariableEntry)elemsAtPos.peek()).merge(entry)) {
      elemsAtPos.push(entry);
    }
*/
  }


  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------

  private Instruction[]			instructions;
  private ArrayList			handlers;
  private ArrayList			lines;
    /* LOCAL VARIABLES NOT USED
  private Hashtable			locals;
    LOCAL VARIABLES NOT USED */
  private int				pc;
  // is a label after the last instruction ?
  private boolean			labelAtEnd;
  private int				lineNumber;
  private int				lastLine;
  private Stack				contexts = new Stack();

  private static final Stack		stack = new Stack();
}
