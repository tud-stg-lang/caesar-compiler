package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CTypeVariable;
import org.caesarj.util.TokenReference;

/**
 * Needed in order to be able to separate common JMethod Declarations from
 * AdviceMethods
 * 
 * @author Ivica Aracic
 */
public class CjAdviceMethodDeclaration extends CjMethodDeclaration {

    protected CjAdviceDeclaration advice;
    
    public CjAdviceMethodDeclaration(
        CjAdviceDeclaration advice,
        TokenReference where,
        int modifiers,
        CTypeVariable[] typeVariables,
        CType returnType,
        String ident,
        JFormalParameter[] parameters,
        CReferenceType[] exceptions,
        JBlock body,
        JavadocComment javadoc,
        JavaStyleComment[] comments) {
        super(
            where,
            modifiers,
            typeVariables,
            returnType,
            ident,
            parameters,
            exceptions,
            body,
            javadoc,
            comments);
        
        this.advice = advice;
    }
    
    public CjAdviceDeclaration getAdvice() {
        return advice;
    }
}