package org.caesarj.compiler.types;

/**
 * This is the AST representation of the A & B & C supertype 
 * 
 * @author Ivica Aracic
 */
public class CCompositeType extends CClassNameType {

    // CTODO
    public CCompositeType(CReferenceType refType[]) {
        super(refType[0].getQualifiedName());        
        this.refType = refType;
    }
        

    // --------------------------------------------------------------

    private CReferenceType refType[];
}
