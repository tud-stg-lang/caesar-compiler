package org.caesarj.compiler.ast;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.patterns.FormalBinding;
import org.aspectj.weaver.patterns.Pointcut;

import org.caesarj.kjc.CBinaryTypeContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CSourceMethod;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JStatement;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.aspectj.CaesarScope;

/**
 * A Pointcut declaration.
 * 
 * @author Jürgen Hallpap
 */
public class PointcutDeclaration extends FjMethodDeclaration {

	public static final PointcutDeclaration[] EMPTY =
		new PointcutDeclaration[0];

	/**The corresponding Pointcut.*/
	private Pointcut pointcut;

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
	public PointcutDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		JavadocComment javadoc,
		Pointcut pointcut) {
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

		
		ResolvedPointcutDefinition rpd =
			resolve(
				context,
				context.getCClass(),
				parameters,
				getTokenReference());

		crosscuttingClass.addResolvedPointcut(rpd);
			
		return null;
	}

	public ResolvedPointcutDefinition resolve(
		CClassContext context,
		CClass caller,
		JFormalParameter[] formalParameters,
		TokenReference tokenReference) {

		List parameterTypes = new ArrayList();
		List formalBindings = new ArrayList();

		for (int i = 0; i < parameters.length; i++) {

			if (!formalParameters[i].isGenerated()) {

				TypeX type =
					TypeX.forSignature(parameters[i].getType().getSignature());

				parameterTypes.add(type);

				formalBindings.add(
					new FormalBinding(
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
			(FormalBinding[]) formalBindings.toArray(new FormalBinding[0]));

		if ((modifiers & ACC_ABSTRACT) == 0) {
//XXX CHANGE I just ignore double resolving pointcuts. seems to work. 
			try{
				pointcut.resolve(new CaesarScope((FjClassContext) context, caller));
			}catch(org.aspectj.weaver.BCException e){
				System.out.println(e+ " in pointcut " +pointcut);
			}
		}

		ResolvedPointcutDefinition rpd =
			new ResolvedPointcutDefinition(
				TypeX.forName(context.getCClass().getQualifiedName()),
				modifiers,
				getIdent(),
				(TypeX[]) parameterTypes.toArray(new TypeX[0]),
				pointcut);

		return rpd;
	}
	
public String toString(){
		return pointcut.toString();
	}
}
