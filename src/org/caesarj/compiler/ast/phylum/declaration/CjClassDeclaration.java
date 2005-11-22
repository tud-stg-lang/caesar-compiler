/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
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
 * $Id: CjClassDeclaration.java,v 1.46 2005-11-22 08:48:30 gasiunas Exp $
 */

package org.caesarj.compiler.ast.phylum.declaration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.aspectj.CaesarDeclareScope;
import org.caesarj.compiler.aspectj.CaesarPointcut;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.ast.phylum.expression.JAssignmentExpression;
import org.caesarj.compiler.ast.phylum.expression.JExpression;
import org.caesarj.compiler.ast.phylum.expression.JNameExpression;
import org.caesarj.compiler.ast.phylum.expression.literal.JNullLiteral;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JExpressionListStatement;
import org.caesarj.compiler.ast.phylum.statement.JReturnStatement;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.ast.phylum.variable.JLocalVariable;
import org.caesarj.compiler.ast.phylum.variable.JVariableDefinition;
import org.caesarj.compiler.ast.visitor.IVisitor;
import org.caesarj.compiler.constants.CaesarConstants;
import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.context.CjExternClassContext;
import org.caesarj.compiler.context.FjClassContext;
import org.caesarj.compiler.export.CCjAdvice;
import org.caesarj.compiler.export.CCjSourceClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.CCompilationUnit;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CModifier;
import org.caesarj.compiler.export.CSourceClass;
import org.caesarj.compiler.export.CSourceField;
import org.caesarj.compiler.types.CClassNameType;
import org.caesarj.compiler.types.CDependentNameType;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.caesarj.util.UnpositionedError;
import org.caesarj.util.Utils;

/**
 * This class represents a cclass in the syntax tree.  
 */
public class CjClassDeclaration extends JClassDeclaration implements CaesarConstants {

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
        if ((advices.length > 0) || (pointcuts.length > 0) || (declares != null && declares.length > 0))
            this.modifiers |= ACC_CROSSCUTTING;
        
        // add wrappee field and method
        if(wrappee != null) {
        	JFieldDeclaration newField = new JFieldDeclaration(
                    where,
                    new JVariableDefinition(
                        where,
                        ACC_PRIVATE,
						JLocalVariable.DES_GENERATED,
                        wrappee,
                        WRAPPER_WRAPPEE_FIELD,
                        new JNullLiteral(where)
                    ),
					true, // [mef] mark as synthetic
                    null, null
                );
        	newField.setGenerated();
            addField(newField);
            
            /* wrappee initialization method */
            JMethodDeclaration wrapperInitMeth = 
            	new JMethodDeclaration(
                        where,
                        ACC_PUBLIC,
                        new CVoidType(),
                        WRAPPER_WRAPPEE_INIT,
                        new JFormalParameter[]{
                            new JFormalParameter(
                                where,
                                JFormalParameter.DES_PARAMETER,
                                wrappee,
                                "w",
                                false
                            )
                        },
                        CReferenceType.EMPTY,
                        new JBlock(
                            where,
                            new JStatement[]{                        
                                new JExpressionListStatement(
                                    where,
                                    new JExpression[]{
                                        new JAssignmentExpression(
                                            where,
                                            new JNameExpression(where, WRAPPER_WRAPPEE_FIELD),
                                            new JNameExpression(where, "w")
                                        )
                                    },
                                    null
                                )
                            },
                            null
                        ),
                        null, null
                    );
            wrapperInitMeth.setGenerated();
            addMethod(wrapperInitMeth);
            
                        
            /* wrappee access method */
            JMethodDeclaration getWrappeeMeth = 
                new JMethodDeclaration(
                    where,
                    ACC_PUBLIC,
                    wrappee,
                    WRAPPER_WRAPPEE_ACCESS,
                    JFormalParameter.EMPTY,
                    CReferenceType.EMPTY,
                    new JBlock(
                        where,
                        new JStatement[]{                        
                            new JReturnStatement(
                                where,
                                new JNameExpression(where, WRAPPER_WRAPPEE_FIELD),
								null
                            )
                        },
                        null
                    ),
                    null, null
                );
            getWrappeeMeth.setGenerated();
            addMethod(getWrappeeMeth); 
        }
    }
    
    public void join(CContext context) throws PositionedError {
        super.join(context);
        
        // IVICA: top-level class may not use the wraps clause
        if(!getSourceClass().isNested()) {
	        check(
	            context,
	            wrappee == null,
	            CaesarMessages.TOPLEVEL_CCLASS_WRAPS);
        }        
    }

    // ----------------------------------------------------------------------
    // CODE GENERATION
    // ----------------------------------------------------------------------
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
    
    public void addMethods(List methodsToAdd) {
        addMethods(
            (JMethodDeclaration[])methodsToAdd.toArray(
                new JMethodDeclaration[methodsToAdd.size()]));

    }


    /*
     * Integration of FjClassDeclaration (Karl Klose)
     */

    /** The declared advices */
    protected CjAdviceDeclaration[] advices;

    /** e.g. declare precedence */
    protected CaesarDeclare[] declares;

    /** e.g. perSingleton, perCflow,..*/
    protected CaesarPointcut perClause;

    /** The declared pointcuts */
    protected CjPointcutDeclaration[] pointcuts;

    protected CSourceClass createSourceClass(CClass owner, CCompilationUnit cunit, String prefix) {
        return new CCjSourceClass(
            owner,
            getTokenReference(),
            modifiers,
            ident,
            prefix + ident,
            isDeprecated(),
            false,
			false,
			cunit,
            this,
            perClause);
    }

    /**
     * Resolves the binding and providing references. Of course it calls the
     * super implementation of the method also.
     */
    public void checkInterface(CContext context) throws PositionedError {

        //statically deployed classes cannot be further overridden
        if (isStaticallyDeployed()) {
        	// inner classes cannot be statically deployed
        	if (isNested()) {
        		context.reportTrouble(
    	            new PositionedError(
    	                getTokenReference(),
    	                CaesarMessages.CANNOT_DEPLOY_VIRTUAL
                    )
                );
        	}
        	
        	// abstract classes cannot be statically deployed
        	if (getSourceClass().isAbstract()) {
            	context.reportTrouble(
    	            new PositionedError(
    	                getTokenReference(),
    	                CaesarMessages.CANNOT_DEPLOY_ABSTRACT
                    )
                );            
            }
        	
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
            pointcuts[j].checkInterface(getContext());
        }

        /*
         * IVICA
         * the following block was originally in initFamilies Method 
         */
        int generatedFields = getSourceClass().hasOuterThis() ? 1 : 0;

        //Initializes the families of the fields.
        Hashtable hashField =
            new Hashtable(fields.length + generatedFields + 1);
        for (int i = fields.length - 1; i >= 0; i--) {
            /* FJTODO
            CSourceField field =
                ((FjFieldDeclaration) fields[i]).initFamily(context);
            */

            // FJADD
            CSourceField field = fields[i].checkInterface(getContext());

            field.setPosition(i);

            hashField.put(field.getIdent(), field);
        }
        if (generatedFields > 0) {
            CSourceField field = outerThis.checkInterface(getContext());

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

        getSourceClass().close(
            interfaces,
            getSourceClass().getSuperType(),
            hashField,
            methodList);

        //ckeckInterface of the advices
        for (int j = 0; j < advices.length; j++) {
            advices[j].checkInterface(getContext());
            //during the following compiler passes
            //the advices should be treated like methods
            getSourceClass().addMethod((CCjAdvice)advices[j].getMethod());
        }
    }
    
    public CCjSourceClass getCjSourceClass() {
        return (CCjSourceClass)getSourceClass();
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
    
    public boolean isWrapper() {
        return wrappee != null;
    }

    public void append(JMethodDeclaration newMethod) {
        Vector methods = new Vector(Arrays.asList(this.methods));
        methods.add(newMethod);
        this.methods =
            (JMethodDeclaration[])Utils.toArray(
                methods,
                JMethodDeclaration.class);
    }    

    /**
     * checkTypeBody
     * Check expression and evaluate and alter context
     * @param context the actual context of analyse
     * @return  a pure java expression including promote node
     * @exception	PositionedError	an error with reference to the source file
     */
    public void checkTypeBody(CContext context) throws PositionedError {
    	
    	// resolve declares
        if (declares != null) {
            for (int j = 0; j < declares.length; j++) {
                declares[j].resolve(
                    new CaesarDeclareScope(
                        (FjClassContext)constructContext(context),
                        getSourceClass()));
            }

            getCjSourceClass().setDeclares(declares);
        }
        
        if (wrappee != null) {
        	
        	 try {
        	 	wrappee = (CReferenceType)wrappee.checkType(context);
        	 }
        	 catch (UnpositionedError e) {
        	 	// IVICA: give him a second chance ;)
                // it could be a dependent type
                try {
                	wrappee = (CReferenceType)new CDependentNameType(((CClassNameType)wrappee)
                        .getQualifiedName()).checkType(context);
                }
                catch (UnpositionedError ue2) {
                    throw ue2.addPosition(getTokenReference());
                }
            }
        	
            try {
                wrappee = (CReferenceType)wrappee.checkType(context);
            }
            catch (UnpositionedError e) {
                throw e.addPosition(getTokenReference());
            }            
        }
        
        // check that we do not override a wrapper
        if(wrappee != null) {
            check(
                context,
                !getSuperClass().getCClass().isWrapper(),
                CaesarMessages.OVERRIDE_WRAPPER
                );
        }
        
        if (advices != null) {
            for (int i = 0; i < advices.length; i++) {
                advices[i].checkBody1(getContext());
            }
        }

        super.checkTypeBody(context);
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
    
    public void recurse(IVisitor p) {
        super.recurse(p);
        for (int i = 0; i < advices.length; i++) {
            advices[i].accept(p);
        }
        for (int i = 0; i < pointcuts.length; i++) {
            pointcuts[i].accept(p);
        }
        /*
        for (int i = 0; i < declares.length; i++) {
            declares[i].traverse(visitorSet);
        } 
        */      
    }
    
    public void sortAdvicesByOrderNr() {
    	for (int i1 = 0; i1 < advices.length; i1++) {
    		for (int i2 = i1+1; i2 < advices.length; i2++) {
    			if (advices[i2].getOrderNr() < advices[i1].getOrderNr()) {
    				CjAdviceDeclaration decl = advices[i2];
    				advices[i2] = advices[i1];
    				advices[i1] = decl;
    			}
    		}
    	}    	
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
	        return new FjClassContext(
	                context,
	                context.getEnvironment(),
	                getSourceClass(),
	                this);
    	}
    	else {
    		return new CjExternClassContext(
    	            context,
    	            context.getEnvironment(),
    	            getSourceClass(),
    	            this,
					originalCompUnit.getExport());
    	}
    }
    
}
