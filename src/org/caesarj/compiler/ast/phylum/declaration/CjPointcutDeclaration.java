package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;
import java.util.List;

import org.caesarj.compiler.aspectj.CaesarFormalBinding;
import org.caesarj.compiler.aspectj.CaesarMember;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.aspectj.CaesarScope;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.export.FjSourceClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * A Pointcut declaration.
 * 
 * @author Jürgen Hallpap
 */
public class CjPointcutDeclaration extends CjMethodDeclaration {

	public static final CjPointcutDeclaration[] EMPTY =
		new CjPointcutDeclaration[0];

	/**The corresponding Pointcut.*/
	private CaesarPointcut pointcut;

	private boolean checked=false;

	/**
	 * Constructor for PointcutDeclaration.
	 * @param where
	 * @param modifiers
	 * @param typeVariables
	 * @param returnType
	 * @param ident
	 * @param parameters
	 * @param exceptions
	 * @param body
	 * @param javadoc
	 * @param comments
	 */
	public CjPointcutDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		JavadocComment javadoc,
		CaesarPointcut pointcut) {
		super(
			where,
			modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			CReferenceType.EMPTY,
			(modifiers & ACC_ABSTRACT) == 0
				? new JBlock(where, new JStatement[0], null)
				: null,
			javadoc,
			null);

		this.pointcut = pointcut;
	}

	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkInterface(CClassContext)
	 */
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {

		FjClassContext caesarContext = (FjClassContext) context;
	
		CBinaryTypeContext typeContext =
			new CBinaryTypeContext(
				context.getClassReader(),
				context.getTypeFactory(),
				context,
				typeVariables,
				(modifiers & ACC_STATIC) == 0);

		CType[] parameterTypes = new CType[parameters.length];
		String[] parameterNames = new String[parameters.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = parameters[i].checkInterface(typeContext);
		}

		FjSourceClass crosscuttingClass = (FjSourceClass) context.getCClass();

		
		CaesarMember rpd =
			resolve(
				context,
				context.getCClass(),
				parameters,
				getTokenReference());

		crosscuttingClass.addResolvedPointcut(rpd);
		checked=true;
		return null;
	}

	public CaesarMember resolve(
		CClassContext context,
		CClass caller,
		JFormalParameter[] formalParameters,
		TokenReference tokenReference) {

		List parameterSignatures = new ArrayList();
		List formalBindings = new ArrayList();

		for (int i = 0; i < parameters.length; i++) {

			if (!formalParameters[i].isGenerated()) {

				String	type = 
					parameters[i].getType().getSignature();

				parameterSignatures.add(type);

				formalBindings.add(
					new CaesarFormalBinding(
						type,
						formalParameters[i].getIdent(),
						i,
						tokenReference.getLine(),
						tokenReference.getLine(),
						tokenReference.getFile()));
			}
		}

		FjClassContext classContext = (FjClassContext) context;
		classContext.setBindings(
			(CaesarFormalBinding[]) formalBindings.toArray(new CaesarFormalBinding[0]));

		if (((modifiers & ACC_ABSTRACT) == 0)&&!checked) {
				pointcut.resolve(new CaesarScope((FjClassContext) context, caller));
		}

		CaesarMember rpd =
			CaesarMember.ResolvedPointcutDefinition(
				context.getCClass().getQualifiedName(),
				modifiers,
				getIdent(),
				(String[]) parameterSignatures.toArray(new String[0]),
				pointcut);

		return rpd;
	}

	
public String toString(){
		return pointcut.toString();
	}

	/**
	 * @return Returns the checked.
	 */
	public boolean isChecked() {
		return checked;
	}

}
