package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.export.Proceed;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**
 * Type comment.
 * 
 * @author Jürgen Hallpap
 */
public class CjProceedDeclaration extends CjMethodDeclaration {

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
	public CjProceedDeclaration(
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

			/* FJRM
			FjFamily[] families = new FjFamily[parameterTypes.length];			
			for (int i = 0; i < families.length; i++) {
				families[i] = ((FjFormalParameter) parameters[i]).getFamily();
			}
			*/

			setInterface(
				new Proceed(
					context.getCClass(),
					ident,
					returnType,
					parameterTypes,
					adviceName));

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
