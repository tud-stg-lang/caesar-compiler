package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.aspectj.CaesarAdviceKind;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.export.CCjAdvice;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

// FJCLEANUP
/**
 * Represents an AdviceDeclaration in the Source Code.
 * 
 * @author Jürgen Hallpap
 */
public class CjAdviceDeclaration
	extends CjMethodDeclaration
	implements CaesarConstants {

	public static final CjAdviceDeclaration[] EMPTY = new CjAdviceDeclaration[0];

	/** Pointcut */
	private CaesarPointcut pointcut;

	/** Kind of Advice (Before,After,AfterReturning,AfterThrowing,Around).*/
	private CaesarAdviceKind kind;

	/** The proceed method for around advices.*/
	private CjProceedDeclaration proceedMethodDeclaration;

	/** Flags, that show which extraArgument are needed (e.g. AroundClosure).*/
	private int extraArgumentFlags = 0;

	/** The parameters for the proceed-method which will be created later on for around-advices.*/
	private JFormalParameter[] proceedParameters;

	/**
	 * Constructor for AdviceDeclaration.
	 * 
	 * @param where
	 * @param modifiers
	 * @param typeVariables
	 * @param returnType
	 * @param parameters
	 * @param exceptions
	 * @param body
	 * @param javadoc
	 * @param comments
	 */
	public CjAdviceDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body,
		JavadocComment javadoc,
		JavaStyleComment[] comments,
		CaesarPointcut pointcut,
		CaesarAdviceKind kind,
		boolean hasExtraParameter) {
		super(
			where,
			modifiers,
			typeVariables,
			returnType,
			ADVICE_METHOD,
			parameters,
			exceptions,
			body,
			javadoc,
			comments);

		this.pointcut = pointcut;
		this.kind = kind;

		if (kind == CaesarAdviceKind.Around) {
			addAroundClosureParameter();
		}

		if (hasExtraParameter) {
			extraArgumentFlags |= CaesarConstants.ExtraArgument;
		}
	}

	/**
	 * Adds an aroundClosure parameter to around advices.
	 */
	private void addAroundClosureParameter() {

		JFormalParameter[] newParameters =
			new JFormalParameter[parameters.length + 1];

		System.arraycopy(parameters, 0, newParameters, 0, parameters.length);

		CType aroundClosureType = new CClassNameType(AROUND_CLOSURE_CLASS);

		newParameters[newParameters.length - 1] =
			new JFormalParameter(
				TokenReference.NO_REF,
				JFormalParameter.DES_GENERATED,
				aroundClosureType,
				AROUND_CLOSURE_PARAMETER,
				false);

		parameters = newParameters;

		//needed for proceed-method creation
		proceedParameters = newParameters;

	}

	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {

		/* FJRM
		// when checking single parameters we need the list of
		// all parameters and the method, so pass them here
		 ((FjAdditionalContext) context).pushContextInfo(this);
		((FjAdditionalContext) context).pushContextInfo(parameters);
		
		// we have to work on the returntype here:
		// if it's an overridden class cast upwards
		FjTypeSystem fjts = new FjTypeSystem();
		*/
		try {
			if (returnType.isReference()) {
				returnType = returnType.checkType(context);
				/* FJRM
				returnType =
					fjts.upperBound(context, (CReferenceType) returnType);
				*/
			}
		} catch (UnpositionedError e) {
			/* FJRM
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.CLASS_AMBIGUOUS) {
				CClass[] candidates =
					(CClass[]) e.getFormattedMessage().getParams()[1];					
				try {
					returnType = fjts.commonOverrideType(context, candidates);
				} catch (UnpositionedError e2) {
					// will be handled later
				}
			}
			*/
		}
		/* FJRM
		// pop parameters and method name from the stack again
		 ((FjAdditionalContext) context).popContextInfo();
		((FjAdditionalContext) context).popContextInfo();
		*/

		CBinaryTypeContext typeContext =
			new CBinaryTypeContext(
				context.getClassReader(),
				context.getTypeFactory(),
				context,
				typeVariables,
				(modifiers & ACC_STATIC) == 0);

		CType[] parameterTypes = new CType[parameters.length];
		String[] parameterNames = new String[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].checkInterface(typeContext);
			parameterNames[i] = parameters[i].getIdent();
		}

		CCjAdvice adviceMethod =
			new CCjAdvice(
				context.getCClass(),
				ACC_PUBLIC,
				ident,
				returnType,
				parameterTypes,
				exceptions,
				typeVariables,
				body,
				pointcut,
				kind,
				extraArgumentFlags);

		setInterface(adviceMethod);

		return adviceMethod;
	}

	/**
	 * Returns whether this is an around advice.
	 */
	public boolean isAroundAdvice() {
		return kind.equals(CaesarAdviceKind.Around);
	}

	/**
	 * Returns the proceedMethodDeclaration.
	 * @return CaesarProceedMethodDeclaration
	 */
	public CjProceedDeclaration getProceedMethodDeclaration() {
		return proceedMethodDeclaration;
	}

	/**
	 * Sets the proceedMethodDeclaration.
	 * @param proceedMethodDeclaration The proceedMethodDeclaration to set
	 */
	public void setProceedMethodDeclaration(CjProceedDeclaration proceedMethodDeclaration) {
		this.proceedMethodDeclaration = proceedMethodDeclaration;
	}

	/**
	 * Method setIdent.
	 * @param ident
	 */
	public void setIdent(String ident) {
		this.ident = ident;
	}

	/**
	 * Returns the kind.
	 * 
	 * @return AdviceKind
	 */
	public CaesarAdviceKind getKind() {
		return kind;	
	}

	/**
	 * Sets the parameters.
	 */
	public void setParameters(JFormalParameter[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * Returns the parameters.
	 */
	public JFormalParameter[] getParameters() {
		return parameters;
	}

	/**
	 * Returns the returnType.
	 */
	public CType getReturnType() {
		return returnType;
	}

	public CReferenceType[] getExceptions() {
		return exceptions;
	}

	/**
	 * Returns the pointcut.
	 * 
	 * @return Pointcut
	 */
	public CaesarPointcut getPointcut() {
		return pointcut;
	}

	/**
	 * Returns the body.
	 */
	public JBlock getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 */
	public void setBody(JBlock body) {
		this.body = body;
	}

	/**
	 * Sets the corresponding bit in the extraArgumentFlag.
	 * 
	 * @param extraArgumentFlags The extraArgumentFlags to set
	 */
	public void setExtraArgumentFlag(int extraArgumentFlag) {
		this.extraArgumentFlags |= extraArgumentFlag;
	}

	/**
	 * Returns the proceedParameters.
	 * 
	 * @return JFormalParameter[]
	 */
	public JFormalParameter[] getProceedParameters() {

		if (isAroundAdvice()) {
			return proceedParameters;
		} else {
			return JFormalParameter.EMPTY;
		}

	}

	/**
	 * @see org.caesarj.kjc.JMethodDeclaration#checkBody1(CClassContext)
	 */
	public void checkBody1(CClassContext context) throws PositionedError {
		super.checkBody1(context);

		//create a method attribute for the advice
		// this has to be done after the pointcut declarations are resolved	
		getCaesarAdvice().createAttribute(
			context,
			context.getCClass(),
			parameters,
			getTokenReference());
	}

	public CCjAdvice getCaesarAdvice() {
		return (CCjAdvice) getMethod();
	}

}