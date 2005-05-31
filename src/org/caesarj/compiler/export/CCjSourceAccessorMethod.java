package org.caesarj.compiler.export;

import org.caesarj.compiler.ast.phylum.declaration.JAccessorMethod;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CCjSourceAccessorMethod extends CSourceMethod {

    protected JAccessorMethod decl;
    
    public CCjSourceAccessorMethod(
        JAccessorMethod decl,
        CClass owner, 
        int modifiers, 
        String ident,
        CType returnType,
		JFormalParameter[] params,
        CType[] paramTypes, 
        CReferenceType[] exceptions,
        boolean deprecated, 
        boolean synthetic, 
        JBlock body
    ) {
        super(owner, modifiers, ident, returnType, params, paramTypes, exceptions,
                deprecated, synthetic, body);
        this.decl = decl;
    }
    
    public JAccessorMethod getDecl() {
        return decl;
    }
}