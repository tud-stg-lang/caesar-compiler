package org.caesarj.compiler.types;

import org.caesarj.compiler.export.CClass;


/**
 * This is the AST representation of the A & B & C supertype 
 * 
 * @author Ivica Aracic
 */
public class CComposite2Type extends CReferenceType {
    
    // CTODO
    public CComposite2Type(CClass clazz, CReferenceType refType[]) {
        super(clazz);        
        this.refType = refType;
    }
          
          
    public CClass getCClass() {
        // merge class here!
        return super.getCClass();        
    }


    // --------------------------------------------------------------

    private CReferenceType refType[];
}
