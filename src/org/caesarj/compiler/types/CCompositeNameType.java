package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * 
 * @author Ivica Aracic
 */
public class CCompositeNameType extends CClassNameType {   

    public CCompositeNameType(CClassNameType refType[]) {
        super("<gen>");
        this.refType = refType;
    }
    
    
	public CType checkType(CTypeContext context) throws UnpositionedError {
        
        CClass         classes[]               = new CClass[refType.length];
        CReferenceType checkedInterfaceTypes[] = new CReferenceType[refType.length];
        CReferenceType checkedImplTypes[]      = new CReferenceType[refType.length];
        
        for(int i=0; i<refType.length; i++) {
            checkedInterfaceTypes[i] = 
                (CReferenceType)refType[i].checkType(context);

            checkedImplTypes[i] = 
                (CReferenceType)(
                    new CClassNameType(
                        checkedInterfaceTypes[i].getImplQualifiedName()
                    )
                ).checkType(context);
                        
            classes[i] = checkedImplTypes[i].getCClass();
        }
        
        return new CCompositeType(checkedInterfaceTypes, checkedImplTypes);            
    }


    public CClassNameType[] getTypeList() {
        return refType;
    }

    // --------------------------------------------------------------

    private CClassNameType refType[];
}
