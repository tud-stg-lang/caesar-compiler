package org.caesarj.compiler.ast;

import org.caesarj.classfile.ClassFileFormatException;
import org.caesarj.classfile.ClassRefInstruction;
import org.caesarj.classfile.CodeInfo;
import org.caesarj.classfile.Instruction;
import org.caesarj.classfile.LocalVarInstruction;
import org.caesarj.classfile.MethodInfo;
import org.caesarj.classfile.PushLiteralInstruction;
import org.caesarj.compiler.codegen.CodeSequence;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.optimize.BytecodeOptimizer;
import org.caesarj.compiler.types.CBooleanType;
import org.caesarj.compiler.types.CByteType;
import org.caesarj.compiler.types.CCharType;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CDoubleType;
import org.caesarj.compiler.types.CFloatType;
import org.caesarj.compiler.types.CIntType;
import org.caesarj.compiler.types.CLongType;
import org.caesarj.compiler.types.CPrimitiveType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CShortType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;

/**
 * The export for the proceed-method.
 * 
 * @author Jürgen Hallpap
 */
public class Proceed extends FjSourceMethod implements CaesarConstants {

	private String adviceName;

	/**
	 * Constructor for CaesarProceedSourceMethod.
	 * @param owner
	 * @param modifiers
	 * @param ident
	 * @param returnType
	 * @param paramTypes
	 * @param exceptions
	 * @param typeVariables
	 * @param deprecated
	 * @param synthetic
	 * @param body
	 * @param families
	 */
	public Proceed(
		CClass owner,
		String ident,
		CType returnType,
		CType[] parameterTypes,
		String adviceName,
		FjFamily[] families) {
		super(
			owner,
			ACC_STATIC,
			ident,
			returnType,
			parameterTypes,
			new CReferenceType[0],
			new CTypeVariable[0],
			false,
			true,
			null,
			families);

		this.adviceName = adviceName;
	}

	/**
	 * @see org.caesarj.kjc.CSourceMethod#genMethodInfo(BytecodeOptimizer, TypeFactory)
	 */
	public MethodInfo genMethodInfo(
		BytecodeOptimizer optimizer,
		TypeFactory factory)
		throws ClassFileFormatException {
		//		return super.genMethodInfo(optimizer, factory);
		return generateProceedMethod();
	}

	/**
	 * Generates the MethodInfo for the proceedMethod.	
	 * 
	 * @return MethodInfo
	 */
	protected MethodInfo generateProceedMethod() {

		CodeSequence codeSequence = CodeSequence.getCodeSequence();

		int nargs = parameters.length;
		int closureIndex = 0;
		for (int i = 0; i < nargs - 1; i++) {
			closureIndex += slotsNeeded(parameters[i]);
		}
		codeSequence.plantInstruction(
			new LocalVarInstruction(opc_aload, closureIndex));

		Instruction pushLiteralInst = new PushLiteralInstruction(nargs - 1);
		codeSequence.plantInstruction(pushLiteralInst);

		codeSequence.plantNewArrayInstruction(
			new CClassNameType("java/lang/Object"));

		int index = 0;
		for (int i = 0; i < nargs - 1; i++) {
			CType type = parameters[i];
			codeSequence.plantNoArgInstruction(opc_dup);

			pushLiteralInst = new PushLiteralInstruction(i);
			codeSequence.plantInstruction(pushLiteralInst);

			generateLoad(codeSequence, type, index);

			index += slotsNeeded(type);
			if (type.isPrimitive()) {
				codeSequence.plantMethodRefInstruction(
					opc_invokestatic,
					CONVERSIONS_CLASS,
					type.toString() + "Object",
					"(" + type.getSignature() + ")Ljava/lang/Object;");
			}

			codeSequence.plantNoArgInstruction(opc_aastore);
		}

		codeSequence.plantMethodRefInstruction(
			opc_invokevirtual,
			AROUND_CLOSURE_CLASS,
			"run",
			"([Ljava/lang/Object;)Ljava/lang/Object;");

		if (returnType instanceof CPrimitiveType) {

			//consider return type
			codeSequence.plantMethodRefInstruction(
				opc_invokestatic,
				CONVERSIONS_CLASS,
				returnType.toString() + "Value",
				"(Ljava/lang/Object;)" + returnType.getSignature());

		} else if (returnType instanceof CVoidType) {

			codeSequence.plantMethodRefInstruction(
				opc_invokestatic,
				CONVERSIONS_CLASS,
				returnType.toString() + "Value",
				"(Ljava/lang/Object;)Ljava/lang/Object;");

		} else if (returnType instanceof CReferenceType) {

			CReferenceType refType = (CReferenceType) returnType;

			codeSequence.plantInstruction(
				new ClassRefInstruction(
					opc_checkcast,
					refType.getQualifiedName()));

		} else {

			throw new InconsistencyException("return type not handled");

		}

		generateReturn(codeSequence, returnType);

		int modifiers = ACC_STATIC;

		CodeInfo codeInfo =
			new CodeInfo(
				codeSequence.getInstructionArray(),
				codeSequence.getHandlers(),
				codeSequence.getLineNumbers(),
				codeSequence.getLocalVariableInfos());

		MethodInfo proceedMethodInfo =
			new MethodInfo(
				(short) modifiers,
				adviceName + PROCEED_METHOD,
				getProceedMethodSignature(),
				getProceedMethodSignature(),
				new String[0],
				codeInfo,
				false,
				true);

		return proceedMethodInfo;
	}

	private static int slotsNeeded(CType type) {
		if (type instanceof CDoubleType || type instanceof CLongType)
			return 2;
		else
			return 1;
	}

	private final void generateLoad(
		CodeSequence codeSequence,
		CType type,
		int resolvedPosition) {
		// Using dedicated int bytecode
		if (type instanceof CIntType) {

			Instruction inst =
				new LocalVarInstruction(opc_iload, resolvedPosition);
			codeSequence.plantInstruction(inst);

			return;
		}

		// Using dedicated float bytecode
		if (type instanceof CFloatType) {

			Instruction inst =
				new LocalVarInstruction(opc_fload, resolvedPosition);
			codeSequence.plantInstruction(inst);

			return;
		}
		// Using dedicated long bytecode
		if (type instanceof CLongType) {
			Instruction inst =
				new LocalVarInstruction(opc_lload, resolvedPosition);
			codeSequence.plantInstruction(inst);

			return;
		}
		// Using dedicated double bytecode
		if (type instanceof CDoubleType) {
			Instruction inst =
				new LocalVarInstruction(opc_dload, resolvedPosition);
			codeSequence.plantInstruction(inst);

			return;
		}
		// boolean, byte, char and short are handled as int
		if ((type instanceof CByteType)
			|| (type instanceof CCharType)
			|| (type instanceof CBooleanType)
			|| (type instanceof CShortType)) {

			Instruction inst =
				new LocalVarInstruction(opc_iload, resolvedPosition);
			codeSequence.plantInstruction(inst);

			return;
		}

		// Reference object
		Instruction inst = new LocalVarInstruction(opc_aload, resolvedPosition);
		codeSequence.plantInstruction(inst);

	}

	private static void generateReturn(
		CodeSequence codeSequence,
		CType returnType) {
		if (returnType instanceof CVoidType) {
			codeSequence.plantNoArgInstruction(opc_return);
		} else if (returnType instanceof CPrimitiveType) {
			if (returnType instanceof CBooleanType
				|| returnType instanceof CIntType
				|| returnType instanceof CByteType
				|| returnType instanceof CShortType
				|| returnType instanceof CCharType) {
				codeSequence.plantNoArgInstruction(opc_ireturn);
			} else if (returnType instanceof CFloatType) {
				codeSequence.plantNoArgInstruction(opc_freturn);
			} else if (returnType instanceof CLongType) {
				codeSequence.plantNoArgInstruction(opc_lreturn);
			} else if (returnType instanceof CDoubleType) {
				codeSequence.plantNoArgInstruction(opc_dreturn);
			}

		} else {
			codeSequence.plantNoArgInstruction(opc_areturn);
		}
	}

	private String getProceedMethodSignature() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("(");

		CType[] params = getParameters();
		for (int i = 0; i < params.length; i++) {
			buffer.append(params[i].getSignature());
		}

		buffer.append(")");
		buffer.append(returnType.getSignature());

		return buffer.toString();

	}

}
