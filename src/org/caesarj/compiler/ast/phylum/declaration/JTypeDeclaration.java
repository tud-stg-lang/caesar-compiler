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
 * $Id: JTypeDeclaration.java,v 1.33 2004-11-23 18:28:01 aracic Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;
import java.util.Hashtable;

import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.KjcMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CCompilationUnitContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.CWarning;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;

/**s
 * This class represents a Java class or interface declaration
 * in the syntax tree.
 */
public abstract class JTypeDeclaration extends JMemberDeclaration {

    // ----------------------------------------------------------------------
    // CONSTRUCTORS
    // ----------------------------------------------------------------------

    /**
     * Constructs a type declaration node in the syntax tree.
     *
     * @param	where		the line of this node in the source code
     * @param	modifiers	the list of modifiers of this class
     * @param	ident		the simple name of this class
     * @param	interfaces	the interfaces implemented by this class
     * @param	fields		the fields defined by this class
     * @param	methods		the methods defined by this class
     * @param	inners		the inner classes defined by this class
     * @param	initializers	the class and instance initializers defined by this class
     * @param	javadoc		java documentation comments
     * @param	comment		other comments in the source code
     */
    public JTypeDeclaration(
        TokenReference where,
        int modifiers,
        String ident,
        CReferenceType[] interfaces,
        JFieldDeclaration[] fields,
        JMethodDeclaration[] methods,
        JTypeDeclaration[] inners,
        JPhylum[] initializers,
        JavadocComment javadoc,
        JavaStyleComment[] comment) {
        super(where, javadoc, comment);

        this.modifiers = modifiers;
        this.ident = ident.intern();
        this.interfaces = interfaces;
        this.fields = fields;
        this.methods = methods;
        this.inners = inners;
        this.body = initializers;
    }

    // CTODO think about this one
    protected CSourceClass createSourceClass(CClass owner, String prefix) {
        return new CSourceClass(
            owner,
            getTokenReference(),
            modifiers,
            ident,
            prefix + ident,
            isDeprecated(),
            false,
            this);
    }

    /**
     * Defines an intermediate external representation of this class to use internally
     *parameters[i]
     */
    public void generateInterface(
        ClassReader classReader,
        CClass owner,
        String prefix) {
        sourceClass = createSourceClass(owner, prefix);

        setInterface(sourceClass);

        CReferenceType[] innerClasses = new CReferenceType[inners.length];
        for (int i = 0; i < inners.length; i++) {
            inners[i].generateInterface(
                classReader,
                sourceClass,
                sourceClass.getQualifiedName() + "$");
            innerClasses[i] = inners[i].getCClass().getAbstractType();
        }

        sourceClass.setInnerClasses(innerClasses); // prevent interface
        uniqueSourceClass = classReader.addSourceClass(sourceClass);
    }

    public void addInners(JTypeDeclaration newDecls[]) {
        JTypeDeclaration newInners[] = new JTypeDeclaration[inners.length + newDecls.length];
        System.arraycopy(inners, 0, newInners, 0, inners.length);
        System.arraycopy(newDecls, 0, newInners, inners.length, newDecls.length);
        inners = newInners;
        
        CReferenceType[] newInnerRefs = new CReferenceType[newDecls.length];
        for(int i=0; i<newInnerRefs.length; i++) {
            newInnerRefs[i] = newDecls[i].getCClass().getAbstractType();
        }
        
        getCClass().addInnerClass(newInnerRefs);
    }
    
    /**
     * Adds a method to the class. This method was pulled up. 
     * @param newMethod
     */
    public void addMethod(JMethodDeclaration newMethod) {
        addMethods(new JMethodDeclaration[]{newMethod});
    }
    
    public void addMethods(JMethodDeclaration[] methodsToAdd) {
        JMethodDeclaration[] newMethods =
            new JMethodDeclaration[methods.length + methodsToAdd.length];

        System.arraycopy(methods, 0, newMethods, 0, methods.length);
        System.arraycopy(
            methodsToAdd,
            0,
            newMethods,
            methods.length,
            methodsToAdd.length);

        methods = newMethods;
    }

    // ----------------------------------------------------------------------
    // ACCESSORS
    // ----------------------------------------------------------------------

    /**
     * Returns the declared modifiers for this type.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Sets the declared modifiers for this type.
     */
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
        if (sourceClass != null) {
            sourceClass.setModifiers(modifiers);
        }
    }

    /**
     * Checks whether this type is nested.
     *
     * JLS 8 (introduction), JLS 9 (introduction) :
     * A nested type (class or interface) is any type whose declaration
     * occurs within the body of another class or interface. A top level
     * type is a type that is not a nested class.
     *
     * @return	true iff this type is nested
     */
    public boolean isNested() {
        return getCClass().isNested();
    }

    public JFieldDeclaration[] getFields() {
        return fields;
    }

    public JConstructorDeclaration getDefaultConstructor() {
        return defaultConstructor;
    }

    public void setDefaultConstructor(JConstructorDeclaration defaultConstructor) {
        this.defaultConstructor = defaultConstructor;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    /**
     * Add a local or anonymous type declaration
     */
    public void addLocalTypeDeclaration(JTypeDeclaration tDecl) {
        if (anoInners == null) {
            anoInners = new ArrayList(5);
        }
        anoInners.add(tDecl);
    }

    // ----------------------------------------------------------------------
    // INTERFACE CHECKING
    // ----------------------------------------------------------------------

    protected CClassContext constructContext(CContext context) {
        return new CClassContext(
            context,
            context.getEnvironment(),
            sourceClass,
            this);
    }

    /** 
     * In the pass the superclass of this class the interfaces must be set, 
     * so that they are  available for the next pass.
     * It is not possible to check the interface of methods, fields, ... in 
     * the same pass.
     */
    public void join(CContext context) throws PositionedError {
        if (self == null) {
            self = constructContext(context);
        }

        //Walter: this method call was inserted instead of resolve 
        //the interfaces here.
        resolveInterfaces(context);

        // Check inners
        for (int i = inners.length - 1; i >= 0; i--) {
            inners[i].join(self);
        }
    }

    /**
     * Resolves the interfaces of this type.
     * @param context
     * @throws PositionedError
     * @author Walter Augusto Werner
     */
    protected void resolveInterfaces(CContext context) throws PositionedError {
        for (int i = 0; i < interfaces.length; i++) {
            try {
                interfaces[i] = (CReferenceType)interfaces[i].checkType(self);
            }
            catch (UnpositionedError e) {
                throw e.addPosition(getTokenReference());
            }

            CClass clazz = interfaces[i].getCClass();

            if( !(getCClass().isMixin() || getCClass().isMixinInterface()) ) {
	            check(
	                context,
	                !clazz.isMixinInterface(),
	                KjcMessages.SUPERINTERFACE_WRONG_TYPE,
	                interfaces[i].getQualifiedName());                
            }            

            check(
                context,
                clazz.isInterface(),
                KjcMessages.SUPERINTERFACE_WRONG_TYPE,
                interfaces[i].getQualifiedName());
            check(
                context,
                clazz.isAccessible(sourceClass),
                KjcMessages.SUPERINTERFACE_NOT_ACCESSIBLE,
                interfaces[i].getQualifiedName());
            check(
                context,
                !(sourceClass.getQualifiedName() == JAV_OBJECT),
                KjcMessages.CIRCULAR_INTERFACE,
                interfaces[i].getQualifiedName());
        }

        sourceClass.setInterfaces(interfaces);
    }

        
    // IVICA: additional passes added
    public void createImplicitCaesarTypes(CContext context) throws PositionedError {
    }
    
    public void adjustSuperType(CContext context) throws PositionedError {
    }
    
    public void checkVirtualClassMethodSignatures(CCompilationUnitContext context) throws PositionedError {
    } 
    
    public void completeCClassInterfaces(CCompilationUnitContext context) throws PositionedError {
    }


    /**
     * Second pass (quick), check interface looks good
     * @exception	PositionedError	an error with reference to the source file
     */
    public abstract void checkInterface(CContext context) throws PositionedError;

    /**
     * Second pass (quick), check interface looks good
     * @exception	PositionedError	an error with reference to the source file
     */
    protected void checkInterface(CContext context, CReferenceType superClass)
        throws PositionedError {
        //     self = new CClassContext(context, 
        //                              context.getEnvironment(), 
        //                              sourceClass, 
        //                              this);

        Hashtable hashField;
        CMethod[] methodList;
        Hashtable hashMethod;

        sourceClass.setSuperClass(superClass); //FIX

        if (!uniqueSourceClass) {
            context.reportTrouble(
                new PositionedError(
                    getTokenReference(),
                    KjcMessages.DUPLICATE_TYPE_NAME,
                    sourceClass.getQualifiedName()));
        }

        /*		
         * @TODO: Klaus: Disabled this check temporarily, but we should include it
         * once we can differentiate generated from user classes
         */
        /*if (!isNested()
        			&& sourceClass.isPublic()
        			&& !getTokenReference().getName().startsWith(ident + ".")
        			)
        		{
        			context.reportTrouble(
        				new PositionedError(
        					getTokenReference(),
        					KjcMessages.CLASS_NAME_FILENAME,
        					ident,
        					getTokenReference().getName()));
        		}
        */
        // If the class is an inner class, add field for this-reference.
        // Add fields of this class
        int generatedFields = getCClass().hasOuterThis() ? 1 : 0;

        hashField = new Hashtable(fields.length + generatedFields + 1);
        for (int i = fields.length - 1; i >= 0; i--) {
            CSourceField field = fields[i].checkInterface(self);

            field.setPosition(i);
            check(
                context,
                hashField.put(field.getIdent(), field) == null,
                KjcMessages.FIELD_RENAME,
                field.getIdent());
        }
        if (generatedFields > 0) {
            CSourceField field = outerThis.checkInterface(self);

            field.setPosition(hashField.size());
            check(
                context,
                hashField.put(JAV_OUTER_THIS, field) == null,
                KjcMessages.FIELD_RENAME,
                JAV_OUTER_THIS);
        }

        // Add methods of this class
        int generatedMethods = 0;

        if (defaultConstructor != null) {
            generatedMethods += 1;
        }
        if (statInit != null && !statInit.isDummy()) {
            generatedMethods += 1;
        }
        if (instanceInit != null && !instanceInit.isDummy()) {
            generatedMethods += 1;
        }

        methodList = new CMethod[methods.length + generatedMethods];
        for (int i = 0; i < methods.length; i++) {
            methodList[i] = methods[i].checkInterface(self);
            for (int j = 0; j < i; j++) {
                check(
                    context,
                    !methodList[i].equals(methodList[j]),
                    KjcMessages.METHOD_REDEFINE,
                    methodList[i]);
            }
        }

        int count = methods.length;

        if (defaultConstructor != null) {
            methodList[count] = defaultConstructor.checkInterface(self);
            count++;
        }
        if (statInit != null) {
            if (!statInit.isDummy()) {
                methodList[count] = statInit.checkInterface(self);
                count++;
            }
            else {
                statInit.checkInterface(self);
            }
        }
        if (instanceInit != null) {
            if (!instanceInit.isDummy()) {
                methodList[count] = instanceInit.checkInterface(self);
                count++;
            }
            else {
                instanceInit.checkInterface(self);
            }
        }

        // Check inners
        for (int i = inners.length - 1; i >= 0; i--) {
            inners[i].checkInterface(self);
        }
        sourceClass.close(this.interfaces, superClass, hashField, methodList);
    }

    /**
     * Checks that same interface is not specified more than once
     *
     * @exception	PositionedError	Error catched as soon as possible
     */
    public void checkInitializers(CContext context) throws PositionedError {
        
        if (getCClass().getSuperClass() != null) {
            check(
                context,
                !getCClass().getSuperClass().dependsOn(getCClass()),
                KjcMessages.CLASS_CIRCULARITY,
                ident);
        }

        for (int i = 0; i < interfaces.length; i++) {
            for (int j = 0; j < i; j++) {
                check(
                    context,
                    !interfaces[i].equals(interfaces[j]),
                    KjcMessages.INTERFACES_DUPLICATE,
                    ident,
                    interfaces[i]);
            }
        }

        // Checks that specified interfaces are not inherited
        for (int i = 0; i < interfaces.length; i++) {
            CClass parent;

            parent = sourceClass.getSuperClass();
            if (parent != null
                && parent.descendsFrom(interfaces[i].getCClass())) {
                context.reportTrouble(
                    new CWarning(
                        getTokenReference(),
                        KjcMessages.INTERFACE_IMPLEMENTED_BY_SUPERCLASS,
                        interfaces[i],
                        parent.getIdent()));
            }

            for (int j = 0; j < interfaces.length; j++) {
                if (j != i
                    && interfaces[j].getCClass().descendsFrom(
                        interfaces[i].getCClass())) {
                    context.reportTrouble(
                        new CWarning(
                            getTokenReference(),
                            KjcMessages.INTERFACE_IMPLEMENTED_BY_SUPERCLASS,
                            interfaces[i],
                            interfaces[j]));
                }
            }
        }
        
        
        // IVICA: check the dependent types
        //**********************************************************************
        boolean refresh = false;
        
        boolean remaining = false;
        
        for (int i = 0; i < fields.length; i++) {
           

            CType t = fields[i].getType(context.getTypeFactory());
            if(t instanceof CClassNameType) {
                // this one couldn't be resolved in checkInterface pass
                
                // try again
                // we can deeper dependencies than 1
                // however, in this first impl. we assume the simple case
                try {
                                       
                    t = t.checkType(self);
                    
                    System.out.println("Dependent type: "+t.toString());
                    
                    fields[i].getField().setType(t);
                    fields[i].getVariable().setType(t);
                }
                catch (UnpositionedError e) {
                    throw e.addPosition(fields[i].getTokenReference());
                }
            
            }
        }
        //**********************************************************************
        
    }

    // ----------------------------------------------------------------------
    // SEMANTIC ANALYSIS
    // ----------------------------------------------------------------------

    /**
     * IVICA: changed owner type to cclass interface (if we have a cclass)
     * -> getOwner().getCorrespondingCClass().getAbstractType()
     */
    public void addOuterThis() {
        if (outerThis == null) {
            sourceClass.setHasOuterThis(true);
            outerThis =
                new JFieldDeclaration(
                    getTokenReference(),
                    new JVariableDefinition(
                        getTokenReference(),
                        ACC_PRIVATE | ACC_FINAL,
                        //getOwner().getCorrespondingCClass().getAbstractType(),
                        getOwner().getAbstractType(),
                        JAV_OUTER_THIS,
                        null),
                    null,
                    null);
            ((CSourceClass)getCClass()).addField(
                new CSourceField(
                    getCClass(),
                    ACC_PRIVATE | ACC_FINAL,
                    JAV_OUTER_THIS,
                    //getOwner().getCorrespondingCClass().getAbstractType(),
                    getOwner().getAbstractType(),
                    false,
                    true));
            // synthetic
        }
    }

    /**
     * checkTypeBody
     * Check expression and evaluate and alter context
     * @param context the actual context of analyse
     * @return  a pure java expression including promote node
     * @exception PositionedError Error catched as soon as possible (for subclasses)
     */
    public void checkTypeBody(CContext context) throws PositionedError {
        
        context.addSourceClass(sourceClass);

        for (int i = 0; i < interfaces.length; i++) {
            check(
                context,
                !interfaces[i].getCClass().dependsOn(sourceClass),
                KjcMessages.CIRCULAR_INTERFACE,
                interfaces[i].getQualifiedName());
        }
        if (sourceClass.getOwner() != null) {
            check(
                context,
                sourceClass.getOwner().canDeclareStatic()
                    || !sourceClass.isStatic(),
                KjcMessages.INNER_DECL_STATIC_MEMBER);
        }
    }

    public void analyseConditions() throws PositionedError {
        // first super
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i].getCClass().analyseConditions();
        }

        // then self 
        for (int i = 0; i < methods.length; i++) {
            methods[i].analyseConditions();
        }
        for (int i = 0; i < inners.length; i++) {
            inners[i].analyseConditions();
        }
        if (anoInners != null) {
            int size = anoInners.size();

            for (int i = 0; i < size; i++) {
                ((JTypeDeclaration)anoInners.get(i)).analyseConditions();
            }
        }
    }
    // ----------------------------------------------------------------------
    // PROTECTED UTILITIES
    // ----------------------------------------------------------------------

    public JTypeDeclaration[] getInners() {
        return inners;
    }

    public void setInners(JTypeDeclaration[] inners) {
        this.inners = inners;
    }


    public CClass getOwner() {
        return getCClass().getOwner();
    }

    
    public void addInterface(CReferenceType newInterface) {
        addInterface(new CReferenceType[]{newInterface});
    }
    
    public void addInterface(CReferenceType newIfcs[]) {
        CReferenceType[] newInterfaces =
            new CReferenceType[interfaces.length + newIfcs.length];

        System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
        System.arraycopy(newIfcs, 0, newInterfaces, interfaces.length, newIfcs.length);

        interfaces = newInterfaces;
    }
    
    public CReferenceType[] getInterfaces() {
        return interfaces;
    }
    
    public JPhylum[] getBody() {return body;}
    
    public JMethodDeclaration[] getMethods() {return methods;}
    
    public String getIdent() {return ident;}

    public void recurse(IVisitor s) {
        for (int i = 0; i < inners.length; i++) {
            inners[i].accept(s);
        }
        for (int i = 0; i < fields.length; i++) {
            fields[i].accept(s);
        }
        for (int i = 0; i < methods.length; i++) {
            methods[i].accept(s);
        }
        // IVICA this has caused that fields has been visited twice
        /*
        for (int i = 0; i < body.length; i++) {
            body[i].accept(s);
        }
        */
    }
    
    public String toString() {
        return ident;
    }
    
    // ----------------------------------------------------------------------
    // PRIVATE DATA MEMBER
    // ----------------------------------------------------------------------

    protected int modifiers;
    protected String ident;

    protected JPhylum[] body;
    protected JFieldDeclaration[] fields;
    protected JMethodDeclaration[] methods;
    protected JTypeDeclaration[] inners;
    private ArrayList anoInners;
    protected CReferenceType[] interfaces;
    // Walter start
    //private	JFieldDeclaration	outerThis;
    protected JFieldDeclaration outerThis;
    //Walter end
    private JConstructorDeclaration defaultConstructor;
    protected JInitializerDeclaration statInit;
    protected JInitializerDeclaration instanceInit;

    // Definitive data
    protected CSourceClass sourceClass;
    // andreeas start
    //private	boolean			uniqueSourceClass = true;
    protected boolean uniqueSourceClass = true;
    //andreas end
    protected CClassContext self;
}
