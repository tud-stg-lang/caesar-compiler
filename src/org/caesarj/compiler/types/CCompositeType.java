package org.caesarj.compiler.types;

import java.security.MessageDigest;

import org.caesarj.compiler.constants.CaesarMessages;
import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CBadClass;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.export.ExportMixer;
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
        super(new CBadClass(qualifiedName));
        this.qualifiedName = qualifiedName;
        this.checkedInterfaceTypes = checkedInterfaceTypes;
        this.checkedImplTypes = checkedImplTypes;        
    }
    
    
	public CType checkType() throws UnpositionedError {
        // CTODO this one should return the final biary representation of the type
        // return this for now
        return this;        
    }
    
	public String getQualifiedName() {
		return qualifiedName;
	}
    
    public CReferenceType[] getInterfaceList() {
        return checkedInterfaceTypes; 
    }

    public CReferenceType[] getClassList() {
        return checkedInterfaceTypes; 
    }

    // --------------------------------------------------------------

    private String qualifiedName;
    private CReferenceType checkedInterfaceTypes[];
    private CReferenceType checkedImplTypes[];
}
