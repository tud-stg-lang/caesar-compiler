package org.caesarj.compiler.types;

import org.caesarj.compiler.export.CClass;


/**
 * this is type with partial mixed export,
 * source classes with their methods and field are still missing and will be
 * generated here 
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
        // merge source classes here!
        return super.getCClass();        
    }


    // --------------------------------------------------------------

    private CReferenceType refType[];
}
