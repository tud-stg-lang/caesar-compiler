package org.caesarj.compiler.export;

import java.util.ArrayList;

import org.caesarj.compiler.types.CCompositeType;
import org.caesarj.compiler.types.CReferenceType;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CCjCompositeClassProxy extends CBadClass {

    private CCompositeType compositeType;

    public CCjCompositeClassProxy(CCompositeType compositeType, String qualifiedName) {
        super(qualifiedName);
        this.compositeType = compositeType;
    }

    public boolean isAccessible(CClass from) {
        return true;
    }   
    
	public boolean descendsFrom(CReferenceType from, CReferenceType[] actuals, CReferenceType[] substitution) {            		
        CReferenceType[] interfaces = compositeType.getInterfaceList();
        for(int i=0; i<interfaces.length; i++)
            if(interfaces[i].getCClass().descendsFrom(from, actuals, substitution))
                return true;
        return false;
	}

    protected boolean descendsFrom(CClass from, ArrayList history) {
        CReferenceType[] interfaces = compositeType.getInterfaceList();
        for(int i=0; i<interfaces.length; i++)
            if(interfaces[i].getCClass().descendsFrom(from, history))
                return true;
        return false;
    }
    
    public boolean descendsFrom(CClass from) {
        CReferenceType[] interfaces = compositeType.getInterfaceList();
        for(int i=0; i<interfaces.length; i++)
            if(interfaces[i].getCClass().descendsFrom(from))
                return true;
        return false;
	}

    

}
