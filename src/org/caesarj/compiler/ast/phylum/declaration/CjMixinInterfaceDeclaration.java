package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.FurtherboundFurtherbindingRelation;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;
import org.caesarj.compiler.typesys.graph.SuperSubRelation;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.runtime.AdditionalCaesarTypeInformation;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

public class CjMixinInterfaceDeclaration extends CjInterfaceDeclaration {

	public CjMixinInterfaceDeclaration(
		TokenReference where, 
		int modifiers,
		String ident, 		
		CReferenceType[] extendedTypes,
		CReferenceType[] implementedTypes,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods, 
		JTypeDeclaration[] inners,
		JPhylum[] initializers
	) {
		super(
			where, 
			modifiers | ACC_MIXIN_INTERFACE, 
			ident, 
			null, 
			fields,
			methods, 
			inners, 
			initializers, 
			null, null);
		
		this.interfaces = new CReferenceType[extendedTypes.length+implementedTypes.length];
		this.extendedTypes = extendedTypes;
		this.implementedTypes = implementedTypes;
		
		System.arraycopy(extendedTypes, 0, interfaces, 0, extendedTypes.length);
		System.arraycopy(implementedTypes, 0, interfaces, extendedTypes.length, implementedTypes.length);
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
	    
	    for (int i = 0; i < implementedTypes.length; i++) {            
            superIfcs.add(implementedTypes[i].getQualifiedName());
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
	
	/**
	 * - check interface circularities on cclass interfaces already here
	 *   so that we can be sure in generateCaesarTypeSystem pass 
	 *   that there are no cyclices in the type graph
	 *   CTODO this step is repeated later on in checkInterfaces -> optimization needed
	 */
	public void join(CContext context) throws PositionedError {
	    
	    super.join(context);    	    	  
	    
	    try {	        
		    for (int i = 0; i < extendedTypes.length; i++) {
	            extendedTypes[i] = (CReferenceType)extendedTypes[i].checkType(self); 
	        }
		    
		    for (int i = 0; i < implementedTypes.length; i++) {
	            implementedTypes[i] = (CReferenceType)implementedTypes[i].checkType(self); 
	        }
	    }
	    catch (UnpositionedError e) {
            throw e.addPosition(getTokenReference());
        }
	    
	    for (int i = 0; i < extendedTypes.length; i++) {
	        // check that all extended types are mixin interfaces
            check(
                context,
                extendedTypes[i].getCClass().isMixinInterface(),
                CaesarMessages.CCLASS_EXTENDS_FROM_CCLASS);
	        
	        // check that a top-level class extends from another top-level class
	        if(!getCClass().isNested()) {
	            check(
	                context,
	                !extendedTypes[i].getCClass().isNested(),
	                CaesarMessages.TOPLEVEL_CCLASS_EXTENDS_TOPLEVEL_CLASS);
	        }
	        
	        // check circularities
	        check(
                context,
                !extendedTypes[i].getCClass().descendsFrom(getCClass()),
                KjcMessages.CLASS_CIRCULARITY,
                ident);
	    }
	    
	    
	    for (int i = 0; i < implementedTypes.length; i++) {
	        // check that we do not have cclass-es in the implements clause of a cclass
	        check(
                context,
                !implementedTypes[i].getCClass().isMixinInterface(),
                KjcMessages.SUPERINTERFACE_WRONG_TYPE,
                implementedTypes[i].getCClass().getQualifiedName());
	    }
	    
    }
	
	public void addInterface(CReferenceType[] newIfcs) {
        super.addInterface(newIfcs);
        
        CReferenceType[] newInterfaces =
            new CReferenceType[implementedTypes.length + newIfcs.length];

        System.arraycopy(implementedTypes, 0, newInterfaces, 0, implementedTypes.length);
        System.arraycopy(newIfcs, 0, newInterfaces, implementedTypes.length, newIfcs.length);

        implementedTypes = newInterfaces;
    }	
	
	private void addMixinInterfaces(CReferenceType[] newIfcs) {
	    super.addInterface(newIfcs);

        CReferenceType[] newInterfaces =
            new CReferenceType[extendedTypes.length + newIfcs.length];

        System.arraycopy(extendedTypes, 0, newInterfaces, 0, extendedTypes.length);
        System.arraycopy(newIfcs, 0, newInterfaces, extendedTypes.length, newIfcs.length);

        extendedTypes = newInterfaces;
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
            addMixinInterfaces((CReferenceType[])ifcList.toArray(new CReferenceType[ifcList.size()]));
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
    
    /**
     * completes the cclass interface with information about methods and fields
     * collected in checkAllInterfaces
     */
    public void completeCClassInterfaces(CCompilationUnitContext context) throws PositionedError {
        CjVirtualClassDeclaration implDecl = getCorrespondingClassDeclaration();
        JMethodDeclaration methods[] = implDecl.getMethods();
        
        List interfaceMethodDecls = new LinkedList();
        List interfaceMethods = new LinkedList();
        
        for (int i = 0; i < methods.length; i++) {
            
            CMethod method = methods[i].getMethod();
            
            if(!method.isConstructor() && method.isPublic() && !method.isStatic()) {
                
                CjMethodDeclaration decl = new CjMethodDeclaration(
        			methods[i].getTokenReference(),
        			ACC_PUBLIC | ACC_ABSTRACT,
        			method.getReturnType(),
        			method.getIdent(),
        			methods[i].getParameters(),
        			method.getExceptions(),
        			null,
        			null,
        			null);
                
                decl.checkInterface(self);
                
                interfaceMethodDecls.add(decl);
                
                interfaceMethods.add(
                    new CSourceMethod(
                        this.getCClass(),
                        ACC_PUBLIC | ACC_ABSTRACT,
                        method.getIdent(),
            			method.getReturnType(),
            			method.getParameters(),
            			method.getExceptions(),
            			false,
            			false,
            			null
                    )
                );
            }
        }
        
        getCClass().addMethod(
            (CSourceMethod[])interfaceMethods.toArray(new CSourceMethod[interfaceMethods.size()])
        );
        
        addMethods(
            (JMethodDeclaration[])interfaceMethodDecls.toArray(new JMethodDeclaration[interfaceMethodDecls.size()])
        );
        
        for (int i = 0; i < inners.length; i++) {
            inners[i].completeCClassInterfaces(context);
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
    public void setCorrespondingClassDeclaration(CjVirtualClassDeclaration caesarClassDeclaration)  {
        this.caesarClassDeclaration = caesarClassDeclaration;
    }
    
    public CjVirtualClassDeclaration getCorrespondingClassDeclaration() {
        return caesarClassDeclaration;
    }
    
    private CjVirtualClassDeclaration caesarClassDeclaration = null;

	private CReferenceType[] extendedTypes;
	private CReferenceType[] implementedTypes;
}
