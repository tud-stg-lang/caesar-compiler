package org.caesarj.compiler.ast.phylum.variable;

import org.caesarj.compiler.family.FjFamily;
import org.caesarj.compiler.types.CType;
import org.caesarj.util.TokenReference;

// FJPULLUP
public class FjFormalParameter extends JFormalParameter {

	protected FjFamily family;

	public FjFormalParameter(
		TokenReference where,
		int desc,
		CType type,
		String ident,
		boolean isFinal) {
		super(where, desc, type, ident, isFinal);
		cacheDesc = desc;
		typeSwitched = false;
	}
	protected int cacheDesc;
	protected boolean typeSwitched;

	public Object clone() {
		return new FjFormalParameter(
			getTokenReference(),
			cacheDesc,
			type,
			getIdent(),
			isFinal()
		);
	}

	/* FJRM
	protected boolean scip() {
		if( name.equals( FjConstants.SELF_NAME ) )
			// self parameter
			// => resolving not nescessary
			return true;

		if( !(type instanceof CReferenceType) )
			// => resolving not nescessary
			return true;
		
		return false;
	}

	public void introduceDownCastVariable(CTypeContext context)
		throws UnpositionedError 
	{
			
		FjMethodDeclaration method = (FjMethodDeclaration)
			((FjAdditionalContext) context).peekContextInfo(1);
		JTypeDeclaration clazz = (JTypeDeclaration)
			((FjAdditionalContext) context).peekContextInfo(2);
		
		int clazzModifiers = clazz.getModifiers();
		if (CModifier.contains(clazzModifiers, (FJC_CLEAN | FJC_VIRTUAL))
			 && ! FjConstants.isImplementationMethodName(method.getIdent())) 
		{
			// downcast in clean classes only needed
			// in implementation method
			return;
		}

	  	FjTypeSystem fjts = new FjTypeSystem();
	  	FjFamily family = getFamily();
		CReferenceType lowerBound = fjts.lowerBound(
			context,
			family.getType().getCClass(),
			((CReferenceType) type).getIdent());
		
		TokenReference ref = getTokenReference();
		//Now, create the expression to be casted before.		
		JExpression castedExpression = createCastedExpression(
			lowerBound, family, clazz);
 
		method.prependStatement(
			new JExpressionStatement(
				getTokenReference(),
				new JAssignmentExpression(
					getTokenReference(),
					new FjNameExpression(
						getTokenReference(),
						name),
					new FjCastExpression(
						getTokenReference(),
						castedExpression,
						lowerBound)),
				null));
				
		method.prependStatement(
			new JVariableDeclarationStatement( 
				ref,
				new FjFamilyVariableDefinition(
					ref,
					0,
					lowerBound,
					name,
					null,
					getFamily() ),
				null));

		name = FjConstants.renameParameter(name);
	}
	*/
	
	/**
	 * Creates the expression to be casted in the 
	 * when down casting the parameter. 
	 * 
	 * Used by introduceCownCastVariable(context)
	 * 
	 * @param lowerBound
	 * @param family
	 * @return
	 */
	/* FJRM
	private JExpression createCastedExpression(
		CReferenceType lowerBound, FjFamily family, JTypeDeclaration clazz)
	{
		FjTypeSystem typeSystem = new FjTypeSystem();
		TokenReference ref = getTokenReference();
		//simple parameter access expression.
		JExpression parameterAccess = 
			new FjNameExpression(
				ref,
				FjConstants.renameParameter(name));
		
		CClass lowerBoundOwner = lowerBound.getCClass().getOwner();
		int clazzModifiers = lowerBoundOwner.getModifiers();
		if (CModifier.contains(clazzModifiers, (FJC_CLEAN | FJC_VIRTUAL)))
		{
			if (clazz instanceof FjCleanClassDeclaration)
			{
				FjCleanClassDeclaration cleanClass = 
					(FjCleanClassDeclaration) clazz;
				//If it depends on this it must be adapted
				if (family.isThis()) 
				{
					if (typeSystem.declaresInner(
						cleanClass.getCleanInterface().getCClass(), 
							lowerBound.getIdent()) != null)
					{
						//parameter._adapt<TypeName>(self)
						return
							new FjMethodCallExpression(
								ref,
								parameterAccess,
								CciConstants.toAdaptMethodName(
									lowerBound.getIdent()),
							new JExpression[]
							{
								new FjThisExpression(ref, true)
							});
					}
				}
				//If it depends on outer this as well.
				else if (family.isOuterThis())
				{
					JClassDeclaration fjOwnerClass = 
						cleanClass.getOwnerDeclaration();
					if (fjOwnerClass instanceof FjCleanClassDeclaration)
					{
						FjCleanClassInterfaceDeclaration cleanOwnerInterface = 
							((FjCleanClassDeclaration) fjOwnerClass).getCleanInterface();
						if (typeSystem.declaresInner(
								cleanOwnerInterface.getCClass(), 
								lowerBound.getIdent()) != null)
						{
							//parameter._adapt<TypeName>(this$)
							return 
								new FjMethodCallExpression(
									ref,
									parameterAccess,
									CciConstants.toAdaptMethodName(
										lowerBound.getIdent()),
								new JExpression[]
								{
									new FjThisExpression(
										ref, 
										new FjNameExpression(
											ref, 
											cleanOwnerInterface.getIdent()))
								});
						}
					}
				
				}
			}
		}
		//Returns the simple parameter access.
		return parameterAccess;
	}

	public void upcastOverriddenType( CTypeContext context ) throws UnpositionedError {

		if( scip() )
			return;
			
		if( getFamily() == null )
			return;

		FjTypeSystem fjts = new FjTypeSystem();
		CClass lowerBound = fjts.lowerBound(
			context,
			getFamily().getType().getCClass(),
			getFamily().getInnerType().getIdent() ).getCClass();
		CClass upperBound = fjts.upperBound(
			context,
			getFamily().getType().getCClass(),
			getFamily().getInnerType().getIdent() ).getCClass();

		if( !upperBound.descendsFrom( lowerBound ))
			introduceDownCastVariable(context);
	}

	public void addFamily( CTypeContext context ) throws UnpositionedError {

		FjMethodDeclaration method = (FjMethodDeclaration)
			((FjAdditionalContext) context).peekContextInfo(1);

		if( scip() )
			return;
		
		FjTypeSystem fjts = new FjTypeSystem();
		//Walter: inserted the second param in the method call bellow
		FjFamily family = fjts.resolveFamily(context, 
			context.getClassContext().getCClass(), type);
		
		// if the typename is qualified by a variable
		// the qualifier has to be resolved to its type
		if( family != null ) {
			FjFamilyContext.getInstance().setFamilyOf( this, family );
			if( family.isParameter() )
				this.type = family.getInnerType();
			else
				this.type = fjts.upperBound( context, family.getInnerType() );
			this.family = family;
		}
	}
	
	public FjFamily getFamily() {
		return family;
	}
	*/
	
	/* (non-Javadoc)
	 * @see org.caesarj.kjc.JFormalParameter#checkInterface(org.caesarj.kjc.CTypeContext)
	 * Walter
	 */
	/* FJRM
	public CType checkInterface(CTypeContext context)
	{		
		try 
		{		
			type = type.checkType(context);		
		} 
		catch (UnpositionedError cue) 
		{
			//The code bellow was not here, the only part was here is 
			//that inside the if...
			if (cue.getFormattedMessage().getDescription()
				!= KjcMessages.CLASS_AMBIGUOUS)
			{
				context.reportTrouble(cue.addPosition(getTokenReference()));
				return context.getTypeFactory().createReferenceType(
				   TypeFactory.RFT_OBJECT);
			}
					
			CClass[] candidates = (CClass[]) 
				cue.getFormattedMessage().getParams()[1];
				
			type = candidates[0].getAbstractType();
		}
		
		return type;
	}
	*/	

}
