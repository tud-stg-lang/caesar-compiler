package org.caesarj.compiler.types;

import org.caesarj.compiler.context.CTypeContext;
import org.caesarj.compiler.export.CClass;
import org.caesarj.util.UnpositionedError;

/**
 * It represents a reference for a collaboration interface when it is woven
 * with an implememntation and a binding.
 * 
 * @author Walter Augusto Werner
 */
public class CciWeaveletReferenceType 
	extends CClassNameType
{
	/** The reference for the collaboration inteface type. */
	private CReferenceType collaborationInterface;
	/** The reference to the providing type. */
	private CReferenceType providing;
	/** The reference to the binding type. */
	private CReferenceType binding;
	
	/**
	 * @param collaborationInterface
	 * @param implementation
	 * @param binding
	 */
	public CciWeaveletReferenceType(CReferenceType collaborationInterface, 
		CReferenceType providing, CReferenceType binding)
	{
		super(collaborationInterface.getQualifiedName());
		this.collaborationInterface = collaborationInterface;
		this.providing = providing;
		this.binding = binding;
	}
	
	/**
	 * Checks the type of the binding and the implementation. 
	 */
	public CType checkType(CTypeContext context) 
		throws UnpositionedError
	{
	
		providing = 
			(CReferenceType) providing.checkType(context);
		
		binding = 
			(CReferenceType) binding.checkType(context);
		
		collaborationInterface =
			(CReferenceType) collaborationInterface.checkType(context);
		
		setClass(collaborationInterface.getCClass());
	
		return this;
	}
	
	/**
	 * @return the qualified name of its implentation.
	 */
	public String getProvidingQualifiedName()
	{
		return providing.getQualifiedName();
	}
	/**
	 * @return the qualified name of its binding.
	 */
	public String getBindingQualifiedName()
	{
		return binding.getQualifiedName();
	}
	
	/**
	 * The valid class is the collaboration interace class.
	 *
	 */
	public CClass getCClass()
	{
		return collaborationInterface.getCClass();
	}

	/**
	 * Returns the type of the binding class.
	 * 
	 * @return CReferenceType
	 */
	public CReferenceType getBindingType()
	{
		return binding;
	}

	/**
	 * Returns the type of the providing class.
	 * 
	 * @return CReferenceType
	 */
	public CReferenceType getProvidingType()
	{
		return providing;
	}
	/**
	 * Overrriden for turn the method public.
	 */
	public boolean isChecked()
	{
		return super.isChecked();
	}

}
