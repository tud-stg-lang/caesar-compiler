package org.caesarj.compiler.ast;

import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;

/**
 * @author Walter Augusto Werner
 */
public class CciWeaveletReferenceType extends CClassNameType
{
	private CReferenceType collaborationInterface;
	private CReferenceType implementation;
	private CReferenceType binding;

	/**
	 * @param clazz
	 */
	public CciWeaveletReferenceType(CReferenceType collaborationInterface, 
		CReferenceType implementation, CReferenceType binding)
	{
		super(collaborationInterface.getQualifiedName());
		this.implementation = implementation;
		this.binding = binding;
	}

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.CType#checkType(org.caesarj.kjc.CTypeContext)
	 */
	public CType checkType(CTypeContext context) throws UnpositionedError
	{
		implementation = 
			(CReferenceType) implementation.checkType(context);
			
		binding = 
			(CReferenceType) binding.checkType(context);
			
		return super.checkType(context);
	}
	
	public String getImplementationQualifiedName()
	{
		return implementation.getQualifiedName();
	}
	
	public String getBindingQualifiedName()
	{
		return binding.getQualifiedName();
	}
	
}
