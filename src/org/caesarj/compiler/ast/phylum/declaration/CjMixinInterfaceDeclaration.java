package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.cclass.CaesarTypeNode;
import org.caesarj.compiler.cclass.CaesarTypeSystem;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

public class CjMixinInterfaceDeclaration extends CjInterfaceDeclaration {

	public CjMixinInterfaceDeclaration(
		TokenReference where, 
		int modifiers,
		String ident, 		
		CReferenceType[] interfaces, 
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods, 
		JTypeDeclaration[] inners,
		JPhylum[] initializers
	) {
		super(
			where, 
			modifiers | ACC_MIXIN_INTERFACE, 
			ident, CTypeVariable.EMPTY, 
			interfaces, 
			fields,
			methods, 
			inners, 
			initializers, 
			null, null);
	}
	
	
    public void adjustSuperType(CContext context) throws PositionedError {
        try {
            JavaQualifiedName qualifiedName =
                new JavaQualifiedName(
                    getCClass().getQualifiedName()
                );
            
            CaesarTypeSystem typeSystem = context.getEnvironment().getCaesarTypeSystem();
            CaesarTypeNode typeNode = typeSystem.getCompleteGraph().getType(qualifiedName);

            List ifcList = new ArrayList(typeNode.getParents().size());
            
            for (Iterator it = typeNode.getImplictParentsSubSet().iterator(); it.hasNext();) {
                CaesarTypeNode parentNode = (CaesarTypeNode) it.next();
                
                CReferenceType superTypeRef = 
                    context.getTypeFactory().createType(
                		parentNode.getQualifiedName().toString(), 
						true
					);
                
                superTypeRef = (CReferenceType)superTypeRef.checkType(context);
                
                ifcList.add(superTypeRef);
            }
            
            // add missing implicit relations 
            addInterface((CReferenceType[])ifcList.toArray(new CReferenceType[ifcList.size()]));
            getCClass().setInterfaces(this.interfaces);
            
            for(int i=0; i<inners.length; i++) {
                inners[i].adjustSuperType(context);
            }
        }
        catch (Throwable e) {
            // MSG
            e.printStackTrace();
            throw new PositionedError(getTokenReference(), CaesarMessages.FATAL_ERROR);
        }
    }
    
    // IVICA generateInterface method has been splited into
    // generating sourceClass and addding inners to sourceClass as needed by CClassFactory
	public void _generateInterface(
		ClassReader classReader,
		CClass owner,
		String prefix
    ) {
	    sourceClass = 
            new CCjSourceClass(
                owner, 
                getTokenReference(), 
                modifiers, 
                ident, 
                prefix + ident, 
                typeVariables, 
                isDeprecated(), 
                false, 
                this
            ); 
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

    
	protected int getAllowedModifiers()	{
		return 	super.getAllowedModifiers() | ACC_MIXIN_INTERFACE;
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
