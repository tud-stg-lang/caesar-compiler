/*
 * Created on 19.11.2004
 */
package org.caesarj.compiler.ast.phylum.declaration;

import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.ast.JavaStyleComment;
import org.caesarj.compiler.ast.JavadocComment;
import org.caesarj.compiler.ast.phylum.statement.JBlock;
import org.caesarj.compiler.ast.phylum.statement.JStatement;
import org.caesarj.compiler.ast.phylum.variable.JFormalParameter;
import org.caesarj.compiler.context.CClassContext;
import org.caesarj.compiler.context.CMethodContext;
import org.caesarj.compiler.export.CMethod;
import org.caesarj.compiler.export.CSourceMethod;
import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.compiler.types.CType;
import org.caesarj.compiler.types.CVoidType;
import org.caesarj.util.InconsistencyException;
import org.caesarj.util.TokenReference;

/**
 * @author Karl Klose
 */
public class CClassBodyContext extends CMethodContext {

    public static final String METHOD_NAME = "$TEMPORARY_METHOD_ENTRY"; 
    
    public CClassBodyContext( CClassContext cctx, KjcEnvironment env ){
        super(cctx, env, null);
    }
    
    public CMethod getCMethod() {
//        System.err.println("Invalid call: CClassBodyContext.getCMethod");
//        throw new InconsistencyException();
        
        
        return new CSourceMethod(
                getClassContext().getCClass(),ACC_PRIVATE,METHOD_NAME,new CVoidType(),
                new CType[0], new CReferenceType[0],false, false,
                	new JBlock(
                	        new TokenReference("",0),
                	        new JStatement[0],
                	        new JavaStyleComment[0]
                	)
            	);
    }
    
    public JMethodDeclaration getMethodDeclaration() {
        return new JMethodDeclaration(
                new TokenReference("",0),
                ACC_PUBLIC,
                new CVoidType(),
                METHOD_NAME,
                new JFormalParameter[0],
                new CReferenceType[0],
               	new JBlock(
            	        new TokenReference("",0),
            	        new JStatement[0],
            	        new JavaStyleComment[0]
            	),
            	new JavadocComment("",false,false),
            	new JavaStyleComment[0]
        );
     }
    
    
}
