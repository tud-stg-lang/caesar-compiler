package org.caesarj.compiler.ast;

import org.caesarj.compiler.CciConstants;
import org.caesarj.compiler.FjConstants;
import org.caesarj.compiler.JavaStyleComment;
import org.caesarj.compiler.JavadocComment;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.UnpositionedError;
import org.caesarj.kjc.CClassNameType;
import org.caesarj.kjc.CContext;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JConstructorCall;
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JReturnStatement;
import org.caesarj.kjc.JStatement;
import org.caesarj.kjc.JThisExpression;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.kjc.JUnqualifiedInstanceCreation;
import org.caesarj.kjc.KjcMessages;
import org.caesarj.kjc.TypeFactory;

/**
 * @author Walter Augusto Werner
 */
public class CciWeaveletClassDeclaration 
	extends FjClassDeclaration
{
	private TypeFactory typeFactory;
	private CciWeaveletReferenceType superCollaborationInterface;

	/**
	 * @param where
	 * @param modifiers
	 * @param ident
	 * @param typeVariables
	 * @param superClass
	 * @param interfaces
	 * @param bindings
	 * @param fields
	 * @param methods
	 * @param inners
	 * @param initializers
	 * @param javadoc
	 * @param comment
	 */
	public CciWeaveletClassDeclaration(
		TokenReference where,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		CciWeaveletReferenceType superCollaborationInterface,
		CReferenceType[] interfaces,
		JFieldDeclaration[] fields,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] inners,
		JPhylum[] initializers,
		JavadocComment javadoc,
		JavaStyleComment[] comment,
		TypeFactory typeFactory)
	{
		super(
			where,
			modifiers,
			ident,
			typeVariables,
			new CClassNameType(
				CciConstants.toCollaborationInterfaceImplName(
					superCollaborationInterface.getQualifiedName())),
			interfaces,
			null,
			fields,
			methods,
			inners,
			initializers,
			javadoc,
			comment);
			
			this.typeFactory = typeFactory;
			this.superCollaborationInterface = superCollaborationInterface;
			updateConstructors();
			addAccessors();
	}
	
	

	/* (non-Javadoc)
	 * @see org.caesarj.kjc.JTypeDeclaration#join(org.caesarj.kjc.CContext)
	 */
	public void join(CContext context) throws PositionedError
	{
		try
		{
			super.join(context);
		}
		catch (PositionedError e)
		{
			if (e.getFormattedMessage().getDescription()
				== KjcMessages.CLASS_EXTENDS_INTERFACE)
			{
				String ifcName =
					e.getFormattedMessage().getParams()[0].toString();
				CType interfaceType;
				try
				{
					interfaceType = new CClassNameType(ifcName)
						.checkType(context);
				}
				catch (UnpositionedError e1)
				{
					throw e1.addPosition(getTokenReference());
				}
				if (! CModifier.contains(interfaceType.getCClass().getModifiers(), 
					CCI_COLLABORATION))
				{
					throw e;
				}
			}
		}
	}
	public String getImplementationTypeName()
	{
		return superCollaborationInterface.getImplementationQualifiedName();
	}
	public String getBindingTypeName()
	{
		return superCollaborationInterface.getBindingQualifiedName();
	}
	
	protected void updateConstructors()
	{
		boolean hasConstructor = false;
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i] instanceof FjConstructorDeclaration)
			{
				((FjConstructorDeclaration)methods[i])
					.updateWeaveletConstructor(this);
				hasConstructor = true;
			}
		}
		
		if (! hasConstructor)
			addMethod(createConstructor());

	}

	protected JMethodDeclaration createConstructor()
	{
		//TODO: Deal with constructor parameters
		JExpression[] arguments = new JExpression[]
		{
			new JUnqualifiedInstanceCreation(getTokenReference(),
				new CClassNameType(
					FjConstants.toImplName(getImplementationTypeName())), 
				new JExpression[0]),
			new JUnqualifiedInstanceCreation(getTokenReference(),
				new CClassNameType(
					FjConstants.toImplName(getBindingTypeName())), 
				new JExpression[0]),
		};
		JConstructorCall constructorCall = new FjConstructorCall(
			getTokenReference(), 
			false, 
			arguments);
		
		JConstructorBlock constructorBody = new FjConstructorBlock(
			getTokenReference(), 
			constructorCall, 
			new JStatement[0]);
		
		return new FjConstructorDeclaration(
			getTokenReference(), 
			ACC_PUBLIC, 
			ident, 
			new JFormalParameter[0], 
			new CReferenceType[0], 
			constructorBody, 
			null, 
			null, 
			typeFactory);
	}
	
	protected void addAccessors()
	{
		addMethod(
			createAccessor(
				CciConstants.IMPLEMENTATION_FIELD_NAME, 
				getImplementationTypeName()));
		
		addMethod(
			createAccessor(
				CciConstants.BINDING_FIELD_NAME, 
				getBindingTypeName()));
	}
	
	protected JMethodDeclaration createAccessor(String name, String typeName)
	{
		JStatement[] statements =
			new JStatement[] 
			{
				new JReturnStatement(
					getTokenReference(),
					new FjCastExpression(
						getTokenReference(), 
						new FjFieldAccessExpression(
							getTokenReference(), 
							new JThisExpression(getTokenReference()), 
							name),
						new CClassNameType(typeName)),
					null)
			};
		JBlock body = new JBlock(getTokenReference(), statements, null);

		return new FjMethodDeclaration(
			getTokenReference(), 
			ACC_PUBLIC, 
			new CTypeVariable[0],
			new CClassNameType(typeName),
			CciConstants.toAccessorMethodName(name),
			new JFormalParameter[0],
			new CReferenceType[0],
			body,
			null, null);
	}

}
