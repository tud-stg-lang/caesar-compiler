package org.caesarj.compiler.ast;

import org.caesarj.kjc.CReferenceType;

/**
 * @author Walter Augusto Werner
 */
public interface CciClass
{
	public CReferenceType[] getBindings();
	public CReferenceType[] getImplementations();
	

}
