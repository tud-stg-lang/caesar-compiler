package org.caesarj.compiler.ast;

import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClass;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JTypeDeclaration;

/**
 * @author walter
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CciWeaveletSourceClass extends CciSourceClass
{
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

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.CClass#lookupClass(org.caesarj.kjc.CClass, java.lang.String)
	 */
	public CClass lookupClass(CClass caller, String name)
		throws UnpositionedError
	{
		CClass foundClass = super.lookupClass(caller, name);

		if (foundClass == null && superCollaborationInterface.isChecked())
		{
			foundClass = searchClass(superCollaborationInterface
				.getImplementationType().getCClass(), caller, name);
			if (foundClass == null)
				foundClass = searchClass(superCollaborationInterface
					.getBindingType().getCClass(), caller, name);
		}
			
		return foundClass;
	}
	
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
