package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.FurtherboundFurtherbindingRelation;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;
import org.caesarj.compiler.typesys.graph.SuperSubRelation;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.runtime.AdditionalCaesarTypeInformation;
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
	
	protected AdditionalCaesarTypeInformation constructAdditionalTypeInformation(CaesarTypeNode n) {	    
	    List mixinList = new LinkedList();
	    List nestedClasses = new LinkedList();
	    List incrementFor = new LinkedList();
	    List superClasses = new LinkedList();
	    List superIfcs = new LinkedList();
	    
	    for (Iterator it = n.getMixinList().iterator(); it.hasNext();) {
            CaesarTypeNode item = (CaesarTypeNode) it.next();
            mixinList.add(item.getQualifiedName().toString());
        }
	    
	    for (Iterator it = n.inners(); it.hasNext();) {
            OuterInnerRelation item = (OuterInnerRelation) it.next();
            nestedClasses.add(item.getInnerNode().getQualifiedName().toString());
        }
	    
	    for (Iterator it = n.incrementFor(); it.hasNext();) {
	        FurtherboundFurtherbindingRelation item = (FurtherboundFurtherbindingRelation) it.next();
            incrementFor.add(item.getFurtherboundNode().getQualifiedName().toString());
        }
	    
	    for (Iterator it = n.parents(); it.hasNext();) {
            SuperSubRelation item = (SuperSubRelation) it.next();
            String parentName = item.getSuperNode().getQualifiedName().toString();
            if(!incrementFor.contains(parentName))
                superClasses.add(parentName);
        }
	    
	    for (int i = 0; i < interfaces.length; i++) {
            String ifcName = interfaces[i].getQualifiedName();
            if(!incrementFor.contains(ifcName) && !superClasses.contains(ifcName))
                superIfcs.add(ifcName);
        }
	    
        AdditionalCaesarTypeInformation addInfo = new AdditionalCaesarTypeInformation(
            n.getQualifiedName().toString(),
            n.isImplicitType(),
            (String[])mixinList.toArray(new String[mixinList.size()]),
            (String[])nestedClasses.toArray(new String[nestedClasses.size()]),
            (String[])incrementFor.toArray(new String[incrementFor.size()]),
            (String[])superClasses.toArray(new String[superClasses.size()]),
            (String[])superIfcs.toArray(new String[superIfcs.size()]),
            getCorrespondingClassDeclaration().getCClass().getQualifiedName()
        );
        
        return addInfo;
	}
	
    public void adjustSuperType(CContext context) throws PositionedError {
        try {
            JavaQualifiedName qualifiedName =
                new JavaQualifiedName(
                    getCClass().getQualifiedName()
                );
            
            CaesarTypeSystem typeSystem = context.getEnvironment().getCaesarTypeSystem();
            CaesarTypeNode typeNode = typeSystem.getCaesarTypeGraph().getType(qualifiedName);

            // IVICA this is not the best place for this 
            getCClass().setAdditionalTypeInformation(
                constructAdditionalTypeInformation(typeNode));
            
            
            List ifcList = new LinkedList();
            
            for (Iterator it = typeNode.implicitParents(); it.hasNext();) {
                CaesarTypeNode parentNode = ((SuperSubRelation)it.next()).getSuperNode();
                
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
