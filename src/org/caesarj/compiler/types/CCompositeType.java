package org.caesarj.compiler.types;

import org.caesarj.compiler.export.CClass;

/**
 * Holds refernce to composite type like A & B & C
 * 
 * @author Ivica Aracic
 */
public class CCompositeType extends CReferenceType {

    private CReferenceType[] compositeList;

	public CCompositeType(CReferenceType[] compositeList) {
		super();
        this.compositeList = compositeList;
	}

	public CCompositeType(CReferenceType[] compositeList, CClass clazz) {
		super(clazz);
        this.compositeList = compositeList;
	}
    
    public CReferenceType[] getCompositeList() {
        return compositeList;
    }

}
