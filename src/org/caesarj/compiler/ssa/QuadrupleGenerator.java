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
 * $Id: QuadrupleGenerator.java,v 1.3 2005-01-24 16:52:57 aracic Exp $
 */

package org.caesarj.compiler.ssa;

import org.caesarj.classfile.ClassRefInstruction;
import org.caesarj.classfile.ClassfileConstants2;
import org.caesarj.classfile.FieldRefInstruction;
import org.caesarj.classfile.IincInstruction;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.InvokeinterfaceInstruction;
import org.caesarj.classfile.JumpInstruction;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.MethodRefInstruction;
import org.caesarj.classfile.MultiarrayInstruction;
import org.caesarj.classfile.NewarrayInstruction;
import org.caesarj.classfile.NoArgInstruction;
import org.caesarj.classfile.PushLiteralInstruction;
import org.caesarj.classfile.SwitchInstruction;
import org.caesarj.util.InconsistencyException;

/**
 * Class to generate quadruple instruction with classfile instructions.
 */
public class QuadrupleGenerator {
    // -------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------
    /**
     * Create a generator of quadruple instructions.
     *
     * @param nbVarSource number of local variables used in the source code
     */
    public QuadrupleGenerator(int nbVarSource) {
	generateVar = new GenerateQVar(nbVarSource);
    }

    // -------------------------------------------------------------------
    // ACCESSOR
    // -------------------------------------------------------------------
    /**
     * Get the number of variables used
     *
     * @return the number of variables used
     */
    public int getVarNumber() {
	return generateVar.getVarNumber();
    }

    // -------------------------------------------------------------------
    // GENERATION
    // -------------------------------------------------------------------
    /**
     * Generate the quadruple instructions correspondind to the classfile
     * instruction inst.
     *
     * @param inst instruction to convert
     * @param nextInst the next instruction
     *                 null if there is no next instruction in the basic bloc
     * @param bb the basic bloc in which the quadruple instruction is inserted
     * @return true iff the nextInst has been generated with inst.
     */
    public boolean generate(Instruction inst, Instruction nextInst,
			 BasicBlock bb) {
	/*
	 * I put variable and constant on the stack without using
	 * stack variable for load instructions.
	 * But if an instruction modify a variable on a stack, I add
	 * before this instruction an affectation, and replace the
	 * variable by a stack variable on the stack.
	 */
	int opcode = inst.getOpcode();

	if (inst.getPushedOnStack() <= 0) {
	    //operation wihout result put on the stack.

 	    if (inst instanceof SwitchInstruction) {
		bb.addInstruction(new QSwitch((SwitchInstruction) inst,
					      generateVar.pop()));
	    } else if (inst instanceof JumpInstruction) {
		switch (inst.getStack()) {
		case 0://goto
		    bb.addInstruction(new QJump(bb.getDefaultNext()));
		    break;
		case -1://condition with one operand
		    QOperand rightOp = new QConstant(new Integer(0), ClassfileConstants2.TYP_INT);
		    int newOpcode = 0;
		    switch (opcode) {
		    case ClassfileConstants2.opc_ifeq:
			newOpcode = ClassfileConstants2.opc_if_icmpeq;
			break;
		    case ClassfileConstants2.opc_ifne:
			newOpcode = ClassfileConstants2.opc_if_icmpne;
			break;
		    case ClassfileConstants2.opc_iflt:
			newOpcode = ClassfileConstants2.opc_if_icmplt;
			break;
		    case ClassfileConstants2.opc_ifge:
			newOpcode = ClassfileConstants2.opc_if_icmpge;
			break;
		    case ClassfileConstants2.opc_ifgt:
			newOpcode = ClassfileConstants2.opc_if_icmpgt;
			break;
		    case ClassfileConstants2.opc_ifle:
			newOpcode = ClassfileConstants2.opc_if_icmple;
			break;
		    case ClassfileConstants2.opc_ifnull:
			rightOp = new QConstant(); //null
			newOpcode = ClassfileConstants2.opc_if_acmpeq;
			break;
		    case ClassfileConstants2.opc_ifnonnull:
			rightOp = new QConstant(); //null
			newOpcode = ClassfileConstants2.opc_if_acmpne;
			break;
		    default:
			throw new InconsistencyException("INVALID UNARY CONDITIONAL JUMP");
		    }
		    bb.addInstruction(new QConditionalJump(bb.getConditionNext(),
							   bb.getDefaultNext(),
							   generateVar.pop(), rightOp,
							   newOpcode));
		    break;
		case -2://condition with two operands
		    QOperand op2 = generateVar.pop();
		    QOperand op1 = generateVar.pop();
		    bb.addInstruction(new QConditionalJump(bb.getConditionNext(),
							   bb.getDefaultNext(),
							   op1, op2, opcode));
		    break;
		}
	    } else if (inst instanceof FieldRefInstruction) {
		//putstatic or putfield
		QOperand value = generateVar.pop();
		QOperand ref = null;
		if (opcode == ClassfileConstants2.opc_putfield) {
		    ref = generateVar.pop();
		}
		bb.addInstruction(new QPutField(((FieldRefInstruction)
						 inst).getFieldRefConstant(),
						ref, value, opcode));
	    } else if (inst instanceof InvokeinterfaceInstruction) {
		InvokeinterfaceInstruction inter = (InvokeinterfaceInstruction) inst;

		int nbParam = nbParam(inter);
		QOperand[] ops = new QOperand[nbParam];
		for (int i = nbParam - 1 ; i >= 0; --i)
		    ops[i] = generateVar.pop();
		bb.addInstruction(new QVoidMethodCall(inter.getInterfaceConstant(),
						      ops, opcode, inter.getNbArgs()));
	    } else if (inst instanceof MethodRefInstruction) {
		MethodRefInstruction meth = (MethodRefInstruction) inst;
		int nbParam = nbParam(meth);
		QOperand[] ops = new QOperand[nbParam];
		for (int i = nbParam - 1 ; i >= 0; --i)
		    ops[i] = generateVar.pop();
		bb.addInstruction(new QVoidMethodCall(meth.getMethodRefConstant(),
						      ops, opcode));
	    } else if (inst instanceof NoArgInstruction) {
		switch(opcode) {
		case ClassfileConstants2.opc_dastore:
		case ClassfileConstants2.opc_lastore:
		case ClassfileConstants2.opc_aastore:
		case ClassfileConstants2.opc_bastore:
		case ClassfileConstants2.opc_castore:
		case ClassfileConstants2.opc_fastore:
		case ClassfileConstants2.opc_iastore:
		case ClassfileConstants2.opc_sastore:
		    QOperand value = generateVar.pop();
		    QOperand index = generateVar.pop();
		    QOperand array = generateVar.pop();
		    bb.addInstruction(new QPutArray(array, index, value, opcode));
		    break;

		case ClassfileConstants2.opc_return:
		    bb.addInstruction(new QReturn());
		    break;

		case ClassfileConstants2.opc_dreturn:
		case ClassfileConstants2.opc_lreturn:
		case ClassfileConstants2.opc_ireturn:
		case ClassfileConstants2.opc_areturn:
		case ClassfileConstants2.opc_freturn:
		    bb.addInstruction(new QReturn(generateVar.pop(), opcode));
		    break;

		case ClassfileConstants2.opc_athrow:
		    bb.addInstruction(new QThrow(generateVar.pop()));
		    break;

		case ClassfileConstants2.opc_monitorenter:
		case ClassfileConstants2.opc_monitorexit:
		    bb.addInstruction(new QMonitor(generateVar.pop(), opcode));
		    break;

		case ClassfileConstants2.opc_pop:
		    generateVar.pop();
		    break;

		case ClassfileConstants2.opc_pop2:
		    generateVar.pop2();
		    break;

		case ClassfileConstants2.opc_nop:
		    /* Nothing to generate by definition */
		    break;
		}
	    } else if (inst instanceof LocalVarInstruction) {
		LocalVarInstruction lvi = (LocalVarInstruction) inst;
		if (lvi.isStore()) {
		    QVar var = generateVar.getVar(lvi.getIndex(),
						  lvi.getOperandType());

		    //verify that the variable is not in the stack
		    verifyVarNotInStack(var, bb);
		    //add the affectation
		    bb.addInstruction(
			    new QAssignment(var,
					new QSimpleExpression(generateVar.pop())));
		} else { //ret
		    bb.addInstruction(
			new QRet(generateVar.getVar(lvi.getIndex(),
						    lvi.getOperandType())));
		}
	    } else if (inst instanceof IincInstruction) {
		IincInstruction iinc = (IincInstruction) inst;
		QVar var = generateVar.getVar(iinc.getVariable(),
					      ClassfileConstants2.TYP_INT);
		//verify that the variable is not in the stack
		verifyVarNotInStack(var, bb);

		bb.addInstruction(
		  new QAssignment(var,
		      new QBinaryOperation(var,
			    new QConstant(new Integer(iinc.getIncrement()),
					  ClassfileConstants2.TYP_INT),
			    opcode)));

	    } else if (inst instanceof ClassRefInstruction) {//checkcast
		ClassRefInstruction cri = (ClassRefInstruction) inst;
		QOperand obj = generateVar.pop();
		bb.addInstruction(new QAssignment(generateVar.push(ClassfileConstants2.TYP_REFERENCE),
					      new QCheckCast(cri.getClassConstant(), obj)));
	    } else {
		throw new InconsistencyException("Instruction inconnue " +
						 opcode + " " +
						 inst);
	    }

	} else { //operation with a result.
	    QExpression expr = null;
	    if (inst instanceof LocalVarInstruction) {// a load
		//I put the variable on the stack
		LocalVarInstruction lvi = (LocalVarInstruction) inst;
		generateVar.push(generateVar.getVar(lvi.getIndex(),
						    lvi.getOperandType()));
	    } else if (inst instanceof NoArgInstruction) {
		switch (opcode) {
		case ClassfileConstants2.opc_freturn://should not be here
		    //it's maybe a bug in NoArgInstruction.
		    bb.addInstruction(new QReturn(generateVar.pop(), opcode));
		    break;


		case ClassfileConstants2.opc_dcmpg:
		case ClassfileConstants2.opc_dcmpl:
		case ClassfileConstants2.opc_lcmp:
		case ClassfileConstants2.opc_fcmpg:
		case ClassfileConstants2.opc_fcmpl:
		case ClassfileConstants2.opc_fadd:
		case ClassfileConstants2.opc_fmul:
		case ClassfileConstants2.opc_fsub:
		case ClassfileConstants2.opc_iadd:
		case ClassfileConstants2.opc_imul:
		case ClassfileConstants2.opc_isub:
		case ClassfileConstants2.opc_ishl:
		case ClassfileConstants2.opc_ishr:
		case ClassfileConstants2.opc_iushr:
		case ClassfileConstants2.opc_iand:
		case ClassfileConstants2.opc_ior:
		case ClassfileConstants2.opc_ixor:
		case ClassfileConstants2.opc_dadd:
		case ClassfileConstants2.opc_dmul:
		case ClassfileConstants2.opc_dsub:
		case ClassfileConstants2.opc_ladd:
		case ClassfileConstants2.opc_land:
		case ClassfileConstants2.opc_lmul:
		case ClassfileConstants2.opc_lor:
		case ClassfileConstants2.opc_lsub:
		case ClassfileConstants2.opc_lxor:
		case ClassfileConstants2.opc_lshl:
		case ClassfileConstants2.opc_lshr:
		case ClassfileConstants2.opc_lushr:
		case ClassfileConstants2.opc_idiv:
		case ClassfileConstants2.opc_irem:
		case ClassfileConstants2.opc_fdiv:
		case ClassfileConstants2.opc_frem:
		case ClassfileConstants2.opc_ddiv:
		case ClassfileConstants2.opc_drem:
		case ClassfileConstants2.opc_ldiv:
		case ClassfileConstants2.opc_lrem:
		    QOperand op2 = generateVar.pop();
		    QOperand op1 = generateVar.pop();
		    expr = new QBinaryOperation(op1, op2, opcode);
		    break;
		case ClassfileConstants2.opc_d2i:
		case ClassfileConstants2.opc_l2i:
		case ClassfileConstants2.opc_f2i:
		case ClassfileConstants2.opc_i2b:
		case ClassfileConstants2.opc_i2c:
		case ClassfileConstants2.opc_i2s:
		case ClassfileConstants2.opc_ineg:
		case ClassfileConstants2.opc_d2f:
		case ClassfileConstants2.opc_l2f:
		case ClassfileConstants2.opc_i2f:
		case ClassfileConstants2.opc_fneg:
		case ClassfileConstants2.opc_d2l:
		case ClassfileConstants2.opc_f2l:
		case ClassfileConstants2.opc_i2l:
		case ClassfileConstants2.opc_lneg:
		case ClassfileConstants2.opc_l2d:
		case ClassfileConstants2.opc_f2d:
		case ClassfileConstants2.opc_i2d:
		case ClassfileConstants2.opc_dneg:
		    QOperand op = generateVar.pop();
		    expr = new QUnaryOperation(op, opcode);
		    break;

		case ClassfileConstants2.opc_aconst_null:
		    //put the constant on the stack.
		    generateVar.push(new QConstant());
			//expr = new QSimpleExpression(new QConstant());
		    break;

		case ClassfileConstants2.opc_aaload:
		case ClassfileConstants2.opc_baload:
		case ClassfileConstants2.opc_caload:
		case ClassfileConstants2.opc_faload:
		case ClassfileConstants2.opc_iaload:
		case ClassfileConstants2.opc_saload:
		case ClassfileConstants2.opc_daload:
		case ClassfileConstants2.opc_laload:
		    QOperand index = generateVar.pop();
		    QOperand array = generateVar.pop();
		    expr = new QGetArray(array, index,
				   ((NoArgInstruction) inst).getReturnType(),
				   opcode);
		    break;

		case ClassfileConstants2.opc_arraylength:
		    QOperand arr = generateVar.pop();
		    expr = new QArrayLength(arr);
		    break;

		case ClassfileConstants2.opc_dup:
		    generateVar.dup();
		    break;

		case ClassfileConstants2.opc_dup_x1:
		    generateVar.dup_x1();
		    break;

		case ClassfileConstants2.opc_dup_x2:
		    generateVar.dup_x2();
		    break;

		case ClassfileConstants2.opc_dup2:
		    generateVar.dup2();
		    break;

		case ClassfileConstants2.opc_dup2_x1:
		    generateVar.dup2_x1();
		    break;

		case ClassfileConstants2.opc_dup2_x2:
		    generateVar.dup2_x2();
		    break;

		case ClassfileConstants2.opc_swap:
		    generateVar.swap();
		    break;

		default:
		    throw new InconsistencyException("Instruction Not Used " +
						     opcode + " " +
						     inst);
		}
	    } else if (inst instanceof PushLiteralInstruction) {
		//put the constant on the stack.
		PushLiteralInstruction pli = (PushLiteralInstruction) inst;
		//expr = new QSimpleExpression(new QConstant(pli.getLiteral(), pli.getReturnType()));
		generateVar.push(new QConstant(pli.getLiteral(), pli.getReturnType()));
	    } else if (inst instanceof InvokeinterfaceInstruction) {
		InvokeinterfaceInstruction inter = (InvokeinterfaceInstruction) inst;

		int nbParam = nbParam(inter);
		QOperand[] ops = new QOperand[nbParam];
		for (int i = nbParam - 1 ; i >= 0; --i)
		    ops[i] = generateVar.pop();
		expr = new QMethodReturn(inter.getInterfaceConstant(), ops,
					 inter.getReturnType(), opcode,
					 inter.getNbArgs());
	    } else if (inst instanceof MethodRefInstruction) {
		MethodRefInstruction meth = (MethodRefInstruction) inst;
		int nbParam = nbParam(meth);
		QOperand[] ops = new QOperand[nbParam];
		for (int i = nbParam - 1 ; i >= 0; --i)
		    ops[i] = generateVar.pop();
		expr = new QMethodReturn(meth.getMethodRefConstant(),
					 ops, meth.getReturnType(), opcode);
	    } else if (inst instanceof FieldRefInstruction) {
		//getstatic or getfield
		QOperand ref = null;
		if (opcode == ClassfileConstants2.opc_getfield) {
		    ref = generateVar.pop();
		}
		FieldRefInstruction fri = (FieldRefInstruction) inst;
		expr = new QGetField(fri.getFieldRefConstant(), ref,
				     fri.getReturnType(),
				     opcode);
	    } else if (inst instanceof ClassRefInstruction) {
		//instanceof, new or anewarray
		ClassRefInstruction cr = (ClassRefInstruction) inst;
		switch (opcode) {
		case ClassfileConstants2.opc_instanceof:
		    expr = new QInstanceOf(generateVar.pop(),
					   cr.getClassConstant());
		    break;
		case ClassfileConstants2.opc_new:
		    expr = new QNew(cr.getClassConstant());
		    break;
		case ClassfileConstants2.opc_anewarray:
		    expr = new QANewArray(cr.getClassConstant(),
					  generateVar.pop());
		    break;

		default:
		    throw new InconsistencyException("Instruction Not Used " +
						     opcode + " " +
						     inst);
		}
	    } else if (inst instanceof NewarrayInstruction) {
		NewarrayInstruction nai = (NewarrayInstruction) inst;
		expr = new QNewArray(nai, generateVar.pop());
	    } else if (inst instanceof MultiarrayInstruction) {
		MultiarrayInstruction mai = (MultiarrayInstruction) inst;
		QOperand[] dims = new QOperand [mai.getDimension()];
		for (int i = dims.length - 1; i >= 0; --i) {
		    dims[i] = generateVar.pop();
		}
		expr = new QMultiArray(mai, dims);
	    } else if (inst instanceof JumpInstruction) {//jsr
		bb.addInstruction(new QJsr(bb.getDefaultNext()));
	    } else {
		throw new InconsistencyException("Unknown Instruction" +
						 opcode + " " +
						 inst);
	    }

	    //expr has been calculated here
	    if (expr != null) {
		if (nextInst != null &&
		    nextInst instanceof LocalVarInstruction &&
		    ((LocalVarInstruction)nextInst).isStore()) {
		    LocalVarInstruction lvi = (LocalVarInstruction) nextInst;
		    QVar var = generateVar.getVar(lvi.getIndex(),
						  lvi.getOperandType());

		    //verify that the variable is not in the stack
		    verifyVarNotInStack(var, bb);

		    bb.addInstruction(new QAssignment(var, expr));
		    return true;
		} else {
		    bb.addInstruction(
		       new QAssignment(generateVar.push(expr.getType()), expr));
		}
	    } else {//expr is null for stack manipulation instructions
		//System.out.println(opcode + " " + inst);
	    }

	}
	return false;
    }

    /**
     * Initialize the entry stack of a basic block with the same
     * parameters of those in the stack array
     *
     * @param bb the basic block.
     * @param stack the array containing the type of the element
     *  in the entry stack
     */
    public void initEntryStack(BasicBlock bb, QOperand[] stack) {
	QOperand[] entryStack = new QOperand[stack.length];
	for (int i = 0; i < stack.length; ++i) {
	    entryStack[i] = generateVar.getNewVar(stack[i].getType());
	}
	bb.setEntryStack(entryStack);
    }

    /**
     * Add all element from the array stack in the current stack
     *
     * @param stack array of elements to put on the stack
     */
    public void setStack(QOperand[] stack) {
	if (stack == null)
	    return;
	for (int i = 0; i < stack.length; ++i) {
	    generateVar.push(stack[i]);
	}
    }

    /**
     * Get the current stack, and remove all elements from it
     *
     * @return the elements on the stack
     */
    public QOperand[] getStack() {
	return generateVar.removeElements();
    }

    /**
     * Add instructions in a basic block to convert the
     * variable in an array to variables in other array
     *
     * Precondition : from.length = to.length
     *
     * @param bb the block in which the instructions are added
     * @param from
     * @param to
     */
    public void addVariableConversionInstruction(BasicBlock bb,
						 QOperand[] from,
						 QOperand[] to) {
	//TO ADD : size test
	for (int i = 0; i < from.length; ++i) {
	    bb.addInstruction(new QAssignment(to[i], new QSimpleExpression(from[i])));
	}
    }

    /**
     * To add an instruction to initialize a catch block.
     *
     * @param bb the catch block.
     */
    public void addInitException(BasicBlock bb) {
	bb.addInstruction(new QDeclareInitialised(generateVar.push(ClassfileConstants2.TYP_REFERENCE),
						  true));
    }

    /**
     * To add an instruction to initialize a catch block with
     * a given variable
     *
     * @param bb the catch block.
     * @param inst the store instruction
     */
    public void addInitException(BasicBlock bb, Instruction inst) {
	if (!(inst instanceof LocalVarInstruction) ||
	    !((LocalVarInstruction) inst).isStore()) {
		throw new InconsistencyException("Instruction in catch block" +
						 " must be a store");
	}
	int register = ((LocalVarInstruction) inst).getIndex();
	bb.addInstruction(new QDeclareInitialised(generateVar.getVar(register, ClassfileConstants2.TYP_REFERENCE), true));
    }

    /**
     * To add an instruction to initialize a subroutine.
     *
     * @param bb the first block of the subroutine.
     */
    public void addInitSubroutine(BasicBlock bb) {
	bb.addInstruction(new QDeclareInitialised(generateVar.push(ClassfileConstants2.TYP_ADDRESS),
						  true));
    }

    /**
     * To add an instruction to initialize a subroutine with
     * a given variable.
     *
     * @param bb the first block of the subroutine.
     * @param inst the store instruction
     */
    public void addInitSubroutine(BasicBlock bb, Instruction inst) {
	if (!(inst instanceof LocalVarInstruction) ||
	    !((LocalVarInstruction) inst).isStore()) {
		throw new InconsistencyException("Instruction in catch block" +
						 " must be a store");
	}
	int register = ((LocalVarInstruction) inst).getIndex();
	bb.addInstruction(new QDeclareInitialised(generateVar.getVar(register, ClassfileConstants2.TYP_ADDRESS), true));
    }


    /**
     * Add a jump instruction to the default next bloc
     *
     * @param bb the block to add the jump.
     */
    public void addJump(BasicBlock bb) {
	bb.addInstruction(new QJump(bb.getDefaultNext()));
    }

    /**
     * Count the number of parameter of a method (including the object
     * if it's an instance method).
     *
     * @param method the method
     */
    public int nbParam(MethodRefInstruction method) {
	int nb = nbParam(method.getMethodRefConstant().getType());
	if (method.getOpcode() != ClassfileConstants2.opc_invokestatic) {
	    nb += 1; //the reference of the object
	}
	return nb;
    }

    /**
     * Count the number of parameter of an interface method
     *
     * @param method the method
     */
    public int nbParam(InvokeinterfaceInstruction method) {
	int nb = nbParam(method.getInterfaceConstant().getType());
	return nb + 1;
    }

    /**
     * Verify that a variable is not in the stack.
     * If the variable is in the stack, a new affectation is added
     * to the basic block from this variable to a new stack variable.
     * Then all occurences of the variable on the stack are replaced
     * by this new variable
     *
     * @param var variable searched
     * @param bb basic block to add an affectation if necessary.
     */
    protected void verifyVarNotInStack(QVar var, BasicBlock bb) {
	QVar varOnStack = generateVar.findVariableOnStack(var.getRegister());
	//if the variable defined is on the stack
	if (varOnStack != null) {
	    QOperand stackVar = generateVar.getNewVar(varOnStack.getType());
	    //we add an affectation to a stack variable
	    bb.addInstruction(new QAssignment(stackVar, new QSimpleExpression(varOnStack)));
	    //we replace on the stack the variable by
	    // the stack variable
	    generateVar.replaceVar(var.getRegister(), stackVar);
	}
    }

    /**
     * Count the number of parameter in a method signature
     *
     * @param signature the signature
     */
    protected int nbParam(String signature) {
	int nb = 0;

	if (signature.charAt(0) != '(') {
	    throw new InconsistencyException("invalid signature " + signature);
	}

	int pos = 1;
    _method_parameters_:
	for (;;) {
	    switch (signature.charAt(pos++)) {
	    case ')':
		break _method_parameters_;

	    case '[':
		while (signature.charAt(pos) == '[') {
		    pos += 1;
		}
		if (signature.charAt(pos) == 'L') {
		    while (signature.charAt(pos) != ';') {
			pos += 1;
		    }
		}
		pos += 1;

		nb +=1;
		break;

	    case 'L':
		while (signature.charAt(pos) != ';') {
		    pos += 1;
		}
		pos += 1;

		nb += 1;
		break;

	    case 'Z':
	    case 'B':
	    case 'C':
	    case 'S':
	    case 'F':
	    case 'I':
	    case 'D':
	    case 'J':
		nb += 1;
		break;

	    default:
		throw new InconsistencyException("invalid signature " + signature);
	    }
	}
	return nb;
    }


    // -------------------------------------------------------------------
    // ATTRIBUTS
    // -------------------------------------------------------------------
    protected GenerateQVar generateVar;
}
