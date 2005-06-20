/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CjMixinInterfaceDeclaration.java,v 1.17 2005-06-20 11:19:03 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CjExternClassContext;
import org.caesarj.compiler.export.CCjIfcSourceClass;
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
	
	public CReferenceType[] getExtendedTypes() {
		return extendedTypes;
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
	public void join(CContext context, boolean recurse) throws PositionedError {
		
		super.join(context, recurse);
	    
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
            
            if(typeNode.inheritsFromCaesarObject()) {
                CReferenceType superTypeRef = 
                    context.getTypeFactory().createType(
                        CaesarConstants.CAESAR_OBJECT_IFC, 
						true
					);
                
                try {
                    superTypeRef = (CReferenceType)superTypeRef.checkType(context);
                }
                catch (UnpositionedError e) {
                    throw e.addPosition(getTokenReference());
                }
                
                addMixinInterfaces(new CReferenceType[]{superTypeRef});
            }
            else {   
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
            }
            
            getCClass().setInterfaces(this.interfaces);
            
            for(int i=0; i<inners.length; i++) {
                inners[i].adjustSuperType(context);
            }
        }
        catch (Throwable e) {
            // MSG
            e.printStackTrace();
            throw new PositionedError(getTokenReference(), CaesarMessages.ERROR_ADJUSTING_MIXIN_SUPERTYPE);
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
            			methods[i].getParameters(),
            			method.getParameters(),
            			method.getExceptions(),
            			false,
            			false,
            			null
                    )
                );
            }
        }
        
        getCClass().setMethods(
            (CSourceMethod[])interfaceMethods.toArray(new CSourceMethod[interfaceMethods.size()])
        );
        
        methods =
            (JMethodDeclaration[])interfaceMethodDecls.toArray(new JMethodDeclaration[interfaceMethodDecls.size()]);        
        
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
            new CCjIfcSourceClass(
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
    
    /**
     * Stores original compilation unit of externalized virtual classes
     */
    protected JCompilationUnit originalCompUnit = null;
    
    public JCompilationUnit getOriginalCompUnit() {
        return originalCompUnit;
    }
    
    public void setOriginalCompUnit(JCompilationUnit cu) {
    	originalCompUnit = cu;
    }

    /**
     * Constructs the class context.
     */
    protected CClassContext constructContext(CContext context) {
    	if (originalCompUnit == null) {
	        return super.constructContext(context);
    	}
    	else {
    		return new CjExternClassContext(
    	            context,
    	            context.getEnvironment(),
    	            sourceClass,
    	            this,
					originalCompUnit.getExport());
    	}
    }
    
    private CjVirtualClassDeclaration caesarClassDeclaration = null;

	private CReferenceType[] extendedTypes;
	private CReferenceType[] implementedTypes;
}
