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
 * $Id: CjVirtualClassDeclaration.java,v 1.18 2004-10-28 16:09:42 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JConstructorBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.typesys.CaesarTypeSystem;
import org.caesarj.compiler.typesys.graph.CaesarTypeNode;
import org.caesarj.compiler.typesys.graph.OuterInnerRelation;
import org.caesarj.compiler.typesys.java.JavaQualifiedName;
import org.caesarj.compiler.typesys.java.JavaTypeNode;
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
        CReferenceType[] superClasses,
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
            modifiers | ACC_MIXIN,
            implClass ? ident+"_Impl" : ident,
            null,
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
              
        
        this.superClasses = superClasses;
        
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

  
    protected String originalIdent;

    public String getOriginalIdent() {
        return originalIdent;
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

        CaesarTypeNode typeNode = typeSystem.getCaesarTypeGraph().getType(qualifiedName);
        JavaTypeNode javaTypeNode = typeSystem.getJavaTypeGraph().getJavaTypeNode(typeNode);
        
        javaTypeNode.setCClass(getCClass());
        javaTypeNode.setDeclaration(this);

        /*
         * add implicit subtypes
         */ 
        List newImpls = new LinkedList();
        List newIfcs  = new LinkedList();           
        
        for(Iterator it = typeNode.implicitInners(); it.hasNext(); ) {
            CaesarTypeNode subNode = ((OuterInnerRelation)it.next()).getInnerNode();
            
            // generate here
            CjMixinInterfaceDeclaration ifcDecl = 
                new CjMixinInterfaceDeclaration(
                    getTokenReference(),
                    ACC_PUBLIC,
                    subNode.getQualifiedName().getIdent(),
                    CReferenceType.EMPTY,
                    CReferenceType.EMPTY,
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
            
            ifcDecl.join(context);
            
            CjVirtualClassDeclaration implDecl =
                new CjVirtualClassDeclaration(
                    getTokenReference(),
                    ACC_PUBLIC,
                    subNode.getQualifiedImplName().getIdent(),
                    CReferenceType.EMPTY,
                    null, // wrappee
                    new CReferenceType[]{ifcDecl.getCClass().getAbstractType()}, // CTODO ifcs
                    new JFieldDeclaration[0],
					new JMethodDeclaration[0],
                    new JTypeDeclaration[0],
                    new JPhylum[0], null, null, 
                    CjPointcutDeclaration.EMPTY,
                    CjAdviceDeclaration.EMPTY,
                    new CaesarDeclare[0],
                    false
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
            
            implDecl.getCClass().setImplicit(true);
            ifcDecl.getCClass().setImplicit(true);
            
            newImpls.add(implDecl);
            newIfcs.add(ifcDecl);
            
            // and recurse into
            implDecl.createImplicitCaesarTypes(context);
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

            CaesarTypeNode typeNode = typeSystem.getCaesarTypeGraph().getType(qualifiedName);
            JavaTypeNode javaTypeNode = typeSystem.getJavaTypeGraph().getJavaTypeNode(typeNode);
            
            
            /*
             * adjust supertype 
             */
            
            JavaTypeNode superType = javaTypeNode.getParent();
            
            CReferenceType superTypeRef = 
                context.getTypeFactory().createType(superType.getQualifiedName().toString(), true);
            
            superTypeRef = (CReferenceType)superTypeRef.checkType(context);
            
            CReferenceType ifcs[] =
                new CReferenceType[]{
                    getMixinIfcDeclaration().getCClass().getAbstractType()
                };
            
            setInterfaces(ifcs);            
            setSuperClass(superTypeRef);
            
            getCClass().setSuperClass(superTypeRef);            
            getCClass().setInterfaces(ifcs);
            
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
     * - check that cclass modifier is always set to public
     */
    public void join(CContext context) throws PositionedError {

        if(!CModifier.contains(ACC_PUBLIC, modifiers)) {
	        context.reportTrouble(
	            new PositionedError(
	                getTokenReference(),
	                CaesarMessages.ONLY_PUBLIC_CCLASS
                )
            );
        }

        if(CModifier.contains(ACC_ABSTRACT, modifiers)) {
	        context.reportTrouble(
	            new PositionedError(
	                getTokenReference(),
	                CaesarMessages.CCLASS_CANNOT_ABSTRACT
                )
            );
        }
        
        super.join(context);
        
    }
    
    /**
     * Resolves the binding and providing references. Of course it calls the
     * super implementation of the method also.
     */
    public void checkInterface(CContext context) throws PositionedError {
        // add outer variable
    	CType outerType = null;
    	
        if(getCClass().getOwner() != null) {
            this.modifiers |= ACC_STATIC;
            getCClass().setModifiers(this.modifiers);
            
            outerType = context.getClassReader().loadClass(
        		context.getTypeFactory(), 
				getCClass().getOwner().convertToIfcQn()
			).getAbstractType();
            
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
        
        // search for default ctor
        int ctorIndex = -1;
        for (int i = 0; i < methods.length; i++) {
			if(methods[i] instanceof JConstructorDeclaration) {
				if(methods[i].getParameters().length == 0) {
					ctorIndex = i;
				}
				else {
					throw new PositionedError(
						methods[i].getTokenReference(),
						CaesarMessages.ONLY_DEF_CTOR_ALLOWED
					);
				}
			}
		}
                
        if(ctorIndex != -1) {
        	// we've found def ctor and only the def ctor
        	methods[ctorIndex] = new CjVirtualClassCtorDeclaration(
				methods[ctorIndex].getTokenReference(),
				ACC_PUBLIC,
				getIdent(),
				outerType,
				new JBlock(
					methods[ctorIndex].getTokenReference(), 
					((JConstructorBlock)methods[ctorIndex].getBlockBody()).getBody(), 
					null
				),
				context.getTypeFactory()
			);
        }
        else {
	        // add default ctor
	        addMethod(
	    		new CjVirtualClassCtorDeclaration(
					getTokenReference(),
					ACC_PUBLIC,
					getIdent(),
					outerType,
					context.getTypeFactory()
				)
			);
        }
    	
        
    	super.checkInterface(context);
		
		// CTODO: check inheritance of full throwable list on method redefinition
    	
    }
    
    
    // check method overriding in the context of virtual classes
    public void checkVirtualClassMethodSignatures(CCompilationUnitContext context) throws PositionedError {
    	for (int i = 0; i < methods.length; i++) {    	        	    
            JMethodDeclaration methodDecl = methods[i];
            CMethod method = methodDecl.getMethod();
            
            if(method.isConstructor())
                continue;
            
            // find initial declaration of the method
            CMethod initialMethodDecl = findInitialDeclaration(method, context);
            
            // set parameter list equal to the inital method declaration 
            if(!initialMethodDecl.equals(method)) {
	            // check return type and parameters
	            if(
	                method.getReturnType().isClassType() 
	                && method.getReturnType().getCClass().isMixinInterface()
	            ) {
	                
                	if(!context.getEnvironment().getCaesarTypeSystem().
	                	isIncrementOf(
	                	    method.getReturnType().getCClass().getQualifiedName(),
	                	    initialMethodDecl.getReturnType().getCClass().getQualifiedName()
	                    )
	                ) {
                	    // different return types and the lower is not increment of the upper
                	    // no modification, just continue
                	    // checkAllBodies will throw an exception
                	    continue;
                	}
                	
	            }
	            
	            
	            // update formal parameter list
	            JFormalParameter origFormalParams[] = methodDecl.getParameters();
	            JFormalParameter[] newFormalParams = new JFormalParameter[origFormalParams.length];
	            
	            for(int p = 0; p < newFormalParams.length; p++) {
	                newFormalParams[p] = new JFormalParameter(
	                    origFormalParams[p].getTokenReference(),
	                    origFormalParams[p].getDescription(),
	                    initialMethodDecl.getParameters()[p],
	                    origFormalParams[p].getIdent(),
	                    origFormalParams[p].isFinal()
                    );
                }
	            
	            // update declaration
	            methodDecl.setReturnType(initialMethodDecl.getReturnType());
	            methodDecl.setParameters(newFormalParams);
	            
	            // update type information
	            method.setReturnType(initialMethodDecl.getReturnType());
	            method.setParameters(initialMethodDecl.getParameters());
            }
        }    	    
    	
    	// recurse into inners
    	for (int i = 0; i < inners.length; i++) {
            inners[i].checkVirtualClassMethodSignatures(context);
        }
    }    

    
    private CMethod findInitialDeclaration(CMethod orig, CContext context) {
        CClass ownerSuperClass = orig.getOwner().getSuperClass();
        CMethod lastFound = orig;
        
        while(ownerSuperClass != null) {
            
            CMethod[] methods = ownerSuperClass.getMethods();
            
            if(methods == null) {
                boolean stopHere = true;
            }
            
            for (int i = 0; i < methods.length; i++) {
                
                CMethod other = methods[i];
                
                if(other.isConstructor())
                    continue;
                
                boolean equals = true;
                
                if(
                    !orig.getIdent().equals(other.getIdent())
                    || orig.getParameters().length != other.getParameters().length
                ) {
                    equals = false;
                }

                // check params
                for (int j = 0; j < orig.getParameters().length && equals; j++) {
                    if (!orig.getParameters()[j].equals(other.getParameters()[j])) {
                        // check if the params are furtherbindings
                        
                        if(orig.getParameters()[j].isClassType()) {                        
	                        if(
	                            !context.getEnvironment().getCaesarTypeSystem().
	                            	isIncrementOf(
	                            	    orig.getParameters()[j].getCClass().getQualifiedName(),
	                            	    other.getParameters()[j].getCClass().getQualifiedName()
	                                )
	                        ) {
	                            equals = false;	                            
	                        }
                        }
                        else {
                            equals = false;
                        }
                    }
                }
                                    
                if(equals) {   
                    lastFound = methods[i];
                }
            }
            
            ownerSuperClass = ownerSuperClass.getSuperClass();
        }
        
        return lastFound;
    }
    
    
    protected int getAllowedModifiers() {
        return super.getAllowedModifiers() | ACC_MIXIN;
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
    
    public CReferenceType[] getSuperClasses() {
        return superClasses;
    }
    
    private CjMixinInterfaceDeclaration mixinIfcDecl = null;

    private CReferenceType[] superClasses;
}
