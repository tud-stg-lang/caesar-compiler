package org.caesarj.compiler.ast;

import org.caesarj.compiler.CaesarMessages;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CBlockContext;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CClassContext;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CExpressionContext;
import org.caesarj.kjc.CField;
import org.caesarj.kjc.CMethod;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;
import org.caesarj.kjc.Constants;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JLocalVariable;
import org.caesarj.kjc.JNullLiteral;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.KjcMessages;

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

		if (expected == null)
			return;

		checkFamilies(
			context,
			expected.toFamily(context.getBlockContext()),
			actual);
	}

	public void checkFamilies(
		CExpressionContext context,
		JExpression expected,
		JExpression actual)
		throws PositionedError
	{

		if (expected == null)
			return;

		checkFamilies(context, expected.getFamily(context), actual);
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
		//TODO: Check this stuff!!
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
			return context
				.lookupClass(
					context.getClassContext().getCClass(),
					innerTypeName)
				.getAbstractType();
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

	public CReferenceType lowerBound(
		CTypeContext context,
		CClass owner,
		String innerTypeName)
		throws UnpositionedError
	{


		CReferenceType inner = declaresInner(owner, innerTypeName);
		if (inner != null)
		{

			CReferenceType type =
				new CClassNameType(
					FjConstants.toIfcName(
						owner.getQualifiedName()
							+ "$"
							+ innerTypeName));
			return (CReferenceType) type.checkType(context);
		}
		if (! owner.isInterface() && owner.getSuperClass() != null)
		{
			try
			{
				return lowerBound(context, owner.getSuperClass(), 
					innerTypeName);
			}
			catch(UnpositionedError e){}
		}
		CReferenceType[] superInterfaces = owner.getInterfaces();
		for (int i = 0; i < superInterfaces.length; i++)
		{
			try
			{
				return lowerBound(context, superInterfaces[i].getCClass(), 
					innerTypeName);
			}
			catch(UnpositionedError e){}
		}


		throw new UnpositionedError(KjcMessages.CLASS_UNKNOWN, innerTypeName);
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
				& Constants.FJC_OVERRIDE)	== 0)
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
		CReferenceType[] inners = outerClass.getInnerClasses();
		while (outerClass != null)
		{
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
