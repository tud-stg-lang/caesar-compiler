package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CBodyContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CModifier;
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


    public void generateInterface(
        ClassReader classReader,
        CClass owner,
        String prefix
    ) {
        sourceClass = new CCjSourceClass(owner, getTokenReference(), modifiers, ident, prefix + ident, typeVariables, isDeprecated(), false, this); 
        setInterface(sourceClass);     
        
        CReferenceType[]    innerClasses = new CReferenceType[inners.length];
        for (int i = 0; i < inners.length; i++) {
          inners[i].generateInterface(classReader, sourceClass, sourceClass.getQualifiedName() + "$");
          innerClasses[i] = inners[i].getCClass().getAbstractType();
        }

        sourceClass.setInnerClasses(innerClasses);
        uniqueSourceClass = classReader.addSourceClass(sourceClass);    
    }
    
    
    // IVICA generateInterface method has been splited into
    // generating sourceClass and addding inners to sourceClass as needed by CClassFactory
	public void _generateInterface(
		ClassReader classReader,
		CClass owner,
		String prefix
    ) {
	    sourceClass = new CCjSourceClass(owner, getTokenReference(), modifiers, ident, prefix + ident, typeVariables, isDeprecated(), false, this); 
	    setInterface(sourceClass);		   
	}
    
    public void generateInterfaceInners(
        ClassReader classReader,
        String prefix
    ) {
        CReferenceType[]    innerClasses = new CReferenceType[inners.length];
        for (int i = 0; i < inners.length; i++) {
          //inners[i].generateInterface(classReader, sourceClass, sourceClass.getQualifiedName() + "$");
          innerClasses[i] = inners[i].getCClass().getAbstractType();
        }

        sourceClass.setInnerClasses(innerClasses);
        uniqueSourceClass = classReader.addSourceClass(sourceClass);
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
			//Jurgen's
			| ACC_PRIVILEGED 
			| ACC_CROSSCUTTING 
			| ACC_DEPLOYED;
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
    
    // IVICA added reference to corresponding CjClassDeclaration    
    public void setCorrespondingClassDeclaration(CjClassDeclaration caesarClassDeclaration)  {
        this.caesarClassDeclaration = caesarClassDeclaration;
    }
    
    public CjClassDeclaration getCorrespondingClassDeclaration() {
        return caesarClassDeclaration;
    }
    
    private CjClassDeclaration caesarClassDeclaration = null;
}
