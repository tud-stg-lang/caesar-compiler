package org.caesarj.compiler.types;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.ExportMixer;
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
            checkedImplTypes[i] = 
                (CReferenceType)(new CClassNameType(refType[i].getQualifiedName()+"_Impl")).checkType(context);
            
            checkedInterfaceTypes[i] = 
                (CReferenceType)refType[i].checkType(context);
            
            classes[i] = checkedInterfaceTypes[i].getCClass();
        }
        
        try {
            CClass mixinList[] = ExportMixer.instance().mix(classes);
            String mixedClassName = ExportMixer.instance().generateClassName(mixinList);
            
            // CTODO
            return new CCompositeType("$gen/"+mixedClassName, checkedInterfaceTypes, checkedImplTypes);
            //return checkedImplTypes[0];
        }
        catch (Exception e) {
            // CTODO create correct error message
			throw new UnpositionedError(CaesarMessages.CANNOT_CREATE, new Object[]{"composite supertype"}, e);
		}
    }

    public CClassNameType[] getTypeList() {
        return refType;
    }

    // --------------------------------------------------------------

    private CClassNameType refType[];
}
