package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.export.FjSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

// FJPULLUP hard
public class FjMethodDeclaration extends JMethodDeclaration {

	public FjMethodDeclaration(
		TokenReference where,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body,
		JavadocComment javadoc,
		JavaStyleComment[] comments) {
		super(
			where,
			modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			exceptions,
			body,
			javadoc,
			comments);
	}

	public String getIdent() {
		return ident;
	}

	/* FJRM
	public CSourceMethod checkInterface___(CClassContext context)
		throws PositionedError {
		
		// when checking single parameters we need the list of
		// all parameters and the method, so pass them here
		((FjAdditionalContext) context).pushContextInfo(this);
		((FjAdditionalContext) context).pushContextInfo(parameters);

		// we have to work on the returntype here:
		// if it's an overridden class cast upwards
		FjTypeSystem fjts = new FjTypeSystem();
		
		
		try {		
			if (returnType.isReference()) {
				returnType = returnType.checkType(context);
				
				returnType =
					fjts.upperBound(context, (CReferenceType) returnType);
				
			}		
		} catch (UnpositionedError e) {
			
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
			
		}
		
		// pop parameters and method name from the stack again
		((FjAdditionalContext) context).popContextInfo();
		((FjAdditionalContext) context).popContextInfo();
		
		
		CType[] parameterTypes = new CType[parameters.length];
		for (int i = 0; i < parameterTypes.length; i++) 
			parameterTypes[i] = parameters[i].getType();

		
		setInterface(new CSourceMethod(context.getCClass(),
						 modifiers,
						 ident,
						 returnType,
						 parameterTypes,
						 exceptions,
						 typeVariables,
						 isDeprecated(),
						 false, // not synthetic
						 body));		

		return (CSourceMethod)getMethod(); 
	}
	*/

	/*protected void setInterface(CMember export) {
		CSourceMethod oldExport = (CSourceMethod) export;
		FjFamily[] families = new FjFamily[ oldExport.getParameters().length ];
		for( int i = 0; i < families.length; i++ ) {
			families[ i ] = ((FjFormalParameter) parameters[ i ]).getFamily();
		}
		FjSourceMethod newExport = new FjSourceMethod(
			oldExport.getOwner(),
			oldExport.getModifiers(),
			oldExport.getIdent(),
			oldExport.getReturnType(),
			oldExport.getParameters(),
			oldExport.getThrowables(),
			oldExport.getTypeVariables(),
			oldExport.isDeprecated(),
			oldExport.isSynthetic(),
			body,
			families );
		FjSourceMethod newExport = createSourceMethod(oldExport, families);
		super.setInterface( newExport );
	}

	protected FjSourceMethod createSourceMethod(
		CSourceMethod oldExport,
		FjFamily[] families)
	{
		return new FjSourceMethod(
					oldExport.getOwner(),
					oldExport.getModifiers(),
					oldExport.getIdent(),
					oldExport.getReturnType(),
					oldExport.getParameters(),
					oldExport.getThrowables(),
					oldExport.getTypeVariables(),
					oldExport.isDeprecated(),
					oldExport.isSynthetic(),
					body,
					families);
	}
	*/
	
	public CMethod initFamilies(CClassContext context)
		throws PositionedError
	{
		/* FJRM
		((FjAdditionalContext) context).pushContextInfo(this);
		((FjAdditionalContext) context).pushContextInfo(parameters);		
		// after checking the parameters we rename overridden ones
		// and introduce downcasted variables with the old name		
		for (int i = 0; i < parameters.length; i++) 
		{
			FjFormalParameter parameter = (FjFormalParameter) parameters[ i ];
			try 
			{
				parameter.addFamily(context);
				parameter.upcastOverriddenType(context);
			} 
			catch (UnpositionedError e) 
			{
				context.reportTrouble(e.addPosition(parameter.getTokenReference()));
			}
		}
		*/
			
		CSourceMethod method = checkInterface1(context);
			
		/* FJRM
		// pop parameters and method name from the stack again
		((FjAdditionalContext) context).popContextInfo();
		((FjAdditionalContext) context).popContextInfo();
		*/
		return method;
	}
		
	/* FJRM
	public void prependStatement(JStatement statement) {

		if ((modifiers & ACC_ABSTRACT) != 0)
			// abstract methods contain no statements
			return;

		JStatement[] statements = body.getBody();
		JStatement[] extendedStatements = new JStatement[statements.length + 1];
		extendedStatements[0] = statement;
		System.arraycopy(statements, 0, extendedStatements, 1, 
			statements.length);
		body = new JBlock(body.getTokenReference(), extendedStatements, null);
	}
	*/

	public FjMethodDeclaration[] getSelfContextMethods(CType selfType) {
		return new FjMethodDeclaration[] { this };
	}
	

	/**
	 * Second pass (quick), check interface looks good
	 * Exceptions are not allowed here, this pass is just a tuning
	 * pass in order to create informations about exported elements
	 * such as Classes, Interfaces, Methods, Constructors and Fields
	 * @return true iff sub tree is correct enough to check code
	 * @exception	PositionedError	an error with reference to the source file
	 */
	public CSourceMethod checkInterface1(CClassContext context)
		throws PositionedError {
		boolean inInterface = context.getCClass().isInterface();
		boolean inCollaborationInterface = 
			CModifier.contains(
				context.getCClass().getModifiers(), 
				CCI_COLLABORATION);
		boolean isExported = true;
		//!(this instanceof JInitializerDeclaration);
		String ident = this.ident;
		//(this instanceof JConstructorDeclaration) ? JAV_CONSTRUCTOR : this.ident;

		// Collect all parsed data
		if (inInterface && isExported) {
			modifiers |= ACC_PUBLIC | ACC_ABSTRACT;
		}

		// 8.4.3 Method Modifiers
		check(
			context,
			CModifier.isSubsetOf(
				modifiers, getAllowedModifiers()),
			KjcMessages.METHOD_FLAGS);
		// 8.4.3.4 Navtive Methods
		// A compile-time error occurs if a native method is declared abstract.
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_NATIVE) == 0,
			KjcMessages.METHOD_ABSTRACT_NATIVE);
		// 8.4.3.1 
		// It is a compile-time error for a private method to be declared abstract.
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_PRIVATE) == 0,
			KjcMessages.METHOD_ABSTRACT_PRIVATE);
		// 8.4.3.1 
		// It is a compile-time error for a static method to be declared abstract.
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_STATIC) == 0,
			KjcMessages.METHOD_ABSTRACT_STATIC);
		// 8.4.3.1 
		// It is a compile-time error for a final method to be declared abstract.
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_FINAL) == 0,
			KjcMessages.METHOD_ABSTRACT_FINAL);
		// 8.1.2 Inner Classes and Enclosing Instances
		// Inner classes may not declare static members, unless they are compile-time constant fields

		check(
			context,
			context.getCClass().canDeclareStatic()
				|| ident == JAV_STATIC_INIT
				|| ((modifiers & ACC_STATIC) == 0),
			KjcMessages.INNER_DECL_STATIC_MEMBER);

		check(
			context,
			(modifiers & ACC_NATIVE) == 0 || (modifiers & ACC_STRICT) == 0,
			KjcMessages.METHOD_NATIVE_STRICT);
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0
				|| (modifiers & ACC_SYNCHRONIZED) == 0,
			KjcMessages.METHOD_ABSTRACT_SYNCHRONIZED);
		check(
			context,
			(modifiers & ACC_ABSTRACT) == 0 || (modifiers & ACC_STRICT) == 0,
			KjcMessages.METHOD_ABSTRACT_STRICT);
		
		//These checks must be done only for base methods...
		if (FjConstants.isBaseMethodName(ident) 
			&& ! FjConstants.isFactoryMethodName(ident)
			&& ! CciConstants.isAdaptMethodName(ident))
		{
			if (inCollaborationInterface)
				check(
					context,
					(modifiers & (CCI_EXPECTED | CCI_PROVIDED)) != 0,
					CaesarMessages.CI_METHOD_FLAGS,
					ident);
			else
				check(
					context,
					(modifiers & (CCI_EXPECTED | CCI_PROVIDED)) == 0,
					CaesarMessages.COLLABORATION_METHOD_OUT_CI,
					ident);
					
			int mask = CCI_EXPECTED | CCI_PROVIDED;
			check(
				context,
				(modifiers & mask) != mask,
				CaesarMessages.PROVIDED_AND_EXPECTED_METHOD,
				ident);
		}
			
		if (inInterface && isExported) {
			check(
				context,
				CModifier.isSubsetOf(modifiers, ACC_PUBLIC | ACC_ABSTRACT
				//Walter
				| CCI_EXPECTED | CCI_PROVIDED),
				KjcMessages.METHOD_FLAGS_IN_INTERFACE,
				this.ident);
		}
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

			setInterface(new FjSourceMethod(
				context.getCClass(),
				modifiers,
				ident,
				returnType,
				parameterTypes,
				exceptions,
				typeVariables,
				isDeprecated(),
				false,
			// not synthetic
			body));

			return (CSourceMethod) getMethod();
		} catch (UnpositionedError cue) {
			throw cue.addPosition(getTokenReference());
		}
	}


	
	protected int getAllowedModifiers()
	{
		return ACC_PUBLIC
			| ACC_PROTECTED
			| ACC_PRIVATE
			| ACC_ABSTRACT
			| ACC_FINAL
			| ACC_STATIC
			| ACC_NATIVE
			| ACC_SYNCHRONIZED
			| ACC_STRICT
			| CCI_EXPECTED
			| CCI_PROVIDED;
	}

	/**
	 * DEBUG - WALTER
	 * @author Walter Augusto Werner
	 */	
	/* FJRM
	public void print()
	{
		super.print();
		System.out.print(" ------> Families: ");
		for (int i = 0; i < parameters.length; i++)
		{
			if (i > 0) System.out.print(", ");
			System.out.print(parameters[i].getIdent());
			System.out.print(" - ");
			System.out.print(((FjFormalParameter)parameters[i]).getFamily());
		}
		System.out.println();

	}
	*/
}
