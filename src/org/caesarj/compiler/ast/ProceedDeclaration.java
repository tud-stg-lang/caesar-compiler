package org.caesarj.compiler.ast;

import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CBinaryTypeContext;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;

/**
 * Type comment.
 * 
 * @author J?rgen Hallpap
 */
public class ProceedDeclaration extends FjMethodDeclaration {

	/** The name of the enclosing advice-method.*/
	private String adviceName;

	/**
	 * Constructor for CaesarProceedMethodDeclaration.
	 * @param where
	 * @param returnType
	 * @param ident
	 * @param parameters
	 * @param adviceName
	 */
	public ProceedDeclaration(
		TokenReference where,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		String adviceName) {
		super(
			where,
			ACC_STATIC,
			new CTypeVariable[0],
			returnType,
			ident,
			parameters,
			new CReferenceType[0],
			new JBlock(where, JBlock.EMPTY, new JavaStyleComment[0]),
			null,
			new JavaStyleComment[0]);

		this.adviceName = adviceName;
	}

	/**
	 * @see familyj.compiler.FjMethodDeclaration#checkInterface1(CClassContext)
	 */
	public CSourceMethod checkInterface1(CClassContext context)
		throws PositionedError {

		
		try {
			for (int i = 0; i < typeVariables.length; i++) {
				typeVariables[i].checkType(context);
				typeVariables[i].setMethodTypeVariable(true);
			}

			CType[] parameterTypes = new CType[parameters.length];
			CBinaryTypeContext typeContext =
				new CBinaryTypeContext(
					context.getClassReader(),
					context.getTypeFactory(),
					context,
					typeVariables,
					(modifiers & ACC_STATIC) == 0);

			returnType = returnType.checkType(typeContext);
			for (int i = 0; i < parameterTypes.length; i++) {
				parameterTypes[i] = parameters[i].checkInterface(typeContext);
			}

			for (int i = 0; i < exceptions.length; i++) {
				exceptions[i] =
					(CReferenceType) exceptions[i].checkType(typeContext);
			}

			FjFamily[] families = new FjFamily[parameterTypes.length];
			for (int i = 0; i < families.length; i++) {
				families[i] = ((FjFormalParameter) parameters[i]).getFamily();
			}

			setInterface(
				new Proceed(
					context.getCClass(),
					ident,
					returnType,
					parameterTypes,
					adviceName,
					families));

			return (CSourceMethod) getMethod();
		} catch (UnpositionedError cue) {
			throw cue.addPosition(getTokenReference());
		}

	}

	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkBody1(CClassContext)
	 */
	public void checkBody1(CClassContext context) throws PositionedError {
		//do nothing, the body is not important, it will be generated during code generation		
	}

}
