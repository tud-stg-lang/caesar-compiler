package org.caesarj.compiler.ast;

import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;



/**
 * AST element for collaboration interface declarations.
 * It is created only when an interface was defined with 
 * the modifier <b>collaboration</b>.
 * 
 * In the collaboration interfaces only <b>provided</b> or 
 * <b>expected</b> methods can be defined.
 * 
 * @author Walter Augusto Werner
 */
public class CciCollaborationInterfaceDeclaration 
	extends CciInterfaceDeclaration
{
	/**
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param typeVariables
	 * @param interfaces
	 * @param fields
	 * @param methods
	 * @param inners
	 * @param initializers
	 * @param javadoc
	 * @param comment
	 */
	public CciCollaborationInterfaceDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment)
	{
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			interfaces,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
	}

	/**
	 * Is it a collaboration interface?
	 */
	public boolean isCollaborationInterface()
	{
		return true;
	}

	public void append(JTypeDeclaration type)
	{
		JTypeDeclaration[] newInners = new JTypeDeclaration[inners.length + 1];
		System.arraycopy(inners, 0, newInners, 0, inners.length);
		newInners[inners.length] = type;
		inners = newInners;
	}

	
//	/**
//	 * It calls for the super implementation of it and after adds the modifier
//	 * OVERRIDE for the inner interfaces that are already defined in the super
//	 * interface, being the interfaces overridings of the virtual types, 
//	 * it also sets the super class of the proxy. 
//	 */
//	public void checkInterface(CContext context) 
//		throws PositionedError
//	{
//		super.checkInterface(context);
//		FjTypeSystem typeSystem = new FjTypeSystem();
//		for (int i = 0; i < interfaces.length; i++)
//		{
//			if (CModifier.contains(interfaces[i].getCClass().getModifiers(), 
//				CCI_COLLABORATION))
//			{
//				JTypeDeclaration[] inners = getInners();
//				for (int j = 0; j < inners.length; j++)
//				{
//					//It is done only for interfaces
//					if (inners[j] instanceof CciInterfaceDeclaration)
//					{
//						//If the super class declares the inner with the same
//						//name as my inner interface, then the modifier 
//						//OVERRIDEN is added and the super type of the proxy
//						//is seted.  
//						CReferenceType superType = typeSystem.declaresInner(
//							interfaces[i].getCClass(), 
//							inners[j].getCClass().getIdent());
//
//						if (superType != null)
//						{
//							inners[j].setModifiers(
//								inners[j].getModifiers() | FJC_OVERRIDE);
//							//The proxy super type is calculated now
//							String innerSuperTypeName = 
//								CciConstants.toCollaborationInterfaceImplName(
//									interfaces[i].getIdent()) 
//								+ "$" +
//								CciConstants.toCollaborationInterfaceImplName(
//									superType.getIdent());
//							try
//							{
//
//								CReferenceType innerSuperType = (CReferenceType)
//									new CClassNameType(innerSuperTypeName)
//										.checkType(context);  
//								 
//								((CciInterfaceDeclaration)inners[j])
//									.setProxyDeclarationSuperType(
//										innerSuperType);
//							}
//							catch (UnpositionedError e)
//							{
//								throw e.addPosition(getTokenReference());
//							}
//							
//						}
//					}
//				}
//			}
//		}
//	}
}
