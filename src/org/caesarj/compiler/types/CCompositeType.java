package org.caesarj.compiler.types;


/**
 * Holds refernce to composite type like A & B & C
 * 
 * @author Ivica Aracic
 */
public class CCompositeType extends CClassNameType {

    private CReferenceType[] compositeList;

	public CCompositeType(CReferenceType[] compositeList) {
		// TODO !!!hack!!! generate here the name of composite class		
		super("java/lang/Object");			
        this.compositeList = compositeList;
        
        if(compositeList.length >= 0 )
			this.qualifiedName = compositeList[0].getQualifiedName().intern();
	}

    
    public CReferenceType[] getCompositeList() {
        return compositeList;
    }

}
