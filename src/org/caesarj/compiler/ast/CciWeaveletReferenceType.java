package org.caesarj.compiler.ast;

import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
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
		this.collaborationInterface = collaborationInterface;
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
		
		collaborationInterface =
			(CReferenceType) collaborationInterface.checkType(context);
		
		setClass(collaborationInterface.getCClass());
//		this.qualifiedName = collaborationInterface.getQualifiedName();
//		
//		super.checkType(context);
		
		return this;
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
	
	public void setBindingType(CReferenceType bindingType)
	{
		binding = bindingType;
	}
	public void setCollaborationInterfaceType(
		CReferenceType collaborationInterfaceType)
	{
		collaborationInterface = collaborationInterfaceType;
	}	
	public void setImplementationType(CReferenceType implementationType)
	{
		implementation = implementationType;
	}
	
	/* (non-Javadoc)
	 * @see org.caesarj.kjc.CType#getCClass()
	 */
	public CClass getCClass()
	{
		return collaborationInterface.getCClass();
	}

	/**
	 * @return
	 */
	public CReferenceType getBindingType()
	{
		return binding;
	}

	/**
	 * @return
	 */
	public CReferenceType getImplementationType()
	{
		return implementation;
	}


	/* (non-Javadoc)
	 * @see org.caesarj.kjc.CReferenceType#isChecked()
	 */
	public boolean isChecked()
	{
		return super.isChecked();
	}

}
