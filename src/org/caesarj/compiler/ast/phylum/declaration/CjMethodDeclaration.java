package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

// FJKEEP we will need this one later
public class CjMethodDeclaration extends JMethodDeclaration {

    public CjMethodDeclaration(
        TokenReference where,
        int modifiers,
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
            returnType,
            ident,
            parameters,
            exceptions,
            body,
            javadoc,
            comments);
    }

}
