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
 * $Id: CjClassDeclaration.java,v 1.17 2004-06-15 16:42:05 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.lang.reflect.Array;
import java.util.*;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.aspectj.CaesarScope;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JUnqualifiedInstanceCreation;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JClassBlock;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.visitor.KjcPrettyPrinter;
import org.caesarj.compiler.ast.visitor.KjcVisitor;
import org.caesarj.compiler.cclass.CaesarTypeNode;
import org.caesarj.compiler.cclass.CaesarTypeSystem;
import org.caesarj.compiler.cclass.JavaQualifiedName;
import org.caesarj.compiler.cclass.JavaTypeNode;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.*;
import org.caesarj.compiler.joinpoint.DeploymentPreparation;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.Utils;

/**
 * This class represents a cclass in the syntax tree.  
 */
public class CjClassDeclaration
    extends JClassDeclaration
    implements CaesarConstants {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Constructs a class declaration node in the syntax tree.
     *
     * @param	where		the line of this node in the source code
     * @param	modifiers	the list of modifiers of this class
     * @param	ident		the simple name of this class
     * @param	superClass	the super class of this class
     * @param	interfaces	the interfaces implemented by this class
     * @param	fields		the fields defined by this class
     * @param	methods		the methods defined by this class
     * @param	inners		the inner classes defined by this class
     * @param	initializers	the class and instance initializers defined by this class
     * @param	javadoc		java documentation comments
     * @param	comment		other comments in the source code
     */

    public CjClassDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CTypeVariable[] typeVariables,
        CReferenceType superClass,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers,
        JavadocComment javadoc,
        JavaStyleComment[] comment) {
        this(
            where,
            modifiers,
            ident,
            typeVariables,
            superClass,
            null,
            interfaces,
            fields,
            methods,
            inners,
            initializers,
            javadoc,
            comment,
            null,
            null,
            null);
    }

    public CjClassDeclaration(
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
        JavaStyleComment[] comment) {
        this(
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
            CjPointcutDeclaration.EMPTY,
            CjAdviceDeclaration.EMPTY,
            null);
    }
    
    public CjClassDeclaration(
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
        this(
            where,
            modifiers,
            implClass ? ident+"_Impl" : ident, // IVICA,
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

    public CjClassDeclaration(
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
            interfaces,
            fields,
            methods,
            inners,
            initializers,
            javadoc,
            comment);
           
        this.wrappee = wrappee;
        this.pointcuts = pointcuts;
        this.advices = advices;
        this.declares = declares;      
        
        // structural detection of crosscutting property
        if ((advices.length > 0) || (pointcuts.length > 0))
            this.modifiers |= ACC_CROSSCUTTING;
    }

    // ----------------------------------------------------------------------
    // CODE GENERATION
    // ----------------------------------------------------------------------

    /**
     * Accepts the specified visitor
     * @param	p		the visitor
     */
    public void accept(KjcVisitor p) {
        super.accept(p);

        p.visitClassDeclaration(
            this,
            modifiers,
            ident,
            typeVariables,
            getSuperClass() != null ? getSuperClass().toString() : null,
            interfaces,
            body,
            methods,
            inners);
    }

    /**
     * Generate the code in pure java form
     * It is useful to debug and tune compilation process
     * @param	p		the printwriter into the code is generated
     */
    public void genInnerJavaCode(KjcPrettyPrinter p) {
        super.accept(p);

        p.visitInnerClassDeclaration(
            this,
            modifiers,
            ident,
            getSuperClass() != null ? getSuperClass().toString() : null,
            interfaces,
            inners,
            body,
            methods);
    }

    public void setMethods(JMethodDeclaration[] methods) {
        this.methods = methods;
    }

    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------

    protected CReferenceType wouldBeSuperClass;

    //  private CClassContext		self;   

    /** 
     * The reference of the wrappee.
     */
    protected CReferenceType wrappee;

    /**
     * The owner reference. It was pulled up.
     */
    protected CjClassDeclaration ownerDecl;

    /**
     * @return CReferenceType the Wrappee type.
     */
    public CReferenceType getWrappee() {
        return wrappee;
    }

    /**
     * Returns all constructors. This method was pulled up. 
     * @return FjConstructorDeclaration[]
     */
    protected JConstructorDeclaration[] getConstructors() {
        Vector contructors = new Vector(methods.length);
        for (int i = 0; i < methods.length; i++) {
            if (methods[i] instanceof JConstructorDeclaration)
                contructors.add(methods[i]);
        }
        return (JConstructorDeclaration[])Utils.toArray(
            contructors,
            JConstructorDeclaration.class);
    }
    
    
    /**
     * Returns the ident of the class
     * @return String
     */
    public String getIdent() {
        return ident;
    }

    /**
     * Adds a field in the class.
     * @param newField field to be inserted
     */
    public void addField(JFieldDeclaration newField) {
        JFieldDeclaration[] newFields =
            new JFieldDeclaration[fields.length + 1];

        System.arraycopy(fields, 0, newFields, 0, fields.length);

        newFields[fields.length] = newField;

        fields = newFields;
    }

    /**
     * Adds fields in the class.
     * @param newFields fields to be inserted
     */
    public void addFields(ArrayList newFields) {
        List tempList = Arrays.asList(fields);
        ArrayList oldFields = new ArrayList(tempList.size() + newFields.size());
        oldFields.addAll(tempList);
        oldFields.addAll(newFields);
        fields =
            (JFieldDeclaration[])oldFields.toArray(
                new JFieldDeclaration[oldFields.size()]);
    }
    
    public void addMethods(ArrayList methodsToAdd) {
        addMethods(
            (JMethodDeclaration[])methodsToAdd.toArray(
                new JMethodDeclaration[methodsToAdd.size()]));

    }

    /* DEBUG
     * (non-Javadoc)
     * @see at.dms.kjc.JTypeDeclaration#print()
     */
    public void print() {
        System.out.print(CModifier.toString(modifiers));
        System.out.print("class ");
        super.print();
        if (getSuperClass() != null)
            System.out.print(" extends " + getSuperClass());
        if (interfaces.length > 0) {
            System.out.print(" implements ");
            for (int i = 0; i < interfaces.length; i++) {
                if (i > 0)
                    System.out.print(", ");

                System.out.print(interfaces[i]);
            }
        }

        System.out.println();
    }
    /*
     * Integration of FjClassDeclaration (Karl Klose)
     */

    protected String originalIdent;

    /** The declared advices */
    protected CjAdviceDeclaration[] advices;

    /** e.g. declare precedence */
    protected CaesarDeclare[] declares;

    /** e.g. perSingleton, perCflow,..*/
    protected CaesarPointcut perClause;

    /** The declared pointcuts */
    protected CjPointcutDeclaration[] pointcuts;

    protected CSourceClass createSourceClass(CClass owner, String prefix) {
        return new CCjSourceClass(
            owner,
            getTokenReference(),
            modifiers,
            ident,
            prefix + ident,
            typeVariables,
            isDeprecated(),
            false,
            this,
            perClause);
    }

    public void append(JTypeDeclaration type) {
        JTypeDeclaration[] newInners =
            (JTypeDeclaration[])Array.newInstance(
                JTypeDeclaration.class,
                inners.length + 1);
        System.arraycopy(inners, 0, newInners, 0, inners.length);
        newInners[inners.length] = type;
        setInners(newInners);
    }

    public CTypeContext getTypeContext() {
        return self;
    }

    public String getOriginalIdent() {
        return originalIdent;
    }

    
    /**
     * IVICA
     * this one generates temporary unchecked export information  of the source class
     * we need this one in order to be able to generate exports for mixin copies
     */    
    public void generateExport(CContext context) throws PositionedError {
        // CTODO default constructor missing
        List methodList = new ArrayList(methods.length);
        for (int i = 0; i < methods.length; i++) {
            CMethod m = methods[i].checkInterface(self);
            methodList.add(m);
        }

        Hashtable hashFieldMap = new Hashtable();
                for (int i = fields.length - 1; i >= 0; i--) {
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
     * IVICA
     */
    public void createImplicitCaesarTypes(CContext context) throws PositionedError {
        try {   
            
            JavaQualifiedName qualifiedName =
                new JavaQualifiedName(
                    getCorrespondingInterfaceDeclaration().getCClass().getQualifiedName()
                );
            
            CaesarTypeSystem typeSystem = context.getEnvironment().getCaesarTypeSystem();

            CaesarTypeNode typeNode = typeSystem.getCompleteGraph().getType(qualifiedName);
            JavaTypeNode javaTypeNode = typeSystem.getJavaGraph().getJavaTypeNode(typeNode);
            
            javaTypeNode.setCClass(getCClass());
            javaTypeNode.setDeclaration(this);
            
            /*
             * add implicit subtypes
             */ 
            List newImpls = new LinkedList();
            List newIfcs  = new LinkedList();           
            
            for(Iterator it = typeNode.getInners().values().iterator(); it.hasNext(); ) {
                CaesarTypeNode subNode = (CaesarTypeNode)it.next();

                if(subNode.isImplicit()) {
                    // generate here
                    CjInterfaceDeclaration ifcDecl = 
                        new CjInterfaceDeclaration(
                            getTokenReference(),
                            ACC_PUBLIC | ACC_CCLASS_INTERFACE,
                            subNode.getQualifiedName().getIdent(),
                            new CTypeVariable[0],
                            new CReferenceType[0],
                            new JFieldDeclaration[0],
                            new JMethodDeclaration[0],
                            new JTypeDeclaration[0],
                            new JPhylum[0],
                            null,
                            null
                        ); 
                    
                    ifcDecl.generateInterface(
                        context.getClassReader(), 
                        getCorrespondingInterfaceDeclaration().getCClass(), 
                        subNode.getQualifiedName().getPrefix()
                    );
                    
                    CjClassDeclaration implDecl =
                        new CjClassDeclaration(
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
                            new JPhylum[0],
                            null,
                            null
                        );
                    
                    implDecl.generateInterface(
                        context.getClassReader(),   
                        this.getCClass(), 
                        subNode.getQualifiedImplName().getPrefix()
                    );
                    
                    implDecl.join(context);
                    
                    implDecl.setCorrespondingInterfaceDeclaration(ifcDecl);
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
            
            getCorrespondingInterfaceDeclaration().addInners(
                (JTypeDeclaration[])newIfcs.toArray(new JTypeDeclaration[newIfcs.size()])
            );
            
            
            // generate factory methods
            if(inners.length > 0) {
                JMethodDeclaration factoryMethods[] = new JMethodDeclaration[inners.length];
                JMethodDeclaration factoryIfcMethods[] = new JMethodDeclaration[inners.length];
                for (int i = 0; i < inners.length; i++) {
                    CjClassDeclaration inner = (CjClassDeclaration)inners[i];
                    factoryMethods[i] = new JMethodDeclaration(
                        getTokenReference(),
                        ACC_PUBLIC,
                        CTypeVariable.EMPTY,
                        inner.getCorrespondingInterfaceDeclaration().getCClass().getTopmostHierarchyInterface().getAbstractType(),
                        "$new"+inner.getCorrespondingInterfaceDeclaration().getCClass().getIdent(),
                        JFormalParameter.EMPTY,
                        CReferenceType.EMPTY,
                        new JBlock(
                            getTokenReference(),
                            new JStatement[]{                                                               
                                new JReturnStatement(
                                    getTokenReference(),
                                    new JUnqualifiedInstanceCreation(
                                        getTokenReference(),
                                        inner.getCClass().getAbstractType(),
                                        JExpression.EMPTY
                                    ),
                                    null
                                )
                            },
                            null
                        ),
                        null, null
                    );                    
                    
                    factoryIfcMethods[i] = new JMethodDeclaration(
                        getTokenReference(),
                        ACC_PUBLIC,
                        CTypeVariable.EMPTY,
                        inner.getCorrespondingInterfaceDeclaration().getCClass().getTopmostHierarchyInterface().getAbstractType(),
                        "$new"+inner.getCorrespondingInterfaceDeclaration().getCClass().getIdent(),
                        JFormalParameter.EMPTY,
                        CReferenceType.EMPTY,
                        null, // block is empty 
                        null, null
                    );
                }
                
                addMethods(factoryMethods);
                getCorrespondingInterfaceDeclaration().addMethods(factoryIfcMethods);
            }            
        }
        catch (Throwable e) {
            // MSG
            e.printStackTrace();
            throw new PositionedError(getTokenReference(), CaesarMessages.CANNOT_CREATE);
        }
    }   
    
    /**
     * IVICA
     */
    public void adjustSuperType(CContext context) throws PositionedError {
        try {
            JavaQualifiedName qualifiedName =
                new JavaQualifiedName(
                    getCorrespondingInterfaceDeclaration().getCClass().getQualifiedName()
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
                    getCorrespondingInterfaceDeclaration().getCClass().getAbstractType()
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
                
        //statically deployed classes are considered as aspects
        if (isStaticallyDeployed()) {
            DeploymentPreparation.prepareForStaticDeployment(
                context,
                (CjClassDeclaration)this);

            modifiers |= ACC_FINAL;
        }
        
        // call JClassDeclaration checkInterface
        super.checkInterface(context);
        
        if (isPrivileged() || isStaticallyDeployed()) {
            getCjSourceClass().setPerClause(
                CaesarPointcut.createPerSingleton());
        }

        //ckeckInterface of the pointcuts
        for (int j = 0; j < pointcuts.length; j++) {
            pointcuts[j].checkInterface(self);
        }

        /*
         * ivica
         * the following block was originally in initFamilies Method 
         */
        int generatedFields = getCClass().hasOuterThis() ? 1 : 0;

        //Initializes the families of the fields.
        Hashtable hashField =
            new Hashtable(fields.length + generatedFields + 1);
        for (int i = fields.length - 1; i >= 0; i--) {
            /* FJTODO
            CSourceField field =
                ((FjFieldDeclaration) fields[i]).initFamily(context);
            */

            // FJADD
            CSourceField field = fields[i].checkInterface(self);

            field.setPosition(i);

            hashField.put(field.getIdent(), field);
        }
        if (generatedFields > 0) {
            CSourceField field = outerThis.checkInterface(self);

            field.setPosition(hashField.size());

            hashField.put(JAV_OUTER_THIS, field);
        }

        int generatedMethods = 0;

        if (getDefaultConstructor() != null)
            generatedMethods++;

        if (statInit != null)
            generatedMethods++;

        if (instanceInit != null)
            generatedMethods++;

        // Initializes the families of the methods.
        CMethod[] methodList = new CMethod[methods.length + generatedMethods];
        int i;
        for (i=0; i < methods.length; i++) {
            // FJTODO initFamilies for CjMethodDeclaration
            /* 
            if (methods[i] instanceof CjMethodDeclaration)
                methodList[i] =
                    ((CjMethodDeclaration) methods[i]).initFamilies(context);
            else
                methodList[i] = methods[i].getMethod();
            */

            // FJADD
            methodList[i] = methods[i].getMethod();
        }

        JConstructorDeclaration defaultConstructor = getDefaultConstructor();
        if (defaultConstructor != null) {
            /*
            if (defaultConstructor instanceof JConstructorDeclaration)
                methodList[i++] =
                    ((JConstructorDeclaration) defaultConstructor)
                                .initFamilies(context);
            else
            */
            // FJADD
            methodList[i++] = defaultConstructor.getMethod();
        }
        if (statInit != null)
            methodList[i++] = statInit.getMethod();

        if (instanceInit != null)
            methodList[i++] = instanceInit.getMethod();

        sourceClass.close(
            interfaces,
            sourceClass.getSuperType(),
            hashField,
            methodList);

        //ckeckInterface of the advices
        for (int j = 0; j < advices.length; j++) {
            advices[j].checkInterface(self);
            //during the following compiler passes
            //the advices should be treated like methods
            getCjSourceClass().addMethod((CCjAdvice)advices[j].getMethod());
        }

        //consider declares
        if (declares != null) {
            for (int j = 0; j < declares.length; j++) {
                declares[j].resolve(
                    new CaesarScope(
                        (FjClassContext)constructContext(context),
                        getCjSourceClass()));
            }

            getCjSourceClass().setDeclares(declares);
        }
    }

    public CCjSourceClass getCjSourceClass() {
        return (CCjSourceClass)sourceClass;
    }

    public boolean isPrivileged() {
        return (modifiers & ACC_PRIVILEGED) != 0;
    }

    public boolean isStaticallyDeployed() {
        return (modifiers & ACC_DEPLOYED) != 0;
    }

    public void setFields(JFieldDeclaration[] fields) {
        this.fields = fields;
    }

    public CjPointcutDeclaration[] getPointcuts() {
        return pointcuts;
    }

    public CjAdviceDeclaration[] getAdvices() {
        return advices;
    }

    public void setPointcuts(CjPointcutDeclaration[] pointcuts) {
        this.pointcuts = pointcuts;
    }

    public void setAdvices(CjAdviceDeclaration[] advices) {
        this.advices = advices;
    }

    public CaesarDeclare[] getDeclares() {
        return declares;
    }

    public void setDeclares(CaesarDeclare[] declares) {
        this.declares = declares;
    }

    public void setPerClause(CaesarPointcut perClause) {
        this.perClause = perClause;
    }

    public boolean isCrosscutting() {
        return CModifier.contains(modifiers, ACC_CROSSCUTTING);
    }

    public void append(JMethodDeclaration newMethod) {
        Vector methods = new Vector(Arrays.asList(this.methods));
        methods.add(newMethod);
        this.methods =
            (JMethodDeclaration[])Utils.toArray(
                methods,
                JMethodDeclaration.class);
    }

    public void addClassBlock(JClassBlock initializerDeclaration) {
        JPhylum[] newBody = new JPhylum[body.length + 1];
        System.arraycopy(body, 0, newBody, 0, body.length);
        newBody[body.length] = initializerDeclaration;
        body = newBody;
    }

    public void addInterface(CReferenceType newInterface) {
        CReferenceType[] newInterfaces =
            new CReferenceType[interfaces.length + 1];

        System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
        newInterfaces[interfaces.length] = newInterface;

        interfaces = newInterfaces;
    }

    /**
     * checkTypeBody
     * Check expression and evaluate and alter context
     * @param context the actual context of analyse
     * @return  a pure java expression including promote node
     * @exception	PositionedError	an error with reference to the source file
     */
    public void checkTypeBody(CContext context) throws PositionedError {
        
        if (advices != null) {
            for (int i = 0; i < advices.length; i++) {
                advices[i].checkBody1(self);
            }
        }

        super.checkTypeBody(context);
    }

    /**
     * Constructs the class context.
     */
    protected CClassContext constructContext(CContext context) {
        return new FjClassContext(
            context,
            context.getEnvironment(),
            sourceClass,
            this);
    }

    /**
     * @return An int with all modifiers allowed for classes.
     */
    protected int getAllowedModifiers() {
        return super.getAllowedModifiers()
            | ACC_CROSSCUTTING
            | getInternalModifiers();
    }

    protected int getInternalModifiers() {
        return ACC_PRIVILEGED | ACC_CROSSCUTTING | ACC_DEPLOYED;
    }

    
    // IVICA added reference to corresponding CjInterfaceDeclaration    
    public void setCorrespondingInterfaceDeclaration(CjInterfaceDeclaration caesarInterfaceDeclaration)  {
        this.caesarInterfaceDeclaration = caesarInterfaceDeclaration;
    }
    
    public CjInterfaceDeclaration getCorrespondingInterfaceDeclaration() {
        return caesarInterfaceDeclaration;
    }
    
    private CjInterfaceDeclaration caesarInterfaceDeclaration = null;

}
