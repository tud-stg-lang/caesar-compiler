package org.caesarj.compiler.ast;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.CciConstants;
import org.caesarj.compiler.constants.FjConstants;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CStdType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

public class JGeneratedInterfaceDeclaration extends FjInterfaceDeclaration
{

	JCaesarClassDeclaration baseDecl;

	public JGeneratedInterfaceDeclaration(
		TokenReference tokenReference,
		String ident,
		int modifiers,
		CReferenceType[] interfaces,
		JMethodDeclaration[] methods,
		JCaesarClassDeclaration baseDecl)
	{
		super(
			tokenReference,
			ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT | ACC_GENERATED_IFC | modifiers,
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
		addInterface(new CClassNameType(
			FjConstants.CHILD_TYPE_NAME), 
			CciConstants.CHILD_TYPE_INDEX);
		this.baseDecl = baseDecl;
	}

	public JMethodDeclaration[] getMethods()
	{
		return (JMethodDeclaration[]) Utils.toArray(
			new Vector(Arrays.asList(methods)),
			JMethodDeclaration.class);
	}

	private static JMethodDeclaration getAbstractMethodDeclaration(JMethodDeclaration m) {
		JMethodDeclaration md =
			new JMethodDeclaration(
				m.getTokenReference(),
				m.modifiers
					& ~(
						CModifier.ACC_NATIVE
							| CModifier.ACC_SYNCHRONIZED
							| CModifier.ACC_FINAL
							| CModifier.ACC_STRICT)
					| CModifier.ACC_ABSTRACT,
				CTypeVariable.EMPTY,
				m.returnType,
				m.ident,
				m.parameters,
				m.exceptions,
				null,
				null,
				null
			);
		return md;
	}
	
	
	public static JMethodDeclaration[] importMethods(JMethodDeclaration[] cleanMethods)
	{
		JMethodDeclaration[] abstractMethods =
			new JMethodDeclaration[cleanMethods.length];
		for (int i = 0; i < cleanMethods.length; i++)
		{
			abstractMethods[i] =
				getAbstractMethodDeclaration(cleanMethods[i]);
	}
		return abstractMethods;
	}


	/**
	 * Adds clean methods, importing it to this context.
	 * 
	 * @param methodToAdd
	 */
	public void addMethods(JMethodDeclaration[] methodsToAdd)
	{
		JMethodDeclaration[] importedMethods = 
			importMethods(methodsToAdd);

		JMethodDeclaration[] newMethods =
			new JMethodDeclaration[methods.length + importedMethods.length];

		System.arraycopy(methods, 0, newMethods, 0, methods.length);
		System.arraycopy(
			importedMethods,
			0,
			newMethods,
			methods.length,
			importedMethods.length);

		methods = newMethods;
	}	

	/**
	 * Adds a non clean method.
	 * 
	 * @param methodToAdd
	 */
	public void addMethod(JMethodDeclaration methodToAdd)
	{
		JMethodDeclaration[] newMethods =
			new JMethodDeclaration[methods.length + 1];

		System.arraycopy(methods, 0, newMethods, 1, methods.length);
		
		newMethods[0] = methodToAdd;

		methods = newMethods;
	}
	
	public JCaesarClassDeclaration getBaseClass()
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
	public void addInterface(CReferenceType newInterface)
	{
		addInterface(newInterface, interfaces.length);
	}
	
	public void addInterface(CReferenceType newInterface, int index)
	{
		CReferenceType[] newInterfaces =
			new CReferenceType[interfaces.length + 1];

		System.arraycopy(interfaces, 0, newInterfaces, 0, index);
		newInterfaces[index] = newInterface;
		System.arraycopy(
			interfaces, 
			index, 
			newInterfaces, 
			index + 1, 
			interfaces.length - index);

		interfaces = newInterfaces;
		
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
