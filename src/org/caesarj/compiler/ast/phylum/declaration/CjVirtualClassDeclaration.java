/*
 * Copyright (C) 1990-2001 DMS Decision Management Systems Ges.m.b.H.
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
 * $Id: CjVirtualClassDeclaration.java,v 1.3 2004-07-09 10:11:18 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.templates.TDefVirtualClassCtorDeclaration;
import org.caesarj.compiler.cclass.CaesarTypeNode;
import org.caesarj.compiler.cclass.CaesarTypeSystem;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.compiler.cclass.JavaTypeNode;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class represents a cclass in the syntax tree.  
 */
public class CjVirtualClassDeclaration extends CjClassDeclaration {
    
    public CjVirtualClassDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CTypeVariable[] typeVariables,
        CReferenceType superClass,
        CReferenceType wrappee,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers,
        JavadocComment javadoc,
        JavaStyleComment[] comment,
        CjPointcutDeclaration[] pointcuts,
        CjAdviceDeclaration[] advices,
        CaesarDeclare[] declares,
		boolean implClass) {
        super(
            where,
            modifiers,
            implClass ? ident+"_Impl" : ident,
            typeVariables,
            superClass,
            wrappee,
            interfaces,
            fields,
            methods,
            inners,
            initializers,
            javadoc,
            comment,
            pointcuts,
            advices,
            declares);
              
        // IVICA 
        if(implClass) {                    
            originalIdent = ident;
            
            //rename constructors
            for(int i=0; i<this.methods.length; i++) {
                JMethodDeclaration method = this.methods[i];
                if(method instanceof JConstructorDeclaration) {
                    method.ident = (method.ident + "_Impl").intern();
                }
            }
        }        
    }

    public CjVirtualClassDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CTypeVariable[] typeVariables,
        CReferenceType superClass,
        CReferenceType wrappee,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers
	) {
    	super(
	        where,
	        modifiers,
	        ident,
	        typeVariables,
	        superClass,
	        wrappee,
	        interfaces,
	        fields,
	        methods,
	        inners,
	        initializers,
	        null, 
			null
		);           
    }
    
    public CjVirtualClassDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CTypeVariable[] typeVariables,
        CReferenceType superClass,
        CReferenceType wrappee,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers,
        JavadocComment javadoc,
        JavaStyleComment[] comment,
        CjPointcutDeclaration[] pointcuts,
        CjAdviceDeclaration[] advices,
        CaesarDeclare[] declares) {
    	super(
	        where,
	        modifiers,
	        ident,
	        typeVariables,
	        superClass,
	        wrappee,
	        interfaces,
	        fields,
	        methods,
	        inners,
	        initializers,
	        javadoc,
	        comment,
	        pointcuts,
	        advices,
	        declares
		);           
    }

  
    protected String originalIdent;

    public String getOriginalIdent() {
        return originalIdent;
    }

    
    /**
     * this one generates temporary unchecked export information  of the source class
     * we need this one in order to be able to generate exports for mixin copies
     */    
    public void generateExport(CContext context) throws PositionedError {
        
    	boolean defCtorFound = false;
    	
        List methodList = new ArrayList(methods.length);
        for (int i = 0; i < methods.length; i++) {
            CMethod m = methods[i].checkInterface(self);            
            methodList.add(m);
        }
                
        Hashtable hashFieldMap = new Hashtable();
        for (int i = 0; i < fields.length; i++) {
            CSourceField field = fields[i].checkInterface(self);
            field.setPosition(i);
            hashFieldMap.put(field.getIdent(), field);
        }

        sourceClass.close(
            this.interfaces, 
            hashFieldMap, 
            (CMethod[])methodList.toArray(new CMethod[methodList.size()])
        );
        
        // Check inners
        for(int k = 0; k < inners.length; k++) {
            inners[k].generateExport(context);
        }
    }
    
    /**
     * this one generates recursively implicit inner types 
     */
    public void createImplicitCaesarTypes(CContext context) throws PositionedError {
        JavaQualifiedName qualifiedName =
            new JavaQualifiedName(
                getMixinIfcDeclaration().getCClass().getQualifiedName()
            );
        
        CaesarTypeSystem typeSystem = context.getEnvironment().getCaesarTypeSystem();

        CaesarTypeNode typeNode = typeSystem.getCompleteGraph().getType(qualifiedName);
        JavaTypeNode javaTypeNode = typeSystem.getJavaGraph().getJavaTypeNode(typeNode);
        
        javaTypeNode.setCClass(getCClass());
        javaTypeNode.setDeclaration(this);
        
        CClassContext cc = context.getClassContext();
        
        CType outerType = null;
        
        // add outer variable
        if(getCClass().getOwner() != null) {
            this.modifiers |= ACC_STATIC;
            getCClass().setModifiers(this.modifiers);
            
            /*
            outerType = 
            	javaTypeNode.getOuter().getDeclaration().getMixinIfcDeclaration().getCClass().getAbstractType();
    		*/
                
            String qn = new JavaQualifiedName(getCClass().getOwner().getQualifiedName()).convertToIfcName().toString();
            outerType = context.getClassReader().loadClass(context.getTypeFactory(), qn).getAbstractType();
            
            JFieldDeclaration outerField = new JFieldDeclaration(
        		getTokenReference(),
				new JVariableDefinition(
					getTokenReference(),
					ACC_FINAL | ACC_PRIVATE,
					outerType, // type
					"$outer",
					null
				),
				true,
				null, null
    		);
            
            addField(outerField);
        }
        
        // add default ctor
        addMethod(
    		new TDefVirtualClassCtorDeclaration(
				getTokenReference(),
				ACC_PUBLIC,
				getIdent(),
				outerType,
				context.getTypeFactory()
			)
		);
        
//      -------------------- cut here -------------------------------------
        // CTODO move this outside of this method -> a separated step
        /*
         * check name clashes if we have more than one parent
         */        
        /*
        if(typeNode.getParents().size() > 1) {            

            CaesarTypeNode joinPoint = null;
            
            // check the class first
            try {
                typeNode.getTopmostNode();
                
                // search for the first inheritance join point
                joinPoint = typeNode.findFirstInheritanceJoinPoint();
            }
            catch(Exception e) {
                throw new PositionedError(
                    getTokenReference(), 
                    CaesarMessages.NAME_CLASH,
                    typeNode.getQualifiedName()
                );
            }
            
            // now check the fields and methods
            // they are compatible, when they exist in the joinPoint class with same signature
            CClass joinPointClass = 
                typeSystem.getJavaGraph().getJavaTypeNode(joinPoint).getCClass();               
            
            CClass typeNodeClass =
                typeSystem.getJavaGraph().getJavaTypeNode(typeNode).getCClass();
            
            // methods                
            HashMap sigMemberMap = new HashMap();
            HashMap sigCollisionMap = new HashMap();
            
            for (Iterator it = typeNode.getParents().iterator(); it.hasNext();) {
                CaesarTypeNode parent = (CaesarTypeNode) it.next();
                JavaTypeNode parentJavaType = typeSystem.getJavaGraph().getJavaTypeNode(parent);
                CClass parentCClass = parentJavaType.getCClass();
                                    
                for (int i = 0; i < parentCClass.getMethods().length; i++) {
                    CMethod m = parentCClass.getMethods()[i];
                    if(!m.isConstructor()) {
                        String sig = m.getIdent()+m.getSignature();
                        sigMemberMap.put(sig, m);
                        Integer count = (Integer)sigCollisionMap.get(sig);
                        count = count==null ? new Integer(1) : new Integer(count.intValue()+1);
                        sigCollisionMap.put(sig, count);
                    }
                }
                
            }
            
            for (Iterator collisionIt = sigCollisionMap.entrySet().iterator(); collisionIt.hasNext();) {
                Map.Entry item = (Map.Entry) collisionIt.next();
                if(((Integer)item.getValue()).intValue() > 1) {
                    CMethod m = (CMethod)sigMemberMap.get(item.getKey());
                    
                    try {
                        CMethod res = joinPointClass.lookupMethod(
                            context,
                            typeNodeClass,
                            null,
                            m.getIdent(),
                            m.getParameters(),
                            CReferenceType.EMPTY
                        );
                    
                        if(res == null)
                            throw new Exception();
                    }
                    catch (Throwable e) {
                        throw new PositionedError(
                            getTokenReference(),
                            CaesarMessages.NAME_CLASH,
                            m.getIdent()
                        );
                    }
                        
                }
            }               
            
            // fields
            sigMemberMap.clear();
            sigCollisionMap.clear();
            
            for (Iterator it = typeNode.getParents().iterator(); it.hasNext();) {
                CaesarTypeNode parent = (CaesarTypeNode) it.next();
                JavaTypeNode parentJavaType = typeSystem.getJavaGraph().getJavaTypeNode(parent);
                CClass parentCClass = parentJavaType.getCClass();
                                    
                for (int i = 0; i < parentCClass.getFields().length; i++) {
                    CField f = parentCClass.getFields()[i];
                
                    String sig = f.getIdent();
                    sigMemberMap.put(sig, f);
                    Integer count = (Integer)sigCollisionMap.get(sig);
                    count = count==null ? new Integer(1) : new Integer(count.intValue()+1);
                    sigCollisionMap.put(sig, count);                
                }
                
            }
            
            for (Iterator collisionIt = sigCollisionMap.entrySet().iterator(); collisionIt.hasNext();) {
                Map.Entry item = (Map.Entry) collisionIt.next();
                if(((Integer)item.getValue()).intValue() > 1) {
                    CField f = (CField)sigMemberMap.get(item.getKey());
                    
                    try {
                        CField res = joinPointClass.lookupField(
                            typeNodeClass,
                            null,
                            f.getIdent()
                        );
                    
                        if(res == null)
                            throw new Exception();
                    }
                    catch (Throwable e) {
                        throw new PositionedError(
                            getTokenReference(),
                            CaesarMessages.NAME_CLASH,
                            f.getIdent()
                        );
                    }
                        
                }
            }                        
        }
//      -------------------- cut here -------------------------------------
        */
        /*
         * add implicit subtypes
         */ 
        List newImpls = new LinkedList();
        List newIfcs  = new LinkedList();           
        
        for(Iterator it = typeNode.getInners().values().iterator(); it.hasNext(); ) {
            CaesarTypeNode subNode = (CaesarTypeNode)it.next();

            if(subNode.isImplicit()) {
                // generate here
                CjMixinInterfaceDeclaration ifcDecl = 
                    new CjMixinInterfaceDeclaration(
                        getTokenReference(),
                        ACC_PUBLIC,
                        subNode.getQualifiedName().getIdent(),
                        new CReferenceType[0],
                        new JFieldDeclaration[0],
                        new JMethodDeclaration[0],
                        new JTypeDeclaration[0],
                        new JPhylum[0]
                    ); 
                
                ifcDecl.generateInterface(
                    context.getClassReader(), 
                    getMixinIfcDeclaration().getCClass(), 
                    subNode.getQualifiedName().getPrefix()
                );
                
                CjVirtualClassDeclaration implDecl =
                    new CjVirtualClassDeclaration(
                        getTokenReference(),
                        ACC_PUBLIC,
                        subNode.getQualifiedImplName().getIdent(),
                        new CTypeVariable[0],
                        context.getTypeFactory().createReferenceType(TypeFactory.RFT_OBJECT),
                        null, // wrappee
                        new CReferenceType[]{ifcDecl.getCClass().getAbstractType()}, // CTODO ifcs
                        new JFieldDeclaration[0],
						new JMethodDeclaration[0],
                        new JTypeDeclaration[0],
                        new JPhylum[0]
                    );
                
                implDecl.generateInterface(
                    context.getClassReader(),   
                    this.getCClass(), 
                    subNode.getQualifiedImplName().getPrefix()
                );
                
                implDecl.join(context); // CTODO do we need this join here?
                
                implDecl.getCClass().close(
                    implDecl.getInterfaces(),
                    new Hashtable(),
                    CMethod.EMPTY
                );
                
                implDecl.setMixinIfcDeclaration(ifcDecl);
                ifcDecl.setCorrespondingClassDeclaration(implDecl);
                
                newImpls.add(implDecl);
                newIfcs.add(ifcDecl);
                
                // and recurse into
                implDecl.createImplicitCaesarTypes(context);
            }
        }
        
        // recurse in original inners
        for(int i=0; i<inners.length; i++) {
            inners[i].createImplicitCaesarTypes(context);
        }
        
        // add inners
        addInners(
            (JTypeDeclaration[])newImpls.toArray(new JTypeDeclaration[newImpls.size()])
        );
        
        getMixinIfcDeclaration().addInners(
            (JTypeDeclaration[])newIfcs.toArray(new JTypeDeclaration[newIfcs.size()])
        );
    }   
    
    /**
     * ajdusts super types of the class
     * remark: in cclass preparation step we've set 
     * interface list to EMPTY and super type to null
     */
    public void adjustSuperType(CContext context) throws PositionedError {
        try {
            JavaQualifiedName qualifiedName =
                new JavaQualifiedName(
                    getMixinIfcDeclaration().getCClass().getQualifiedName()
                );
            
            CaesarTypeSystem typeSystem = context.getEnvironment().getCaesarTypeSystem();

            CaesarTypeNode typeNode = typeSystem.getCompleteGraph().getType(qualifiedName);
            JavaTypeNode javaTypeNode = typeSystem.getJavaGraph().getJavaTypeNode(typeNode);
            
            
            /*
             * adjust supertype 
             */
            
            JavaTypeNode superType = javaTypeNode.getParent();
            
            CReferenceType superTypeRef = 
                context.getTypeFactory().createType(superType.getQualifiedName().toString(), true);
            
            superTypeRef = (CReferenceType)superTypeRef.checkType(context);
            
            getCClass().setSuperClass(superTypeRef);
            setSuperClass(superTypeRef);
            
            setInterfaces(
                new CReferenceType[]{
                    getMixinIfcDeclaration().getCClass().getAbstractType()
                }
            );
            
            for(int i=0; i<inners.length; i++) {
                inners[i].adjustSuperType(context);
            }
        }
        catch (Throwable e) {
            // MSG
            e.printStackTrace();
            throw new PositionedError(getTokenReference(), CaesarMessages.CANNOT_CREATE);
        }
    }   
    
    
    /**
     * Resolves the binding and providing references. Of course it calls the
     * super implementation of the method also.
     */
    public void checkInterface(CContext context) throws PositionedError {
		super.checkInterface(context);
		
		// CTODO: check inheritance of full throwable list on method redefinition
    }
    
    /**
     * reference to corresponding mixin interface
     */     
    public void setMixinIfcDeclaration(CjMixinInterfaceDeclaration caesarInterfaceDeclaration)  {
        this.mixinIfcDecl = caesarInterfaceDeclaration;
    }
    
    public CjMixinInterfaceDeclaration getMixinIfcDeclaration() {
    	if(mixinIfcDecl == null)
    		throw new InconsistencyException("mixin interface has not been assigned to this virtual class");
        return mixinIfcDecl;
    }
    
    private CjMixinInterfaceDeclaration mixinIfcDecl = null;

}
