package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CCjCompositeClassProxy;
import org.caesarj.util.UnpositionedError;

/**
 * 
 * @author Ivica Aracic
 */
public class CCompositeType extends CReferenceType {   

    public CCompositeType(
        String qualifiedName, 
        CReferenceType checkedInterfaceTypes[],
        CReferenceType checkedImplTypes[]
    ) {
        super(new CCjCompositeClassProxy(qualifiedName));
        this.qualifiedName = qualifiedName;
        this.checkedInterfaceTypes = checkedInterfaceTypes;
        this.checkedImplTypes = checkedImplTypes;        
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

    // --------------------------------------------------------------

    private String qualifiedName;
    private CReferenceType checkedInterfaceTypes[];
    private CReferenceType checkedImplTypes[];
}
