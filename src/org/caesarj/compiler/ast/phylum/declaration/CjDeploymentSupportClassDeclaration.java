package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.aspectj.CaesarDeclare;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.JPhylum;
import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;

/**
 * This class declaration is only for the generated deployment support classes.
 * 
 * @author Jürgen Hallpap
 */
public class CjDeploymentSupportClassDeclaration extends CjClassDeclaration {

    private CjClassDeclaration crosscuttingClass;

    private String postfix;

    public CjDeploymentSupportClassDeclaration(
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
        JavaStyleComment[] comment,
        CjClassDeclaration crosscuttingClass,
        String postfix) {
        this(
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
            comment,
            CjPointcutDeclaration.EMPTY,
            CjAdviceDeclaration.EMPTY,
            null,
            crosscuttingClass,
            postfix);
    }

    public CjDeploymentSupportClassDeclaration(
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
        JavaStyleComment[] comment,
        CjPointcutDeclaration[] pointcuts,
        CjAdviceDeclaration[] advices,
        CaesarDeclare[] declares,
        CjClassDeclaration crosscuttingClass,
        String postfix) {
        super(
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
            pointcuts,
            advices,
            declares);

        this.crosscuttingClass = crosscuttingClass;
        this.postfix = postfix;
    }

    
    public void createImplicitCaesarTypes(CContext context) {
        // do nothing here
    }
    
    public void adjustSuperType(CContext context) throws PositionedError {
		// do nothing here
	}
    
    /**
     * Sets the superclass of this deployment class if needed.
     */
    public void checkInterface(CContext context) throws PositionedError {
        super.checkInterface(context); 
    }
}
