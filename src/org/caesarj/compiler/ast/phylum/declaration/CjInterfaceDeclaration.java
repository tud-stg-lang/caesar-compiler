package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Hashtable;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.*;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

// FJKEEP 
public class CjInterfaceDeclaration 
	extends JInterfaceDeclaration 
{

	public CjInterfaceDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment) {
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}

	protected void checkModifiers(CContext context) throws PositionedError {
		int modifiers = getModifiers();

		check(
			context,
			CModifier.isSubsetOf(
				modifiers,
				getAllowedModifiers()),
			KjcMessages.NOT_INTERFACE_MODIFIERS,
			CModifier.toString(
				CModifier.notElementsOf(
					modifiers,
					getAllowedModifiers())));



		// JLS 9.1.1 : The access modifiers protected and private pertain only
		// to member interfaces within a directly enclosing class declaration.
		check(
			context,
			(isNested()
				&& getOwner().getCClass().isClass()
				&& !(context instanceof CBodyContext))
				|| !CModifier.contains(modifiers, ACC_PROTECTED | ACC_PRIVATE),
			KjcMessages.INVALID_INTERFACE_MODIFIERS,
			CModifier.toString(
				CModifier.getSubsetOf(modifiers, ACC_PROTECTED | ACC_PRIVATE)));

		// JLS 9.1.1 : The access modifier static pertains only to member interfaces.
		check(
			context,
			isNested() || !CModifier.contains(modifiers, ACC_STATIC),
			KjcMessages.INVALID_INTERFACE_MODIFIERS,
			CModifier.toString(CModifier.getSubsetOf(modifiers, ACC_STATIC)));

		// JLS 8.5.2 : Member interfaces are always implicitly static.
		if (isNested()) {
			setModifiers(modifiers | ACC_STATIC);
		}

		// JLS 9.5 : A member type declaration in an interface is implicitly
		// static and public.
		if (isNested() && getOwner().getCClass().isInterface()) {
			setModifiers(modifiers | ACC_STATIC | ACC_PUBLIC);
		}
	}
	
	/* FJRM
	protected CClassContext constructContext(CContext context) {
		return new FjInterfaceContext(
			context,
			context.getEnvironment(),
			sourceClass,
			this);
	}
	
	
	public void checkInterface(CContext context) throws PositionedError {

		// when checking members we need the
		// ifc declaration, so pass it here
		if( context instanceof FjAdditionalContext )
			((FjAdditionalContext) self).pushContextInfo(
				((FjAdditionalContext) context).peekContextInfo() );
		((FjAdditionalContext) self).pushContextInfo( this );
		
		boolean atLeastOneResolved = true;
		int i = 0;
		// in each iteration there has to be at least
		// one field to be checked, if not, there is a
		// circularity, so stop
		while( atLeastOneResolved ) {
			atLeastOneResolved = false;
			for( int j = 0; j < getFields().length; j++ ) {
				FjFieldDeclaration field = ((FjFieldDeclaration) getFields()[ j ]);
				if( !field.isChecked() ) {
					field.setChecked( true );
					field.checkInterface( self );
					if( field.isChecked() )
						atLeastOneResolved = true;
				}
			}
			i++;
		}

		super.checkInterface(context);
		((FjInterfaceContext) self).popContextInfo();
		((FjInterfaceContext) self).popContextInfo();
	}
	*/

	/**
	 * I just copied it here, after I will see if everything is needed.
	 * @param context
	 * @throws PositionedError
	 * @author Walter Augusto Werner
	 */
	public void initFamilies(CClassContext context) 
		throws PositionedError		
	{
		//Initializes the families of the fields.
		
		Hashtable hashField = new Hashtable(fields.length + 1);
		for (int i = fields.length - 1; i >= 0 ; i--) 
		{
			/* FJRM
			CSourceField field = ((FjFieldDeclaration)fields[i])
				.initFamily(context);
			*/
			
			// FJADD
			CSourceField field = fields[i].checkInterface(context);
							
			field.setPosition(i);
			
			hashField.put(field.getIdent(), field);
		}
		
		
		// Initializes the families of the methods.
		CMethod[] methodList = new CMethod[methods.length];
		int i = 0;
		for (; i < methods.length; i++)
		{ 
			/* FJRM
			if (methods[i] instanceof FjMethodDeclaration)
				methodList[i] = ((FjMethodDeclaration)methods[i])
					.initFamilies(context);
			else
			*/
				methodList[i] = methods[i].checkInterface(context);
		}
		
		
		sourceClass.close(sourceClass.getInterfaces(), 
			sourceClass.getSuperType(), hashField, methodList);
			
		for (int j = 0; j < inners.length; j++)
		{
			/* FJRM
			FjClassContext innerContext = new FjClassContext(context, 
				context.getEnvironment(), 
				(CSourceClass)inners[j].getCClass(), 
				this);
				
			((FjAdditionalContext)innerContext).pushContextInfo(this);
			*/
			
			if (inners[j] instanceof JClassDeclaration)
				((JClassDeclaration)inners[j]).initFamilies(null);
			else if (inners[j] instanceof CjInterfaceDeclaration)
				((CjInterfaceDeclaration)inners[j]).initFamilies(null);
							
		}
	}
	
	public void generateInterface(
		ClassReader classReader,
		CClass owner,
		String prefix) {
	    sourceClass = new CCjSourceClass(owner, getTokenReference(), modifiers, ident, prefix + ident, typeVariables, isDeprecated(), false, this); 
	
	    setInterface(sourceClass);
	
	    CReferenceType[]	innerClasses = new CReferenceType[inners.length];
	    for (int i = 0; i < inners.length; i++) {
	      inners[i].generateInterface(classReader, sourceClass, sourceClass.getQualifiedName() + "$");
	      innerClasses[i] = inners[i].getCClass().getAbstractType();
	    }
	
	    sourceClass.setInnerClasses(innerClasses);
	    uniqueSourceClass = classReader.addSourceClass(sourceClass);
	}

	public JTypeDeclaration[] getInners() {
		return inners;
	}

	/**
	 * Walter
	 * Returns the modifiers that are allowed in this definition.
	 */
	protected int getAllowedModifiers()
	{
		return 	ACC_PUBLIC 
			| ACC_PROTECTED
			| ACC_PRIVATE
			| ACC_ABSTRACT
			| ACC_STATIC
			| ACC_STRICT
			| ACC_INTERFACE
			| CCI_COLLABORATION
			| CCI_BINDING
			| CCI_PROVIDING
			| CCI_WEAVELET
			//Jurgen's
			| ACC_PRIVILEGED 
			| ACC_CROSSCUTTING 
			| ACC_DEPLOYED;
//		Walter: Collaboration, binding and providing inserted

			
	}	
	
	public void print()
	{
		System.out.print(CModifier.toString(modifiers));
		System.out.print("interface ");
		super.print();
		System.out.print(" extends ");
		for (int i = 0; i < interfaces.length; i++)
		{
			if (i > 0)
				System.out.print(", ");
			System.out.print(interfaces[i]);
		}
		
		System.out.println();
		
	}
}
