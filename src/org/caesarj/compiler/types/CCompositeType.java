package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.util.UnpositionedError;


public class CCompositeType extends CClassNameType {   

    // CTODO
    public CCompositeType(CReferenceType refType[]) {
        super("<UNDEFINED>");        
        this.refType = refType;
    }
    
    public CType checkType(CTypeContext context) throws UnpositionedError {
        CReferenceType refTypeCopy[] = new CReferenceType[refType.length];
                
        for(int i=0; i<refType.length; i++)
            refTypeCopy[i] = (CReferenceType)refType[i].checkType(context);
            
        // merge all classes here to one and map to final class name
            
        return new CComposite2Type(refTypeCopy[0].getCClass(), refTypeCopy);
    }

    // --------------------------------------------------------------

    private CReferenceType refType[];
}
