package org.caesarj.compiler.cclass;

import org.caesarj.compiler.context.CContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.CType;

/**
 * ...
 * 
 * @author Ivica Aracic
 */
public class CastUtils {

    private static CastUtils singleton = new CastUtils();

    public static CastUtils instance() {
        return singleton;
    }

    private CastUtils() {        
    }
    
    public CType castFrom(CContext context, CType t, CClass contextClass) {
        
        CType res = null;
        //CArrayType arrayType = t.isArrayType() ? (CArrayType)t : null;        
        //CType type = arrayType != null ? arrayType.getBaseType() : t;
        CType type = t;
        
        if(type.isClassType()) {
		    CClass tClass = type.getCClass();
		        
		    if(tClass.isMixinInterface() && contextClass.isMixin()) {
	            String tNewClassQn = 
	                context.getEnvironment().getCaesarTypeSystem().
	                	findInContextOf(
	                	    tClass.getQualifiedName(),
		                    contextClass.convertToIfcQn()
		                );
	            
	            if(tNewClassQn != null) {
		            CClass newPrefixClass = 
		                context.getClassReader().loadClass(
		                    context.getTypeFactory(),
		                    tNewClassQn
		                );
		            
		            CType newT = newPrefixClass.getAbstractType();          
		            
		            res = newT;
	            }	        
	        }  
        }
        
        /*
        if(arrayType != null) {
            res = new CArrayType(res, arrayType.getArrayBound());
        }
        */
        
        return res;
    }
}
