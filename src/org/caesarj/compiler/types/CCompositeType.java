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
        CReferenceType checkedInterfaceTypes[],
        CReferenceType checkedImplTypes[]
    ) {
        super();        
        this.qualifiedName = "<gen>";
        this.checkedInterfaceTypes = checkedInterfaceTypes;
        this.checkedImplTypes = checkedImplTypes;
        setClass(new CCjCompositeClassProxy(this, qualifiedName));        
    }
    
	public CType checkType(CTypeContext context) throws UnpositionedError {
        return this;
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
    
    /**
	 *
	 */

	public boolean isAssignableTo(CTypeContext context, CType dest) {
		for(int i=0; i<checkedInterfaceTypes.length; i++)
            if(checkedInterfaceTypes[i].isAssignableTo(context, dest))
                return true;
        return false;
	}     

    // --------------------------------------------------------------

    private String qualifiedName;
    private CReferenceType checkedInterfaceTypes[];
    private CReferenceType checkedImplTypes[];
	    
}
