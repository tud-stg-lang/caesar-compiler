package org.caesarj.compiler.types;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CCjCompositeClassProxy;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.ExportMixer;
import org.caesarj.mixer.MixinList;
import org.caesarj.util.UnpositionedError;

/**
 * 
 * @author Ivica Aracic
 */
public class CCompositeType extends CReferenceType {   

    public CCompositeType(
        MixinList mixinList, 
        CReferenceType checkedInterfaceTypes[],
        CReferenceType checkedImplTypes[]
    ) {
        super();
        //this.qualifiedName = "generated/_VCTestCase_1_Impl$ColG_Impl$XXX";        
        this.qualifiedName = mixinList.generateClassName();
        this.mixinList = mixinList;
        this.checkedInterfaceTypes = checkedInterfaceTypes;
        this.checkedImplTypes = checkedImplTypes;
        setClass(new CCjCompositeClassProxy(this, qualifiedName));        
    }
    
	public CType checkType(CTypeContext context) throws UnpositionedError {
        // at this time point mixed type should be generated an ready to load
        CReferenceType type = 
            context.getTypeFactory().createType(getQualifiedName(), true);
            
        return type.checkType(context);
    }
    
	public String getQualifiedName() {
		return qualifiedName;
	}
    
    public CReferenceType[] getInterfaceList() {
        return checkedInterfaceTypes; 
    }

    public CReferenceType[] getClassList() {
        return checkedImplTypes; 
    }
    
    public MixinList getMixinList() {
        return mixinList;
    }
    
    /**
	 *
	 */

	public boolean isAssignableTo(CTypeContext context, CType dest) {
		for(int i=0; i<checkedInterfaceTypes.length; i++)
            if(checkedInterfaceTypes[i].isAssignableTo(context, dest))
                return true;
        return false;
	}

    public boolean isAssignableTo(CTypeContext context, CType dest, boolean inst) {
        for(int i=0; i<checkedInterfaceTypes.length; i++)
            if(checkedInterfaceTypes[i].isAssignableTo(context, dest, inst))
                return true;
        return false;
    }

    public boolean isAssignableTo(
    	CTypeContext context,
    	CType dest,
    	CReferenceType[] substitution) {
        for(int i=0; i<checkedInterfaceTypes.length; i++)
            if(checkedInterfaceTypes[i].isAssignableTo(context, dest, substitution))
                return true;
        return false;
    }

    public boolean isAssignableTo(
    	CTypeContext context,
    	CType dest,
    	CReferenceType[] substitution,
    	boolean inst) {
        for(int i=0; i<checkedInterfaceTypes.length; i++)
            if(checkedInterfaceTypes[i].isAssignableTo(context, dest, substitution, inst))
                return true;
        return false;
    }

    // --------------------------------------------------------------

    public static CCompositeType createCompositeType(CReferenceType type1, CReferenceType type2) 
    throws UnpositionedError {
        try {
            CReferenceType interfaces[] = new CReferenceType[]{type1.getCClass().getInterfaces()[0], type2.getCClass().getInterfaces()[0]};
            CReferenceType impls[] = new CReferenceType[]{type1, type2};
    
            MixinList mixinList = ExportMixer.instance().mix(new CClass[]{type1.getCClass(), type2.getCClass()});
    
            return 
                new CCompositeType(
                    mixinList,
                    interfaces,
                    impls
                );                
        }
        catch (Exception e) {
            // TODO change error message
            e.printStackTrace();
			throw new UnpositionedError(CaesarMessages.CANNOT_CREATE);
		}
    }

    // --------------------------------------------------------------

    private MixinList mixinList;
    private String qualifiedName;
    private CReferenceType checkedInterfaceTypes[];
    private CReferenceType checkedImplTypes[];
	    
}
