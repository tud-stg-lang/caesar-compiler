package org.caesarj.compiler.ast;

import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeContext;

/**
 * It represents a reference for a collaboration interface when it is woven
 * with an implememntation and a binding.
 * 
 * @author Walter Augusto Werner
 */
public class CciWeaveletReferenceType extends CClassNameType
{
	/** The reference for the collaboration inteface type. */
	private CReferenceType collaborationInterface;
	/** The reference to the implementation type. */
	private CReferenceType implementation;
	/** The reference to the binding type. */
	private CReferenceType binding;
	
	/**
	 * @param collaborationInterface
	 * @param implementation
	 * @param binding
	 */
	public CciWeaveletReferenceType(CReferenceType collaborationInterface, 
		CReferenceType implementation, CReferenceType binding)
	{
		super(collaborationInterface.getQualifiedName());
		this.implementation = implementation;
		this.binding = binding;
	}
	
	/**
	 * Checks the type of the binding and the implementation. 
	 */
	public CType checkType(CTypeContext context) 
		throws UnpositionedError
	{
		implementation = 
			(CReferenceType) implementation.checkType(context);
			
		binding = 
			(CReferenceType) binding.checkType(context);
			
		return super.checkType(context);
	}
	
	/**
	 * @return the qualified name of its implentation.
	 */
	public String getImplementationQualifiedName()
	{
		return implementation.getQualifiedName();
	}
	/**
	 * @return the qualified name of its binding.
	 */
	public String getBindingQualifiedName()
	{
		return binding.getQualifiedName();
	}
	
}
