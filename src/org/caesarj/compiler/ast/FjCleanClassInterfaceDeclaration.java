package org.caesarj.compiler.ast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;

import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CStdType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.util.Utils;

public class FjCleanClassInterfaceDeclaration extends FjInterfaceDeclaration
{

	FjCleanClassDeclaration baseDecl;

	public FjCleanClassInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		CReferenceType[] interfaces,
		FjCleanMethodDeclaration[] methods,
		FjCleanClassDeclaration baseDecl)
	{
		super(
			tokenReference,
			ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT,
			ident,
			CTypeVariable.EMPTY,
			interfaces,
			new JFieldDeclaration[0],
		// clean classes - no fields
		importMethods(methods), new JTypeDeclaration[0], // inners are possible
		new JPhylum[0],
			new JavadocComment(
				"Automatically generated interface.",
				false,
				false),
			new JavaStyleComment[0]);
		addInterface(new CClassNameType(FjConstants.CHILD_TYPE_NAME));
		this.baseDecl = baseDecl;
	}

	public FjMethodDeclaration[] getMethods()
	{
		return (FjMethodDeclaration[]) Utils.toArray(
			new Vector(Arrays.asList(methods)),
			FjMethodDeclaration.class);
	}

	protected static FjMethodDeclaration[] importMethods(FjCleanMethodDeclaration[] cleanMethods)
	{

		FjMethodDeclaration[] abstractMethods =
			new FjMethodDeclaration[cleanMethods.length * 2];
		for (int i = 0; i < cleanMethods.length; i++)
		{
			abstractMethods[2 * i] =
				cleanMethods[i].getAbstractMethodDeclaration();
			abstractMethods[2 * i + 1] =
				cleanMethods[i]
					.getForwardSelfToImplementationMethod(CStdType.Object)
					.getAbstractMethodDeclaration();
		}
		return abstractMethods;
	}

	public FjCleanClassDeclaration getBaseClass()
	{
		return baseDecl;
	}

	public void append(JTypeDeclaration type)
	{
		JTypeDeclaration[] newInners =
			(JTypeDeclaration[]) Array.newInstance(
				JTypeDeclaration.class,
				inners.length + 1);
		for (int i = 0; i < inners.length; i++)
		{
			newInners[i] = inners[i];
		}
		newInners[inners.length] = type;
		inners = newInners;
	}

	public void addInterface(CReferenceType ifc)
	{
		Vector interfaces = new Vector(Arrays.asList(this.interfaces));
		interfaces.add(ifc);
		this.interfaces =
			(CReferenceType[]) Utils.toArray(interfaces, CReferenceType.class);
	}

	public String getIdent()
	{
		return ident;
	}

	public void join(CContext context) throws PositionedError
	{
		try
		{
			super.join(context);
		}
		catch (PositionedError e)
		{
			// an error occuring here will occur in the
			// base class this class is derived from, too
		}
	}
}
