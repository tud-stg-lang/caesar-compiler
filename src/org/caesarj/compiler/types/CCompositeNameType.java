package org.caesarj.compiler.types;

import java.util.StringTokenizer;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.ExportMixer;
import org.caesarj.mixer.Linearizator;
import org.caesarj.mixer.Mixer;
import org.caesarj.mixer.MixinList;
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
        
        try {
            MixinList mixinList = ExportMixer.instance().mix(classes);
            String mixedClassName = mixinList.generateClassName();
            
            // IVICA !!!
            mixedClassName = "generated/_VCTestCase_1_Impl$ColG_Impl$XXX";
            return new CCompositeType(mixedClassName, checkedInterfaceTypes, checkedImplTypes);            
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
