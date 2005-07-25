/*
 * Created on 22.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.family;

import org.caesarj.compiler.types.CReferenceType;
import org.caesarj.util.UnpositionedError;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ClassAccess extends MemberAccess {

	public ClassAccess(boolean finalPath, Path prefix, String field, CReferenceType type) {
        super(finalPath, prefix, field, type);
    }
    
    public Path clonePath() {
        return new ClassAccess(finalPath, prefix==null ? null : prefix.clonePath(), name, type);
    }
    
    public Path getTypePath() throws UnpositionedError {
        return new ContextExpression(null, 0, null);
    }
}
