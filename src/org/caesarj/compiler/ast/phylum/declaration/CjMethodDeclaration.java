package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBinaryTypeContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

// FJKEEP
public class CjMethodDeclaration extends JMethodDeclaration {

	public CjMethodDeclaration(
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
		
	public CSourceMethod checkInterface(CClassContext context)
		throws PositionedError {
		super.checkInterface(context);
		return checkInterface1(context);
	}

	/* FJRM
	public CMethod initFamilies(CClassContext context)
		throws PositionedError
	{			
		return checkInterface1(context);
	}
	*/
		

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
			
		if (inInterface && isExported) {
			check(
				context,
				CModifier.isSubsetOf(modifiers, ACC_PUBLIC | ACC_ABSTRACT),
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

			setInterface(new CSourceMethod(
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
			| ACC_STRICT;
	}

}
