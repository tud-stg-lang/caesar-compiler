package org.caesarj.compiler.family;

import org.caesarj.compiler.ast.FjAdditionalContext;
import org.caesarj.compiler.ast.FjFamilyContext;
import org.caesarj.compiler.ast.phylum.declaration.FjMethodDeclaration;
import org.caesarj.compiler.ast.phylum.declaration.JTypeDeclaration;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.constants.*;
import org.caesarj.compiler.context.*;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.UnpositionedError;

public class FjTypeSystem
{

	protected FjFamilyContext familyContext;

	public FjTypeSystem()
	{
		familyContext = FjFamilyContext.getInstance();
	}

	/*	
	public String isolateIdent( String name ) {
		if( name.indexOf( '.' ) > name.indexOf( '/' ) )
			if( name.indexOf( '$' ) > name.indexOf( '.' ) )
				return CClass.splitQualifiedName( name, '$' )[1];
			else
				return CClass.splitQualifiedName( name, '.' )[1];
		else
			if( name.indexOf( '$' ) > name.indexOf( '/' ) )
				return CClass.splitQualifiedName( name, '$' )[1];
			else
				return CClass.splitQualifiedName( name, '/' )[1];
	}
	*/

	public String[] splitQualifier(String typeName)
	{
		int firstDot = typeName.indexOf('.');
		if (firstDot <= 0)
			firstDot = typeName.indexOf('/');
		if (firstDot > 0)
		{
			String qualifier = typeName.substring(0, firstDot);
			String remainder = typeName.substring(firstDot + 1);
			return new String[] { qualifier, remainder };
		}
		else
		{
			return null;
		}
	}

	public void checkInFamily(
		CExpressionContext context,
		JExpression expected,
		JExpression actual)
		throws PositionedError
	{

/* FJRM
		if (expected == null)
			return;

		checkFamilies(
			context,
			expected.toFamily(context.getBlockContext()),
			actual);
*/
	}

	public void checkFamilies(
		CExpressionContext context,
		JExpression expected,
		JExpression actual)
		throws PositionedError
	{

		if (expected == null)
			return;

/* FJRM
		checkFamilies(context, expected.getFamily(context), actual);
*/
	}

	public void checkFamilies(
		CExpressionContext context,
		FjFamily expected,
		JExpression actual)
		throws PositionedError
	{

		// null-literal matches every family
		if (actual instanceof JNullLiteral)
			return;

/* FJRM
		try
		{
			FjFamily actualFamily = actual.getFamily(context);
			context.check(
				(expected == null || expected.isSubFamily(actualFamily)),
				CaesarMessages.WRONG_FAMILY,
				expected,
				(actualFamily == null) ? "no family" : actualFamily.toString());
		}
		catch (UnpositionedError e)
		{
			throw e.addPosition(actual.getTokenReference());
		}
*/
	}

	public FjFamily resolveFamily(
		CTypeContext context,
		CClass currentClass,
		CType type)
		throws UnpositionedError
	{
		return resolveFamily(context, currentClass, type, true);
	}

	public FjFamily resolveFamily(
		CTypeContext context,
		CClass currentClass,
		CType type,
		boolean resolveThis)
		throws UnpositionedError
	{
		if (!type.isReference())
			return null;
		String[] split = splitQualifier(type.toString());

		if (split != null)
		{
			FjFamily family =
				resolveFamily(context, currentClass, split[0], split[1]);
			if (family != null)
				return family;
		}

		if (!resolveThis)
			return null;

		try
		{
			type = type.checkType(context);
		}
		catch (UnpositionedError e)
		{
			if (e.getFormattedMessage().getDescription()
				!= KjcMessages.CLASS_AMBIGUOUS)
				throw e;
			CClass[] candidates =
				(CClass[]) e.getFormattedMessage().getParams()[1];
			type = commonOverrideType(context, candidates);
		}
		if ((type.getCClass().getModifiers() & Constants.FJC_VIRTUAL) != 0
			&& currentClass.descendsFrom(type.getCClass().getOwner()))
						
			return resolveFamily(
				context,
				currentClass,
				FjConstants.THIS_NAME,
				type.toString());

		else if (
			(type.getCClass().getModifiers() & Constants.FJC_VIRTUAL) != 0
				&& currentClass.getOwner() != null
				&& currentClass.getOwner().descendsFrom(
					type.getCClass().getOwner()))
	
			return resolveFamily(
				context,
				currentClass,
				FjConstants.OUTER_THIS_NAME,
				type.toString());
	
		else
			return null;
	}

	public FjFamily resolveFamily(
		CTypeContext context,
		String name,
		String remainder)
		throws UnpositionedError
	{
		return resolveFamily(
			context,
			context.getClassContext().getCClass(),
			name,
			remainder);
	}

	public FjFamily resolveFamily(
		CTypeContext context,
		CClass currentClass,
		String name,
		String remainder)
		throws UnpositionedError
	{
		FjFamily family = null;
		if (context instanceof FjAdditionalContext)
		{
			((FjAdditionalContext) context).pushContextInfo(currentClass);
			family = resolveFamily((FjAdditionalContext) context, name, true);
			((FjAdditionalContext) context).popContextInfo();
		}
		else
			family =
				resolveFamily(
					(CBlockContext) context,
					currentClass,
					name,
					true);

		if (family == null)
			return null;

		CReferenceType familyType =
			(CReferenceType) family.getType().checkType(context);

		if (!familyType.isReference())
			return null;

		CClass familyClass = familyType.getCClass();

		try
		{
			//familyContext.
			CClass innerClass = familyClass.lookupClass(familyClass, remainder);
			if (innerClass == null)
			{
				FjFamily otherFamily =
						resolveFamily(
							context,
							familyClass,
							new CClassNameType(remainder.replace('.', '/')),
							false);

				if (otherFamily != null)
					return new FjLinkedFamily(family, otherFamily);

				return null;
			}
			else
			{
				family.setInnerType(context, innerClass.getAbstractType());
				return family;
			}
		}
		catch (UnpositionedError e)
		{
			return null;
		}
	}

	protected FjFamily resolveFamily(
		FjAdditionalContext context,
		String name,
		boolean check)
		throws UnpositionedError
	{
		if (context.peekContextInfo(1) instanceof JFormalParameter[])
		{
			// "checking method parameters"
			JFormalParameter[] otherParams =
				(JFormalParameter[]) context.peekContextInfo(1);
			FjMethodDeclaration method =
				(FjMethodDeclaration) context.peekContextInfo(2);

			int i = 0;
			while (otherParams != null && i < otherParams.length)
			{
				if (otherParams[i].getIdent().equals(name))
				{
					// qualifier has to be declared final
					if (check)
						context.getParent().check(
							(otherParams[i].getModifiers()
								& Constants.ACC_FINAL)
								!= 0
								&& otherParams[i].getType().isReference(),
							CaesarMessages.FINAL_REFERENCE_NEEDED,
							name);
					return familyContext.addTypesFamilies(
						new FjParameterFamily(
							context.getParent(),
							method,
							otherParams[i],
							otherParams,
							i),
						(CReferenceType) otherParams[i].getType().checkType(
							context.getParent()));
				}
				i++;
			}
		}

		
		CClass clazz = null;
		if (context.peekContextInfo() instanceof CClass)
			clazz = (CClass) context.peekContextInfo();
		else if (context.peekContextInfo(1) instanceof JTypeDeclaration)
			clazz = ((JTypeDeclaration) context.peekContextInfo(1)).getCClass();
		else if (context.peekContextInfo(2) instanceof JTypeDeclaration)
			clazz = ((JTypeDeclaration) context.peekContextInfo(2)).getCClass();
			
		if (clazz != null)
		{
			FjFamily familyThis = resolveThisFamily(context.getParent(), 
				clazz, name);

			if (familyThis != null)
				return familyThis;
		
			return resolveFieldFamily(context.getParent(), clazz, name, check);
		}
		
		return null;
		
	}

	public FjFamily resolveFamily(
		CBlockContext context,
		String name,
		boolean check)
		throws UnpositionedError
	{
		return resolveFamily(
			context,
			context.getClassContext().getCClass(),
			name,
			check);
	}

	public FjFamily resolveFamily(
		CBlockContext context,
		CClass currentClass,
		String name,
		boolean check)
		throws UnpositionedError
	{

		if (name == FjConstants.THIS_NAME)
			return familyContext.addTypesFamilies(
				new FjThisFamily(context.getClassContext()),
				currentClass.getAbstractType());

		if ((name == FjConstants.OUTER_THIS_NAME
			|| name == Constants.JAV_OUTER_THIS)
			&& currentClass.getOwner() != null)
			return familyContext.addTypesFamilies(
				new FjOuterThisFamily(context.getClassContext()),
				context.getClassContext().getCClass().getAbstractType());

		// CBlockContext => checking variable definitions		
		// first have a look in the local vars		
		JLocalVariable var = context.lookupLocalVariable(name.intern());
		if (var != null)
		{
			if (check)
				context.check(
					var.getType().isReference() && var.isFinal(),
					CaesarMessages.FINAL_REFERENCE_NEEDED,
					var.getIdent());
			return familyContext.addTypesFamilies(
				new FjVariableFamily(context, var),
				(CReferenceType) var.getType());
		}

		// if there is no local vars found, maybe it's a field?
		return resolveFieldFamily(context, currentClass, name, check);
	}
	
	/**
	 * Resolve the family is the family is supposed to be a this or outerThis
	 * family.
	 *
	 * @author Walter Augusto Werner 
	 */
	protected FjFamily resolveThisFamily(
		CClassContext context,
		CClass currentClass,
		String name)
	{
		if (name == FjConstants.THIS_NAME)
			return familyContext.addTypesFamilies(
				new FjThisFamily(context),
				cleanInterface(currentClass).getAbstractType());

		if (name == FjConstants.OUTER_THIS_NAME)
			return familyContext.addTypesFamilies(
				new FjOuterThisFamily(context),
				cleanInterface(currentClass.getOwner()).getAbstractType());

		return null;
	}

	
	/**
	 * Resolve the family if the family is a field of the type or of its owner.
	 *
	 * @author Walter Augusto Werner 
	 */
	protected FjFamily resolveFieldFamily(
		CContext context,
		CClass currentClass,
		String name,
		boolean check)
		throws UnpositionedError
	{
		CField field =
			currentClass.lookupField(currentClass, null, name.intern());
		
		if (field != null)
		{
			if (check)
				context.check(
					field.getType().isReference() && field.isFinal(),
					CaesarMessages.FINAL_REFERENCE_NEEDED,
					field.getIdent());
			return familyContext.addTypesFamilies(
				new FjFieldFamily(context.getClassContext(), field),//TODO: Review the context here
				(CReferenceType) field.getType());
		}
		
		// maybe it's an owner's field?
		CClass owner = currentClass.getOwner();
		if (owner != null)
			return resolveFieldFamily(context, owner, name, check);
			
		// no, it's neither
		return null;
	}

	
	public CReferenceType commonOverrideType(
		CTypeContext context,
		CClass[] candidates)
		throws UnpositionedError
	{
		CReferenceType[] types = new CReferenceType[candidates.length];
		for (int i = 0; i < candidates.length; i++)
		{
			types[i] = candidates[i].getAbstractType();
			if (!types[i].getCClass().isNested())
				// a non-nested candidate!
				// we are ambigious ...
				throw new UnpositionedError(
					KjcMessages.CLASS_AMBIGUOUS,
					candidates[0].getIdent(),
					candidates);
		}

		boolean overrideClassFound = false;
		for (int i = 0; i < types.length; i++)
		{
			if ((candidates[i].getModifiers() & Constants.FJC_OVERRIDE) != 0)
				overrideClassFound = true;
		}
		if (!overrideClassFound)
			// no overriding candidate
			// => we are ambigious ...
			throw new UnpositionedError(
				KjcMessages.CLASS_AMBIGUOUS,
				candidates[0].getIdent(),
				candidates);

		for (int i = 0; i < types.length; i++)
		{
			CClass owner = candidates[i].getOwner();
			//types[ i ] = computeType( context, owner, candidates[i].getIdent() );
			types[i] = upperBound(context, types[i]);
		}

		// only if both candidates have been resolved
		// to the same base-type we are not ambigious
		if (types[0].getQualifiedName().equals(types[1].getQualifiedName()))
			return types[0];
		else
			throw new UnpositionedError(
				KjcMessages.CLASS_AMBIGUOUS,
				candidates[0].getIdent(),
				candidates);
	}

	public CReferenceType lowerBound(
		CTypeContext context,
		String innerTypeName)
	{
		CClass lowerBound = null;
		try
		{
			CClass clazz = context
				.lookupClass(
					context.getClassContext().getCClass(),
					innerTypeName);
		
			return clazz.getAbstractType();
		}
		catch (UnpositionedError e)
		{
			if (e.getFormattedMessage().getDescription()
				!= KjcMessages.CLASS_AMBIGUOUS)
				return null;

			// the first candidate found is that of the first
			// interface found i.e. the most recent implementation
			CClass[] candidates =
				(CClass[]) e.getFormattedMessage().getParams()[1];

			return candidates[0].getAbstractType();
		}
	}
	


	/**
	 * This method was changed to consider the collaboration interfaces.
	 * When it does consider, it returns the lower collaboration interface
	 * that is in the hierarchy of the type found (of course, only if 
	 * it has one :) ). The places where is found  the sign TEMPORAL are there
	 * with this porpouse.
	 */
	public CReferenceType lowerBound(
		CTypeContext context,
		CClass owner,
		String innerTypeName)
		throws UnpositionedError
	{
		return lowerBound(context, owner, innerTypeName, false);
	}
	public CReferenceType lowerBound(
		CTypeContext context,
		CClass owner,
		String innerTypeName,
		boolean considerCollaborationInterface)
		throws UnpositionedError
	{
		//If it is a proxy, take the clean interface
		if (FjConstants.isIfcImplName(owner.getIdent()))
			owner = owner.getInterfaces()[0].getCClass();
						
		CReferenceType inner = declaresInner(owner, innerTypeName);
		if (inner != null)
		{
			CReferenceType type = (CReferenceType)
				new CClassNameType(
					FjConstants.toIfcName(
						owner.getQualifiedName()
							+ "$"
							+ innerTypeName)).checkType(context);
							
//			//TEMPORAL!
//			if (! considerCollaborationInterface
//				|| ! CModifier.contains(type.getCClass().getModifiers(), 
//					Constants.FJC_OVERRIDE))
//				return (CReferenceType) type;
//			else
//			{
//				CClass result = getClassInHierarchy(
//					type.getCClass(), Constants.CCI_COLLABORATION);
//				return result == null ? (CReferenceType) type : result.getAbstractType();
//			}
//			//TEMPORAL END ;)
//			//It was just this return... 
			return (CReferenceType) type;


		}
		if (! owner.isInterface() && owner.getSuperClass() != null)
		{
			try
			{
				return lowerBound(context, owner.getSuperClass(), 
					innerTypeName, considerCollaborationInterface);
			}
			catch(UnpositionedError e){}
		}
		CReferenceType[] superInterfaces = owner.getInterfaces();
		for (int i = 0; i < superInterfaces.length; i++)
		{
			try
			{
				return lowerBound(context, superInterfaces[i].getCClass(), 
					innerTypeName, considerCollaborationInterface);
			}
			catch(UnpositionedError e){}
		}


		throw new UnpositionedError(KjcMessages.CLASS_UNKNOWN, innerTypeName);
	}
	
	/**
	 * Returns the class in the hierarchy which contains the modifier passed,
	 * or null if the class is not found. The other param is used when it is
	 * needed to find the class only if it does not have this modifier. 
	 * (This method is very similar
	 * to the method getSuperCollaborationInterfaceClass defined in 
	 * FjCleanClassDeclaration, but it returns the clazz itself instead of 
	 * return its parent. It was not reused, since this method is here just
	 * because of the problem of the down cast variable that are 
	 * Collaboration Interfaces, so if when the problem is resolved, try
	 * to merge the methods)
	 * @return CClass
	 */
	public static CClass getClassInHierarchy(
		CClass clazz, int modifier, int nModifier)
	{
		if (CModifier.contains(clazz.getModifiers(), nModifier))
			return null;
			
		if (CModifier.contains(clazz.getModifiers(), modifier))
			return clazz;

		CClass superClass;
		//If it is an interface the second interface is its super type.
		if (clazz.isInterface() && clazz.getInterfaces().length > 1)
			superClass = clazz.getInterfaces()
				[CciConstants.SUPER_TYPE_INDEX].getCClass();
		else
			superClass = clazz.getSuperClass();

		if (superClass != null)
			return getClassInHierarchy(superClass, modifier, nModifier);
		
		// No, it does not have a ci
		return null;		
	}
	/**
	 * Just an adapter for getClassInHierarchy(CClass clazz, 
	 * int modifier, int nModifier), passing 0 as last param.
	 * @param clazz
	 * @param modifier
	 * @param nModifier
	 * @return
	 */
	public static CClass getClassInHierarchy(
		CClass clazz, int modifier)
	{
		return getClassInHierarchy(clazz, modifier, 0);
	}
	

	public CReferenceType upperBound(CTypeContext context, CReferenceType type)
		throws UnpositionedError
	{
		String typeName = type.getIdent();

		if (typeName.indexOf('.') >= 0)
			// the name is already package-qualified
			return type;

		if ((type.getCClass().getModifiers() & Constants.FJC_OVERRIDE) == 0)
			// no override class
			// ==> no upcast
			return type;

		return upperBound(
			context,
			type.getCClass().getOwner(),
			type.getIdent());
	}

	public CReferenceType upperBound(
		CTypeContext context,
		CClass owner,
		String innerTypeName)
		throws UnpositionedError
	{
		
		CReferenceType inner = declaresInner(owner, innerTypeName);
		if (inner != null
			&& (inner.getCClass().getModifiers() 
				& Constants.FJC_OVERRIDE) == 0)
		{
			return (CReferenceType) new CClassNameType(
				FjConstants.toIfcName(
					owner.getQualifiedName()
						+ "$"
						+ innerTypeName)).checkType(context);
		}
		
		if (! owner.isInterface())
		{
			CClass ownerSuper = owner.getSuperClass();
			if (ownerSuper != null)
			{
				CReferenceType returnType = upperBound(context, 
					owner.getSuperClass(), innerTypeName);
				if (returnType != null)
					return returnType;
			}
		}
		CReferenceType[] superInterfaces = owner.getInterfaces();
		for (int i = 0; i < superInterfaces.length; i++)
		{
			CReferenceType returnType = upperBound(context, 
				superInterfaces[i].getCClass(), innerTypeName);
				
			if (returnType != null)
				return returnType;
		}
		return null;
	}

	public CClass cleanInterface(CClass clazz)
	{
		if (clazz.isInterface())
			return clazz;
		else if (
			FjConstants.isBaseName(clazz.getIdent())
				|| FjConstants.isIfcImplName(clazz.getIdent()))
			return clazz.getInterfaces()[0].getCClass();
		else
			return clazz;
	}

	public CClass superClassOf(CClass clazz)
	{

		CClass nextSuperClass = null;
		if (clazz.isInterface())
		{
			if (clazz.getInterfaces().length > 1)
				// ==> there is a super-ifc at index 1
				nextSuperClass = clazz.getInterfaces()[1].getCClass();
			else if (clazz.getInterfaces().length == 1)
				// ==> no super-ifc => return Child-ifc
				nextSuperClass = clazz.getInterfaces()[0].getCClass();
			else
				return null;
		}
		else
			nextSuperClass = clazz.getSuperClass();
		return nextSuperClass;
	}

	public boolean isCleanIfc(CTypeContext context, CClass clazz)
	{
		if (FjConstants.isBaseName(clazz.getIdent()))
			return false;
		try
		{
			return context.lookupClass(
				clazz,
				FjConstants.toProxyName(clazz.getQualifiedName()))
				!= null;
		}
		catch (UnpositionedError e)
		{
			return false;
		}
	}
	//Walter: It was protected, now it is public
	public CReferenceType declaresInner(
		CClass outerClass,
		String innerClassName)
	{
		CReferenceType[] inners = outerClass.getInnerClasses();
		for (int i = 0; i < inners.length; i++)
		{
			if (inners[i].getIdent().equals(innerClassName))
				return inners[i];
		}
		return null;
	}

	public boolean hasInner(CClass outerClass, String innerClassName)
	{
		
		while (outerClass != null)
		{
			CReferenceType[] inners = outerClass.getInnerClasses();
			for (int i = 0; i < inners.length; i++)
			{
				if (inners[i].getIdent().equals(innerClassName))
					return true;
			}
			outerClass = superClassOf(outerClass);
		}
		return false;
	}

	public boolean hasMethod(CClass clazz, String methodName)
	{

		if (clazz == null)
			return false;

		CMethod[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].getIdent().equals(methodName))
				return true;
		}
		return hasMethod(superClassOf(clazz), methodName);
	}

}
