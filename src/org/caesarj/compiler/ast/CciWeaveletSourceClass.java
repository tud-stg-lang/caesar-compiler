package org.caesarj.compiler.ast;

import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JTypeDeclaration;

/**
 * It represents the source class of the weavelet classes. It contains 
 * a reference of <code>CciWeaveletReferenceType<\code> where it finds
 * the binding and the providing references.
 * 
 * @author Walter Augusto Werner
 */
public class CciWeaveletSourceClass 
	extends FjSourceClass
{
	/**
	 * The reference where I find the binding and the providing classes.
	 */
	private CciWeaveletReferenceType superCollaborationInterface;
	
	/**
	 * @param owner
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param qualifiedName
	 * @param typeVariables
	 * @param deprecated
	 * @param synthetic
	 * @param decl
	 */
	public CciWeaveletSourceClass(
		CClass owner,
		TokenReference where,
		int modifiers,
		String ident,
		String qualifiedName,
		CciWeaveletReferenceType superCollaborationInterface,
		CTypeVariable[] typeVariables,
		boolean deprecated,
		boolean synthetic,
		JTypeDeclaration decl)
	{
		super(
			owner,
			where,
			modifiers,
			ident,
			qualifiedName,
			typeVariables,
			deprecated,
			synthetic,
			decl);
		this.superCollaborationInterface = superCollaborationInterface;
	}

	/**
	 * If the method is not found in the context of this class, try the 
	 * binding and the providing references.
	 */
	public CClass lookupClass(CClass caller, String name)
		throws UnpositionedError
	{
		CClass foundClass = super.lookupClass(caller, name);

		if (foundClass == null && superCollaborationInterface.isChecked())
		{
			foundClass = searchClass(superCollaborationInterface
				.getProvidingType().getCClass(), caller, name);
			if (foundClass == null)
				foundClass = searchClass(superCollaborationInterface
					.getBindingType().getCClass(), caller, name);
		}
			
		return foundClass;
	}
	
	/**
	 * Looks for the class. It returns null if the class is not found.
	 * 
	 * @param searcher
	 * @param caller
	 * @param name
	 * @return
	 * @throws UnpositionedError
	 */
	protected CClass searchClass(CClass searcher, CClass caller, String name)
		throws UnpositionedError
	{
		CClass foundClass = null;
		while (searcher != null)
		{
			foundClass = searcher.lookupClass(caller, name);
			if (foundClass != null)
				return foundClass;
			searcher = searcher.getOwner();
		}
		return foundClass;		
	}
}
