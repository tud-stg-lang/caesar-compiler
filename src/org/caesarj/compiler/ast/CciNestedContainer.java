/*
 * Created on 16.07.2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.caesarj.compiler.ast;

import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.JTypeDeclaration;

/**
 * @author walter
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface CciNestedContainer
{
	public void fixBindingMethods(CReferenceType binding);
	public JTypeDeclaration[] getInners();
	
}
