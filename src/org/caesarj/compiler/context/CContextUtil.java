/*
 * Created on 25.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.compiler.context;

import org.caesarj.compiler.export.CClass;
import org.caesarj.util.InconsistencyException;

/**
 * @author vaidas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CContextUtil {
	static public CContext findClassContext(CContext ctx, CClass clazz) {
		while (ctx != null && 
        	(!(ctx instanceof CClassContext) || 
        	((CClassContext)ctx).getCClass() != clazz)) {
			ctx = ctx.getParentContext();
		}
		return ctx;
	}
	
	static public int getRelativeDepth(CContext ctxInner, CContext ctxOuter) {
		int k = 0;
		while (ctxInner != null && ctxInner != ctxOuter) {
			ctxInner = ctxInner.getParentContext();
			k++;
        }
		if (ctxInner == null) {
			throw new InconsistencyException();
		}
		return k;		
	}
}
