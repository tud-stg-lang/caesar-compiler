package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * 
 * @author Ivica Aracic
 */
public class CCompositeType extends CClassNameType {   

    // CTODO CCompositeType
    public CCompositeType(CReferenceType refType[]) {
        super("<gen>");
        this.refType = refType;
    }
    
    
	public CType checkType(CTypeContext context) throws UnpositionedError {
        CReferenceType refTypeCopy[] = new CReferenceType[refType.length];
        CClass classes[] = new CClass[refType.length];
        

        /*        
        for(int i=0; i<refType.length; i++) {
            refTypeCopy[i] = (CReferenceType)refType[i].checkType(context);
            classes[i] = refTypeCopy[i].getCClass();            
        }
        
        // merge all classes here to one and map to final class name        
        CClass mixedClass = ExportMixer.instance().mix(classes);
        return new CComposite2Type(mixedClass, refTypeCopy);
        */
        return null;
    }

    // --------------------------------------------------------------

    private CReferenceType refType[];
}
